package subway.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import subway.persistence.entity.SectionEntity;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.persistence.entity.RowMapperUtil.sectionEntityRowMapper;

@JdbcTest
@DisplayName("Section Dao")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectionDaoTest {

    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        sectionDao = new SectionDao(dataSource);
    }

    @Test
    @DisplayName("저장 성공")
    void save_success() {
        // given
        SectionEntity sectionEntity = new SectionEntity(1L, 10, 1L, 5L);

        // when
        sectionDao.insert(sectionEntity);

        // then
        String sql = "SELECT * FROM section WHERE previous_station_id = ? AND next_station_id = ?";
        List<SectionEntity> response = jdbcTemplate.query(sql, sectionEntityRowMapper, 1, 5);
        assertThat(response).hasSize(1)
                .anyMatch(entity -> entity.getLineId() == 1L)
                .anyMatch(entity -> entity.getPreviousStationId() == 1L)
                .anyMatch(entity -> entity.getNextStationId() == 5L)
                .anyMatch(entity -> entity.getDistance() == 10);
    }

    @Test
    @DisplayName("line id 와 previous station id 로 구간 조회 성공")
    @Sql("/section_test_data.sql")
    void findByLineIdAndPreviousStationId_success() {
        // given, when
        final List<SectionEntity> result = sectionDao.findByLineIdAndPreviousStationId(1L, 1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLineId()).isEqualTo(1L);
        assertThat(result.get(0).getPreviousStationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("구간 삭제 성공")
    @Sql("/section_test_data.sql")
    void delete_success() {
//        // given
//        final String selectSql = "SELECT id FROM section";
//        List<Long> resultBeforeRemove = jdbcTemplate.query(selectSql, (rs, rn) -> rs.getLong("id"));
//        final SectionEntity sectionEntity = new SectionEntit;
//
//        // when
//        sectionDao.delete(sectionEntity);
//
//        // then
//        List<Long> resultAfterRemove = jdbcTemplate.query(selectSql, (rs, rn) -> rs.getLong("id"));
//        assertThat(resultAfterRemove.size()).isEqualTo(resultBeforeRemove.size() - 1);
    }

}
