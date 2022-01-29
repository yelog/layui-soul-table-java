# 说明：Dockerfile 过程分为两部分。第一次用来解压 jar 包，并不会在目标镜像内产生 history/layer。第二部分将解压内容分 layer 拷贝到目标镜像内
# 目的：更新镜像时，只需要传输代码部分，依赖没有变动则不更新，节省发包时的网络传输量
# 原理：在第二部分中，每次 copy 就会在目标镜像内产生一层 layer，将依赖和代码分开，
#      绝大部分更新都不会动到依赖，所以只需更新代码几十k左右的代码层即可

FROM amazoncorretto:11.0.11  as builder
WORKDIR /build
COPY target/layui-soul-table-java.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract && rm app.jar

FROM amazoncorretto:11.0.11
LABEL maintainer="jaytp@qq.com"
WORKDIR /tmp

# 依赖
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
# 应用代码
COPY --from=builder /build/application/ ./

# 容器运行时启动命令
CMD java -Xms256m -Xmx256m org.springframework.boot.loader.JarLauncher
