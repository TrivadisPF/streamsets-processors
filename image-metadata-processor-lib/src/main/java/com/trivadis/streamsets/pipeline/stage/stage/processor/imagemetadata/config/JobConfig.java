package com.trivadis.streamsets.pipeline.stage.stage.processor.imagemetadata.config;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;

public class JobConfig {
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
}
