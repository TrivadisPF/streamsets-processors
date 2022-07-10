package com.trivadis.streamsets.stage.processor.headerdetailparser.config;

import com.streamsets.pipeline.api.Label;

public enum PropertyNameFormatType implements Label {
    ORIGINAL("Keep Original"),
    LOWER_CAMEL_CASE("camelCase"),
    UPPER_CAMEL_CASE("Upper CamelCase"),
    ;

    private final String label;

    PropertyNameFormatType(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
