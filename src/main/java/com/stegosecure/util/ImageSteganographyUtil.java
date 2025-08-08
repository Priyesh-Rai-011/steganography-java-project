package com.stegosecure.util;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

/**
 * LSB Steganography - The main code
 */
public class ImageSteganographyUtil {
    private static final String MESSAGE_DELIMITER = "###EOM###";

    public static int getMaxCapacity(BufferedImage image) {
        int totalBits = image.getWidth() * image.getHeight() * 3;
        int delimiterBits = MESSAGE_DELIMITER.getBytes(StandardCharsets.UTF_8).length * 8;
        return totalBits - delimiterBits;
    }

    private static int setBit(int colorComponent, boolean bit) {
        return bit ? (colorComponent | 1) : (colorComponent & 0xFE);
    }

    private static char getBit(int colorComponent) {
        return (colorComponent & 1) == 1 ? '1' : '0';
    }

    private static String binaryToString(String binaryString) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= binaryString.length() - 8; i += 8) {
            String binaryByte = binaryString.substring(i, i + 8);
            int charValue = Integer.parseInt(binaryByte, 2);
            result.append((char) charValue);
        }
        return result.toString();
    }

    public static BufferedImage hideMessage(BufferedImage originalImage, String message) throws Exception {
        // *** MODIFICATION START ***
        // Check for capacity at the very beginning to fail fast.
        String messageWithDelimiter = message + MESSAGE_DELIMITER;
        byte[] messageBytes = messageWithDelimiter.getBytes(StandardCharsets.UTF_8);
        int requiredBits = messageBytes.length * 8;
        int maxCapacity = getMaxCapacity(originalImage);

        if (requiredBits > maxCapacity) {
            throw new IllegalArgumentException("❌ Message too large! Need " + requiredBits + " bits, but image only has " + maxCapacity + " bits available");
        }
        // *** MODIFICATION END ***

        try {
            BufferedImage stegoImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    originalImage.getType()
            );
            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {
                    stegoImage.setRGB(x, y, originalImage.getRGB(x, y));
                }
            }

            StringBuilder binaryMessage = new StringBuilder();
            for (byte b : messageBytes) {
                String binaryByte = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                binaryMessage.append(binaryByte);
            }

            int messageIndex = 0;
            outerLoop:
            for (int y = 0; y < stegoImage.getHeight(); y++) {
                for (int x = 0; x < stegoImage.getWidth(); x++) {
                    if (messageIndex >= binaryMessage.length()) {
                        break outerLoop;
                    }
                    int pixel = stegoImage.getRGB(x, y);
                    int alpha = (pixel >> 24) & 0xFF;
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;

                    if (messageIndex < binaryMessage.length()) {
                        red = setBit(red, binaryMessage.charAt(messageIndex++) == '1');
                    }
                    if (messageIndex < binaryMessage.length()) {
                        green = setBit(green, binaryMessage.charAt(messageIndex++) == '1');
                    }
                    if (messageIndex < binaryMessage.length()) {
                        blue = setBit(blue, binaryMessage.charAt(messageIndex++) == '1');
                    }

                    int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    stegoImage.setRGB(x, y, newPixel);
                }
            }
            return stegoImage;
        } catch (Exception e) {
            // This will now only catch unexpected errors, not the capacity error.
            throw new Exception("----- Error hiding message in image: " + e.getMessage(), e);
        }
    }

    public static String revealMessage(BufferedImage stegoImage) throws Exception {
        try {
            StringBuilder binaryMessage = new StringBuilder();
            outerLoop:
            for (int y = 0; y < stegoImage.getHeight(); y++) {
                for (int x = 0; x < stegoImage.getWidth(); x++) {
                    int pixel = stegoImage.getRGB(x, y);
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;

                    binaryMessage.append(getBit(red));
                    binaryMessage.append(getBit(green));
                    binaryMessage.append(getBit(blue));

                    if (binaryMessage.length() % 8 == 0) {
                        String currentMessage = binaryToString(binaryMessage.toString());
                        if (currentMessage.contains(MESSAGE_DELIMITER)) {
                            int delimiterIndex = currentMessage.indexOf(MESSAGE_DELIMITER);
                            return currentMessage.substring(0, delimiterIndex);
                        }
                    }
                }
            }
            throw new Exception("❌ No valid hidden message found in the image");
        } catch (Exception e) {
            throw new Exception("❌ Error revealing message from image: " + e.getMessage(), e);
        }
    }
}
