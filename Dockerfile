FROM p7hb/docker-spark:2.1.0

MAINTAINER Abhiram Iyenger <abhirj87@gmail.com>

VOLUME ["/home"]
ADD ./target /home/
#ADD ./src/main/resources/start_job.sh /home/start_job.sh

# We will be running our Spark jobs as `root` user.
USER root

# Working directory is set to the home folder of `root` user.
WORKDIR /home
RUN echo "start-master.sh > /home/run.txt" >> ~/.bashrc
RUN echo "start-slave.sh spark://spark:7077 > /home/run-slave.txt" >> ~/.bashrc
# RUN echo "cd /home/ ; curl -O ftp://ita.ee.lbl.gov/traces/NASA_access_log_Jul95.gz" >>  ~/.bashrc
RUN echo "gunzip -f NASA_access_log_Jul95.gz" >> ~/.bashrc
RUN echo "cd /home ; mkdir -p input ; cp NASA_access_log_Jul95 input/" >> ~/.bashrc
RUN echo "sh /home/start_job.sh" >> ~/.bashrc
# Expose ports for monitoring.
# SparkContext web UI on 4040 -- only available for the duration of the application.
# Spark master’s web UI on 8080.
# Spark worker web UI on 8081.
EXPOSE 4040 8080 8081

CMD ["/bin/bash"]