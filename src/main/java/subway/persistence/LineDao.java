package subway.persistence;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.exception.DuplicatedLineNameException;
import subway.persistence.entity.LineEntity;
import subway.exception.LineNotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static subway.persistence.entity.RowMapperUtil.lineEntityRowMapper;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    public LineDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public LineEntity insert(final LineEntity lineEntity) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(lineEntity);
        try {
            final long id = insertAction.executeAndReturnKey(params).longValue();
            return LineEntity.of(id, lineEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedLineNameException();
        }
    }

    public LineEntity findByName(final String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        final LineEntity result = jdbcTemplate.queryForObject(sql, lineEntityRowMapper, name);

        if (result.isEmpty()) {
            throw new LineNotFoundException();
        }

        return result.get(0);
    }

    public void update(LineEntity newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{newLine.getName(), newLine.getColor(), newLine.getId()});
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }

}
