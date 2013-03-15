package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ConfigurationDao;
import net.ontrack.backend.db.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class ConfigurationJdbcDao extends AbstractJdbcDao implements ConfigurationDao {

    @Autowired
    public ConfigurationJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public String getValue(String name) {
        return getFirstItem(SQL.CONFIGURATION_GET, params("name", name), String.class);
    }

    @Override
    @Transactional
    public void setValue(String name, String value) {
        MapSqlParameterSource params = params("name", name);
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        // Removes the key
        t.update(SQL.CONFIGURATION_DELETE, params);
        // Insert the key again
        if (value != null) {
            t.update(SQL.CONFIGURATION_INSERT, params.addValue("value", value));
        }
    }
}