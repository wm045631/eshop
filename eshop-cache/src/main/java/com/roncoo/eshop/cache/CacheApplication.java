package com.roncoo.eshop.cache;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@MapperScan("com.roncoo.eshop.cache.mapper")
@RestController
public class CacheApplication {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return new DataSource();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @RequestMapping(value = {"", "/", "index", "index.html"})
    public String index() {
        return "<h2>cache service worked !</h2>";
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

//    /**
//     * 采用传统的JedisCluster
//     *
//     * @return
//     */
//    @Bean
//    public JedisCluster JedisClusterFactory() {
//        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
//        jedisClusterNodes.add(new HostAndPort("172.20.3.173", 7005));
//        jedisClusterNodes.add(new HostAndPort("172.20.3.174", 7001));
//        jedisClusterNodes.add(new HostAndPort("172.20.3.175", 7003));
//        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
//        return jedisCluster;
//    }

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }

}