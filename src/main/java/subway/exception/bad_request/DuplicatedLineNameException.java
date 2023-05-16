package subway.exception.bad_request;

public class DuplicatedLineNameException extends BadRequestException {

    private static final String MESSAGE = "이미 존재하는 노선입니다.";

    public DuplicatedLineNameException() {
        super(MESSAGE);
    }
}
