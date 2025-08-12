# Application & API Documentation

This document provides a detailed overview of the application's architecture, the technology stack, and the REST API endpoints.

---

### ► Technology Stack

| Component           | Technology                                                                                           | Purpose                                             |
| :------------------ | :--------------------------------------------------------------------------------------------------- | :-------------------------------------------------- |
| **Core Language** | **Java 17** | Powers all backend logic.                           |
| **Backend Framework**| **Spring Boot 3.2.1** | Handles server setup, web routing, and DI.         |
| **Build Tool** | **Maven** | Manages dependencies and the build lifecycle.       |
| **Web Server** | **Embedded Tomcat** | Handles all incoming HTTP requests.                 |
| **Frontend** | **HTML5, CSS3, JavaScript (ES6+)** | Provides a clean, framework-free user interface.    |
| **Cryptography** | **Java Cryptography Architecture (JCA)** | Used for robust **AES-GCM** encryption.               |
| **Image Handling** | **`java.awt.image.BufferedImage`** | Enables direct, low-level pixel access for LSB.     |

---

### ► Architecture Deep Dive

The Java source code follows a standard layered architecture to promote separation of concerns.

* `com.stegosecure`
    * **`StegoSecureApplication.java`**: The main entry point for the Spring Boot application.
    * `controller`
        * `StegoController.java`: Defines all REST API endpoints, handles input validation, and maps HTTP requests to the service layer.
    * `service`
        * `StegoService.java`: Contains the core business logic, orchestrating calls to the encryption and steganography utilities.
    * `util`
        * `AESUtil.java`: A helper class dedicated to performing AES encryption and decryption.
        * `ImageSteganographyUtil.java`: A helper class for hiding and revealing data within image pixels.

---

<img width="842" height="731" alt="Image" src="https://github.com/user-attachments/assets/59a3cadd-fbfc-4aa9-9ea1-c82b4df33aa7" />

---

### ► API Endpoints

The backend exposes the following REST API endpoints. All endpoints expect `multipart/form-data`.

#### Hide a Message
Encrypts a message and hides it in an image.

* **Endpoint**: `POST /api/hide`
* **Request**: `image` (file), `message` (text), `key` (text)
* **Success Response**: `200 OK` with the new PNG image file.
* **cURL Example**:
    ```sh
    curl -X POST http://localhost:8080/api/hide \
         -F "image=@/path/to/your/image.png" \
         -F "message=This is a secret" \
         -F "key=supersecret" \
         --output stego_image.png
    ```

#### Reveal a Message
Extracts and decrypts a message from an image.

* **Endpoint**: `POST /api/reveal`
* **Request**: `image` (file), `key` (text)
* **Success Response**: `200 OK` with a JSON object: `{"message": "..."}`
* **cURL Example**:
    ```sh
    curl -X POST http://localhost:8080/api/reveal \
         -F "image=@/path/to/stego_image.png" \
         -F "key=supersecret"
    ```

#### Check Image Capacity
Checks the maximum number of characters an image can hold.

* **Endpoint**: `POST /api/capacity`
* **Request**: `image` (file)
* **Success Response**: `200 OK` with JSON: `{"capacityCharacters": 120774}`
* **cURL Example**:
    ```sh
    curl -X POST http://localhost:8080/api/capacity \
         -F "image=@/path/to/your/image.png"
    ```
