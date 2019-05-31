FROM ubuntu

RUN apt-get update
RUN apt-get install -y cmake git libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev openjdk-8-jdk software-properties-common

RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update

RUN echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections && apt-get install -y oracle-java8-installer
RUN apt-get install -y ant



RUN git clone https://github.com/Itseez/opencv.git\
    && cd opencv\
    && git checkout 3.4\ 
    && mkdir build\
    && cd build

# RUN export JAVA_HOME=/usr/lib/jvm/java1.8.0-openjdk-amd64

# RUN export ANT_HOME=/usr/bin/ant

RUN cd /opencv/build/ &&  cmake -D BUILD_SHARED_LIBS=OFF ..

RUN cd /opencv/build/ && make -j8

RUN git clone https://github.com/Nassafy/ESIRTPDockerSampleApp.git

WORKDIR /ESIRTPDockerSampleApp

RUN  apt-get install -y maven\
     &&	mvn install:install-file -Dfile=/opencv/build/bin/opencv-346.jar -DgroupId=org.opencv -DartifactId=opencv -Dversion=3.4.6 -Dpackaging=jar \
     && mvn install\
     && mvn package\
     && java -Djava.library.path=/opencv/build/lib/ -jar target/fatjar-0.0.1-SNAPSHOT.jar 	
WORKDIR /

RUN mkdir buildimage/ \
    && cp /opencv/build/lib/ buildimage/opencv/build/lib/\
    && cp /ESIRTPDockerSample/target/fatjar-0.0.1-SNAPSHOT.jar buildimage/
  	
VOLUME ["/buildimage"] 	
	
