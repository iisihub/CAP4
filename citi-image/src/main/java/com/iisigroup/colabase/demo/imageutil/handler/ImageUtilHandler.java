package com.iisigroup.colabase.demo.imageutil.handler;

import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.colabase.tool.ImageBuilder;
import com.iisigroup.colabase.tool.ImageUtil;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * demo use handler
 * </pre>
 *
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/03/12,AndyChen,new
 *          </ul>
 * @since 2018/03/12
 */
@Controller("demoimageutilhandler")
public class ImageUtilHandler extends MFormHandler {

    private static final String RESULT_KEY = "result";

    public Result demo(Request params) {

        // [_pa, inputFilesPath, _AuditLogTS, _isAjax, inputFolderPath, formAction, inputType]
        AjaxFormResult result = new AjaxFormResult();
        String inputType = params.get("inputType", "");

        File outputFolder = new File(params.get("outputFilePath"));
        if (!outputFolder.exists() || !outputFolder.isDirectory()) {
            result.set(RESULT_KEY, "please make sure your output folder path is correct.");
            return result;
        }
        ImageBuilder imageBuilder = null;
        switch (inputType) {
        // folder
        case "0":
            File folder = new File(params.get("inputFolderPath"));
            if (!folder.exists() || !folder.isDirectory()) {
                result.set(RESULT_KEY, "please make sure your folder path is correct.");
                return result;
            }
            imageBuilder = ImageUtil.fromSrc(folder);
            break;
        // multi files
        case "1":
            String[] filesPath = params.get("inputFilesPath", "").split(",");
            List<File> files = new ArrayList<>();
            for (String path : filesPath) {
                File file = new File(path);
                if (!file.exists()) {
                    result.set(RESULT_KEY, "file: " + file.getName() + " is not exists. Please check the file.");
                    return result;
                }
                files.add(file);
            }
            imageBuilder = ImageUtil.fromSrc(files);
            break;
        default:
            result.set(RESULT_KEY, "please choose a input type!");
            return result;
        }
        try {
            imageBuilder.writeToFiles(outputFolder, "tiff", true);
        } catch (IOException e) {
            result.set(RESULT_KEY, "output file fail, make sure path is correct.");
        }
        if (outputFolder.exists()) {
            result.set(RESULT_KEY, "trans file is success created in " + outputFolder.getAbsolutePath());
        }
        return result;
    }

    public Result demoBase64(Request params) {
        AjaxFormResult result = new AjaxFormResult();
        String inputFilePath = params.get("inputFilePath", "");
        File file = new File(inputFilePath);
        if (!file.exists() || file.isDirectory()) {
            result.set(RESULT_KEY, "please make sure your file is exists or not a directory.");
            return result;
        }
        String base64Str = ImageUtil.convertImageToBase64String(file);
        result.set(RESULT_KEY, base64Str);
        return result;
    }

}
