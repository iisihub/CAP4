package com.iisigroup.colabase.tool;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ImageBuilder {
    enum Orientation {
        /**
         * 直式
         */
        PORTRAIT,
        /**
         * 橫式
         */
        LANDSCAPE
    }

    /**
     * 關閉 ImageBuilder，釋放快取記憶體。
     */
    void close();

    /**
     * 檢查 ImageBuilder 是否關閉。
     *
     * @return ImageBuilder 是否關閉
     */
    boolean checkIsClosed();

    /**
     * 調整圖片尺寸。
     *
     * @param width
     *            圖片寬
     * @param height
     *            圖片高
     * @return ImageBuilder
     * @throws IOException
     */
    ImageBuilder resize(int width, int height) throws IOException;


    /**
     * 旋轉圖片。
     *
     * @param orientation
     *            圖片方向
     * @return ImageBuilder
     */
    ImageBuilder rotate(Orientation orientation);

    /**
     * 合併成多頁 TIFF。
     *
     * @param destLocation
     *            目標目錄路徑
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之 TIFF
     * @throws IOException
     */
    File combineAndWriteToMultipageTIFF(String destLocation, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 合併成多頁 TIFF。
     *
     * @param destLocation
     *            目標目錄
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之 TIFF
     * @throws IOException
     */
    File combineAndWriteToMultipageTIFF(File destLocation, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 合併成多頁 TIFF。
     *
     * @param destLocation
     *            目標目錄路徑
     * @param quality
     *            TIFF 品質，0.0f - 1.0f
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之 TIFF
     * @throws IOException
     */
    File combineAndWriteToMultipageTIFF(String destLocation, float quality, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 合併成多頁 TIFF。
     *
     * @param destLocation
     *            目標目錄
     * @param quality
     *            TIFF 品質，0.0f - 1.0f
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之 TIFF
     * @throws IOException
     */
    File combineAndWriteToMultipageTIFF(File destLocation, float quality, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 寫出圖片。
     *
     * @param destLocation
     *            目標目錄路徑
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之圖片 List
     * @throws IOException
     */
    List<File> writeToFiles(String destLocation, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 寫出圖片。
     *
     * @param destLocation
     *            目標目錄
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之圖片 List
     * @throws IOException
     */
    List<File> writeToFiles(File destLocation, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 寫出圖片。
     *
     * @param destLocation
     *            目標目錄路徑
     * @param fileType
     *            寫出之圖片格式
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之圖片 List
     * @throws IOException
     */
    List<File> writeToFiles(String destLocation, String fileType, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 寫出圖片。
     *
     * @param destLocation
     *            目標目錄
     * @param fileType
     *            寫出之圖片格式
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之圖片 List
     * @throws IOException
     */
    List<File> writeToFiles(File destLocation, String fileType, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 寫出圖片。
     *
     * @param destLocation
     *            目標目錄路徑
     * @param fileType
     *            寫出之圖片格式
     * @param quality
     *            圖片品質，0.0f - 1.0f
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之圖片 List
     * @throws IOException
     */
    List<File> writeToFiles(String destLocation, String fileType, float quality, boolean isCloseBuilderAfterWrote) throws IOException;

    /**
     * 寫出圖片。
     *
     * @param destLocation
     *            目標目錄
     * @param fileType
     *            寫出之圖片格式
     * @param quality
     *            圖片品質，0.0f - 1.0f
     * @param isCloseBuilderAfterWrote
     *            結束時是否關閉 ImageBuilder
     * @return 產出之圖片 List
     * @throws IOException
     */
    List<File> writeToFiles(File destLocation, String fileType, float quality, boolean isCloseBuilderAfterWrote) throws IOException;

}
