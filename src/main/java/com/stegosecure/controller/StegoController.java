package com.stegosecure.controller;

import com.stegosecure.service.StegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * StegoController - REST API Endpoints
 * Provides HTTP endpoints for steganography operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend to call these APIs
public class StegoController {

    // Initialize the SLF4J logger for this class
    private static final Logger logger = LoggerFactory.getLogger(StegoController.class);

    @Autowired
    private StegoService stegoService;

    /**
     * Test endpoint to verify server is running
     * GET /api/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("GET /api/test - Endpoint called");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "StegoSecure Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", new String[]{
                "POST /api/hide - Hide message in image",
                "POST /api/reveal - Reveal message from image",
                "POST /api/capacity - Check image capacity",
                "GET /api/test - Test endpoint"
        });

        logger.info("GET /api/test - Success response sent");
        return ResponseEntity.ok(response);
    }

    /**
     * Hide a message in an image
     * POST /api/hide
     * @param image PNG image file (multipart/form-data)
     * @param message Secret message to hide (form parameter)
     * @param key AES encryption key (form parameter)
     * @return Stego-image as downloadable PNG file
     */
    @PostMapping(value = "/hide", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> hideMessage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("message") String message,
            @RequestParam("key") String key) {

        logger.info("POST /api/hide - HIDE MESSAGE API CALLED for file: {}", image.getOriginalFilename());

        try {
            // Input validation
            if (image.isEmpty()) {
                logger.warn("Hide request failed: Empty image file provided.");
                return ResponseEntity.badRequest().body(createErrorResponse("Image file is required"));
            }
            if (message == null || message.trim().isEmpty()) {
                logger.warn("Hide request failed: Message is empty.");
                return ResponseEntity.badRequest().body(createErrorResponse("Message is required"));
            }
            if (key == null || key.trim().isEmpty()) {
                logger.warn("Hide request failed: Encryption key is empty.");
                return ResponseEntity.badRequest().body(createErrorResponse("Encryption key is required"));
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.equals("image/png")) {
                logger.warn("Hide request failed: Invalid file type '{}'", contentType);
                return ResponseEntity.badRequest().body(createErrorResponse("Only PNG images are supported"));
            }

            logger.info("Input validation passed for hide request.");

            // Hide message using service
            byte[] stegoImageBytes = stegoService.hideMessage(image.getBytes(), message.trim(), key.trim());

            // Prepare response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "encoded_image.png");
            headers.setContentLength(stegoImageBytes.length);

            logger.info("HIDE MESSAGE API COMPLETED SUCCESSFULLY. Returning stego-image of size {} bytes.", stegoImageBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(stegoImageBytes);

        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during hide request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Critical error in hide message API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to hide message: " + e.getMessage()));
        }
    }

    /**
     * Reveal a hidden message from a stego-image
     * POST /api/reveal
     * @param image Stego-image PNG file (multipart/form-data)
     * @param key AES decryption key (form parameter)
     * @return JSON response with revealed message
     */
    @PostMapping(value = "/reveal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> revealMessage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("key") String key) {

        logger.info("POST /api/reveal - REVEAL MESSAGE API CALLED for file: {}", image.getOriginalFilename());

        try {
            // Input validation
            if (image.isEmpty()) {
                logger.warn("Reveal request failed: Empty image file provided.");
                return ResponseEntity.badRequest().body(createErrorResponse("Image file is required"));
            }
            if (key == null || key.trim().isEmpty()) {
                logger.warn("Reveal request failed: Decryption key is empty.");
                return ResponseEntity.badRequest().body(createErrorResponse("Decryption key is required"));
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.equals("image/png")) {
                logger.warn("Reveal request failed: Invalid file type '{}'", contentType);
                return ResponseEntity.badRequest().body(createErrorResponse("Only PNG images are supported"));
            }

            logger.info("Input validation passed for reveal request.");

            // Reveal message using service
            String revealedMessage = stegoService.revealMessage(image.getBytes(), key.trim());

            // Create JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", revealedMessage);
            response.put("timestamp", System.currentTimeMillis());
            response.put("messageLength", revealedMessage.length());

            logger.info("REVEAL MESSAGE API COMPLETED SUCCESSFULLY. Revealed message length: {}", revealedMessage.length());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during reveal request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Critical error in reveal message API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to reveal message: " + e.getMessage()));
        }
    }

    /**
     * Get image capacity information
     * POST /api/capacity
     * @param image PNG image file (multipart/form-data)
     * @return JSON response with capacity information
     */
    @PostMapping(value = "/capacity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getImageCapacity(@RequestParam("image") MultipartFile image) {
        logger.info("POST /api/capacity - CAPACITY CHECK API CALLED for file: {}", image.getOriginalFilename());

        try {
            // Input validation
            if (image.isEmpty()) {
                logger.warn("Capacity check failed: Empty image file provided.");
                return ResponseEntity.badRequest().body(createErrorResponse("Image file is required"));
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.equals("image/png")) {
                logger.warn("Capacity check failed: Invalid file type '{}'", contentType);
                return ResponseEntity.badRequest().body(createErrorResponse("Only PNG images are supported"));
            }

            logger.info("Input validation passed for capacity check.");

            // Get capacity using service
            int capacityInCharacters = stegoService.getImageCapacity(image.getBytes());

            // Create JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("capacityCharacters", capacityInCharacters);
            response.put("timestamp", System.currentTimeMillis());

            logger.info("Capacity check completed: {} characters", capacityInCharacters);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Critical error in capacity check API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to check capacity: " + e.getMessage()));
        }
    }

    /**
     * Helper method to create consistent error responses
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
