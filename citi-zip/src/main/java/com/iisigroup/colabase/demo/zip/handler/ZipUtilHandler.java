package com.iisigroup.colabase.demo.zip.handler;

import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.colabase.zip.tool.ZipUtil;

import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;

import java.io.File;

/**
 * <pre>
 * demo zip function handler
 * </pre>
 *
 * @author JohnsonHo
 * @version
 *          <ul>
 *          <li>2018/03/24,JohnsonHo,new
 *          </ul>
 * @since 2018/03/24
 */
@Controller("demoziphandler")
public class ZipUtilHandler extends MFormHandler {

    private static final String RESULT = "result";
    private static final String FAIL = "Fail, cause : ";
    
    public Result zipDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        try {
            File destination = new File(request.get("zipOutPath"));
            File unzipFiles = new File(request.get("zipFile"));
            String isOverwrite = request.get("overwrite");
            Boolean overwrite = ("Y").equals(isOverwrite);
            String password = request.get("zipPassword");
            String userDefineName = request.get("zipName");

            ZipUtil.isExistsFolder(destination, true);
            ZipUtil.zip(new File(destination, userDefineName + ".zip"), overwrite, password, unzipFiles);
            result.set(RESULT, "Success, zip path : " + destination + "\\" + userDefineName + ".zip");
        } catch (Exception e) {
            result.set(RESULT, FAIL + e.getClass());
        }
        return result;
    }

    public Result unzipDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        try {
            File destination = new File(request.get("unzipOutPath"));
            File unzipFiles = new File(request.get("unzipFile"));
            String password = request.get("unzipPassword");

            ZipUtil.unzip(unzipFiles, password, destination);
            result.set(RESULT, "Success, unzip path : " + destination);
        } catch (Exception e) {
            result.set(RESULT, FAIL + e.getClass());
        }
        return result;
    }

    public Result isEmptyFolderDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        try {
            String isEmptyFolder1 = request.get("isEmptyFolder1");
            String isEmptyFolder2 = request.get("isEmptyFolder2");

            Boolean isEmpty = ZipUtil.isEmptyFolder(false, isEmptyFolder1, isEmptyFolder2);
            result.set(RESULT, "Success,  is empty folder ? : " + isEmpty);
        } catch (Exception e) {
            result.set(RESULT, FAIL + e.getClass());
        }
        return result;
    }

}
