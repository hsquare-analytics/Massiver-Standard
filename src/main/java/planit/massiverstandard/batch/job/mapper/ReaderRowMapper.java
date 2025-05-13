package planit.massiverstandard.batch.job.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReaderRowMapper implements RowMapper<Map<String, Object>> {

	@Override
	public Map<String, Object> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Map<String, Object> map = new HashMap<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String columnName = metaData.getColumnName(i);

            Object value;
            if (metaData.getColumnType(i) == Types.CLOB) {
                Clob clob = resultSet.getClob(i);
                value = (clob != null) ? clob.getSubString(1, (int) clob.length()) : null;
            }
            else if (metaData.getColumnType(i) == Types.BLOB) {
                Blob blob = resultSet.getBlob(i);
                value = (blob != null) ? blob.getBytes(1, (int) blob.length()) : null;
            }
            else {
                value = resultSet.getObject(i);
            }

			map.put(columnName, value);
		}

		return map;
	}
}
