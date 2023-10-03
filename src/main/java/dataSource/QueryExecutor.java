package dataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class QueryExecutor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void executeQuery(String query) {
        jdbcTemplate.execute(query);
    }
}
