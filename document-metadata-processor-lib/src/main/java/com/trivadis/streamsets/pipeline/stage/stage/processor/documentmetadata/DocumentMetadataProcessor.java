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
package com.trivadis.streamsets.pipeline.stage.stage.processor.documentmetadata;


import com.streamsets.pipeline.api.FileRef;
import com.streamsets.pipeline.api.Processor;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;
import com.streamsets.pipeline.api.el.ELEval;
import com.trivadis.streamsets.pipeline.stage.stage.processor.documentmetadata.config.JobConfig;
import com.trivadis.streamsets.pipeline.stage.stage.processor.documentmetadata.config.PropertyNameFormatType;
import com.trivadis.streamsets.pipeline.stage.util.FileRefUtil;
import com.trivadis.streamsets.pipeline.stage.util.StringUtil;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.NullOutputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This executor is an example and does not actually perform any actions.
 */
public class DocumentMetadataProcessor extends SingleLaneRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentMetadataProcessor.class);
    private final JobConfig jobConfig;
    private Processor.Context context;

    public DocumentMetadataProcessor(JobConfig jobConfig) {
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
            LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<>();

            try (InputStream is = fileRef.createInputStream(getContext(), InputStream.class)) {
                Parser parser = new AutoDetectParser();
                NullOutputStream nos = new NullOutputStream();
                BodyContentHandler handler = new BodyContentHandler(nos);
                Metadata metadata = new Metadata();
                ParseContext context = new ParseContext();

                parser.parse(is, handler, metadata, context);

                String[] metadataNames = metadata.names();
                for(String name : metadataNames) {
                    fieldMap.put(transformValueIfNeeded(name), Field.create(metadata.get(name)));
                }

                if (jobConfig.removeWholeFile) {
                    record.delete("/fileRef");
                }

                record.set(jobConfig.outputField, Field.createListMap(fieldMap));
                batchMaker.addRecord(record);
            } catch (IOException e) {
                LOG.error("IOException", e);
                throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_02, e.getMessage(), e);
            } catch (TikaException e) {
                LOG.error("IOException", e);
                throw new OnRecordErrorException(record, Errors.IMAGE_METADATA_02, e.getMessage(), e);
            } catch (SAXException e) {
                e.printStackTrace();
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