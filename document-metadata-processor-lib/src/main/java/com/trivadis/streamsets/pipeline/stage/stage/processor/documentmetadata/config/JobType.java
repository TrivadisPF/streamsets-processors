package com.trivadis.streamsets.pipeline.stage.stage.processor.documentmetadata.config;

import com.streamsets.pipeline.api.Label;

public enum JobType implements Label {
    IMAGE_METADATA("Retrieve Document Metadata"),
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
