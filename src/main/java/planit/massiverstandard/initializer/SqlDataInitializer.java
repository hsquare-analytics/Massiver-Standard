package planit.massiverstandard.initializer;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import planit.massiverstandard.database.DataBase;

import javax.sql.DataSource;

@Configuration
public class SqlDataInitializer {

    public void init(DataBase dataBase, String sqlRoute) {
        DataSource dataSource = dataBase.createDataSource();

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(sqlRoute));
        populator.execute(dataSource);
    }
}
