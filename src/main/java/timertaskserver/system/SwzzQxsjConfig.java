package timertaskserver.system;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"timertaskserver.workserver.data.swzzqxsj"},sqlSessionFactoryRef = "swzzqxsjserveSessionFactory")
public class SwzzQxsjConfig {
    @Autowired
    @Qualifier("swzzqxsjDataSource")
    public DataSource dataSource;

    @Bean
    public SqlSessionFactory swzzqxsjserveSessionFactory() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/work/swzzqxsj/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }
    @Bean
    public SqlSessionTemplate swzzqxsjserveSession() throws Exception{
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(swzzqxsjserveSessionFactory());
        return sqlSessionTemplate;
    }
}
