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
    public String outputField = "/imageConcepts";
}
