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

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.streamsets.pipeline.api.Processor;
import com.trivadis.streamsets.pipeline.stage.stage.processor.imagemetadata.config.JobConfig;
import com.trivadis.streamsets.pipeline.stage.util.FileRefUtil;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;
import com.streamsets.pipeline.api.el.ELEval;
import com.streamsets.pipeline.api.FileRef;

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
                final ImageMetadata metadata = Imaging.getMetadata(is, fileName);

                // do something with metadata
                if (metadata != null) {
                    if (metadata instanceof JpegImageMetadata) {
                        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                        LinkedHashMap<String, Field> directoryMap = new LinkedHashMap<>();

                        TiffImageMetadata tiff = jpegMetadata.getExif();
                        for (TiffDirectory dir : tiff.contents.directories) {

                            LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();
                            for (TiffField field : dir.entries) {
                                fieldMap.put(field.getTagName(), Field.create(field.getValue().toString()));
                            }
                            directoryMap.put(dir.description(), Field.create(fieldMap));
                        }

                        record.set("/metadata", Field.createListMap(directoryMap));
                        batchMaker.addRecord(record);

                    } else if (metadata instanceof GenericImageMetadata) {
                        LOG.info("Image metadata: " + metadata.toString(fileName));
                    }
                }


            } catch (IOException e) {
                LOG.error("IOException", e);
                throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_02, e.getMessage(), e);
            } catch (ImageReadException e) {
                LOG.error("ImageReadException", e);
                throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_03, e.getMessage(), e);
            }
        } catch(TransformerStageCheckedException ex){
            LOG.error(ex.getMessage(), ex.getParams(), ex);
            throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_03, ex.getMessage(), ex);
        }
    }

    private String getValue(final JpegImageMetadata jpegMetadata,
                                      final TagInfo tagInfo) {
        String retValue = null;
        final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
        if (field != null) {
            FieldType type = field.getFieldType();
        }
        return retValue;
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

}