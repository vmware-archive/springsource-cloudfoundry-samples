# Spring AMQP Stocks Demo for CloudFoundry

This app is the same as the one in [Spring AMQP
Samples](http://github.com/SpringSource/spring-amqp-samples), but
enhanced to run nicely in [CloudFoundry](http://cloudfoundry.com).
Unlike most of the other CloudFoundry samples it uses Spring 3.0 (not
3.1 which hasn't shipped yet).

## Set Up

The app is a vanilla Spring MVC web application, so you can build with
Maven (or your tool of choice) and deploy with `vmc`, or use the
SpringSource ToolSuite CloudFoundry server tooling.

First create the services (the names are not significant).  Using the
command line:

    $ vmc create-service rabbitmq rabbitmq
    $ vmc create-service redis redis

Then build and deploy:

    $ mvn package
    $ vmc push stocks --nostart --path target
    $ vmc bind-service rabbitmq stocks
    $ vmc bind-service redis stocks
    $ vmc start stocks
    
The app works without redis, but you don't get persistent config
changes, or consistent changes across a cluster at runtime (see
polling frequency use case below).

## Basic Use Cases

### Read Ticker Quotes

User opens the app in a browser, and the table at the bottom of the
page updates stock quotes as they arrive on the server.  It uses
simple AJAX polling to a RESTful service in the application.  The
service in turn is hooked up to a ticker stream that comes in over
RabbitMQ.  

### Buy Stock

User enters amount (number of shares) and ticker symbol in the form at
the top of the screen and submits (no validation is done so you can
"buy" non-existent stocks).  The server sends a buy message to the
broker, and it is picked up by the trading application (actually the
same app for this demo), executed, and a response is sent back.  The
client polls for the response using a unique trade id assigned when he
submitted the order (so no other clients can get his response, but the
server can be scaled to multiple instances behind a load balancer).

### Change Ticker Quote Polling Frequency

Change the frequency from the default (every 5 seconds) to every minute:

    $ curl http://stocks.vcap.me/refresh/trigger -d quote.trigger="0 * * * * *"

Verify that the change is persisted for the next restart:

    $ curl http://stocks.vcap.me/env
    ...
    ,
      "env": {
        "quote.trigger": "0 * * * * *"
      }
    }

The `env` properties are persisted in Redis (if the service was
bound).
