******************************************************************************************
BUILD
******************************************************************************************

To build the whole project, navigate to MessageBroker module and run the following command:
mvn clean install 

In order to modify the Topics that are used whilst the application is running, navigate to
core/src/main/resources (refer to Report, Section 4. Assumptions, Part b) : 
i. SampleMultiLevelWildcardTopics.txt - Samples for Multi Level Wildcard Topicsii. SampleSingleLevelWildcardTopics.txt - Samples for Single Level Wildcard Topicsiii. SampleStandardTopics.txt - Samples for StandardTopics


The executable modules are listed below, i.e. (broker, publisher, subscriber)

******************************************************************************************
BROKER
******************************************************************************************

To execute the server, navigate to the broker module and run the following command:
mvn exec:java

This executes the server.

******************************************************************************************
PUBLISHER
******************************************************************************************

To execute the publisher, navigate to the publisher module and run the following command:
mvn exec:java


******************************************************************************************
SUBSCRIBER
******************************************************************************************

To execute the subscriber, navigate to the subscriber module and run the following command:
mvn exec:java


