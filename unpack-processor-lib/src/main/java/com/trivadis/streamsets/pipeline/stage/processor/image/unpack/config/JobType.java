package com.trivadis.streamsets.pipeline.stage.processor.image.unpack.config;

import com.streamsets.pipeline.api.Label;

public enum JobType implements Label {
    CREATE_THUMBNAIL("Unpack Content to local folder"),
    ;

    private final String label;

    JobType(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
