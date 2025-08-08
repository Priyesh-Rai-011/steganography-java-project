package com.stegosecure.service;

import com.stegosecure.util.AESUtil;
import com.stegosecure.util.ImageSteganographyUtil;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * StegoService - The Core Business Logic
 * Combines AES encryption with LSB steganography for complete security
 */
@Service
public class StegoService {

    /**
     * Hide an encrypted message inside a PNG image
     * @param imageBytes Original PNG image as byte array
     * @param message Plain text message to hide
     * @param encryptionKey AES encryption key
     * @return Stego-image as byte array (PNG format)
     * @throws Exception If hiding process fails
     */
    public byte[] hideMessage(byte[] imageBytes, String message, String encryptionKey) throws Exception {
        try {
            System.out.println("\n🚀 HIDE MESSAGE SERVICE STARTED");
            System.out.println("📝 Message to hide: \"" + message + "\"");
            System.out.println("🔑 Using encryption key: " + encryptionKey);
            System.out.println("📦 Original image size: " + imageBytes.length + " bytes");

            // Step 1: Convert byte array to BufferedImage
            System.out.println("\n📸 Step 1: Loading image...");
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            if (originalImage == null) {
                throw new IllegalArgumentException("❌ Invalid image format! Please use PNG files.");
            }

            System.out.println("✅ Image loaded successfully: " + originalImage.getWidth() + "x" + originalImage.getHeight());
            System.out.println("🎨 Image type: " + getImageTypeString(originalImage.getType()));

            // Step 2: Encrypt the message using AES
            System.out.println("\n🔒 Step 2: Encrypting message...");
            String encryptedMessage = AESUtil.encrypt(message, encryptionKey);
            System.out.println("✅ Message encrypted successfully");
            System.out.println("📏 Encrypted message length: " + encryptedMessage.length() + " characters");

            // Step 3: Check if image has enough capacity
            System.out.println("\n📊 Step 3: Checking image capacity...");
            int maxCapacity = ImageSteganographyUtil.getMaxCapacity(originalImage);
            int requiredBits = encryptedMessage.length() * 8; // 8 bits per character

            System.out.println("📈 Image capacity: " + maxCapacity + " bits (" + (maxCapacity/8) + " characters)");
            System.out.println("📉 Required space: " + requiredBits + " bits (" + encryptedMessage.length() + " characters)");

            if (requiredBits > maxCapacity) {
                throw new IllegalArgumentException("❌ Message too large! Need " + requiredBits + " bits, but image only has " + maxCapacity + " bits available");
            }

            System.out.println("✅ Image has sufficient capacity");

            // Step 4: Hide encrypted message in image using LSB
            System.out.println("\n🎭 Step 4: Hiding encrypted message in image...");
            BufferedImage stegoImage = ImageSteganographyUtil.hideMessage(originalImage, encryptedMessage);
            System.out.println("✅ Message hidden successfully in stego-image");

            // Step 5: Convert BufferedImage back to byte array (PNG format)
            System.out.println("\n💾 Step 5: Converting stego-image to PNG bytes...");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean success = ImageIO.write(stegoImage, "PNG", outputStream);

            if (!success) {
                throw new IOException("❌ Failed to write PNG image");
            }

            byte[] stegoImageBytes = outputStream.toByteArray();
            System.out.println("✅ Stego-image created successfully");
            System.out.println("📦 Final stego-image size: " + stegoImageBytes.length + " bytes");

            // Summary
            System.out.println("\n🎉 HIDE MESSAGE SERVICE COMPLETED SUCCESSFULLY!");
            System.out.println("🔐 Security: Message encrypted with AES-128");
            System.out.println("🎭 Steganography: Hidden using LSB technique");
            System.out.println("👁️ Visibility: Changes are invisible to human eye");
            System.out.println("=" + "=".repeat(60));

            return stegoImageBytes;

        } catch (Exception e) {
            System.out.println("❌ HIDE MESSAGE SERVICE FAILED: " + e.getMessage());
            throw new Exception("Hide message operation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Reveal and decrypt a hidden message from a stego-image
     * @param stegoImageBytes Stego-image as byte array
     * @param encryptionKey AES decryption key (must match the key used for hiding)
     * @return Original plain text message
     * @throws Exception If reveal process fails
     */
    public String revealMessage(byte[] stegoImageBytes, String encryptionKey) throws Exception {
        try {
            System.out.println("\n🔍 REVEAL MESSAGE SERVICE STARTED");
            System.out.println("🔑 Using decryption key: " + encryptionKey);
            System.out.println("📦 Stego-image size: " + stegoImageBytes.length + " bytes");

            // Step 1: Convert byte array to BufferedImage
            System.out.println("\n📸 Step 1: Loading stego-image...");
            BufferedImage stegoImage = ImageIO.read(new ByteArrayInputStream(stegoImageBytes));

            if (stegoImage == null) {
                throw new IllegalArgumentException("❌ Invalid image format! Please use PNG files.");
            }

            System.out.println("✅ Stego-image loaded successfully: " + stegoImage.getWidth() + "x" + stegoImage.getHeight());
            System.out.println("🎨 Image type: " + getImageTypeString(stegoImage.getType()));

            // Step 2: Extract encrypted message from image using LSB
            System.out.println("\n🔍 Step 2: Extracting hidden message from image...");
            String encryptedMessage = ImageSteganographyUtil.revealMessage(stegoImage);
            System.out.println("✅ Encrypted message extracted successfully");
            System.out.println("📏 Extracted encrypted message length: " + encryptedMessage.length() + " characters");

            // Step 3: Decrypt the extracted message using AES
            System.out.println("\n🔓 Step 3: Decrypting extracted message...");
            String originalMessage = AESUtil.decrypt(encryptedMessage, encryptionKey);
            System.out.println("✅ Message decrypted successfully");
            System.out.println("📝 Original message: \"" + originalMessage + "\"");

            // Summary
            System.out.println("\n🎉 REVEAL MESSAGE SERVICE COMPLETED SUCCESSFULLY!");
            System.out.println("🔍 Steganography: Message extracted using LSB technique");
            System.out.println("🔓 Security: Message decrypted with AES-128");
            System.out.println("✅ Result: Original message recovered perfectly");
            System.out.println("=" + "=".repeat(60));

            return originalMessage;

        } catch (Exception e) {
            System.out.println("❌ REVEAL MESSAGE SERVICE FAILED: " + e.getMessage());
            throw new Exception("Reveal message operation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get image capacity for hiding messages
     * @param imageBytes Image as byte array
     * @return Maximum number of characters that can be hidden
     * @throws Exception If capacity calculation fails
     */
    public int getImageCapacity(byte[] imageBytes) throws Exception {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image == null) {
                throw new IllegalArgumentException("❌ Invalid image format!");
            }

            int capacityInBits = ImageSteganographyUtil.getMaxCapacity(image);
            return capacityInBits / 8; // Convert bits to characters

        } catch (Exception e) {
            throw new Exception("Failed to calculate image capacity: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to get human-readable image type
     * @param imageType BufferedImage type constant
     * @return Human-readable string
     */
    private String getImageTypeString(int imageType) {
        switch (imageType) {
            case BufferedImage.TYPE_INT_RGB: return "RGB (24-bit)";
            case BufferedImage.TYPE_INT_ARGB: return "ARGB (32-bit with transparency)";
            case BufferedImage.TYPE_INT_ARGB_PRE: return "ARGB Pre-multiplied";
            case BufferedImage.TYPE_3BYTE_BGR: return "BGR (24-bit)";
            case BufferedImage.TYPE_4BYTE_ABGR: return "ABGR (32-bit)";
            case BufferedImage.TYPE_BYTE_GRAY: return "Grayscale (8-bit)";
            case BufferedImage.TYPE_USHORT_GRAY: return "Grayscale (16-bit)";
            default: return "Unknown (" + imageType + ")";
        }
    }

    // 🧪 TESTING METHOD
    public void testStegoService() {
        System.out.println("\n🧪 Testing Complete StegoService...");

        try {
            // Create test image
            BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < 200; y++) {
                for (int x = 0; x < 200; x++) {
                    int red = (int)(Math.random() * 256);
                    int green = (int)(Math.random() * 256);
                    int blue = (int)(Math.random() * 256);
                    int rgb = (red << 16) | (green << 8) | blue;
                    testImage.setRGB(x, y, rgb);
                }
            }

            // Convert to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(testImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            System.out.println("🖼️  Created test image: 200x200 pixels (" + imageBytes.length + " bytes)");

            // Test data
            String testMessage = "This is a secret message that will be encrypted and hidden in the image!";
            String testKey = "mySecretPassword123";

            System.out.println("📝 Test Message: " + testMessage);
            System.out.println("🔑 Test Key: " + testKey);

            // Test capacity
            int capacity = getImageCapacity(imageBytes);
            System.out.println("📊 Image capacity: " + capacity + " characters");

            // Test hide message
            byte[] stegoImageBytes = hideMessage(imageBytes, testMessage, testKey);
            System.out.println("🔒 Message hidden in stego-image (" + stegoImageBytes.length + " bytes)");

            // Test reveal message
            String revealedMessage = revealMessage(stegoImageBytes, testKey);
            System.out.println("🔓 Revealed message: " + revealedMessage);

            // Verify
            boolean success = testMessage.equals(revealedMessage);
            System.out.println("✅ Complete StegoService Test: " + (success ? "PASSED" : "FAILED"));

            if (success) {
                System.out.println("🎉 StegoService is working perfectly!");
                System.out.println("🔐 Encryption + 🎭 Steganography = 💪 Complete Security!");
            } else {
                System.out.println("❌ Something went wrong with the complete service");
            }

        } catch (Exception e) {
            System.out.println("❌ StegoService Test Failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=" + "=".repeat(60));
    }
}