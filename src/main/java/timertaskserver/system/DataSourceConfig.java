package timertaskserver.system;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Primary
    @Bean(name="swzzmodeDataSource")
    @Qualifier("swzzmodeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.swzzmode")
    public DataSource swzzmodeDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name="swzzdataDataSource")
    @Qualifier("swzzdataDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.swzzdata")
    public DataSource swzzdataDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name="swzzqxsjDataSource")
    @Qualifier("swzzqxsjDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.swzzqxsj")
    public DataSource swzzqxsjDataSource(){
        return DataSourceBuilder.create().build();
    }

//    @Primary
//    @Bean(name="swzzfloodDataSource")
//    @Qualifier("swzzfloodDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.swzzflood")
//    public DataSource swzzfloodDataSource(){
//        return DataSourceBuilder.create().build();
//    }

    @Primary
    @Bean(name="zjtyphoonDataSource")
    @Qualifier("zjtyphoonDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.zjtyphoon")
    public DataSource zjtyphoonDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name="swzzwaterDataSource")
    @Qualifier("swzzwaterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.swzzwater")
    public DataSource swzzwaterDataSource(){
        return DataSourceBuilder.create().build();
    }
}
