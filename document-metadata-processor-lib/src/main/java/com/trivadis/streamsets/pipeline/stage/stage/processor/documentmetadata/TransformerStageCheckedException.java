package com.trivadis.streamsets.pipeline.stage.stage.processor.documentmetadata;

import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.StageException;

public class TransformerStageCheckedException extends StageException {

    public TransformerStageCheckedException(ErrorCode errorCode, Object... params) {
        super(errorCode, params);
    }
}