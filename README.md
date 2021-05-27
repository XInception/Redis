# Redis
redis审核工具

# 工作流程
source -> input inception -> upstream -> redis -> output inception->downstream -> target


## TODO
- client 断线重连
- server 断线重连


## 如何使用
```
@echo off
chcp 65001
java -Dfile.encoding=utf-8  -jar ./target/XIncRedis.jar -c ./application-server.properties
```
 