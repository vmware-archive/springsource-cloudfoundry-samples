package org.cloudfoundry.picalc;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import org.cloudfoundry.picalc.config.QueueNames;
import org.cloudfoundry.picalc.messaging.MasterHandler;
import org.cloudfoundry.picalc.messaging.WorkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private Queue workQueue;
	
	@Autowired
	private MasterHandler masterHandler;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		logger.info("Welcome home!");
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);	
		String formattedDate = dateFormat.format(date);		
		model.addAttribute("serverTime", formattedDate );
		
		model.addAttribute(new CalcParams());
		
		return "home";
	}
	
	@RequestMapping(value = "/purge", method = RequestMethod.GET) 
	@ResponseBody
	public String purgeQueue() {
		RabbitAdmin admin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());		
		admin.purgeQueue(QueueNames.WORK_QUEUE_NAME, true);
		admin.purgeQueue(QueueNames.RESULT_QUEUE_NAME, true);
		return QueueNames.WORK_QUEUE_NAME + "and " + QueueNames.RESULT_QUEUE_NAME + " queues were purged.";
				
	}
	
	@RequestMapping(value = "/queue", method = RequestMethod.GET) 
	@ResponseBody
	public String queue() {
		return "Message count for " + QueueNames.WORK_QUEUE_NAME + " = " + this.declareQueuePassive(this.workQueue).getMessageCount();	
	}
	
	@RequestMapping(value="/calculate", method=RequestMethod.POST)
	public String publish(Model model, CalcParams calcParams) {
		long start = System.nanoTime();
		double dPi = getPi(calcParams.getNrOfMessages());
		long calcTime = (System.nanoTime() - start) / 1000000; 
		double error = dPi - Math.PI;		
		model.addAttribute("piValue", dPi);
		model.addAttribute("error", error);
		model.addAttribute("calcTime", calcTime);
		logger.info("PI = " + dPi + ", error = " + error);
		model.addAttribute("calculated", true);
		return home(model);
	}
	
	
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
	
	public DeclareOk declareQueuePassive(final Queue queue) {
		return  this.rabbitTemplate.execute(new ChannelCallback<DeclareOk>() {
			public DeclareOk doInRabbit(Channel channel) throws Exception {
				return channel.queueDeclarePassive(queue.getName());
			}
		});
	}

	
}
