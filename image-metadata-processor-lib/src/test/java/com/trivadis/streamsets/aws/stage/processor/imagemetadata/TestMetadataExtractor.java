package com.trivadis.streamsets.aws.stage.processor.imagemetadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import org.apache.commons.imaging.*;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestMetadataExtractor {

    @Test
    public void testGetMetadata() throws IOException, ImageReadException, ImageProcessingException {
        File fileJpeg = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.jpg");
        File filePng = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.png");
        File fileTif = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.tif");
        File fileGif = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.gif");
        File fileBmp = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.bmp");
        File fileHeic = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.heic");

        Metadata metadata = ImageMetadataReader.readMetadata(fileJpeg);
        System.out.println(metadata.getDirectoryCount());

        Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);

        for (GpsDirectory dir : gpsDirectories) {
            GeoLocation geoLocation = dir.getGeoLocation();
            System.out.println(geoLocation.getLatitude());
            System.out.println(dir.getGpsDate());
            System.out.println(dir.getName());
        }

        for (Directory directory : metadata.getDirectories()) {
            System.out.println(directory.getName());

            if (directory instanceof GpsDirectory) {
                GpsDirectory gpsDirectory = (GpsDirectory) directory;
                System.out.println(gpsDirectory.getGeoLocation().getLatitude());
                System.out.println(gpsDirectory.getGeoLocation().getLongitude());

            }

            for (Tag tag : directory.getTags()) {
                System.out.println("      " + tag.getTagName() + " =  " + tag.getDescription() );
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
    }

}
