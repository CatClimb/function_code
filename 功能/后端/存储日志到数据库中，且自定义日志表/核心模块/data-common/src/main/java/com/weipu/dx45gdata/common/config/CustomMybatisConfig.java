package com.weipu.dx45gdata.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@MapperScan({"com.weipu.dx45gdata.common.mapper"})
@ComponentScan(basePackages={"com.weipu.dx45gdata.common"})
public class CustomMybatisConfig  {
    @Value("${mapper-locations}")
    private String mapperLocations;

    private DataSource dataSource;

    public CustomMybatisConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        sqlSessionFactoryBean.setDataSource(dataSource);
        org.apache.ibatis.session.Configuration cfg = new org.apache.ibatis.session.Configuration();//configuration
        sqlSessionFactoryBean.setConfiguration(cfg);
        return sqlSessionFactoryBean.getObject();
    }

}
