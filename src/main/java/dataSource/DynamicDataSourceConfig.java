package dataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;


@Configuration
public class DynamicDataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(environment.getProperty("com.mysql.cj.jdbc.Driver"));
        dataSourceBuilder.url(environment.getProperty("jdbc:mysql://localhost:3306/restaurant"));
        dataSourceBuilder.username(environment.getProperty("mm"));
        dataSourceBuilder.password(environment.getProperty("mm"));
        return dataSourceBuilder.build();
    }
}
