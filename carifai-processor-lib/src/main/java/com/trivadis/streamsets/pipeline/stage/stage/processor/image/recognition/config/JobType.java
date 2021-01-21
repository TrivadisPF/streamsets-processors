package com.trivadis.streamsets.pipeline.stage.stage.processor.image.recognition.config;

import com.streamsets.pipeline.api.Label;

public enum JobType implements Label {
    PREDICT_CONCEP("Predict Concept in Image"),
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
