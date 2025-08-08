package com.stegosecure.util;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
/**
 * AES Encryption/Decryption Utility class
 * Handles all Crypto operations for secure message in image transfer
 */

public class AESUtil {
    private static final String ALGO = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static SecretKeySpec createSecretKey(String key) throws Exception {
        try {
            // Hash the key using SHA-256 to get consistent 256-bit output
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(key.getBytes(StandardCharsets.UTF_8));
            // Use only first 16 bytes (128 bits) for AES-128
            keyBytes = Arrays.copyOf(keyBytes, 16);
            return new SecretKeySpec(keyBytes, ALGO);
        } catch (Exception e) {     throw new Exception("---- AES Key creation failed: " + e.getMessage(), e);
        }
    }
    // ENCRYPTION
    public static String encrypt(String p_text, String key) throws Exception{
        try {
            SecretKeySpec secretkey = createSecretKey(key); // proper 16 bits aes key from user's string
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE,secretkey);
            byte[] encryptedBytes = cipher.doFinal(p_text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
            // returning as base 64 string
        }
        catch (Exception e){    throw new Exception("##### AES Encryption failed :  " + e.getMessage());
        }
    }
    // DECRYPTION
    public static String decrypt(String enc_text, String key) throws Exception{
        try{
            SecretKeySpec secretkey = createSecretKey(key); // creates the same secret key as CREATED in the encrypted function
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE,secretkey);
            // decoding Base 64 string message from encrypt function
            byte[] enc_bytes = Base64.getDecoder().decode(enc_text);
            // decrypting the message
            byte[] dec_bytes = cipher.doFinal(enc_bytes);
            return new String(dec_bytes);
        }
        catch (Exception e){    throw new Exception("#### AES Decryption failed"+e.getMessage());}
    }




    // 🧪 TESTING METHOD - We'll use this to verify encryption works
    public static void testEncryption() {
        System.out.println("\n🧪 Testing AES Encryption...");

        try {
            // Test data
            String originalMessage = "Hello World! This is a secret message.";
//            String testKey = "mySecretKey12345";
            String testKey = "PriyeshRaiJavaSDEAWSDeveloper";

            System.out.println("📝 Original Message: " + originalMessage);
            System.out.println("🔑 Test Key: " + testKey);

            // Test encryption
            String encrypted = encrypt(originalMessage, testKey);
            System.out.println("🔒 Encrypted: " + encrypted);

            // Test decryption
            String decrypted = decrypt(encrypted, testKey);
            System.out.println("🔓 Decrypted: " + decrypted);

            // Verify they match
            boolean success = originalMessage.equals(decrypted);
            System.out.println("✅ Encryption Test: " + (success ? "PASSED" : "FAILED"));

            if (success) {
                System.out.println("🎉 AES Utility is working perfectly!");
            } else {
                System.out.println("❌ Something went wrong with encryption/decryption");
            }

        } catch (Exception e) {
            System.out.println("❌ AES Test Failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=" + "=".repeat(50));
    }

    // 🧪 ADDITIONAL TEST - Test with different keys
    public static void testWithDifferentKeys() {
        System.out.println("\n🧪 Testing with Different Keys...");

        try {
            String message = "Secret Message";
            String key1 = "password123";
            String key2 = "differentPassword";

            // Encrypt with key1
            String encrypted = encrypt(message, key1);
            System.out.println("🔒 Encrypted with key1: " + encrypted);

            // Try to decrypt with key1 (should work)
            String decrypted1 = decrypt(encrypted, key1);
            System.out.println("🔓 Decrypted with key1: " + decrypted1);
            System.out.println("✅ Same key test: " + (message.equals(decrypted1) ? "PASSED" : "FAILED"));

            // Try to decrypt with key2 (should fail)
            try {
                String decrypted2 = decrypt(encrypted, key2);
                System.out.println("❌ Different key should have failed but didn't!");
            } catch (Exception e) {
                System.out.println("✅ Different key test: PASSED (correctly failed)");
            }

        } catch (Exception e) {
            System.out.println("❌ Key test failed: " + e.getMessage());
        }

        System.out.println("=" + "=".repeat(50));
    }
}
