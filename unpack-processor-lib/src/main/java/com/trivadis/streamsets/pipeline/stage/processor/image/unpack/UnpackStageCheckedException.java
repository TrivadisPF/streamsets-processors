package com.trivadis.streamsets.pipeline.stage.processor.image.unpack;

import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.StageException;

public class UnpackStageCheckedException extends StageException {

    public UnpackStageCheckedException(ErrorCode errorCode, Object... params) {
        super(errorCode, params);
    }
}