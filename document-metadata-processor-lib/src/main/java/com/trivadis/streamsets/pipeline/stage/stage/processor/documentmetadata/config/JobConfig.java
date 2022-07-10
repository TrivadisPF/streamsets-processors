package com.trivadis.streamsets.pipeline.stage.stage.processor.documentmetadata.config;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;

public class JobConfig {
    /**
    @ConfigDef(
            required = true,
            type = ConfigDef.Type.MODEL,
            label = "Job Type",
            description = "Type of job that will be executed.",
            defaultValue = "IMAGE_METADATA",
            displayPosition = 10,
            displayMode = ConfigDef.DisplayMode.BASIC,
            group = "JOB"
    )
    @ValueChooserModel(JobTypeChooserValues.class)
    public JobType jobType;
    **/

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.STRING,
            defaultValue = "/documentMetadata",
            label = "Output Field",
            description = "Use an existing field or a new field. Using an existing field overwrites the original value",
            group = "JOB",
            displayPosition = 50
    )
    public String outputField = "/documentMetadata";

    @ConfigDef(
            required = false,
            type = ConfigDef.Type.MODEL,
            defaultValue = "ORIGINAL",
            label = "Property Name Format",
            description = "How to format the property names?",
            group = "JOB",
            displayPosition = 60
    )
    @ValueChooserModel(PropertyNameFormatChooserValues.class)
    public PropertyNameFormatType propertyNameFormatType = PropertyNameFormatType.ORIGINAL;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.BOOLEAN,
            defaultValue = "false",
            label = "Remove WholeFile",
            description = "Should we remove the Whole File after the processor has done its work?",
            group = "JOB",
            displayPosition = 70
    )
    public boolean removeWholeFile = false;

}
