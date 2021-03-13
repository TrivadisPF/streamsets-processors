package com.trivadis.streamsets.pipeline.stage.processor.image.unpack.config;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;

public class JobConfig {
    @ConfigDef(
            required = true,
            type = ConfigDef.Type.MODEL,
            label = "Job Type",
            description = "Type of job that will be executed.",
            defaultValue = "CREATE_THUMBNAIL",
            displayPosition = 10,
            displayMode = ConfigDef.DisplayMode.BASIC,
            group = "JOB"
    )
    @ValueChooserModel(JobTypeChooserValues.class)
    public JobType jobType;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.STRING,
            defaultValue = "/temp",
            label = "Local Output Directory",
            description = "Specify a local output directory where the unpacked content will be placed to",
            group = "JOB",
            displayPosition = 60
    )
    public String outputDir = "/temp";

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.MODEL,
            label = "Packaging Format",
            description = "Format of the packaging",
            defaultValue = "ZIP",
            displayPosition = 90,
            displayMode = ConfigDef.DisplayMode.ADVANCED,
            group = "JOB"
    )
    @ValueChooserModel(PackagingFormatChooserValues.class)
    public PackagingFormat thumbnailFormat;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.BOOLEAN,
            defaultValue = "false",
            label = "Remove WholeFile",
            description = "Should we remove the Whole File after the processor has done its work?",
            group = "JOB",
            displayPosition = 100,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public boolean removeWholeFile = false;

}
