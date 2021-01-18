package com.trivadis.streamsets.aws.stage.processor.imagemetadata;

import org.apache.commons.imaging.*;
import org.apache.commons.imaging.common.GenericImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.gif.GifImageMetadata;
import org.apache.commons.imaging.formats.gif.GifImageMetadataItem;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.icc.IccProfileInfo;
import org.junit.Test;

import java.awt.*;
import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestImaging {

    @Test
    public void testGetMetadata() throws IOException, ImageReadException {
        File fileJpeg = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.jpg");
        File filePng = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.png");
        File fileTif = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.tif");
        File fileGif = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.gif");
        File fileBmp = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.bmp");
        ImageMetadata metadata = Imaging.getMetadata(fileBmp);

        if (metadata instanceof JpegImageMetadata) {

            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            //System.out.println(jpegMetadata.getExif());

            TiffImageMetadata tiff = jpegMetadata.getExif();
            System.out.println(tiff.getGPS().getLatitudeAsDegreesNorth());
            System.out.println(tiff.getGPS().getLongitudeAsDegreesEast());

            for (TiffDirectory dir : tiff.contents.directories) {
                System.out.println(dir.description());

                for (TiffField field : dir.entries) {
                    System.out.println(field.getTagName() + "=" + field.getValue().toString());
                }
            }
        } else if (metadata instanceof TiffImageMetadata) {
            TiffImageMetadata tiffMetadata = (TiffImageMetadata) metadata;

            for (TiffDirectory dir : tiffMetadata.contents.directories) {
                System.out.println(dir.description());

                for (TiffField field : dir.entries) {
                    System.out.println(field.getTagName() + "=" + field.getValue().toString());
                }
            }
        }

    }

    @Test
    public void testGetMetadata2() throws IOException, ImageReadException {
        File file = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.jpg");
        ImageMetadata metadata = Imaging.getMetadata(file);

        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MAKE).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_MODEL).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_ORIENTATION).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_XRESOLUTION).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_YRESOLUTION).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_SOFTWARE).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_DATE_TIME).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_HOST_COMPUTER).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_YCBCR_POSITIONING).getValue());
        System.out.println(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_YCBCR_POSITIONING).getValue());


    }

    @Test
    public void testGuessFormat() throws IOException, ImageReadException {

        File file = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.jpg");

        ImageFormat format = Imaging.guessFormat(file);
        System.out.println(format);
    }

    @Test
    public void testGetInfo() throws IOException, ImageReadException {

        File file = new File("/Users/gus/workspace/git/trivadispf/streamsets-addons/image-metadata-processor-lib/src/test/test/image.jpg");
        Map<String, Object> params = new HashMap<>();
        params.put(ImagingConstants.PARAM_KEY_FORMAT, ImageFormats.JPEG);
        ImageInfo info = Imaging.getImageInfo(file);
        System.out.println(info);

    }
}
