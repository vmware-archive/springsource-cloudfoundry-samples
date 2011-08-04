# Spring AMQP &pi; Calculation Demo for CloudFoundry

This demo is an adaptation of the [Akka Getting Started Tutorial](http://akka.io/docs/akka/1.1.1/intro/getting-started-first-scala.html) that calculates the value of &pi; but using RabbitMQ worker queues instead of Actors.  For an introduction to using worker queues with RabbitMQ see the [RabbitMQ Work Queues tutorial](http://www.rabbitmq.com/tutorials/tutorial-two-java.html).  

The demo consists of two web applications.  The first is named 'picalc-master' and the second is named 'picalc-worker'.  

The master application takes input from a form specifying how to divide up the calculation into multiple parts.  For each part of the calculation, the master sends a message to a Task Queue.  Workers consume messages from the Task Queue and reply with the result to a Result Queue.  The Master reads from the Result Queue, aggregates all the individual results, and displays the final result.

The advantage of this architecture is that you can parallelize the work by adding more workers.  This is because each worker will take messages from the Task Queue and process them concurrently.  Using the vmc command 'vmc instances picalc-worker 4' you can vary the the number of worker process. (In this case, to 4).  To maximize the effect of scaling up, each worker is single threaded.

## Setup

This demo will use the command line 'vmc' tool to interact with CloudFoundry.  Follow the steps in [Prerequisites for Sample Applications](https://github.com/SpringSource/cloudfoundry-samples/wiki/Prerequisites-for-Sample-Applications) if you have not already installed the 'vmc' client. 

If you have already installed vmc, you should update the current version by typing 'gem update vmc'

After logging into CloudFoundry, create a RabbitMQ service.  Using the command line:

    $ vmc create-service rabbitmq rabbitmq-calc
    
    
Then build and deploy the master (Note: pick a different name for the deployed URL)

    $ cd picalc-master
    $ mvn package
    
    $ vmc push picalc-master --nostart --path target
    Application Deployed URL: 'picalc-master.cloudfoundry.com'? y
    Detected a Java SpringSource Spring Application, is this correct? [Yn]:
    Memory Reservation [Default:512M] (64M, 128M, 256M, 512M, 1G or 2G) 256M
    Creating Application: OK
    Would you like to bind any services to 'picalc-master'? [yN]: y
    Would you like to use an existing provisioned service [yN]? y
    The following provisioned services are available:
    1. rabbitmq-calc
    2. mongodb-hello
    Please select one you wish to provision: 1
    Binding Service: OK
    Uploading Application:
      Checking for available resources: OK
      Processing resources: OK
      Packing application: OK
      Uploading (187K): OK
    Push Status: OK    
    
    $ vmc start picalc-master

Then build and deploy the worker (Note: pick a different name for the deployed URL)

    $ cd picalc-worker
    $ mvn package
    
    $ vmc push picalc-worker --nostart --path target
    Application Deployed URL: 'picalc-worker.cloudfoundry.com'?
    Detected a Java SpringSource Spring Application, is this correct? [Yn]: Y
    Memory Reservation [Default:512M] (64M, 128M, 256M, 512M, 1G or 2G) 256M
    Creating Application: OK
    Would you like to bind any services to 'picalc-worker'? [yN]: y
    Would you like to use an existing provisioned service [yN]? y
    The following provisioned services are available:
    1. rabbitmq-calc
    2. mongodb-hello
    Please select one you wish to provision: 1
    Binding Service: OK
    Uploading Application:
      Checking for available resources: OK
      Processing resources: OK
      Packing application: OK
      Uploading (6K): OK
    Push Status: OK
    
    $ vmc start picalc-worker
    
## Using the application

Open the URL of your master application.  In this demo it is [http://picalc-master.cloudfoundry.com/](http://picalc-master.cloudfoundry.com/)

The form will ask you to enter the "Number of messages to publish per calculation request".  Enter 5000.

The form will then update when the calculation has been completed

    Value: 3.1415926335897875

    Error: -2.000000565161031E-8

    Calc Time: 6234 ms.
    
    
## Increasing the number of workers

Using the command line increase the number of workers to 4

     $vmc instances picalc-worker 4
     Scaling Application instances up to 4: OK
     
You can see how many instances are running by

     $vmc instances picalc-worker
     
     +-------+---------+--------------------+
     | Index | State   | Start Time         |
     +-------+---------+--------------------+
     | 0     | RUNNING | 08/04/2011 02:13PM |
     | 1     | RUNNING | 08/04/2011 02:12PM |
     | 2     | RUNNING | 08/04/2011 02:12PM |
     | 3     | RUNNING | 08/04/2011 02:12PM |  
     +-------+---------+--------------------+
     
Now enter 5000 again in the form.  The new results are
    
    Value: 3.141592633589787

    Error: -2.000000609569952E-8

    Calc Time: 3325 ms.
    
## Looking at the worker code

This project uses Spring AMQP and as such, the logic in the worker process for performing the calculation can be inside a POJO.  There are no dependencies on any Rabbit or Spring APIs, increasing the testability and portability of the processing logic.  Here is a version of the handler code but with logging statements removed.

        public class WorkerHandler {

            public ResultMessage handleMessage(WorkMessage workMessage) {

                double result = calculatePiFor(workMessage.getStart(), workMessage.getNrOfElements());

                ResultMessage resultMessage = new ResultMessage();
                resultMessage.setCorrelationId(workMessage.getCorrelationId());
                resultMessage.setResult(result);               
                return resultMessage;
           }
        
           private double calculatePiFor(int start, int nrOfElements) {
                double acc = 0.0;
                for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
                    acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
                }
                return acc;
           }
        }
        
The WorkMessage class is a simple container of data and is serialized as a JSON payload inside a RabbitMQ message.      

## Looking at the master code

The master code has additional complexity due to the need to wait and aggregate the results.  Each web request is associated with a unique UUID and responses are aggregated per UUID.  The following code shows the use of RabbitTemplate in the Controller to send the message, specify the reply to address, and aggregate the results


        public double getPi(int nrOfMessages) {
                String corrId = UUID.randomUUID().toString();
                
                int nrOfElements = 10000;
                
                masterHandler.registerAggregate(corrId, nrOfMessages);
                
                for (int i = 0; i < nrOfMessages; i++) {
                        
                        WorkMessage workMessage = new WorkMessage();
                        workMessage.setCorrelationId(corrId);
                        workMessage.setStart(i);
                        workMessage.setNrOfElements(nrOfElements);
                        
                        rabbitTemplate.convertAndSend(workMessage, new MessagePostProcessor() {                         
                                public Message postProcessMessage(Message message) throws AmqpException {
                                        message.getMessageProperties().setReplyToAddress(new Address("direct://piExchange/" + QueueNames.RESULT_QUEUE_NAME));
                                        return message;
                                }
                        });
                }
                
                logger.info("Sent messages to workers.  Waiting for response...");
                try {
                        masterHandler.await(corrId);
                } catch (InterruptedException e) {
                        logger.error("Error waiting for response from workers", e);
                }
                return masterHandler.getResult(corrId);
        }

## Aggregation using Spring Integration

The Spring Integration project provides more extensive support for [Enterprise Integration Patterns](http://www.eaipatterns.com/) such as the [Aggregator](http://www.eaipatterns.com/Aggregator.html) used above.  The implementation of the Aggregator used in this code was derived from that available in the [Enterprise Integration Patterns Book](http://amazon.com/o/asin/0321200683/ref=nosim/enterpriseint-20).

You can read more about Spring Integration's support for the Aggregator pattern [here](http://static.springsource.org/spring-integration/reference/htmlsingle/#aggregator) and also see it in action with RabbitMQ in [this blog article](http://krams915.blogspot.com/2011/03/spring-integration-2-integrating.html)

