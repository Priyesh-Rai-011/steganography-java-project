# Testing Strategy

This document outlines the testing strategy for the StegoSecure application, ensuring the reliability, security, and correctness of its core functionalities.

---

### ► Our Approach to Quality

The project employs a multi-layered testing approach, combining fine-grained **unit tests** for component logic and comprehensive **integration tests** for the complete workflow. This strategy ensures that individual parts work correctly and that they function together as a whole.

---

### ✅ Test Suite Breakdown

The tests are located in `src/test/java/com/stegosecure`, mirroring the main source structure.

| Test Class                          | Type            | Purpose                                                                                                                              |
| :---------------------------------- | :-------------- | :----------------------------------------------------------------------------------------------------------------------------------- |
| **`AESUtilTest.java`** | Unit Test       | <li>Verifies successful encryption and decryption.</li><li>Ensures decryption **fails** with an incorrect key (critical security check).</li> |
| **`ImageSteganographyUtilTest.java`**| Unit Test       | <li>Tests the core LSB logic for hiding and revealing data.</li><li>Confirms an exception is thrown if a message is too large.</li>      |
| **`StegoServiceTest.java`** | Integration Test| <li>Uses `@SpringBootTest` to test the full application context.</li><li>Validates the end-to-end `hide` and `reveal` workflow.</li>     |

---

### ► How to Run Tests

You can execute the complete test suite using the standard Maven command from the project's root directory. The build will only succeed if all tests pass.

```sh
mvn test
