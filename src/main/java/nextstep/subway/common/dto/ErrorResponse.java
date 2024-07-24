package nextstep.subway.common.dto;

import nextstep.subway.common.constant.ErrorCode;

public class ErrorResponse {
    private Integer status;
    private String code;
    private String description;

    public ErrorResponse () {}

    public ErrorResponse(ErrorCode collectedErrorResponse) {
        this.status = collectedErrorResponse.getStatus();
        this.code = collectedErrorResponse.getCode();
        this.description = collectedErrorResponse.getDescription();
    }

    public Integer getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
