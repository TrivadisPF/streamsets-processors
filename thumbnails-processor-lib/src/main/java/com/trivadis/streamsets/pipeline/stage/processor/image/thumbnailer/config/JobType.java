package com.trivadis.streamsets.pipeline.stage.processor.image.thumbnailer.config;

import com.streamsets.pipeline.api.Label;

public enum JobType implements Label {
    CREATE_THUMBNAIL("Create Thumbnail for an Image"),
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
