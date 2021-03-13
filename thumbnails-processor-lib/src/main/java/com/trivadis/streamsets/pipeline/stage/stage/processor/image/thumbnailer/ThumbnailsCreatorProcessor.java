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
package com.trivadis.streamsets.pipeline.stage.stage.processor.image.thumbnailer;

import com.streamsets.pipeline.api.*;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;
import com.streamsets.pipeline.api.el.ELEval;
import com.trivadis.streamsets.pipeline.stage.stage.processor.image.thumbnailer.config.JobConfig;
import com.trivadis.streamsets.pipeline.stage.util.FileRefUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This executor is an example and does not actually perform any actions.
 */
public class ThumbnailsCreatorProcessor extends SingleLaneRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ThumbnailsCreatorProcessor.class);
    private final JobConfig jobConfig;
    private Processor.Context context;

    public ThumbnailsCreatorProcessor(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    //	private ErrorRecordHandler errorRecordHandler;
    private Map<String, ELEval> evals;

    @Override
    protected List<ConfigIssue> init() {
        List<ConfigIssue> issues = super.init();

        this.context = getContext();


//        if (jobConfig.predictionInPercentageGreaterEqual < 0 || jobConfig.predictionInPercentageGreaterEqual > 100) {
//            issues.add(context.createConfigIssue(Groups.JOB.name(), "conf.predictionInPercentageGreaterEqual", Errors.CLARIFAI_REKOGNITION_03));
//        }

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

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Thumbnails.of(is)
                        .width(jobConfig.width)
                        .height(jobConfig.height)
                        .rotate(jobConfig.rotate)
                        .outputQuality(jobConfig.quality / 100)
                        .outputFormat(jobConfig.thumbnailFormat.getLabel())
                        .toOutputStream(baos);
//                        .asBufferedImage();

                byte[] bytes = baos.toByteArray();

                record.set(jobConfig.outputField, Field.create(bytes));
                batchMaker.addRecord(record);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(TransformerStageCheckedException ex){
            LOG.error(ex.getMessage(), ex.getParams(), ex);
            throw new OnRecordErrorException(record, Errors.CLARIFAI_REKOGNITION_02, ex.getMessage(), ex);
        }
    }

    private void validateRecord(Record record) throws StageException {
        try {
            FileRefUtil.validateWholeFileRecord(record);
        } catch (IllegalArgumentException e) {
            throw new TransformerStageCheckedException(Errors.CLARIFAI_REKOGNITION_01, e.toString(), e);
        }
    }
}