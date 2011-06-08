Grails Pet Clinic application
=============================

This is a Grails port of the standard Spring Pet Clinic web application. It's very simple, consisting of a few domain classes for persistence and three controllers to support the user interface.

The domain model
----------------

This is the basic domain model for the Pet Clinic application:

               Person               Clinic
              /      \
          Owner      Vet
            |         |
           Pet    Speciality
          /   \
    PetType  Visit

An `Owner` has many `Pet`s, which in turn has many `Visit`s and a `PetType`. A `Vet` has many `Speciality`s. `Clinic` is on its own, but has a controller dedicated to it.

Building and running
--------------------

To run this application, make sure you have the appropriate version of Grails installed (1.3.7 at the time of writing - but check application.properties). Then:

    grails run-app

This will start the application in development mode and use an in-memory HSQLDB database.

Deploying to Cloud Foundry
--------------------------

To save some effort, it's worth changing the name of the project before deploying to Cloud Foundry because the default URL is based on it and petclinic-grails.cloudfoundry.com is already taken! So, open the `application.properties` file in the root of the project and change the value for `app.name`, for example:

    app.name=petclinic-grails-pal

where 'pal' could be your Cloud Foundry account name.

Once that's done, the application already has the Cloud Foundry plugin configured so all you need to do is run:

    grails prod cf-push

to deploy the application to the cloud. When it asks for a URL use the default if you changed the application name, or provide a unique *.cloudfoundry.com host name. If you don't have a MySQL service provisioned yet, the command will also ask whether you want to provision and bind one to the application. Agree to this to ensure the application will work when it starts up.

That's it. As long as the application successfully deploys, you'll be able to access it via the URL you chose. If it doesn't start successfully, you will be shown the server logs so that you have some idea of what went wrong.

For more information on the commands you can use with Cloud Foundry, check out the plugin's [user guide](http://grails-plugins.github.com/grails-cloud-foundry/docs/manual/index.html).
