package com.trivadis.streamsets.pipeline.stage.stage.processor.image.recognition.config;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.api.credential.CredentialValue;

public class JobConfig {
    @ConfigDef(
            required = true,
            type = ConfigDef.Type.MODEL,
            label = "Job Type",
            description = "Type of job that will be executed.",
            defaultValue = "PREDICT_CONCEP",
            displayPosition = 10,
            displayMode = ConfigDef.DisplayMode.BASIC,
            group = "JOB"
    )
    @ValueChooserModel(JobTypeChooserValues.class)
    public JobType jobType;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.CREDENTIAL,
            defaultValue = "",
            label = "Clarifai API Key",
            description = "The Clarifai API key",
            group = "JOB",
            displayPosition = 50
    )
    public CredentialValue clarifaiApiKey;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.STRING,
            defaultValue = "/imageConcepts",
            label = "Output Field",
            description = "Use an existing field or a new field. Using an existing field overwrites the original value",
            group = "JOB",
            displayPosition = 60
    )
    public String outputField = "/concepts";

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.STRING,
            defaultValue = "",
            label = "Clarifai Model ID",
            description = "ID for pre-created or custom Clarifai model",
            group = "JOB",
            displayPosition = 40
    )
    public String clarifaiModelId = "";

    @ConfigDef(
            required = false,
            type = ConfigDef.Type.NUMBER,
            defaultValue = "0",
            label = "Minimum probability threshold (%)",
            description = "Equal and higher value for concept prediction will be returned",
            group = "JOB",
            displayPosition = 50,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public Integer predictionInPercentageGreaterEqual = 0;

    @ConfigDef(
            required = false,
            type = ConfigDef.Type.NUMBER,
            defaultValue = "20",
            label = "Number of concepts returned",
            description = "Number of concepts to return",
            group = "JOB",
            displayPosition = 60,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public Integer maxConcepts = 20;

    @ConfigDef(
            required = true,
            type = ConfigDef.Type.BOOLEAN,
            defaultValue = "false",
            label = "Remove WholeFile",
            description = "Should we remove the Whole File after the processor has done its work?",
            group = "JOB",
            displayPosition = 70,
            displayMode = ConfigDef.DisplayMode.ADVANCED
    )
    public boolean removeWholeFile = false;
}
