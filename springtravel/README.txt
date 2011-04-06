Spring Travel Reference Application
- demonstrates Spring Framework 3 with Web Flow 2.1

NOTE: 
At this time this sample is fully functional and upgraded. 
However, some work is still in progress as listed below. 

Known issues:
- The sample currently uses an extension for resource handling until mvc:resources support is integrated into Spring Framework 3.1 trunk.  This built-in resource handling support in Spring Framework will supercede the ResourceServlet in the Spring JavaScript module (see SPR-7116).
- JSR303 support is not yet integrated


TODO:
 - support user registration using Facebook + Spring Social
 - support RESTful authentication from clients in a secure way (SSL + some sort of shared secret? )
