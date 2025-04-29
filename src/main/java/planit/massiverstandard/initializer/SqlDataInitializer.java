package planit.massiverstandard.initializer;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import planit.massiverstandard.datasource.entity.DataSource;
import planit.massiverstandard.datasource.util.DataSourceResolver;

@Configuration
public class SqlDataInitializer {

    public void init(DataSource dataSourceEntity, String sqlRoute) {
        javax.sql.DataSource dataSource = DataSourceResolver.createDataSource(dataSourceEntity);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(sqlRoute));
        populator.execute(dataSource);
    }
}
