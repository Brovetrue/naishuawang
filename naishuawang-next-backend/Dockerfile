# 使用官方的 openjdk 镜像作为基础镜像
FROM openjdk:11

# 设置工作目录
WORKDIR /app

# 将当前目录下的 JAR 包复制到容器中的 /app 目录
COPY ./target/naishuawang-next-backend-0.0.1-SNAPSHOT.jar /app/naishuawang-next-backend-0.0.1.jar

# 暴露容器的端口（假设应用在 8080 端口运行）
EXPOSE 8123

# 启动 JAR 包
ENTRYPOINT ["java", "-jar", "/app/naishuawang-next-backend-0.0.1.jar"]
