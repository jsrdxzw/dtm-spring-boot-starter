package com.jsrdxzw.dtmspringbootstarter.exception;

import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import lombok.*;

/**
 * @author xuzhiwei
 * @date 2022/4/5 15:57
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DtmException extends RuntimeException {

    private DtmResultEnum dtmResult;

    public DtmException(String msg) {
        super(msg);
    }

    public static DtmException failure() {
        DtmResultEnum failure = DtmResultEnum.FAILURE;
        DtmException dtmException = new DtmException(failure.getDesc());
        dtmException.setDtmResult(failure);
        return dtmException;
    }

    public static DtmException ongoing() {
        DtmResultEnum failure = DtmResultEnum.ONGOING;
        DtmException dtmException = new DtmException(failure.getDesc());
        dtmException.setDtmResult(failure);
        return dtmException;
    }
}
