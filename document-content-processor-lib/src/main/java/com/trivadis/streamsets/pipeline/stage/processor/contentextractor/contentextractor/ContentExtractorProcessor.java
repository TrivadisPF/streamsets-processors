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
package com.trivadis.streamsets.pipeline.stage.processor.contentextractor.contentextractor;

import com.streamsets.pipeline.api.FileRef;
import com.streamsets.pipeline.api.Processor;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.trivadis.streamsets.pipeline.stage.processor.contentextractor.contentextractor.config.JobConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.tika.metadata.Metadata;

public class ContentExtractorProcessor extends SingleLaneRecordProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ContentExtractorProcessor.class);
    private final JobConfig jobConfig;
    private Processor.Context context;

    public ContentExtractorProcessor(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ConfigIssue> init() {
        // Validate configuration values and open any required resources.
        List<ConfigIssue> issues = super.init();

        this.context = getContext();

        // If issues is not empty, the UI will inform the user of each configuration issue in the list.
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Clean up any open resources.
        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {

        String fileName = record.get("/fileInfo/filename").getValueAsString();
        FileRef fileRef = record.get("/fileRef").getValueAsFileRef();

        try {
            InputStream inputStream = fileRef.createInputStream(getContext(), InputStream.class);
            String content = extractContentUsingParser(inputStream);

            inputStream = fileRef.createInputStream(getContext(), InputStream.class);
            Metadata metadata = extractMetadataUsingParser(inputStream);

            if (jobConfig.removeWholeFile) {
                record.delete("/fileRef");
            }
            record.set(jobConfig.outputField, Field.create(content));

            batchMaker.addRecord(record);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new OnRecordErrorException(record, Errors.SAMPLE_01, e);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new OnRecordErrorException(record, Errors.SAMPLE_00, e);
        }
    }

    public String extractContentUsingParser(InputStream stream)
            throws IOException, TikaException, SAXException {

        Parser parser = new AutoDetectParser();
        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        parser.parse(stream, handler, metadata, context);
        return handler.toString();
    }

    public Metadata extractMetadataUsingParser(InputStream stream)
            throws IOException, TikaException, SAXException {

        Parser parser = new AutoDetectParser();
        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        parser.parse(stream, handler, metadata, context);
        return metadata;
    }
}
