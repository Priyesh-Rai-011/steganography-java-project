package com.stegosecure.util;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

/**
 * LSB Steganography - The main code
 * */
public class ImageSteganographyUtil {
    private static final String MESSAGE_DELIMITER = "###EOM###";
    public static int getMaxCapacity(BufferedImage image) {
        // 3 bits per pixel (RGB channels), minus space for delimiter
        int totalBits = image.getWidth() * image.getHeight() * 3;
        int delimiterBits = MESSAGE_DELIMITER.getBytes(StandardCharsets.UTF_8).length * 8;
        return totalBits - delimiterBits;
    }

    /**
     * Set the least significant bit of a color component */
    private static int setBit(int colorComponent, boolean bit) {
        if (bit) {
            return colorComponent | 1;        // Set LSB to 1 (OR with 00000001)
        } else {
            return colorComponent & 0xFE;     // Set LSB to 0 (AND with 11111110)
        }
    }

    /**
     * Get the least significant bit of a color component  */
    private static char getBit(int colorComponent) {
        return (colorComponent & 1) == 1 ? '1' : '0';  // AND with 00000001
    }
    /**
     * Convert binary string to text string */
    private static String binaryToString(String binaryString) {
        StringBuilder result = new StringBuilder();

        // Process 8 bits at a time to form characters
        for (int i = 0; i < binaryString.length() - 7; i += 8) {
            String binaryByte = binaryString.substring(i, i + 8);
            int charValue = Integer.parseInt(binaryByte, 2);  // Convert binary to integer
            result.append((char) charValue);                 // Convert integer to character
        }

        return result.toString();
    }
// =====================================================================================================================
    public static BufferedImage hideMessage(BufferedImage originalImage, String message) throws Exception{
        try {
            System.out.println("---------------------------------------");
            System.out.println("----- Image size: " + originalImage.getWidth() + "X" + originalImage.getHeight()+"\n----- Message length: " + message.length() + " characters");
            System.out.println("---------------------------------------");
            String msgDelimiter = message + MESSAGE_DELIMITER;
            byte[] msgBytes = msgDelimiter.getBytes(StandardCharsets.UTF_8);
//            System.out.println("Total bytes to hide =  " + msgBytes.length); // just for testing purpose
//  Creating a perfect copy of the original image to convert it into a stego image
            int W = originalImage.getWidth(), H = originalImage.getHeight(), T = originalImage.getType();
            BufferedImage stegoImage = new BufferedImage(W,H,T);
//  Copying each and every pixel from the original image
            for (int y = 0; y < H; y++){
                for (int x=0; x<originalImage.getWidth(); x++) {
                    stegoImage.setRGB(x, y, originalImage.getRGB(x, y));    }}
//            System.out.println("Stego image is copied successfully"); // just for testing purpose
//  Converting each byte (Eg :- A,s,b, ,  etc..)
            StringBuilder binaryMessage = new StringBuilder();
            for (byte b : msgBytes){
//                String binaryByte = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ','0');
                String binaryByte = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                binaryMessage.append(binaryByte);
            }
            System.out.println("Binary message length : "+binaryMessage.length()+" bits");
            int maxCap = getMaxCapacity(stegoImage);
            if (binaryMessage.length() > maxCap){
                throw new IllegalArgumentException("❌ Message too large! Need " + binaryMessage.length() + " bits, but image only has " + maxCap + " bits available");
            }
            System.out.println("✅ Image has enough capacity: " + maxCap + " bits available");
//  ----------------------------------------------
//  Storing binary message in the image lsb
//  ----------------------------------------------
            int messageIndex = 0, pixelsModified = 0;
            outerLoop:
            for (int y = 0; y < stegoImage.getHeight() && messageIndex < binaryMessage.length(); y++) {
                for (int x = 0; x < stegoImage.getWidth() && messageIndex < binaryMessage.length(); x++) {
                    int pixel = stegoImage.getRGB(x, y);

                    // Extract ARGB components (Alpha, Red, Green, Blue)
                    int alpha = (pixel >> 24) & 0xFF;  // Alpha channel (transparency)
                    int red = (pixel >> 16) & 0xFF;    // Red channel
                    int green = (pixel >> 8) & 0xFF;   // Green channel
                    int blue = pixel & 0xFF;           // Blue channel

                    // Hide message bits in LSBs of RGB channels (skip alpha for PNG compatibility)
                    if (messageIndex < binaryMessage.length()) {
                        red = setBit(red, binaryMessage.charAt(messageIndex++) == '1');
                    }
                    if (messageIndex < binaryMessage.length()) {
                        green = setBit(green, binaryMessage.charAt(messageIndex++) == '1');
                    }
                    if (messageIndex < binaryMessage.length()) {
                        blue = setBit(blue, binaryMessage.charAt(messageIndex++) == '1');
                    }

                    // Reconstruct the pixel with modified LSBs
                    int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    stegoImage.setRGB(x, y, newPixel);
                    pixelsModified++;

                    // Break when message is fully hidden
                    if (messageIndex >= binaryMessage.length()) {
                        break outerLoop;
                    }
                }
            }
            System.out.println("----- Modified "+pixelsModified+" pixels");
            System.out.println("✅ Message hidden successfully!");

            return stegoImage;
        }
        catch (Exception e){
            throw new Exception("----- Error hiding message in image : " + e.getMessage(), e);
        }
    }
    public static String revealMessage(BufferedImage stegoImage) throws Exception {
        try {
            System.out.println("🔍 Extracting hidden message from image...");
            System.out.println("📏 Image size: " + stegoImage.getWidth() + "x" + stegoImage.getHeight());

            StringBuilder binaryMessage = new StringBuilder();
            int width = stegoImage.getWidth();
            int height = stegoImage.getHeight();
            int pixelsProcessed = 0;

            // Extract LSBs from image pixels
            outerLoop:
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = stegoImage.getRGB(x, y);

                    // Extract RGB components
                    int red = (pixel >> 16) & 0xFF;
                    int green = (pixel >> 8) & 0xFF;
                    int blue = pixel & 0xFF;

                    // Extract LSBs from RGB channels
                    binaryMessage.append(getBit(red));
                    binaryMessage.append(getBit(green));
                    binaryMessage.append(getBit(blue));

                    pixelsProcessed++;

                    // Check if we have enough bits to form complete bytes and look for delimiter
                    if (binaryMessage.length() % 8 == 0 && binaryMessage.length() >= 8) {
                        String currentMessage = binaryToString(binaryMessage.toString());
                        if (currentMessage.contains(MESSAGE_DELIMITER)) {
                            // Found delimiter! Extract message without delimiter
                            int delimiterIndex = currentMessage.indexOf(MESSAGE_DELIMITER);
                            String extractedMessage = currentMessage.substring(0, delimiterIndex);

                            System.out.println("✅ Found message delimiter!");
                            System.out.println("📦 Processed " + pixelsProcessed + " pixels");
                            System.out.println("💬 Extracted message length: " + extractedMessage.length() + " characters");
                            System.out.println("✅ Message revealed successfully!");

                            return extractedMessage;
                        }
                    }

                    // Safety check to prevent infinite loops
                    if (binaryMessage.length() > width * height * 3) {
                        break outerLoop;
                    }
                }
            }

            // If no delimiter found, try to return what we have (fallback)
            System.out.println("⚠️  No delimiter found, trying fallback extraction...");
            if (binaryMessage.length() > 0) {
                String message = binaryToString(binaryMessage.toString());
                if (message.contains(MESSAGE_DELIMITER)) {
                    int delimiterIndex = message.indexOf(MESSAGE_DELIMITER);
                    return message.substring(0, delimiterIndex);
                }
            }

            throw new Exception("❌ No valid hidden message found in the image");

        } catch (Exception e) {
            throw new Exception("❌ Error revealing message from image: " + e.getMessage(), e);
        }
    }
// =====================================================================================================================
// TESTING METHOD
    public static void testSteganography() {
        System.out.println("\n🧪 Testing LSB Steganography...");

        try {
            // Create a simple test image (100x100 pixels, RGB)
            BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

            // Fill with random colors
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    int red = (int)(Math.random() * 256);
                    int green = (int)(Math.random() * 256);
                    int blue = (int)(Math.random() * 256);
                    int rgb = (red << 16) | (green << 8) | blue;
                    testImage.setRGB(x, y, rgb);
                }
            }

            System.out.println("🖼️  Created test image: 100x100 pixels");

            // Test message
            String testMessage = "This is a secret message for testing steganography!";
            System.out.println("📝 Test Message: " + testMessage);

            // Test capacity
            int capacity = getMaxCapacity(testImage);
            System.out.println("📊 Image capacity: " + capacity + " bits (" + (capacity/8) + " characters)");

            // Hide message
            BufferedImage stegoImage = hideMessage(testImage, testMessage);
            System.out.println("🔒 Message hidden in stego-image");

            // Reveal message
            String revealedMessage = revealMessage(stegoImage);
            System.out.println("🔓 Revealed Message: " + revealedMessage);

            // Verify
            boolean success = testMessage.equals(revealedMessage);
            System.out.println("✅ Steganography Test: " + (success ? "PASSED" : "FAILED"));

            if (success) {
                System.out.println("🎉 LSB Steganography is working perfectly!");
            } else {
                System.out.println("❌ Something went wrong with steganography");
            }

        } catch (Exception e) {
            System.out.println("❌ Steganography Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("="+"=".repeat(60));
    }
}
