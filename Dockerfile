FROM openjdk:11
ADD target/skillbox_blog_engine.jar skillbox_blog_engine.jar

# подключение к heroku mysql ####################################
#ENV JAWSDB_URL mysql://ofxp0lorou4t997f:joqz7gsen9s2d8j2@pei17y9c5bpuh987.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/j40gg3aid3x1ftdr?useUnicode=true&characterEncoding=utf-8&reconnect=true
#ENTRYPOINT ["java", "-jar","skillbox_blog_engine.jar"]

# подключение к локальному mysql ##########################
RUN apt-get update && apt-get install -y net-tools
#RUN apt-get update && apt-get install -y iproute2
#RUN /sbin/ip route|awk '/default/ { print $3 }'
ENTRYPOINT export DOCKER_HOST_IP=$(route -n | awk '/UG[ \t]/{print $2}') && echo "env DOCKER_HOST_IP="$DOCKER_HOST_IP && java -jar skillbox_blog_engine.jar
###########################################################

EXPOSE 8080
EXPOSE 3306