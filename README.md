<h2>Spark-Docker Application</h2>
<h3>Description</h3>
This is a Spark application that downloads the dataset at ftp://ita.ee.lbl.gov/traces/NASA_access_log_Jul95.gz and uses Apache Spark to determine the top-n most frequent visitors and urls for each day of the trace. The java application is packaged with a "Dockerized Apache Spark" run in a containerized environment. 

For the dockerized spark image I have used the p7hb/docker-spark:2.1.0 as my base image. The detailed documentation is in the link below:
<p>https://github.com/P7h/docker-spark</p>


<h3>Requirements</h3>
Java 1.8 or later.
Maven 3 or later

<h3>Usage</h3>
<ul>
    <li>Build the application with: <b>mvn clean install</b></li>
<li>Command[from root of the project]: <b>./src/main/resources/run.sh</b> Once the application is built, triggering <b>run.sh</b> in the main/resources will launch the application.</li>
<li>start_job.sh shell script available in the main/resources folder will contain the command that starts the spark job  once the docker container is up. This script will be copied from main/resources to the container in the previous step. Make sure that this script is run from the root of the project like shown below.</li>
<li>The Spark job takes 5 arguments. If the number of arguments are not 5 then usage will be displayed. If it is started without any arguments then a sample dataset stored in the project will be run for demonstration purposes.</li>
</ul>
<p>Eg: </p>

    spark-submit --class org.log_analyser.AccessLogProcessor --master spark://spark:7077 top_n_processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    input \#input dir where the data is downloaded
    output_data \ #dir table name by which the output of the program will be stored
    spark://spark:7077 \ #spark master
    5 \ #number of partition 
    3 # this is the N in topN. Say if N=3 then for each day we will have top 3 visitors displayed
    
./src/main/resources/run.sh is the shell script which builds the docker image from the Dockerfile available in the root of the project folder. 

    Eg: from the root of the project run the following
        $./src/main/resources/run.sh

        Sending build context to Docker daemon  543.7MB
        Step 1/13 : FROM p7hb/docker-spark:2.1.0
         ---> 0f1469d4cf27
        Step 2/13 : MAINTAINER Abhiram Iyenger <abhirj87@gmail.com>
         ---> Using cache
        .
        .
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
        .
        .
        .
<h3>Validation of results</h3>

Once the program completes, randomly data is checked and verfied by running wc -l command against the source.
If the counts are not matching then it will be notified on screen.
After validation it will leave us in the Spark container so that if we can recheck the results.
Do a exit to come out of the container.


    Eg: Happy Path:
    Run Completed :) !!
    Moment of Truth: Yes its time for validation!!
    Matched!!! count from  wc command: 1199  count in o/p data: 1199
    Matched!!! count from  wc command: 828  count in o/p data: 828
    Matched!!! count from  wc command: 460  count in o/p data: 460
    root@spark:/home# 
    
    Eg: When Validation fails
    Validation failed!!" 222 500
    Validation failed!!"
    Validation failed!!"
    Validation failed!!"
    