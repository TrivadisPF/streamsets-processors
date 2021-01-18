package com.trivadis.streamsets.aws.stage.processor.imagemetadata;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.ExifToolOptions;
import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.core.StandardOptions;
import com.thebuzzmedia.exiftool.core.StandardTag;
import com.thebuzzmedia.exiftool.core.UnspecifiedTag;
import com.thebuzzmedia.exiftool.core.handlers.TagHandler;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

public class TestExifTool {

    @Test
    public void testMetadata() {
        File fileJpeg = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.jpg");
        File filePng = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.png");
        File fileTif = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.tif");
        File fileGif = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.gif");
        File fileBmp = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.bmp");

        StandardOptions opts = StandardOptions.builder().withHumanReadableFormat().withDateFormat("%Y.%m.%d %H:%M:%S").build();

        try (ExifTool exifTool = new ExifToolBuilder().build()) {
            Map<Tag,String> metadata =
                 exifTool.getImageMeta(fileJpeg, opts);

            for (Map.Entry<Tag, String> entry : metadata.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }

    }
}
