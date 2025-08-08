# Application & API Documentation

This document provides a detailed overview of the primary application source code, the technologies used, and the REST API endpoints.

## 🛠️ Technology Stack

* **Backend Framework:** **Spring Boot 3.2.1** provides the foundation for the application, handling server setup, dependency injection, and web routing.
* **Core Language:** **Java 17** is used for all backend logic.
* **Web Server:** An embedded **Tomcat** server is used to handle HTTP requests.
* **Frontend:** The UI is built with standard **HTML5**, **CSS3**, and modern **JavaScript (ES6+)**. No external frontend frameworks are used.
* **Build Tool:** **Maven** manages the project's dependencies and build lifecycle, as defined in the `pom.xml`.
* **Core Algorithms:**
    * **Cryptography:** The standard Java Cryptography Architecture (`javax.crypto`) is used for robust AES encryption.
    * **Steganography:** Image manipulation is handled by `java.awt.image.BufferedImage` for direct, low-level pixel access required for LSB steganography.

## 📂 Source Code Structure

The Java source code follows a standard layered architecture to promote separation of concerns.

* `com.stegosecure`
    * **`StegoSecureApplication.java`**: The main entry point for the Spring Boot application.
    * **`/controller`**:
        * `StegoController.java`: Defines all REST API endpoints (`/api/hide`, `/api/reveal`, etc.), handles input validation, and maps HTTP requests to the appropriate service methods.
    * **`/service`**:
        * `StegoService.java`: Contains the core business logic. It orchestrates the entire process, calling the utility classes for encryption and steganography and handling any errors that arise.
    * **`/util`**:
        * `AESUtil.java`: A helper class dedicated to performing AES encryption and decryption.
        * `ImageSteganographyUtil.java`: A helper class containing the low-level logic for hiding and revealing data within the least significant bits of image pixels.

## 📡 API Endpoints

The backend exposes the following REST API endpoints.

| Method | Endpoint         | Description                                        | Request Body (form-data)                             | Success Response                                      |
| :----- | :--------------- | :------------------------------------------------- | :--------------------------------------------------- | :---------------------------------------------------- |
| `POST` | `/api/hide`      | Encrypts and hides a message in an image.          | `image` (file), `message` (text), `key` (text)       | `200 OK` with the new PNG image file for download.      |
| `POST` | `/api/reveal`    | Extracts and decrypts a message from an image.     | `image` (file), `key` (text)                         | `200 OK` with a JSON object: `{"message": "..."}`       |
| `POST` | `/api/capacity`  | Checks the maximum characters an image can hold.   | `image` (file)                                       | `200 OK` with JSON: `{"capacityCharacters": 120774}`   |
| `GET`  | `/api/test`      | A test endpoint to verify the server is running.   | (None)                                               | `200 OK` with a JSON status message.                    |