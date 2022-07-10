package com.trivadis.streamsets.pipeline.stage.processor.image.thumbnailer.config;

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
