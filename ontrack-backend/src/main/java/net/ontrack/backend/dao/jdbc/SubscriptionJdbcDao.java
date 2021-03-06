package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityID;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

@Component
public class SubscriptionJdbcDao extends AbstractJdbcDao implements SubscriptionDao {

    @Autowired
    public SubscriptionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> findAccountIds(Map<Entity, Integer> entities) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(ACCOUNT) FROM SUBSCRIPTION S");
        MapSqlParameterSource params = new MapSqlParameterSource();
        int count = 0;
        for (Map.Entry<Entity, Integer> entry : entities.entrySet()) {
            Entity entity = entry.getKey();
            int entityId = entry.getValue();
            if (count == 0) {
                sql.append(" WHERE ");
            } else {
                sql.append(" OR ");
            }
            sql.append(String.format("(%s = :entity%d)", entity.name(), count));
            params.addValue(String.format("entity%d", count), entityId);
            count++;
        }
        return getNamedParameterJdbcTemplate().queryForList(
                sql.toString(),
                params,
                Integer.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Set<EntityID> findEntitiesByAccount(int accountId) {
        final Set<EntityID> entityIDs = new HashSet<>();
        getNamedParameterJdbcTemplate().query(
                SQL.SUBSCRIPTION_BY_ACCOUNT,
                params("account", accountId),
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        for (Entity entity : Entity.values()) {
                            int id = rs.getInt(entity.name());
                            if (!rs.wasNull()) {
                                entityIDs.add(new EntityID(entity, id));
                            }
                        }
                    }
                }
        );
        return entityIDs;
    }

    @Override
    @Transactional
    public Ack subscribe(int userId, Entity entity, int entityId) {
        MapSqlParameterSource params = params("account", userId).addValue("entityId", entityId);
        // Deletion
        getNamedParameterJdbcTemplate().update(
                format(SQL.SUBSCRIPTION_DELETE, entity.name()),
                params
        );
        // Insertion
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        format(SQL.SUBSCRIPTION_CREATE, entity.name()),
                        params)
        );
    }

    @Override
    @Transactional
    public Ack unsubscribe(int userId, Entity entity, int entityId) {
        MapSqlParameterSource params = params("account", userId).addValue("entityId", entityId);
        // Deletion
        getNamedParameterJdbcTemplate().update(
                format(SQL.SUBSCRIPTION_DELETE, entity.name()),
                params
        );
        // OK
        return Ack.OK;
    }
}
