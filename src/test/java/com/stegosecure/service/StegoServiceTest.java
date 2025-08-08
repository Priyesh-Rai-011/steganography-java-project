package com.stegosecure.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StegoServiceTest {

    @Autowired
    private StegoService stegoService;

    private byte[] testImageBytes;

    @BeforeEach
    void setUp() throws IOException {
        // Create a blank 200x200 image and convert it to a byte array before each test
        BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "png", baos);
        testImageBytes = baos.toByteArray();
    }

    @Test
    void testHideAndRevealIntegrationSuccess() throws Exception {
        // Given
        String originalMessage = "Full integration test for StegoService!";
        String key = "strong-password-for-testing";

        // When
        // 1. Hide the message
        byte[] stegoImageBytes = stegoService.hideMessage(testImageBytes, originalMessage, key);

        // 2. Reveal the message
        String revealedMessage = stegoService.revealMessage(stegoImageBytes, key);

        // Then
        assertNotNull(stegoImageBytes, "The resulting stego image bytes should not be null.");
        assertTrue(stegoImageBytes.length > 0, "The stego image byte array should not be empty.");
        assertEquals(originalMessage, revealedMessage, "The revealed message should match the original message.");
    }

    @Test
    void testRevealWithWrongKeyFails() throws Exception {
        // Given
        String originalMessage = "Testing decryption failure.";
        String correctKey = "correct-key-123";
        String wrongKey = "wrong-key-456";

        // When
        byte[] stegoImageBytes = stegoService.hideMessage(testImageBytes, originalMessage, correctKey);

        // Then
        assertThrows(Exception.class, () -> {
            stegoService.revealMessage(stegoImageBytes, wrongKey);
        }, "Revealing a message with the wrong key should throw an exception.");
    }
}
