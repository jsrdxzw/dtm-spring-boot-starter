package com.jsrdxzw.dtmspringbootstarter.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author xuzhiwei
 * @date 2022/4/5 16:23
 */
@Getter
@AllArgsConstructor
public enum DtmResultEnum {
    /**
     * ResultSuccess for result of a trans/trans branch
     * Same as HTTP status 200 and GRPC code 0
     */
    SUCCESS("SUCCESS", HttpStatus.OK),

    /**
     * ResultFailure for result of a trans/trans branch
     * Same as HTTP status 409 and GRPC code 10
     */
    FAILURE("FAILURE", HttpStatus.CONFLICT),

    /**
     * ResultOngoing for result of a trans/trans branch
     * Same as HTTP status 425 and GRPC code 9
     */
    ONGOING("ONGOING", HttpStatus.TOO_EARLY);

    private final String desc;
    private final HttpStatus status;
}
