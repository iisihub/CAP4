/* 
 * CaptchaHandler.java
 * 
 * Copyright (c) 2016 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.captcha.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.security.annotation.Captcha;
import com.iisigroup.cap.security.service.CheckCodeService;
import com.iisigroup.cap.utils.CapAppContext;

import nl.captcha.audio.Sample;

/**
 * <pre>
 * create captcha handler
 * </pre>
 * 
 * @since 2016年6月14日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2016年6月14日,Sunkist Wang,new
 *          </ul>
 */
@Controller("captchahandler")
public class CaptchaHandler extends MFormHandler {

    public static String DEFAULT_RENDER = "capCaptcha";

    @Autowired
    private CapAppContext capAppContext;

    /**
     * create captcha image
     * 
     * @throws IOException
     */
    public Result img(Request request) {
        CheckCodeService captcha = capAppContext.getBean(DEFAULT_RENDER);
        BufferedImage img = captcha.createCheckCode(true);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
        return new ByteArrayDownloadResult(request, baos.toByteArray(), "image");
    }

    /**
     * create audio wav
     * 
     * @throws IOException
     */
    public Result audio(Request request) {
        CheckCodeService captcha = capAppContext.getBean(DEFAULT_RENDER);
        Sample audio = captcha.createCheckCode(false);
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            AudioSystem.write(audio.getAudioInputStream(), AudioFileFormat.Type.WAVE, baos);
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
        return new ByteArrayDownloadResult(request, baos.toByteArray(), "audio/wave");
    }

    /**
     * 動態驗証測試 掛上 @Captcha 即可自動檢查 captcha 欄位
     * 
     * @param request
     * @return IResult
     */
    @Captcha("audioCaptcha")
    public Result checkCaptcha(Request request) {
        return new AjaxFormResult().set(Constants.AJAX_NOTIFY_MESSAGE, "check ok!");
    }

}
