package subway.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import subway.exception.bad_request.DuplicatedLineNameException;
import subway.persistence.entity.LineEntity;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static subway.persistence.entity.RowMapperUtil.lineEntityRowMapper;

@JdbcTest
@DisplayName("Line Dao")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/line_test_data.sql")
class LineDaoTest {

    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        lineDao = new LineDao(dataSource);
    }

    @Test
    @DisplayName("생성 성공")
    void insert_success() {
        // given
        final String lineName = "신분당선";
        final String color = "bg_red_600";

        final LineEntity lineEntity = new LineEntity(lineName, color);

        // when
        final LineEntity resultEntity = lineDao.insert(lineEntity);

        // then
        final String sql = "SELECT * FROM line WHERE id = ?";
        final LineEntity response = jdbcTemplate.queryForObject(sql, lineEntityRowMapper, resultEntity.getId());

        assertAll(
                () -> assertThat(response.getName()).isEqualTo(lineName),
                () -> assertThat(response.getColor()).isEqualTo(color)
        );
    }

    @Test
    @DisplayName("생성 실패 - 중복된 노선명")
    void insert_fail_duplicated_line_name() {
        // given
        final String lineName = "2호선";
        final String color = "bg_red_600";

        final LineEntity lineEntity = new LineEntity(lineName, color);

        // when, then
        assertThatThrownBy(() -> lineDao.insert(lineEntity))
                .isInstanceOf(DuplicatedLineNameException.class);
    }

}
