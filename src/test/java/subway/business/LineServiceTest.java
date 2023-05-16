package subway.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.business.dto.LineDto;
import subway.business.dto.SectionCreateDto;
import subway.exception.bad_request.DuplicatedLineNameException;
import subway.exception.not_found.LineNotFoundException;
import subway.persistence.LineDao;
import subway.persistence.SectionDao;
import subway.persistence.StationDao;
import subway.persistence.entity.LineEntity;
import subway.persistence.entity.SectionDetailEntity;
import subway.persistence.entity.SectionEntity;
import subway.persistence.entity.StationEntity;
import subway.presentation.dto.request.StationDeleteInLineRequest;
import subway.presentation.dto.response.LineDetailResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineDao lineDao;
    @Mock
    private StationDao stationDao;
    @Mock
    private SectionDao sectionDao;
    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("생성 성공")
    void save_success() {
        // given
        final long lineId = 1L;
        final long firstStationId = 1L;
        final long lastStationId = 2L;
        final LineDto lineDto = new LineDto("신분당선", "bg-red-600");
        final SectionCreateDto sectionCreateDto = new SectionCreateDto(10, "강남", "신사");

        final LineEntity lineEntity = LineEntity.of(lineId, new LineEntity(lineDto.getName(), lineDto.getColor()));
        final StationEntity firstStation = new StationEntity(firstStationId, sectionCreateDto.getFirstStation());
        final StationEntity lastStation = new StationEntity(lastStationId, sectionCreateDto.getLastStation());

        given(lineDao.insert(any())).willReturn(lineEntity);
        given(stationDao.findByName(sectionCreateDto.getFirstStation())).willReturn(firstStation);
        given(stationDao.findByName(sectionCreateDto.getLastStation())).willReturn(lastStation);
        given(sectionDao.insert(any())).willReturn(any());

        // when, then
        assertThat(lineService.save(lineDto, sectionCreateDto)).isEqualTo(1L);
    }

    @Test
    @DisplayName("생성 실패 - 중복된 노선 이름")
    void save_fail_duplicated_line_name() {
        // given
        final LineDto lineDto = new LineDto("신분당선", "bg-gogi-600");
        final SectionCreateDto sectionCreateDto = new SectionCreateDto(10, "강남", "신사");

        given(lineDao.insert(any())).willThrow(DuplicatedLineNameException.class);

        // when, then
        assertThatThrownBy(() -> lineService.save(lineDto, sectionCreateDto))
                .isInstanceOf(DuplicatedLineNameException.class);
    }

    @Test
    @DisplayName("전체 조회 성공")
    void findAll_success() {
        // given
        final List<SectionDetailEntity> sectionDetailEntities = List.of(
                new SectionDetailEntity(1L, 2, 1L, "1호선", "bg-blue-600", 1L, "청량리", 2L, "회기"),
                new SectionDetailEntity(2L, 3, 1L, "1호선", "bg-blue-600", 2L, "회기", 3L, "외대앞"),
                new SectionDetailEntity(3L, 4, 2L, "2호선", "bg-green-600", 4L, "시청", 5L, "을지로입구"),
                new SectionDetailEntity(4L, 3, 2L, "2호선", "bg-green-600", 5L, "을지로입구", 6L, "을지로3가")
        );
        given(sectionDao.findSectionDetail()).willReturn(sectionDetailEntities);

        // when
        final List<LineDetailResponse> responses = lineService.findAll();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.get(0).getId()).isEqualTo(1L),
                () -> assertThat(responses.get(0).getName()).isEqualTo("1호선"),
                () -> assertThat(responses.get(0).getColor()).isEqualTo("bg-blue-600"),
                () -> assertThat(responses.get(0).getStations()).hasSize(3),
                () -> assertThat(responses.get(0).getStations().get(1).getId()).isEqualTo(2L),
                () -> assertThat(responses.get(0).getStations().get(1).getName()).isEqualTo("회기")
        );
    }

    @Test
    @DisplayName("id로 노선 조회 성공")
    void findById_success() {
        // given
        final long lineId = 1L;
        final String lineName = "1호선";
        final String lineColor = "bg-blue-600";
        final List<SectionDetailEntity> sectionDetailEntities = List.of(
                new SectionDetailEntity(1L, 2, lineId, lineName, lineColor, 1L, "청량리", 2L, "회기"),
                new SectionDetailEntity(2L, 3, lineId, lineName, lineColor, 2L, "회기", 3L, "외대앞")
        );
        given(sectionDao.findSectionDetailByLineId(lineId)).willReturn(sectionDetailEntities);

        // when
        final LineDetailResponse response = lineService.findById(lineId);

        // then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(lineId),
                () -> assertThat(response.getName()).isEqualTo(lineName),
                () -> assertThat(response.getColor()).isEqualTo(lineColor),
                () -> assertThat(response.getStations()).hasSize(3),
                () -> assertThat(response.getStations().get(0).getName()).isEqualTo("청량리"),
                () -> assertThat(response.getStations().get(2).getName()).isEqualTo("외대앞")
        );
    }

    @Test
    @DisplayName("id로 노선 조회 실패 - 존재하지 않는 id")
    void findById_fail_id_not_found() {
        // given
        final long invalidLineId = 10L;

        // when
        when(sectionDao.findSectionDetailByLineId(invalidLineId)).thenThrow(LineNotFoundException.class);

        // then
        assertThatThrownBy(() -> lineService.findById(invalidLineId))
                .isInstanceOf(LineNotFoundException.class);
    }

    @Test
    @DisplayName("노선에 역 삭제 성공")
    void deleteStation_success() {
        // given
        final long lineId = 1L;
        final String stationName = "잠실새내";
        final StationDeleteInLineRequest request = new StationDeleteInLineRequest("잠실새내");
        final SectionEntity frontSection = new SectionEntity(1L, lineId, 3, 1L, 2L);
        final SectionEntity backSection = new SectionEntity(2L, lineId, 3, 2L, 3L);
        final List<SectionEntity> entities = List.of(frontSection, backSection);
        final List<SectionDetailEntity> sectionDetailEntities = List.of(
                new SectionDetailEntity(1L, 6, lineId, "2호선", "bg-green-600", 1L, "잠실", 3L, "종합운동장")
        );

        given(sectionDao.findByLineIdAndPreviousStationNameOrNextStationName(1L, stationName)).willReturn(entities);
        doNothing().when(sectionDao).delete(frontSection);
        doNothing().when(sectionDao).delete(backSection);
        given(sectionDao.insert(any())).willReturn(any());
        given(sectionDao.findSectionDetailByLineId(lineId)).willReturn(sectionDetailEntities);

        // when
        final LineDetailResponse response = lineService.deleteStation(lineId, request);

        // then
        assertAll(
                () -> assertThat(response.getStations()).hasSize(2),
                () -> assertThat(response.getName()).isEqualTo("2호선")
        );
    }

}
