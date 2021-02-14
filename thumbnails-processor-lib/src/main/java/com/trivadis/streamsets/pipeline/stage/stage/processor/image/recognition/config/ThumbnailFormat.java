package com.trivadis.streamsets.pipeline.stage.stage.processor.image.recognition.config;

import com.streamsets.pipeline.api.Label;

public enum ThumbnailFormat implements Label {
    PNG("png"),
    ;

    private final String label;

    ThumbnailFormat(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
