package com.trivadis.streamsets.pipeline.stage.processor.image.unpack.config;

import com.streamsets.pipeline.api.Label;

public enum PackagingFormat implements Label {
    ZIP("zip"),
    ;

    private final String label;

    PackagingFormat(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
