package planit.massiverstandard.columntransform;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ColumnTransformService {

    private JdbcTemplate jdbcTemplate;

    public ColumnTransformService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
}
