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
package com.trivadis.streamsets.pipeline.stage.stage.processor.image.recognition;

import com.clarifai.channel.ClarifaiChannel;
import com.clarifai.credentials.ClarifaiCallCredentials;
import com.clarifai.grpc.api.*;
import com.clarifai.grpc.api.status.StatusCode;
import com.google.protobuf.ByteString;
import com.streamsets.pipeline.api.*;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;
import com.streamsets.pipeline.api.el.ELEval;
import com.trivadis.streamsets.pipeline.stage.stage.processor.image.recognition.config.JobConfig;
import com.trivadis.streamsets.pipeline.stage.util.FileRefUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This executor is an example and does not actually perform any actions.
 */
public class ClarifaiImageRecognitionProcessor extends SingleLaneRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ClarifaiImageRecognitionProcessor.class);
    private final JobConfig jobConfig;
    private Processor.Context context;

    public ClarifaiImageRecognitionProcessor(JobConfig jobConfig) {
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

            V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(ClarifaiChannel.INSTANCE.getGrpcChannel())
                    .withCallCredentials(new ClarifaiCallCredentials(jobConfig.clarifaiApiKey.get()));

            // Read from incoming FileRef, write to output file
            try (InputStream is = fileRef.createInputStream(getContext(), InputStream.class)) {

                // Process the image using Input Stream is
                byte[] bytes = IOUtils.toByteArray(is);

                MultiOutputResponse response = stub.postModelOutputs(
                        PostModelOutputsRequest.newBuilder()
                                .setModelId(jobConfig.clarifaiModelId)
                                .addInputs(
                                        Input.newBuilder().setData(
                                                Data.newBuilder().setImage(
                                                        Image.newBuilder().setBase64(ByteString.copyFrom(bytes))
                                                )
                                        )
                                )
                                .build()
                );

                if (response.getStatus().getCode() != StatusCode.SUCCESS) {
                    throw new RuntimeException("Request failed, status: " + response.getStatus());
                }

                LinkedHashMap<String, Field> conceptMap = new LinkedHashMap<>();

                for (Concept c : response.getOutputs(0).getData().getConceptsList()) {
                    System.out.println( String.format("%12s: %,.2f", c.getName(), c.getValue()));
                    conceptMap.put(c.getName(), Field.create(c.getValue()));
                }

                if (jobConfig.removeWholeFile) {
                    record.delete("/fileRef");
                }

                record.set(jobConfig.outputField, Field.createListMap(conceptMap));
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