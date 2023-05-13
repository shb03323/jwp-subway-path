package subway.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.business.SectionService;
import subway.business.dto.SectionInsertDto;
import subway.presentation.dto.request.SectionRequest;
import subway.presentation.dto.response.SectionResponse;
import subway.presentation.query_option.SubwayDirection;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<List<SectionResponse>> add(@RequestBody SectionRequest request) {
        final List<SectionResponse> responses = sectionService.save(
                new SectionInsertDto(
                        request.getLineName(),
                        SubwayDirection.from(request.getDirection()),
                        request.getStandardStationName(),
                        request.getAdditionalStationName(),
                        request.getDistance())
        );

        return ResponseEntity.created(URI.create("/sections")).body(responses);
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestParam("lineid") Long lineId, @RequestParam("stationid") Long stationId) {
        sectionService.remove(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

}