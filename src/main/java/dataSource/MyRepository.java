package dataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class MyRepository {

    private  JdbcTemplate jdbcTemplate;

    @Autowired
    public MyRepository(@Qualifier("dataSource") DataSource dataSource) {
        try {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        } catch (Exception e) {
            // Exception occurred during data source injection
            // Handle the exception or provide a fallback mechanism

        }
    }

    public void executeQuery(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception e) {
            // Exception occurred during query execution
            // Handle the exception or provide appropriate error handling
        }
    }
}
