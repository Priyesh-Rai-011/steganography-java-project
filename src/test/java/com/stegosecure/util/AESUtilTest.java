package com.stegosecure.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AESUtilTest {

    @Test
    void testEncryptionDecryptionSuccess() throws Exception {
        // Given
        String originalMessage = "This is a top secret message for the JUnit test!";
        String key = "mySuperSecretKey123";

        // When
        String encrypted = AESUtil.encrypt(originalMessage, key);
        String decrypted = AESUtil.decrypt(encrypted, key);

        // Then
        assertNotNull(encrypted, "Encrypted string should not be null.");
        assertNotEquals(originalMessage, encrypted, "Encrypted message should not be the same as the original.");
        assertEquals(originalMessage, decrypted, "Decrypted message should match the original.");
    }

    @Test
    void testDecryptionWithWrongKeyFails() throws Exception {
        // Given
        String originalMessage = "Another secret message.";
        String correctKey = "correct-key";
        String wrongKey = "wrong-key";

        // When
        String encrypted = AESUtil.encrypt(originalMessage, correctKey);

        // Then
        // Assert that trying to decrypt with the wrong key throws an exception
        assertThrows(Exception.class, () -> AESUtil.decrypt(encrypted, wrongKey),
                "Decrypting with the wrong key should throw an exception.");
    }

    @Test
    void testEncryptWithEmptyMessage() throws Exception {
        // Given
        String emptyMessage = "";
        String key = "a-key";

        // When
        String encrypted = AESUtil.encrypt(emptyMessage, key);
        String decrypted = AESUtil.decrypt(encrypted, key);

        // Then
        assertNotNull(encrypted, "Encrypted string of an empty message should not be null.");
        assertEquals(emptyMessage, decrypted, "Decrypting an encrypted empty message should result in an empty string.");
    }
}
