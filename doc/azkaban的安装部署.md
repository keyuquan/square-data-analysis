说明：azkaban不像其他开源软件那样，可以直接下载编译安装包。而是需要先从Github中下载源码再进行编译。

---
### 编译安装
#### 1、下载源码
从github上下载azkaban官方源码，目前最新release为3.34.x，而我编译的版本是3.32.x。因为我在编译3.34.x版本的时候，编译报错了。

```
git clone https://github.com/azkaban/azkaban.git
```
源码下载下来后，可以相关目录介绍如下：

```
azkaban-common : 常用工具类
azkaban-db : 对应的sql脚本
azkaban-Hadoop-secutity-plugin : hadoop有关kerberos插件
azkaban-solo-server: web和executor运行在同一进程的项目
azkaban-web-server：azkaban的web-server单独模块
azkaban-executor-server: azkaban的executor-server单独模块
azkaban-spi: azkaban存储接口以及exception类
```
其实，我们用到的也就是azkaban-db、azkaban-solo-server、azkaban-web-server和azkaban-executor-server四个目录。

#### 2、编译
进入到下载的azkaban目录中进行编译：

```
cd D:\opensourcecode\temp\azkaban
gradlew installDist
gradlew distTar
```
这个过程有点漫长，尤其是第一次编译的时候，需要下载一些第三方包。编译完成后，就可以将编译好的文件拷贝出来：

```
D:\opensourcecode\temp\azkaban\azkaban-db\build\distributions\azkaban-db-3.32.2.tar.gz
D:\opensourcecode\temp\azkaban\azkaban-exec-server\build\distributions\azkaban-exec-server-3.32.2.tar.gz
D:\opensourcecode\temp\azkaban\azkaban-solo-server\build\distributions\azkaban-solo-server-3.32.2.tar.gz
D:\opensourcecode\temp\azkaban\azkaban-web-server\build\distributions\azkaban-web-server-3.32.2.tar.gz
```
这个编译包可以直接上传上linux服务器上，进行加压后修改配置项。而我，是在window本地解压后，再上传到服务器上的（下面进行详细讲解配置的修改）。
### 配置mysql数据库
#### 1、安装mysql数据库
这里省略安装步骤，因为我是直接拿的现有的mysql数据库来使用的，使用的版本是5.7.22-0ubuntu0.16.04.1。
#### 2.创建数据库
创建一个azkaban的数据库

```
CREATE DATABASE azkaban；
```
授权：

```
CREATE USER 'azkaban'@'%' IDENTIFIED BY 'password';
GRANT ALL ON azkaban.* to 'azkaban'@'%' WITH GRANT OPTION;
flush privileges;
```
#### 3、导入建表语句

```
mysql -uazkaban -p
> SOURCE azkaban_3.30/azkaban-db-0.1.0-SNAPSHOT/create-all-sql-0.1.0-SNAPSHOT.sql;
```
而我并没有在服务器上通过脚本执行，而是拿着azkaban-solo-server-3.32.2.tar.gz包里面的create-all-sql-3.32.2.sql文件，直接通过mysql客户端进行执行的。  

另外，执行下面的sql语句，azkaban库中添加各个执行服务器的ip/域名和端口：

```
INSERT INTO `azkaban`.`executors`(`id`,`host`,`port`,`active`)VALUES(1,isec-hdp01,12321,1);
INSERT INTO `azkaban`.`executors`(`id`,`host`,`port`,`active`)VALUES(2,isec-hdp02,12321,1);
```

### 配置Azkaban
这里将安装两个exec-server和一个web-server，相关组件分配如下：

```
将采用multiple executor mode安装模式，组件分配如下：
isec-hdp01       172.18.164.125       azkaban-web-server  azkaban-exec-server
isec-hdp02       172.18.164.126       azkaban-exec-server mysql-server
```
### 配置Azkaban Web Server
#### 1、配置jetty ssl

```
Enter keystore password:  
Re-enter new password: 
What is your first and last name?
  [Unknown]:  YY
What is the name of your organizational unit?
  [Unknown]:  YY
What is the name of your organization?
  [Unknown]:  YY
What is the name of your City or Locality?
  [Unknown]:  shanghai
What is the name of your State or Province?
  [Unknown]:  shanghai
What is the two-letter country code for this unit?
  [Unknown]:  CN
Is CN=YY, OU=YY, O=YY, L=shanghai, ST=shanghai, C=CN correct?
  [no]:  yes
```
将生成的keystone文件拷贝到isec-hdp01的/opt/azkaban的安装目录下，供后面web-server进行使用。
2、修改conf/azkaban.properties配置文件

```
# Azkaban Personalization Settings
azkaban.name=ISEC
azkaban.label=ISEC Azkaban
azkaban.color=#FF3601
azkaban.default.servlet.path=/index
web.resource.dir=/opt/azkaban/azkaban-web-server-3.32.2/web/
default.timezone.id=Asia/Shanghai
# Azkaban UserManager class
user.manager.class=azkaban.user.XmlUserManager
user.manager.xml.file=/opt/azkaban/azkaban-web-server-3.32.2/conf/azkaban-users.xml
executor.global.properties=/opt/azkaban/azkaban-web-server-3.32.2/conf/global.properties
azkaban.project.dir=/data/data8/azkaban/web-projects
azkaban.execution.dir=/data/data8/azkaban/web-executions
# mysql
database.type=mysql
mysql.port=3306
mysql.host=isec-hdp02
mysql.database=azkaban
mysql.user=azkaban2
mysql.password=azkaban2
mysql.numconnections=100
# Velocity dev mode
velocity.dev.mode=false
# Azkaban Jetty server properties.
#jetty.use.ssl=false
#jetty.maxThreads=25
#jetty.port=8808
jetty.maxThreads=25
jetty.port=8081
jetty.ssl.port=8809
jetty.keystore=/opt/azkaban/keystore
jetty.password=123456
jetty.keypassword=123456
jetty.truststore=/opt/azkaban/keystore
jetty.trustpassword=123456
jetty.excludeCipherSuites=SSL_RSA_WITH_DES_CBC_SHA,SSL_DHE_RSA_WITH_DES_CBC_SHA,SSL_DHE_DSS_WITH_DES_CBC_SHA,SSL_RSA_EXPORT_WITH_RC4_40_MD5,SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA,SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA,SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_DHE_RSA_WITH_AES_256_CBC_SHA,TLS_DHE_DSS_WITH_AES_256_CBC_SHA,TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,TLS_DHE_DSS_WITH_AES_128_CBC_SHA256,TLS_DHE_RSA_WITH_AES_128_CBC_SHA,TLS_DHE_DSS_WITH_AES_128_CBC_SHA
# Azkaban Executor settings
executor.port=12321
# mail settings
mail.sender=
mail.host=
job.failure.email=
job.success.email=
lockdown.create.projects=false
cache.directory=cache
# JMX stats
jetty.connector.stats=true
executor.connector.stats=true
# Azkaban plugin settings
azkaban.jobtype.plugin.dir=/opt/azkaban/azkaban-web-server-3.32.2/plugins/jobtypes
# 初始化job时单个job最大申请内存大小
job.max.Xms=1G
# 单个job执行总内存大小设置（包括初始化要分配的内存和运行时需要给予的内存）
job.max.Xmx=2G

azkaban.use.multiple.executors=true
azkaban.executorselector.filters=StaticRemainingFlowSize,CpuStatus
azkaban.executorselector.comparator.NumberOfAssignedFlowComparator=1
azkaban.executorselector.comparator.Memory=1
azkaban.executorselector.comparator.LastDispatched=1
azkaban.executorselector.comparator.CpuUsage=1
```
发邮件这块属性没有配置，后面需要的时候再加上。
3、上传log4j文件
在azkaban目录下创建logs文件夹，位置跟conf在同一级，在conf目录中上传log4j文件。简单示例如下：

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

如果没有log4j文件，将会以下错误：

```
Exception: java.lang.StackOverflowError thrown from the UncaughtExceptionHan
```
4、用户配置

添加管理员用户及密码：

```
<azkaban-users>
  <user groups="azkaban" password="azkaban" roles="admin" username="azkaban"/>
  <user password="metrics" roles="metrics" username="metrics"/>
  <user username="admin" password="admin" roles="admin,metrics"/>#新增管理员
  <role name="admin" permissions="ADMIN"/>
  <role name="metrics" permissions="METRICS"/>
</azkaban-users>
```
这是默认的配置，根据自己的需要进行密码的更改。
5、修改bin目录下的脚步文件

将所有的文件进行授权，默认脚步是没有执行权限的：


```
cd /opt/azkaban/azkaban-exec-server-3.32.2/bin
chmod +x *
```

然后修改start-exec.sh.sh文件，将：


```
$base_dir/bin/azkaban-web-start.sh $base_dir >$base_dir/logs/webServerLog_`date +%F+%T`.out 2>&1 &
修改为：
$base_dir/bin/azkaban-web-start.sh $base_dir >/dev/null 2>&1 &
```
因为我的log4j文件已经指定了输出日志，这里的shell就无需重复输出了

6、启动web-server

进入到web-server的目录，执行如下脚本，需要在bin级目录执行，否则汇报找不到配置文件的错误。

```
cd /opt/azkaban/azkaban-web-server-3.32.2
./bin/start-web.sh
```
要注意，一定要给bin目录下面的shell文件授权，默认拷贝上去是没有权限的。另外也要注意一下文件格式，默认是doc格式，要转化为unix。否则，都会运行不成功。

```
打开浏览器，访问https://47.106.68.167:8809/index，用刚刚添加的管理员账户密码登录（用户名和密码自行登录服务器查看配置）。
```
### 配置 Azkaban Executor Server
#### 1、修改conf/azkaban.properties
```
# Azkaban Personalization Settings
default.timezone.id=Asia/Shanghai
# Loader for projects
executor.global.properties=/opt/azkaban/azkaban-exec-server-3.32.2/conf/global.properties
azkaban.project.dir=/data/data8/azkaban/exec-projects
azkaban.execution.dir=/data/data8/azkaban/exec-executions
#mysql
database.type=mysql
mysql.port=3306
mysql.host=isec-hdp02
mysql.database=azkaban
mysql.user=azkaban2
mysql.password=azkaban2
mysql.numconnections=100
# Azkaban Executor settings
executor.maxThreads=50
executor.port=12321
executor.flow.threads=30
# JMX stats
jetty.connector.stats=true
executor.connector.stats=true
# Azkaban plugin settings
azkaban.jobtype.plugin.dir=/opt/azkaban/azkaban-exec-server-3.32.2/plugins/jobtypes
azkaban.webserver.url=https://47.106.68.167:8809
# 初始化job时单个job最大申请内存大小
job.max.Xms=1G
# 单个job执行总内存大小设置（包括初始化要分配的内存和运行时需要给予的内存）
job.max.Xmx=2G
# 每个flow内job执行的最大并发线程数
flow.num.job.threads=10
```
#### 2、上传log4j文件

```
log4j.rootLogger=INFO,R
log4j.appender.R=org.apache.log4j.RollingFileAppender
log4j.appender.R.File=/data/data8/azkaban/logs/portal.log
log4j.appender.R.Encoding=UTF-8
log4j.appender.R.MaxFileSize=3MB
log4j.appender.R.Append=true 
log4j.appender.R.MaxBackupIndex=2
log4j.appender.R.layout=org.apache.log4j.PatternLayout
log4j.appender.R.layout.ConversionPattern=%-d %t %-5p [%c{1}:%L] %m%n
```
#### 3、修改bin目录下的脚步文件
将所有的文件进行授权，默认脚步是没有执行权限的：

```
cd /opt/azkaban/azkaban-exec-server-3.32.2/bin
chmod +x *
```
然后修改start-exec.sh.sh文件，将：

```
bin/azkaban-executor-start.sh "$@" >logs/executorServerLog__`date +%F+%T`.out 2>&1 &
修改为：
bin/azkaban-executor-start.sh "$@" >/dev/null 2>&1 &
```
因为我的log4j文件已经指定了输出日志，这里的shell就无需重复输出了

#### 4、启动exec-server
进入到exec-server的目录，执行如下脚本，需要在bin级目录执行，否则汇报找不到配置文件的错误。

```
cd /opt/azkaban/azkaban-exec-server-3.32.2
./bin/start-exec.sh
```

#### 5、将其他节点的Azkaban Executor Server服务启动起来
1、将当前节点的azkaban-exec-server-3.32.2目录拷贝到目标机器，然后执行上面的启动命令即可。




