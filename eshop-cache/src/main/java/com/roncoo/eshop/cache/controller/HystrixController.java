package com.roncoo.eshop.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.roncoo.eshop.cache.hystrix.HttpClientUtils;
import com.roncoo.eshop.cache.model.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/hystrix")
@DefaultProperties(defaultFallback = "defaultFail")
public class HystrixController {

    // fallbackMethod方法的参数必须和该方法一致
    @HystrixCommand(fallbackMethod = "productInfoFailed",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")
            })
    @GetMapping("/productInfo500")
    public Object productInfo500(Long productId) throws InterruptedException {
        log.info("get productInfo by id = {}", productId);
        TimeUnit.MILLISECONDS.sleep(1000);
        String url = "http://cdh1:8081/ehcache/getProductInfo?productId=" + productId;
        String res = HttpClientUtils.sendGetRequest(url);
        log.info("getProductInfo by hystrix success");
        return JSONObject.parseObject(res, ProductInfo.class);
    }

    @HystrixCommand
    private String productInfoFailed(Long productId) {
        String s = "get productInfo Failed, productId = " + productId;
        log.info(s);
        return s;
    }

    /**
     * 当访问1次http://localhost:8081/hystrix2/test1?id=1和2次http://localhost:8081/hystrix2/test1?id=2，错误率达66%超过了设置的50%。服务进入熔断。
     * 再请求一次http://localhost:8081/hystrix2/test1?id=1时，返回的不再是"test_1",而是"default fail"
     * @param id
     * @return
     */
    @HystrixCommand(commandProperties =
            {
                    // 熔断器在整个统计时间内是否开启的阀值
                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
                    // 至少有3个请求才进行熔断错误比率计算
                    /**
                     * 设置在一个滚动窗口中，打开断路器的最少请求数。
                     比如：如果值是20，在一个窗口内（比如10秒），收到19个请求，即使这19个请求都失败了，断路器也不会打开。
                     */
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
                    //当出错率超过50%后熔断器启动
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    // 熔断器工作时间，超过这个时间，先放一个请求进去，成功的话就关闭熔断，失败就再等一段时间
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
                    // 统计滚动的时间窗口
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
            })
    @GetMapping("/test1")
    public String test1(@RequestParam("id") Integer id) {
        System.out.println("id:" + id);

        if (id % 2 == 0) {
            throw new RuntimeException();
        }
        return "test_" + id;
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500"),
            // 滑动统计的桶数量
            /**
             * 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，
             *那么一个bucket的时间即1秒。必须符合rolling window % numberBuckets == 0。默认1
             */
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10"),
            // 设置滑动窗口的统计时间。熔断器使用这个时间
            /** 设置统计的时间窗口值的，毫秒值。
             circuit break 的打开会根据1个rolling window的统计来计算。
             若rolling window被设为10000毫秒，则rolling window会被分成n个buckets，
             每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
             **/
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "15"),
                    /**
                     * BlockingQueue的最大队列数，当设为-1，会使用SynchronousQueue，值为正时使用LinkedBlcokingQueue。
                     */
                    @HystrixProperty(name = "maxQueueSize", value = "15"),
                    /**
                     * 设置存活时间，单位分钟。如果coreSize小于maximumSize，那么该属性控制一个线程从实用完成到被释放的时间.
                     */
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                    /**
                     * 设置队列拒绝的阈值,即使maxQueueSize还没有达到
                     */
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
            })
    @GetMapping("/test2")
    public String test2() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
        return "test2";
    }

    private String defaultFail() {
        System.out.println("HystrixController default fail");
        return "HystrixController default fail";
    }
}
