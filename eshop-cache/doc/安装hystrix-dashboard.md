#### 安装hystrix-dashboard

##### 安装

```

2、安装gradle(貌似不需要)
类似于maven，一种java里面的打包和构建的工具，hystrix是用gradle去管理打包和构建的
配置环境变量，GRADLE_HOME
配置PATH，%GRADLE_HOME%/bin
gradle -v

3、下载tomcat7
解压缩
配置CATALINA_HOME环境变量

4、下载hystrix-dashboard的war包

cp hystrix-dashboard-*.war apache-tomcat-7.*/webapps/hystrix-dashboard.war

5、下载turbin

下载并解压缩

cp turbine-web/build/libs/turbine-web-*.war ./apache-tomcat-7.*/webapps/turbine.war

在/WEB-INF/classes下放置配置文件config.properties
主要是用来配置hystrix监控集群
turbine.ConfigPropertyBasedDiscovery.default.instances=cdh1,cdh2
turbine.instanceUrlSuffix=:8081/hystrix.stream

turbin是用来监控一个集群的，可以将一个集群的所有机器都配置在这里

6、启动我们的服务缓存服务

7、启动tomcat中的hystrix dashboard和turbin
cd apache-tomcat-7.*/bin,执行startup.bat或者startup.sh即可启动tomcat。

访问：localhost:8080/hystrix-dashboard

http://localhost:8081/hystrix.stream，配置监控单个机器
http://localhost:8080/turbine/turbine.stream，配置监控整个集群

8、发送几个请求，看看效果

9、hystrix dashboard

hystrix的dashboard可以支持实时监控metric

netflix开始用这个dashboard的时候，大幅度优化了工程运维的操作，帮助节约了恢复系统的时间。大多数生产系统的故障持续时间变得很短，而且影响幅度小了很多，主要是因为hystrix dashborad提供了可视化的监控。

截图说明，dashboard上的指标都是什么？

圆圈的颜色和大小代表了健康状况以及流量，折线代表了最近2分钟的请求流量

集群中的机器数量，请求延时的中位数以及平均值

最近10秒内的异常请求比例，请求QPS，每台机器的QPS，以及整个集群的QPS

断路器的状态

最近一分钟的请求延时百分比，TP90，TP99，TP99.5

几个有颜色的数字，代表了最近10秒钟的统计，以1秒钟为粒度

- 成功的请求数量，绿颜色的数字; 
- 短路的请求数量，蓝色的数字; 
- timeout超时的请求数量，黄色的数字; 
- 线程池reject的请求数量，紫色的数字; 
- 请求失败，抛出异常的请求数量，红色的数字
```

##### 注册HystrixMetricsStreamServlet

```
Spring boot默认是不支持JSP(JavaServer Pages)的.
所以想用JSP就必须使用外部容器来运行，即不能使用嵌入式的Tomcat或Jetty。

Spring boot默认为我们提供了注册Servlet三大组件Servlet、Filter、Listener的接口。我们只需按需配置和添加少量的代码即可实现添加Servlet的功能。

由于SpringBoot默认是以jar包的方式启动嵌入式的Servlet容器来启动SpringBoot的web应用，没有web.xml文件。

所以想用使用Servlet功能，就必须要借用Spring Boot提供的ServletRegistrationBean接口。将对应的Servlet绑定url，借助springboot内置的web容器运行。

xxxServlet一般是打成war包，放到tomcat的webapp目录下执行的。需要借助外部容器。HystrixMetricsStreamServlet类或者父类中，自己实现了doGet、doPost方法。
```

```java
1、安装hystrix-metrics-event-stream
<dependency>
    <groupId>com.netflix.hystrix</groupId>
    <artifactId>hystrix-metrics-event-stream</artifactId>
    <version>1.5.18</version>
</dependency>

@Bean
public ServletRegistrationBean indexServletRegistration() {
   // 注册Servlet
    ServletRegistrationBean registration =
      new ServletRegistrationBean(new HystrixMetricsStreamServlet());
   // 绑定url
    registration.addUrlMappings("/hystrix.stream");
    return registration;
}
```

##### 测试

```
测试接口：http://localhost:8081/ehcache/getProductInfosByHystrix?productIds=1,2,3

访问：http://localhost:8080/hystrix-dashboard/
配置"缓存服务(单节点)：http://localhost:8081/hystrix.stream
```

![hystrix-dashboard](images\hystrix-dashboard.jpg)

解读：

由于该方法配置的超时时间是1s，而中间sleep 2s，导致所有请求都Timeout了。

降级执行productInfoFailed方法，并都执行成功。

由于并没有设置熔断器何时打开，所以并没有触发熔断 (Circuit状态为Closed)

```java
@HystrixCommand(
        fallbackMethod = "productInfoFailed",
        commandKey = "getProductByHystrix",
        groupKey = "ProductGroup",
        threadPoolKey = "ProductThreadPool",
        commandProperties = {
           @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
        })
public ProductInfo getProductByHystrix(String productId) {
    log.info("getProductInfo by hystrix id = {}", productId);
    String url = "http://cdh1:8081/ehcache/getProductInfo?productId=" + productId;
    String res = HttpClientUtils.sendGetRequest(url);
    try {
        TimeUnit.MILLISECONDS.sleep(2000);
    } catch (InterruptedException e) {
        log.info("sleep interrupted");
    }
    return JSONObject.parseObject(res, ProductInfo.class);
}
```