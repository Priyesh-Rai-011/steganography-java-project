# Testing Strategy

This document outlines the testing strategy for the StegoSecure application, ensuring the reliability and correctness of its core functionalities.

## 🧪 Testing Approach

The project employs a multi-layered testing approach, combining unit tests for individual components and integration tests for the complete workflow.

* **Unit Tests**: These are focused on a single class or component to verify its logic in isolation. They are fast, reliable, and form the foundation of our testing pyramid.
* **Integration Tests**: These tests verify that different components of the application work together as expected. For this project, the primary integration test ensures that a message that is hidden can be successfully revealed, covering the entire service layer.

## ✅ Key Test Classes

The tests are located in the `src/test/java/com/stegosecure` directory, mirroring the main source structure.

* **`util/AESUtilTest.java`**:
    * Verifies that the `AESUtil` class can correctly encrypt and decrypt messages.
    * Ensures that decryption fails when the wrong key is used, which is a critical security check.

* **`util/ImageSteganographyUtilTest.java`**:
    * Tests the core LSB logic by hiding and then revealing a message from a test image.
    * Confirms that an exception is thrown if a message is too large for the carrier image.

* **`service/StegoServiceTest.java`**:
    * This is an **integration test** that uses the live Spring Boot application context (`@SpringBootTest`).
    * It tests the entire `hideMessage` and `revealMessage` workflow, ensuring the service correctly coordinates the encryption and steganography utilities.

## ▶️ How to Run Tests

You can run all tests using the standard Maven command from the project's root directory:

```sh
mvn test