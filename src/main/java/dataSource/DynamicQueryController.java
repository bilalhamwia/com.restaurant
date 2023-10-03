package dataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicQueryController {

    @Autowired
    private QueryExecutorService queryExecutorService;

    @PostMapping("/execute-query")
    public ResponseEntity<String> executeQuery(@RequestBody String query) {
        try {
            queryExecutorService.executeDynamicQuery(query);
            return ResponseEntity.ok("Query executed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
