package subway.persistence.rowmapper.util;

import org.springframework.jdbc.core.RowMapper;
import subway.persistence.rowmapper.SectionDetail;
import subway.persistence.entity.LineEntity;
import subway.persistence.entity.SectionEntity;
import subway.persistence.entity.StationEntity;

public class RowMapperUtil {

    public static final RowMapper<StationEntity> stationEntityRowMapper = (rs, rn) -> new StationEntity(
            rs.getLong("id"),
            rs.getString("name")
    );

    public static final RowMapper<LineEntity> lineEntityRowMapper = (rs, rowNum) -> new LineEntity(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
    );

    public static final RowMapper<SectionEntity> sectionEntityRowMapper = (rs, rn) -> new SectionEntity(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getInt("distance"),
            rs.getLong("previous_station_id"),
            rs.getLong("next_station_id")
    );

    public static final RowMapper<SectionDetail> sectionDetailRowMapper = (rs, rn) -> new SectionDetail(
            rs.getLong("id"),
            rs.getInt("distance"),
            rs.getLong("line_id"),
            rs.getString("line_name"),
            rs.getString("line_color"),
            rs.getLong("previous_station_id"),
            rs.getString("previous_station_name"),
            rs.getLong("next_station_id"),
            rs.getString("next_station_name")
    );

}
