package com.iisigroup.cap.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * <pre>
 * Config server.
 * </pre>
 * 
 * @since Apr 29, 2019
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>Apr 29, 2019,Sunkist Wang,new
 *          </ul>
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}