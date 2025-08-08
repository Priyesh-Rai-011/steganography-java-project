package com.stegosecure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main Spring Boot Application Class
 * This ENTRY POINT of StegoSecure backend server
 */
@SpringBootApplication
public class StegoSecureApplication {
    private static final Logger logger = LoggerFactory.getLogger(StegoSecureApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(StegoSecureApplication.class, args);

        logger.info("✅ StegoSecure Backend Started Successfully!");
        logger.info("📡 Server running on: http://localhost:8080");
        logger.info("🧪 Ready for backend testing!");
        logger.info("🔗 Test URL: http://localhost:8080/api/test");
    }
}
