package com.trivadis.streamsets.aws.stage.processor.imagemetadata;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.core.StandardOptions;
import com.trivadis.streamsets.pipeline.stage.util.StringUtil;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class TestStringUtil {

    @Test
    public void testToCamelCase() {
        System.out.println(StringUtil.toCamelCase("Test_Case", false));
        System.out.println(StringUtil.toCamelCase("Test12Case", false));
        System.out.println(StringUtil.toCamelCase("Test12_Case", false));

        System.out.println(StringUtil.toCamelCase("Test_Case", true));
        System.out.println(StringUtil.toCamelCase("Test12Case", true));
        System.out.println(StringUtil.toCamelCase("Test12_Case", true));
    }
}
