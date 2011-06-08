Grails Twitter application
==========================

This moderately complex Twitter clone demonstrates how you can combine a standard SQL store, MongoDB, and Redis all within a single Grails application deployed on Cloud Foundry. It's pretty fast too!

The messages (MongoDB)
----------------------

Each message that a user posts is stored in a MongoDB database as an instance of `org.grails.twitter.Status`. If you take a look at the class, you will see the line:

    static mapWith = "mongo"

which binds the domain class to MongoDB instead of MySQL. You will also see that there is a constraint limiting the size of a message to 160 characters.

Status messages have no complex relationships but you can expect a large volume of them. That makes MongoDB an ideal data store for them. In addition, MongoDB works well with embedded collections of simple types like strings, hence that's how tags are implemented:

    List<String> tags = []

The downside of this approach is that you don't have a separate domain class to query for tags. Instead, the `cacheTags()` method of `org.grails.twitter.TagService` uses a map-reduce function to aggregate tag information.

The users (MySQL)
-----------------

Access control for this sample application is handled via Spring Security. It's possible to configure the library to use MongoDB for the users, but it's far simpler to user MySQL instead. So, the `org.grails.twitter.auth.Person` and `org.grails.twitter.auth.Authority` domain classes are both mapped via Hibernate.

Now, in most Grails applications `Status` would have a direct reference to the `Person` class, i.e. the author of the status message, but this isn't possible when the two domain classes are mapped by different providers. So, `Status` stores an explicit `Authority` instance ID (as the property `authorId`) and provides easy access to the associated `Authority` instance via the read-only, transient `author` property.

Caching (Redis)
---------------

Evaluation of the tags and how many messages contain each of them is a fairly expensive operation, so we don't want to keep doing it every time the main page is hit. So, when a status message is posted and saved, the tags are re-evaluated on a background thread and cached in a Redis database. Every time the tags are required for the main page, they are pulled from Redis. This is all handled inside `TagService`.

Those are the basics of how the application fits together and how each of the data stores is used. Trying it out is a simple case of getting hold of the source code from GitHub, then building and running it.

Building and running
--------------------

To run this application, make sure you have the appropriate version of Grails installed (1.3.7 at the time of writing - but check application.properties). Also make sure you have instances of MongoDB and Redis running, both on the default settings. Then:

    grails run-app

This will start the application in development mode and use the local MongoDB and Redis instances plus an in-memory HSQLDB database.

Deploying to Cloud Foundry
--------------------------

To save some effort, it's worth changing the name of the project before deploying to Cloud Foundry because the default URL is based on it - and grailstwitter.cloudfoundry.com is already taken! So, open the `application.properties` file in the root of the project and change the value for `app.name`, for example:

    app.name=grailstwitter-pal

where 'pal' could be your Cloud Foundry account name.

Once that's done, the application already has the Cloud Foundry plugin configured, so all you need to do is run:

    grails prod cf-push

to deploy the application to the cloud. When it asks for a URL use the default if you changed the application name, or provide a unique *.cloudfoundry.com host name. If you don't have any services provisioned yet, the command will also ask whether you want to provision and bind them to the application. Agree for each of the MySQL, MongoDB, and Redis services.

That's it. As long as the application successfully deploys, you'll be able to access it via the URL you chose. If it doesn't start successfully, you will be shown the server logs so that you have some idea of what went wrong.

For more information on the commands you can use with Cloud Foundry, check out the plugin's [user guide](http://grails-plugins.github.com/grails-cloud-foundry/docs/manual/index.html).
