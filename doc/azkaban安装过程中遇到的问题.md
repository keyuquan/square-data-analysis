### webserver启动异常"No active executor found" 
修改数据库executors表中的active字段为1,并且在关闭exector时使用kill -9,不能使用azkaban-executor_shutdown.sh,否则要重新设置executors表中的active字段为1. 

### 执行azkaban任务时,executor报错,但不影响任务的执行:
```
2017-07-27 16:09:49 INFO  ExecutorServlet:115 - User  has called action log on 3
2017-07-27 16:09:49 ERROR ExecutorServlet:186 - Running flow 3 not found.
azkaban.executor.ExecutorManagerException: Running flow 3 not found.
    at azkaban.execapp.FlowRunnerManager.readFlowLogs(FlowRunnerManager.java:472)
    at azkaban.execapp.ExecutorServlet.handleFetchLogEvent(ExecutorServlet.java:183)
    at azkaban.execapp.ExecutorServlet.doGet(ExecutorServlet.java:120)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:707)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:820)
    at org.mortbay.jetty.servlet.ServletHolder.handle(ServletHolder.java:511)
    at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:401)
    at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:182)
    at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:766)
    at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:152)
    at org.mortbay.jetty.Server.handle(Server.java:326)
    at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
    at org.mortbay.jetty.HttpConnection$RequestHandler.headerComplete(HttpConnection.java:928)
    at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:549)
    at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:212)
    at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:404)
    at org.mortbay.jetty.bio.SocketConnector$Connection.run(SocketConnector.java:228)
    at org.mortbay.thread.QueuedThreadPool$PoolThread.run(QueuedThreadPool.java:582)
```

这个问题在github有人提出同样问题,可能是azkaban的一个bug. 
https://github.com/azkaban/azkaban/issues/1128

### 启动报错: 

```
Exception: java.lang.StackOverflowError thrown from the UncaughtExceptionHandler in thread "main"
```

原因：缺少log4j.properties 
解决办法： 
在Azkaban-web-server 的conf目录下创建文件夹conf/log4j.properties： 
写入以下内容


```
log4j.rootLogger=INFO,R
log4j.appender.R=org.apache.log4j.RollingFileAppender
log4j.appender.R.File=/data/data8/azkaban/logs/web-server.log
log4j.appender.R.Encoding=UTF-8
log4j.appender.R.MaxFileSize=3MB
log4j.appender.R.Append=true 
log4j.appender.R.MaxBackupIndex=2
log4j.appender.R.layout=org.apache.log4j.PatternLayout
log4j.appender.R.layout.ConversionPattern=%-d %t %-5p [%c{1}:%L] %m%n
```
### 启动报如下错误：

```
Exception in thread "main" java.lang.RuntimeException: java.lang.reflect.InvocationTargetException
    at azkaban.webapp.AzkabanWebServer.loadUserManager(AzkabanWebServer.java:230)
    at azkaban.webapp.AzkabanWebServer.<init>(AzkabanWebServer.java:181)
    at azkaban.webapp.AzkabanWebServer.main(AzkabanWebServer.java:726)
Caused by: java.lang.reflect.InvocationTargetException
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:526)
    at azkaban.webapp.AzkabanWebServer.loadUserManager(AzkabanWebServer.java:226)
    ... 2 more
Caused by: java.lang.IllegalArgumentException: User xml file conf/azkaban-users.xml doesn't exist.
    at azkaban.user.XmlUserManager.parseXMLFile(XmlUserManager.java:87)
    at azkaban.user.XmlUserManager.<init>(XmlUserManager.java:81)
    ... 7 more
```
这个是因为源码编译后，默认配置里面所有的路径，都是按照相对路径编写的。在运行的时候，就会报如上错误。要将所有的相对路径改为绝对路径，重启服务。

### Missing required property 'azkaban.native.lib'

plugins/jobtypes/commonprivate.properties下把 azkaban.native.lib=false就行了


