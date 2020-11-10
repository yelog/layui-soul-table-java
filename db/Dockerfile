FROM mysql:8.0.21

MAINTAINER yelog(jaytp@qq.com)

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./soulTable.sql /docker-entrypoint-initdb.d
