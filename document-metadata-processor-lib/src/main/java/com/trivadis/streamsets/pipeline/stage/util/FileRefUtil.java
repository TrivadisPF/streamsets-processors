package com.trivadis.streamsets.pipeline.stage.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.impl.Utils;

import java.util.Set;

public class FileRefUtil {

    private FileRefUtil() {}

    //Metric Constants
    private static final String GAUGE_NAME = "File Transfer Statistics";
    public static final String FILE = "File";
    public static final String TRANSFER_THROUGHPUT = "Transfer Rate";
    public static final String SENT_BYTES = "Sent Bytes";
    public static final String REMAINING_BYTES = "Remaining Bytes";
    public static final String TRANSFER_THROUGHPUT_METER = "transferRate";
    public static final String COMPLETED_FILE_COUNT = "Completed File Count";

    public static final String BRACKETED_TEMPLATE = "%s (%s)";

    //Whole File Record constants
    public static final String FILE_REF_FIELD_NAME = "fileRef";
    public static final String FILE_INFO_FIELD_NAME = "fileInfo";

    public static final String FILE_REF_FIELD_PATH = "/" + FILE_REF_FIELD_NAME;
    public static final String FILE_INFO_FIELD_PATH = "/" + FILE_INFO_FIELD_NAME;

//    public static final String WHOLE_FILE_SOURCE_FILE_INFO_PATH = "/" + WholeFileProcessedEvent.SOURCE_FILE_INFO;
//    public static final String WHOLE_FILE_TARGET_FILE_INFO_PATH = "/" + WholeFileProcessedEvent.TARGET_FILE_INFO;

    public static final Joiner COMMA_JOINER = Joiner.on(",");

    public static final ImmutableSet<String> MANDATORY_METADATA_INFO =
            new ImmutableSet.Builder<String>().add("size").build();


    public static final Set<String> MANDATORY_FIELD_PATHS =
            ImmutableSet.of(FILE_REF_FIELD_PATH, FILE_INFO_FIELD_PATH, FILE_INFO_FIELD_PATH + "/size");

    public static void validateWholeFileRecord(Record record) {
        Set<String> fieldPathsInRecord = record.getEscapedFieldPaths();
        Utils.checkArgument(
                fieldPathsInRecord.containsAll(MANDATORY_FIELD_PATHS),
                Utils.format(
                        "Record does not contain the mandatory fields {} for Whole File Format.",
                        COMMA_JOINER.join(Sets.difference(MANDATORY_FIELD_PATHS, fieldPathsInRecord))
                )
        );
    }

}
