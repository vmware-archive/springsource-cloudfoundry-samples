CloudFoundry
============

cloudfoundry is a Node.js helper library for http://www.cloudfoundry.org/


Usage
-----
Use npm or download. Then add to your code:

	var cloudfoundry = require('cloudfoundry');
	
	// is app running in cloud?
	cloudfoundry.cloud
	
	// host you should use
	cloudfoundry.host
	
	// port you should use
	cloudfoundry.port
	
	// your app information
	cloudfoundry.app
	
	{
	    instance_id: '7bcc459686eda42a8d696b3b398ed6d1',
	    instance_index: 0,
	    name: 'example',
	    uris: ['example.cloudfoundry.com'],
	    users: ['igo@example.com'],
	    version: '11ad1709af24f01286b2799bc90553454cdb96c6-1',
	    start: '2011-05-07 19:23:39 +0000',
	    runtime: 'node',
	    state_timestamp: 1304796219,
	    port: 55690,
	    limits: {
	        fds: 256,
	        mem: 134217728,
	        disk: 2147483648
	    },
	    host: '172.30.49.112'
	}
	
	// services bound to your app
	cloudfoundry.services
	
	{
	    'mongodb-1.8': [{
	        name: 'test-mongodb',
	        label: 'mongodb-1.8',
	        plan: 'free',
	        credentials: {
	            hostname: '172.30.48.65',
	            port: 25009,
	            username: '...',
	            password: '...',
	            name: 'mongodb-...',
	            db: 'db'
	        },
	        version: '1.8'
	    }],
	    'redis-2.2': [{
	        name: 'test-redis',
	        label: 'redis-2.2',
	        plan: 'free',
	        credentials: {
	            node_id: 'redis_node_2',
	            hostname: '172.30.48.41',
	            port: 5008,
	            password: '...',
	            name: 'redis-...'
	        },
	        version: '2.2'
	    }],
	    'mysql-5.1': [{
	        name: 'test-mysql',
	        label: 'mysql-5.1',
	        plan: 'free',
	        credentials: {
	            node_id: 'mysql_node_4',
	            hostname: '172.30.48.23',
	            port: 3306,
	            password: '...',
	            name: '...',
	            user: '...'
	        },
	        version: '5.1'
	    }]
	}
	
	// quick access to services
	cloudfoundry.<service>.<name>
	// for example, quick access to your mongodb
	cloudfoundry.mongodb['test-mongodb'].credentials.hostname
	cloudfoundry.mongodb['test-mongodb'].credentials.port
	cloudfoundry.mongodb['test-mongodb'].credentials.db
	cloudfoundry.mongodb['test-mongodb'].credentials.username
	cloudfoundry.mongodb['test-mongodb'].credentials.password


License
-------
Released under MIT License. Enjoy and Fork!