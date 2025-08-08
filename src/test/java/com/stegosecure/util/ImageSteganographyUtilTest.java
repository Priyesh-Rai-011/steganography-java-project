package com.stegosecure.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageSteganographyUtilTest {

    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        // Create a blank 100x100 pixel image for each test to ensure tests are independent
        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }

    @Test
    void testHideAndRevealMessageSuccess() throws Exception {
        // Given
        String secretMessage = "JUnit testing for steganography is working!";

        // When
        BufferedImage stegoImage = ImageSteganographyUtil.hideMessage(testImage, secretMessage);
        String revealedMessage = ImageSteganographyUtil.revealMessage(stegoImage);

        // Then
        assertNotNull(stegoImage, "The stego-image should not be null.");
        assertEquals(secretMessage, revealedMessage, "The revealed message must match the original secret message.");
    }

    @Test
    void testMessageTooLargeForImageThrowsException() {
        // Given
        // Create a very small 1x1 image
        BufferedImage smallImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        String longMessage = "This message is definitely too long to be hidden in a tiny 1x1 pixel image.";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> ImageSteganographyUtil.hideMessage(smallImage, longMessage),
                "Hiding a message that exceeds image capacity should throw an IllegalArgumentException.");
    }

    @Test
    void testRevealFromOriginalImageThrowsException() {
        // Given
        // An original image with no hidden message
        BufferedImage originalImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);

        // When & Then
        assertThrows(Exception.class, () -> ImageSteganographyUtil.revealMessage(originalImage),
                "Attempting to reveal a message from an image with no hidden content should throw an exception.");
    }
}
