package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.entity.SectionEntity;
import subway.service.converter.LineConverter;
import subway.service.dto.LineDto;
import subway.service.dto.SectionCreateDto;
import subway.ui.dto.LineInitialRequest;
import subway.ui.dto.LineResponse;
import subway.entity.LineEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public long save(final LineDto lineDto, final SectionCreateDto sectionCreateDto) {
        final Long lineId = lineDao.insert(LineConverter.toEntity(lineDto));
        final Long previousStationId = stationDao.findIdByName(sectionCreateDto.getPreviousStationName());
        final Long nextStationId = stationDao.findIdByName(sectionCreateDto.getNextStationName());
        sectionDao.insert(new SectionEntity.Builder()
                .lineId(lineId)
                .distance(sectionCreateDto.getDistance())
                .previousStationId(previousStationId)
                .nextStationId(nextStationId)
                .build());
        return lineId;
    }

    public Long saveLine(LineInitialRequest request) {
        Long id = lineDao.insert(new LineEntity(request.getName(), request.getColor()));
        return id;
        // TODO: LineResponse 처리하기
    }

    public List<LineResponse> findLineResponses() {
        List<LineEntity> persistLines = findLines();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public List<LineEntity> findLines() {
        return lineDao.findAll();
    }

    public LineResponse findLineResponseById(Long id) {
        LineEntity persistLine = findLineById(id);
        return LineResponse.of(persistLine);
    }

    public LineEntity findLineById(Long id) {
        return lineDao.findById(id);
    }

    public void updateLine(Long id, LineInitialRequest lineUpdateRequest) {
        lineDao.update(new LineEntity(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
