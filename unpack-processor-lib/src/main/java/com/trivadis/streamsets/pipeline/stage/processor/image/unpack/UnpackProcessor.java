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
package com.trivadis.streamsets.pipeline.stage.processor.image.unpack;


import com.streamsets.pipeline.api.*;
import com.streamsets.pipeline.api.base.OnRecordErrorException;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;
import com.streamsets.pipeline.api.el.ELEval;
import com.trivadis.streamsets.pipeline.lib.OutputFileRef;
import com.trivadis.streamsets.pipeline.stage.processor.image.unpack.config.JobConfig;
import com.trivadis.streamsets.pipeline.util.FileRefUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jetbrains.annotations.NotNull;

/**
 * This executor is an example and does not actually perform any actions.
 */
public class UnpackProcessor extends SingleLaneRecordProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(UnpackProcessor.class);

    private static final String PERMISSIONS = "permissions";
    private static final String FILE = "file";
    private static final String FILE_NAME = "filename";

    private final JobConfig jobConfig;
    private Processor.Context context;
    public UnpackProcessor(JobConfig jobConfig) {
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
                File destDir = new File(jobConfig.outputDir);
                byte[] buffer = new byte[1024];
                ZipInputStream zis = new ZipInputStream(is);
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    File newFile = newFile(destDir, zipEntry);
                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        // fix for Windows-created archives
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("Failed to create directory " + parent);
                        }

                        // write file content
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();

                        // Create a reference to an output file
                        OutputFileRef outputFileRef;
                        outputFileRef = new OutputFileRef("/tmp", newFile.getName());

                        LinkedHashMap<String, Field> listMap = new LinkedHashMap<>();

                        listMap.put("/fileRef", Field.create(outputFileRef));
                        listMap.put("/fileInfo", com.trivadis.streamsets.pipeline.lib.FileRefUtil.createFieldForMetadata(getFileMetadata(newFile)));

                        record.set(Field.createListMap(listMap));
                        batchMaker.addRecord(record);
                    }

                    zipEntry = zis.getNextEntry();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(UnpackStageCheckedException ex){
            LOG.error(ex.getMessage(), ex.getParams(), ex);
            throw new OnRecordErrorException(record, Errors.CLARIFAI_REKOGNITION_02, ex.getMessage(), ex);
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private void validateRecord(Record record) throws StageException {
        try {
            FileRefUtil.validateWholeFileRecord(record);
        } catch (IllegalArgumentException e) {
            throw new UnpackStageCheckedException(Errors.CLARIFAI_REKOGNITION_01, e.toString(), e);
        }
    }

    // From com.streamsets.pipeline.stage.origin.spooldir.SpoolDirSource
    @NotNull
    private Map<String, Object> getFileMetadata(File file) throws IOException {
        boolean isPosix = file.toPath().getFileSystem().supportedFileAttributeViews().contains("posix");
        Map<String, Object> metadata = new HashMap<>(Files.readAttributes(file.toPath(), isPosix? "posix:*" : "*"));
        metadata.put(FILE_NAME, file.getName());
        metadata.put(FILE, file.getPath());
        if (isPosix && metadata.containsKey(PERMISSIONS) && Set.class.isAssignableFrom(metadata.get(PERMISSIONS).getClass())) {
            Set<PosixFilePermission> posixFilePermissions = (Set<PosixFilePermission>)(metadata.get(PERMISSIONS));
            //converts permission to rwx- format and replace it in permissions field.
            // (totally containing 9 characters 3 for user 3 for group and 3 for others)
            metadata.put(PERMISSIONS, PosixFilePermissions.toString(posixFilePermissions));
        }
        return metadata;
    }

}