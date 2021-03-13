package com.trivadis.streamsets.pipeline.stage.stage.processor.image.thumbnailer.config;

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
            defaultValue = "/imageConcepts",
            label = "Output Field",
            description = "Use an existing field or a new field. Using an existing field overwrites the original value",
            group = "JOB",
            displayPosition = 60
    )
    public String outputField = "/thumbnail";

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.NUMBER,
            defaultValue = "160",
            label = "Width",
            description = "The Width of the thumbnail",
            group = "JOB",
            displayPosition = 70,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public Integer width = 160;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.NUMBER,
            defaultValue = "160",
            label = "Height",
            description = "The Height of the thumbnail",
            group = "JOB",
            displayPosition = 75,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public Integer height = 160;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.NUMBER,
            defaultValue = "0",
            label = "Rotate",
            description = "the degree by which the thumbnail should be rotated.",
            group = "JOB",
            displayPosition = 80,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public Integer rotate = 0;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.NUMBER,
            defaultValue = "100",
            label = "Output Quality (%)",
            description = "A value from 0 to 100 which indicates the quality setting to use for the compression of the thumbnail. 0 indicates the lowest quality, 100 indicates the highest quality.",
            group = "JOB",
            displayPosition = 85,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public Integer quality = 100;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.MODEL,
            label = "Thumbnail Format",
            description = "Format of the thumbnail.",
            defaultValue = "PNG",
            displayPosition = 90,
            displayMode = ConfigDef.DisplayMode.ADVANCED,
            group = "JOB"
    )
    @ValueChooserModel(ThumbnailFormatChooserValues.class)
    public ThumbnailFormat thumbnailFormat;

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
