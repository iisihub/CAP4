/*
 * CapSerialization.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <pre>
 * Object seriaization utilities
 * </pre>
 * 
 * @since 2003/5/20
 * @author Malo Jwo
 * @version
 *          <ul>
 *          <li>2011/6/27,iristu,copy from gaia
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public class CapSerialization {

    private boolean compress = false;

    CapSerialization(boolean compress) {
        this.compress = compress;
    }

    private static CapSerialization inst = new CapSerialization(false);
    private static CapSerialization compressInst = new CapSerialization(true);

    public static CapSerialization newInstance() {
        return inst;
    }

    public static CapSerialization newCompressInstance() {
        return compressInst;
    }

    public boolean isCompress() {
        return compress;

    }

    public void setCompress(boolean b) {
        compress = b;
    }

    /**
     * compress byte array data with GZIP.
     * 
     * @param input
     *            the input data
     * @return the compressed data
     * @throws IOException
     */
    public byte[] compress(byte[] input) throws IOException {
        byte[] result = null;
        ByteArrayOutputStream baout = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipout = new GZIPOutputStream(baout);) {
            gzipout.write(input);
            gzipout.finish();
            result = baout.toByteArray();
            return result;
        }
    }

    /**
     * decompress byte array data with GZIP.
     * 
     * @param input
     *            the input compressed data
     * @return the decompress data
     * @throws IOException
     */
    public byte[] decompress(byte[] input) throws IOException {

        byte[] buf = new byte[2048];
        byte[] result = null;
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        try (GZIPInputStream gzipin = new GZIPInputStream(new ByteArrayInputStream(input));) {
            int size;
            while ((size = gzipin.read(buf)) != -1) {
                baout.write(buf, 0, size);
            }
            result = baout.toByteArray();
            return result;
        }

    }

    /**
     * Load data from savedData string
     * 
     * @param in
     *            the saved string
     * @return the original data
     */
    public Object loadData(String in) {
        return loadDataFromByteArray(CapString.hexStrToByteArray(in), compress);
    }

    public Object loadDataFromByteArray(byte[] in, boolean compress) {
        if (in == null) {
            return null;
        }
        try (ByteArrayInputStream bais = compress ? new ByteArrayInputStream(decompress(in)) : new ByteArrayInputStream(in); ObjectInputStream ois = new ObjectInputStream(bais);) {
            Object o = ois.readObject();
            return o;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * Serializate the object to string
     * 
     * @param o
     *            the input object
     * @return the object's serialized string
     */
    public String saveData(Object o) {
        return CapString.byteArrayToHexString(saveDataToByteArray(o, compress));
    }

    public byte[] saveDataToByteArray(Object o, boolean compress) {
        if (o == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(o);
            byte[] out = baos.toByteArray();
            return compress ? compress(out) : out;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}
