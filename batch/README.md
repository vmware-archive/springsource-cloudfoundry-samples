# Spring Batch Cloud Sample

This app is the same as the one in [Spring Batch Admin
Samples](http://github.com/SpringSource/spring-batch-admin), but
enhanced to run nicely in [CloudFoundry](http://cloudfoundry.com).
Unlike most of the other CloudFoundry samples it uses Spring 3.0 (not
3.1 which hasn't shipped yet).

## Set Up

The app is a vanilla Spring MVC web application, so you can build with
Maven (or your tool of choice) and deploy with `vmc`, or use the
SpringSource ToolSuite CloudFoundry server tooling.

First create the services (the names are significant because they are
hard-coded in the application).  Using the command line:

    $ vmc create-service mysql mysql

Then build and deploy:

    $ mvn package
    $ vmc push batch --nostart --path target
    $ vmc bind-service mysql batch
    $ vmc start batch
    
The application will run fine without the mysql service, but it will
use an in-memory database, so only one instance makes sense, and the
data are lost on restart.

## Basic Use Cases

Basic use cases are described in more detail in the [Spring Batch Admin user guide](http://static.springframework.org/spring-batch-admin/reference).
