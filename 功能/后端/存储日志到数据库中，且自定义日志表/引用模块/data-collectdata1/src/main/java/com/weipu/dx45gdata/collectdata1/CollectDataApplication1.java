package com.weipu.dx45gdata.collectdata1;











import com.weipu.dx45gdata.common.mapper.AbDataMapper;
import com.weipu.dx45gdata.common.xApplication;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class CollectDataApplication1 {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext run = SpringApplication.run(new Class[]{CollectDataApplication1.class,}, args);
//        AbDataMapper bean = run.getBean(AbDataMapper.class);
//        HikariDataSource dataSource = (HikariDataSource) run.getBean("dataSource");
//        System.out.println( dataSource.getDataSource());
////        AbData abData = new AbData( );
//        abData.setCity_name("ss");
//        abData.setCity_id("1");
//        abData.setDistrict_id("1");
//        abData.setDistrict_name("!");
//        abData.setProvince_id("1");
//        abData.setProvince_name("1");
//
//        bean.insert(abData);

//        System.out.println(bean);
        CountDownLatch latch = new CountDownLatch( 1 );
        latch.await();
    }
}
