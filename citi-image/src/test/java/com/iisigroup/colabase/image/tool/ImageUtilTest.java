package com.iisigroup.colabase.image.tool;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by AndyChen on 2018/3/22.
 */
public class ImageUtilTest {

    // 測試檔案資料夾
    private final String testFileFolderPath = "/Users/VALLA/Desktop/testImageUtil/targetFolder";
    // 轉檔後放置資料夾
    private final String transFilePlaceFolderPath = "/Users/VALLA/Desktop/testImageUtil/resultFolder";

    // 測試檔案名稱
    private final String testFileName = "IamEN.jpg";

    private File targetImageFile = new File(testFileFolderPath, testFileName);

    private File targetImagesFolder = new File(testFileFolderPath);

    private File placeFolder = new File(transFilePlaceFolderPath);

    private ImageBuilder imageFileBuilder;

    private ImageBuilder imageFilesBuilder;

    @Before
    public void setUp() throws Exception {
        imageFileBuilder = ImageUtil.fromSrc(targetImageFile);
        imageFilesBuilder = ImageUtil.fromSrc(targetImagesFolder);

    }

    @Test
    public void test_from_folder_read_file() throws Exception {
        ImageBuilder imageBuilder = ImageUtil.fromSrc(targetImageFile);
        assertNotNull("imageFileBuilder must have instance", imageBuilder);
    }

    @Test
    public void test_file_to_base64_string() throws Exception {
        String base64Str = ImageUtil.convertImageToBase64String(targetImageFile);
        System.out.println("base64Str: " + base64Str);
        assertNotEquals("base 64 str must not \"\"", base64Str, "");
    }

    @Test
    public void test_file_resize_to_800_x_600() throws Exception {
        imageFileBuilder.resize(800, 600);
        imageFileBuilder.writeToFiles(placeFolder, true);
        assertNotNull(new File(placeFolder, testFileName));
    }

    @Test
    public void test_rotate_to_tiff() throws Exception {
        imageFileBuilder.rotate(ImageBuilder.Orientation.PORTRAIT);
        imageFileBuilder.writeToFiles(placeFolder, true);
        assertNotNull(new File(placeFolder, testFileName));
    }

    @Test
    public void test_file_trans_to_tiff() throws Exception {
        imageFileBuilder.writeToFiles(placeFolder, "TIFF", true);
        String newFileName = testFileName.replaceAll("\\..*$", ".TIFF");
        assertNotNull(new File(placeFolder, newFileName));
    }

    @Test
    public void test_files_combind_to_one_tiff() throws Exception {
        imageFilesBuilder.combineAndWriteToMultipageTIFF(placeFolder, true);
        // output_combine_20180302_05_00_53.tiff
        boolean checkFile = false;
        for (File file : placeFolder.listFiles()) {
            if (file.getName().contains("output_combine_"))
                checkFile = true;
        }
        assertTrue(checkFile);
    }

    @Test(expected = Exception.class)
    public void test_target_file_not_found() throws Exception {
        ImageUtil.fromSrc("I'm not exsist");
        fail();
    }
}