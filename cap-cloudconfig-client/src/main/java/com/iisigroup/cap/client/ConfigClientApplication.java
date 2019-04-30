package com.iisigroup.cap.client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * <pre>
 * Test config client
 * </pre>
 * 
 * @since Apr 29, 2019
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>Apr 29, 2019,Sunkist Wang,new
 *          </ul>
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

}
