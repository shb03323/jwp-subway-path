package subway.controller.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class StationRegisterInLineRequest {

    @NotNull(message = "standardStationId 이 null 입니다.")
    private final Long standardStationId;
    @NotNull(message = "newStationId 이 null 입니다.")
    private final Long newStationId;
    @NotNull(message = "distance 가 null 입니다.")
    @Positive(message = "거리는 양의 정수만 가능합니다.")
    private final Integer distance;

    public StationRegisterInLineRequest(final Long standardStationId,
                                        final Long newStationId, final Integer distance) {
        this.standardStationId = standardStationId;
        this.newStationId = newStationId;
        this.distance = distance;
    }

    public Long getStandardStationId() {
        return standardStationId;
    }

    public Long getNewStationId() {
        return newStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
