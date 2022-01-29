FROM moxm/java:1.8-full as builder

MAINTAINER jaytp@qq.com

ADD https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.75/bin/apache-tomcat-8.5.75.tar.gz /usr/local

RUN tar -xvf /usr/local/apache-tomcat-8.5.75.tar.gz -C /usr/local/

ENV TZ=Asia/Shanghai

ENV CATALINA_HOME /usr/local/apache-tomcat-8.5.75

ENV PATH $CATALINA_HOME/bin:$PATH

RUN mkdir -p "$CATALINA_HOME"

WORKDIR $CATALINA_HOME

COPY target/layui-soul-table-java.war ./webapps/

#ENTRYPOINT ["/usr/local/apache-tomcat-8.5.75/bin/catalina.sh", "run"]
#CMD sleep 60; rm -rf webapps/ROOT/*; unzip -oq layui-soul-table-java.war -d webapps/ROOT/;
CMD sleep 60; sh bin/catalina.sh run;
#CMD  while true; do echo hello world; sleep 1; done
EXPOSE 8080
