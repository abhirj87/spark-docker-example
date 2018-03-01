geolocation_lookup
Description
This is a Spring boot RESTful web service (HTTP) which is capable of looking up a physical street address given a set of geographic coordinates (longitude and latitude values). For example, given the latitude '33.969601' and longitude '-84.100033', the service will return the address of the NCR office in Duluth, GA (2651 Satellite Blvd, Duluth, GA 30096).  

This implementation will delegate to the geocoding API for Google Maps to perform the lookup.

The service will accept latitude and longitude coordinates for a location on Earth. and for a valid set of coordinates, it will return the full, street address (including city, state/province, and zip/postal code) of the location at those coordinates.

The service will cache (locally) the last 10 lookups and provide an additional RESTful API for retrieving this stored data.  The data returned from this API will be a collection of the lookups performed, including the longitude and latitude values, the address found, and the date/time of the lookup.

The API is documented in using swagger.

The application produces and consumes json and free text.

Example: http://localhost:8001/swagger-ui.html#/geo-account-controller

Requirements
Java 1.8 or later.
Usage
The applciation can be run by the fowllowing mvn command from the parent directory.

mvn spring-boot:run

Detailed API documentation is availabel thru swagger. Ex: http://localhost:8001/swagger-ui.html#/geo-account-controller

Examples: It supports GET and POST methods and application/json only.

Ex 1:Getting address via POST(sending the json via request body)
localhost:8001/geolocation/latlng/ { "lat" : 33.969601, "lng" : -84.100033 }

Response:

{ "latLng":{"lat":33.969601,"lng":-84.100033}, "geocodingAddress":"2651 Satellite Blvd, Duluth, GA 30096, USA", "timeOfrequest":"Tue Jan 24 23:42:32 EST 2017" }

Ex 2: Getting address via GET
http://localhost:8001/geolocation/latlng/40.714224,-73.961452

Response: {"latLng":{"lat":33.969601,"lng":-84.100033},"geocodingAddress":"2651 Satellite Blvd, Duluth, GA 30096, USA","timeOfrequest":"Tue Jan 24 23:42:32 EST 2017"}

Ex 3: Doing a lookup of recent 10 address searches
http://localhost:8001/geolocation/recent_lookups/

Response:

[{"latLng":{"lat":33.969601,"lng":-84.100033},"geocodingAddress":"2651 Satellite Blvd, Duluth, GA 30096, USA","timeOfrequest":"Tue Jan 24 23:42:32 EST 2017"},{"latLng":{"lat":40.714224,"lng":-73.0},"geocodingAddress":"Trustees Walk, Patchogue, NY 11772, USA","timeOfrequest":"Tue Jan 24 23:44:13 EST 2017"}]

running the application:
./src/main/resources/run.sh


Sending build context to Docker daemon  543.7MB
Step 1/13 : FROM p7hb/docker-spark:2.1.0
 ---> 0f1469d4cf27
Step 2/13 : MAINTAINER Abhiram Iyenger <abhirj87@gmail.com>
 ---> Using cache
 ---> cce069b1a632
Step 3/13 : VOLUME /home
 ---> Using cache
 ---> 8613c4817517
Step 4/13 : ADD ./target /home/
 ---> 383e31449d37
Step 5/13 : USER root
 ---> Running in 4592f29a0367
 ---> 8db8bbd6b955
Removing intermediate container 4592f29a0367
Step 6/13 : WORKDIR /home
 ---> 10af77c1b046
Removing intermediate container 8f7a26297c8a
Step 7/13 : RUN echo "start-master.sh > /home/run.txt" >> ~/.bashrc
 ---> Running in 1a40304d689d
 ---> a80397aed926
Removing intermediate container 1a40304d689d
Step 8/13 : RUN echo "start-slave.sh spark://spark:7077 > /home/run-slave.txt" >> ~/.bashrc
 ---> Running in 393a416b5f03
 ---> 9a5114a8f52a
Removing intermediate container 393a416b5f03
Step 9/13 : RUN echo "gunzip NASA_access_log_Jul95.gz" >> ~/.bashrc
 ---> Running in 0f0b2ccae439
 ---> 40c4b63fef92
Removing intermediate container 0f0b2ccae439
Step 10/13 : RUN echo "cd /home ; mkdir input ; cp NASA_access_log_Jul95 input/" >> ~/.bashrc
 ---> Running in e20399e54ffc
 ---> afa9d06d7da1
Removing intermediate container e20399e54ffc
Step 11/13 : RUN echo "sh /home/start_job.sh" >> ~/.bashrc
 ---> Running in e9bb81917dc9
 ---> e6ff216e4aea
Removing intermediate container e9bb81917dc9
Step 12/13 : EXPOSE 4040 8080 8081
 ---> Running in 1ce54a0ad334
 ---> e8dd56c06e16
Removing intermediate container 1ce54a0ad334
Step 13/13 : CMD /bin/bash
 ---> Running in b1c7eb78947c
 ---> 4cad8e4de9da
Removing intermediate container b1c7eb78947c
18/03/01 20:44:05 INFO SparkContext: Running Spark version 2.1.0
18/03/01 20:44:05 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
18/03/01 20:44:05 INFO SecurityManager: Changing view acls to: root
18/03/01 20:44:05 INFO SecurityManager: Changing modify acls to: root
18/03/01 20:44:05 INFO SecurityManager: Changing view acls groups to: 
18/03/01 20:44:05 INFO SecurityManager: Changing modify acls groups to: 
18/03/01 20:44:05 INFO SecurityManager: SecurityManager: authentication disabled; ui acls disabled; users  with view permissions: Set(root); groups with view permissions: Set(); users  with modify permissions: Set(root); groups with modify permissions: Set()
18/03/01 20:44:06 INFO Utils: Successfully started service 'sparkDriver' on port 35939.
18/03/01 20:44:06 INFO SparkEnv: Registering MapOutputTracker
18/03/01 20:44:06 INFO SparkEnv: Registering BlockManagerMaster
18/03/01 20:44:06 INFO BlockManagerMasterEndpoint: Using org.apache.spark.storage.DefaultTopologyMapper for getting topology information
18/03/01 20:44:06 INFO BlockManagerMasterEndpoint: BlockManagerMasterEndpoint up
18/03/01 20:44:06 INFO DiskBlockManager: Created local directory at /tmp/blockmgr-ef7a4b5b-9b93-4582-b3a0-8ea3f065a76a
18/03/01 20:44:06 INFO MemoryStore: MemoryStore started with capacity 366.3 MB
18/03/01 20:44:06 INFO SparkEnv: Registering OutputCommitCoordinator
18/03/01 20:44:06 INFO Utils: Successfully started service 'SparkUI' on port 4040.
18/03/01 20:44:06 INFO SparkUI: Bound SparkUI to 0.0.0.0, and started at http://localhost:4040
18/03/01 20:44:06 INFO SparkContext: Added JAR file:/home/top_n_processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar at spark://localhost:35939/jars/top_n_processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar with timestamp 1519937046711
18/03/01 20:44:06 INFO StandaloneAppClient$ClientEndpoint: Connecting to master spark://spark:7077...
18/03/01 20:44:06 INFO TransportClientFactory: Successfully created connection to spark/172.17.0.2:7077 after 25 ms (0 ms spent in bootstraps)
18/03/01 20:44:07 INFO StandaloneSchedulerBackend: Connected to Spark cluster with app ID app-20180301204407-0000
18/03/01 20:44:07 INFO Utils: Successfully started service 'org.apache.spark.network.netty.NettyBlockTransferService' on port 35593.
18/03/01 20:44:07 INFO NettyBlockTransferService: Server created on localhost:35593