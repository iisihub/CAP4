package com.iisigroup.colabase.json.util;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/6/15 AndyChen,new
 *          </ul>
 * @since 2018/6/15
 */
public class JsonFileUtil {

    public static String loadFileFromConfigPath(String path) {
        StringBuilder result = new StringBuilder();
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream()); BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("here is something wrong when reading config path: " + path);
        }
        return result.toString();
    }
}
