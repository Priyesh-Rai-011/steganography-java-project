# StegoSecure: A Steganography & Encryption Tool

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java-blue?style=for-the-badge&logo=java" alt="Language: Java">
  <img src="https://img.shields.io/badge/Framework-Spring%20Boot-green?style=for-the-badge&logo=spring" alt="Framework: Spring Boot">
  <img src="https://img.shields.io/badge/Build-Maven-red?style=for-the-badge&logo=apache-maven" alt="Build: Maven">
  <img src="https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge" alt="License: MIT">
</p>

A robust web application for hiding encrypted data within images using Least Significant Bit (LSB) steganography and modern AES-GCM encryption.

---


### ► Live Deployments

The application is deployed and publicly accessible on the following platforms:

1.  **AWS Elastic Container Service**
    * **Live URL:** [http://stego-secure-alb-1248487578.ap-south-1.elb.amazonaws.com/](http://stego-secure-alb-1248487578.ap-south-1.elb.amazonaws.com/)

2.  **Render**
    * **Live URL:** [https://stegosecure.onrender.com/](https://stegosecure.onrender.com/)


---

#### ► Preview
![Image](https://github.com/user-attachments/assets/a32c1ab4-9a5a-4f6c-ae8c-2a30d8e58ea3)

---

### ► Key Features

* **Secure by Design**: Utilizes **AES-GCM**, a modern authenticated encryption cipher that protects against common cryptographic attacks.
* **LSB Steganography**: Implements low-level pixel manipulation to embed encrypted data directly into PNG image files.
* **RESTful API**: Exposes a clear and intuitive API for all core functions: hiding, revealing, and checking image capacity.
* **Live Capacity Calculation**: A frontend feature that instantly informs the user of the maximum data an image can securely store.

---

### ► Project Architecture

StegoSecure uses a classic three-tier architecture to ensure a clean separation of concerns.

* **Controller Layer (`/controller`)**: Defines REST endpoints, validates all incoming HTTP requests and user input, and routes calls to the service layer.
* **Service Layer (`/service`)**: Orchestrates the core business logic, coordinating the encryption and steganography utilities to perform `hide` and `reveal` operations.
* **Utility Layer (`/util`)**: Contains specialized, single-responsibility classes for low-level tasks like AES encryption (`AESUtil`) and pixel manipulation (`ImageSteganographyUtil`).
* **Architectural Diagram** for your reference -
<img width="1661" height="946" alt="Image" src="https://github.com/user-attachments/assets/0865f368-2745-4649-a6e9-7efd02c1ac5b" />

---

## ► Getting Started Locally

**Prerequisites:**
* Java 17+
* Apache Maven

1.  **Clone & Build:**
    ```sh
    git clone https://github.com/Priyesh-Rai-011/steganography-java-project.git
    cd steganography-java-project
    mvn clean install
    ```

2.  **Run the Application:**
    ```sh
    mvn spring-boot:run
    ```

3.  **Access in Browser:**
    Navigate to **[http://localhost:8080](http://localhost:8080)**

---

### ► Detailed Documentation

* **[Application & API Documentation](./src/main/README.md)**: Details on the technology stack, architecture, and API endpoints.
* **[Testing Strategy](./src/test/README.md)**: An overview of the project's unit and integration testing strategy.
