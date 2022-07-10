# Image Metadata Extractor

Supported pipeline types: **Data Collector**

Image Metadata Extractor extract the available metadata from an image made available as a StreamSets Whole File. It uses the MetaData Extractor java library for accessing the metadata.


## Implementation Overview

## Generated Record

## Configuring the Image Metadata Extractor

Configure a Image Metadata Extractor processor to extract the image metdata.

1. In the Properties panel, on the **General** tab, configure the following properties:

General Property | Description
------------- | -------------
Name  | Stage name.
Description  | Optional description.
Required Fields | Fields that must include data for the record to be passed into the stage.<p/>**Tip:** You might include fields that the stage uses.<p/>Records that do not include all required fields are processed based on the error handling configured for the pipeline.
Preconditions  | Conditions that must evaluate to TRUE to allow a record to enter the stage for processing. Click **Add** to create additional preconditions.<p/>Records that do not meet all preconditions are processed based on the error handling configured for the stage
On Record Error  | Error record handling for the stage

2. In the Properties panel, on the **Job** tab, configure the following properties:

Job Property | Description
------------- | -------------
Job Type  | Job Type. Use the Image Extractor job.
Description  | Optional description.
Output Field  | Name of the field in the record to pass the image metadata structured value. You can specify an existing field or a new field. If the field does not exist, Image Metadata Extractor creates the field.
Remove WholeFile Reference | 