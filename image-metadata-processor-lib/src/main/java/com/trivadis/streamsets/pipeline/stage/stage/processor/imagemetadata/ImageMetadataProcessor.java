/*
 * Copyright 2017 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trivadis.streamsets.pipeline.stage.stage.processor.imagemetadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import com.streamsets.pipeline.api.*;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;
import com.streamsets.pipeline.api.el.ELEval;
import com.trivadis.streamsets.pipeline.stage.stage.processor.imagemetadata.config.JobConfig;
import com.trivadis.streamsets.pipeline.stage.stage.processor.imagemetadata.config.PropertyNameFormatChooserValues;
import com.trivadis.streamsets.pipeline.stage.stage.processor.imagemetadata.config.PropertyNameFormatType;
import com.trivadis.streamsets.pipeline.stage.util.FileRefUtil;
import com.trivadis.streamsets.pipeline.stage.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This executor is an example and does not actually perform any actions.
 */
public class ImageMetadataProcessor extends SingleLaneRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMetadataProcessor.class);
    private final JobConfig jobConfig;
    private Processor.Context context;

    public ImageMetadataProcessor(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    //	private ErrorRecordHandler errorRecordHandler;
    private Map<String, ELEval> evals;

    @Override
    protected List<ConfigIssue> init() {
        List<ConfigIssue> issues = super.init();

        this.context = getContext();

        return issues;
    }

    @Override
    public void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {
        // Get existing file's details
        String fileName = record.get("/fileInfo/filename").getValueAsString();
        FileRef fileRef = record.get("/fileRef").getValueAsFileRef();
        try {
            validateRecord(record);
            // Read from incoming FileRef, write to output file
            try (InputStream is = fileRef.createInputStream(getContext(), InputStream.class)) {
                final Metadata metadata = ImageMetadataReader.readMetadata(is);

                // do something with metadata
                if (metadata != null) {
                    LinkedHashMap<String, Field> directoryMap = new LinkedHashMap<>();


                    for (Directory directory : metadata.getDirectories()) {
                        LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();

                        System.out.println(directory.getName());

                        for (Tag tag : directory.getTags()) {
                            fieldMap.put(transformValueIfNeeded(tag.getTagName()), Field.create(tag.getDescription()));

                        }

                        if (directory instanceof GpsDirectory) {
                            GpsDirectory gpsDirectory = (GpsDirectory) directory;
//                            System.out.println(gpsDirectory.getGeoLocation().getLatitude());
//                            System.out.println(gpsDirectory.getGeoLocation().getLongitude());

                            fieldMap.put(transformValueIfNeeded("latitude"), Field.create(gpsDirectory.getGeoLocation().getLatitude()));
                            fieldMap.put(transformValueIfNeeded("longitude"), Field.create(gpsDirectory.getGeoLocation().getLongitude()));
                        }

                        directoryMap.put(transformValueIfNeeded(directory.getName()), Field.create(fieldMap));

                        if (directory.hasErrors()) {
                            for (String error : directory.getErrors()) {
                                System.err.format("ERROR: %s", error);
                            }
                        }
                    }

                    if (jobConfig.removeWholeFile) {
                        record.delete("/fileRef");
                    }

                    record.set(jobConfig.outputField, Field.createListMap(directoryMap));
                    batchMaker.addRecord(record);
                }

            } catch (IOException e) {
                LOG.error("IOException", e);
                throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_02, e.getMessage(), e);
            } catch (ImageProcessingException e) {
                LOG.error("IOException", e);
                throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_02, e.getMessage(), e);
            }
        } catch(TransformerStageCheckedException ex){
            LOG.error(ex.getMessage(), ex.getParams(), ex);
            throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_03, ex.getMessage(), ex);
        }
    }


    /**
     * Validate the record is a whole file record
     * @param record the {@link com.streamsets.pipeline.api.Record} whole file record
     */
    private void validateRecord(Record record) throws StageException {
        try {
            FileRefUtil.validateWholeFileRecord(record);
        } catch (IllegalArgumentException e) {
            throw new TransformerStageCheckedException(Errors.IMAGE_METADATA_01, e.toString(), e);
        }
    }

    private String transformValueIfNeeded(String value) {
        String transformedValue = value;
        if (jobConfig.propertyNameFormatType.equals(PropertyNameFormatType.UPPER_CAMEL_CASE)) {
            transformedValue = StringUtil.toCamelCase(value, true);
        } else if (jobConfig.propertyNameFormatType.equals(PropertyNameFormatType.LOWER_CAMEL_CASE))
            transformedValue = StringUtil.toCamelCase(value, false);
        return transformedValue;
    }

}