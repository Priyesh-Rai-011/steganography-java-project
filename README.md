# StegoSecure

A professional steganography platform that combines AES encryption with Least Significant Bit (LSB) steganography to securely hide encrypted messages within PNG images.

## Table of Contents

- [Overview](#overview)
- [Demo](#demo)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## Overview

StegoSecure provides a robust solution for secure data hiding by implementing a dual-layer security approach:

1. **Encryption Layer**: Messages are first encrypted using AES-256 encryption
2. **Steganography Layer**: Encrypted messages are then embedded into PNG images using LSB technique

This ensures that even if the presence of hidden data is detected, the content remains unreadable without the correct decryption key.

## Demo

![StegoSecure Demo](demo/stegosecure-demo.gif)

*Demo showing the complete workflow: encoding a secret message into an image and then decoding it back*

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend Layer                           │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   HTML5/CSS3    │  │   JavaScript    │  │  Responsive UI  │ │
│  │   Interface     │  │   Controllers   │  │   Components    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                   │
                          HTTP REST API Calls
                                   │
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Backend                       │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                 Controller Layer                            │ │
│  │                                                             │ │
│  │    StegoController (@RestController)                       │ │
│  │    ├── POST /api/hide                                      │ │
│  │    ├── POST /api/reveal                                    │ │
│  │    ├── POST /api/capacity                                  │ │
│  │    └── GET  /api/test                                      │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                   │                             │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                 Service Layer                               │ │
│  │                                                             │ │
│  │         StegoService (@Service)                             │ │
│  │         ├── hideMessage()                                  │ │
│  │         ├── revealMessage()                                │ │
│  │         └── getImageCapacity()                             │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                   │                             │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                 Utility Layer                               │ │
│  │                                                             │ │
│  │  ┌─────────────────┐         ┌─────────────────────────────┐ │ │
│  │  │    AESUtil      │         │ ImageSteganographyUtil      │ │ │
│  │  │                 │         │                             │ │ │
│  │  │ ├── encrypt()   │         │ ├── hideMessage()           │ │ │
│  │  │ ├── decrypt()   │         │ ├── revealMessage()         │ │ │
│  │  │ └── createKey() │         │ ├── getMaxCapacity()        │ │ │
│  │  │                 │         │ ├── setBit()                │ │ │
│  │  │                 │         │ ├── getBit()                │ │ │
│  │  │                 │         │ └── binaryToString()        │ │ │
│  │  └─────────────────┘         └─────────────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                   │
┌─────────────────────────────────────────────────────────────────┐
│                     Data Processing                             │
│                                                                 │
│  Original Image (PNG) → AES Encryption → LSB Embedding         │
│                           ↓                    ↓               │
│  Stego Image (PNG) ← AES Decryption ← LSB Extraction           │
└─────────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

#### Frontend Layer
- **HTML5/CSS3 Interface**: Provides responsive UI with drag-and-drop file handling
- **JavaScript Controllers**: Manages user interactions, form validation, and API communication
- **Canvas-based Preview**: Real-time image preview with capacity calculations

#### Controller Layer (StegoController)
- Handles HTTP requests and responses
- Validates input parameters (file type, size, required fields)
- Manages file upload operations
- Returns appropriate HTTP status codes and error messages

#### Service Layer (StegoService)
- Orchestrates business logic between encryption and steganography
- Manages image processing workflow
- Handles BufferedImage conversions and byte array operations
- Provides capacity calculations

#### Utility Layer
- **AESUtil**: Implements AES encryption/decryption with SHA-256 key derivation
- **ImageSteganographyUtil**: Implements LSB steganography algorithms with message delimiters

### Data Flow

1. **Encoding Process**:
   ```
   User Input → Image Upload → Capacity Check → Message Encryption 
   → LSB Embedding → Stego Image Generation → File Download
   ```

2. **Decoding Process**:
   ```
   Stego Image Upload → LSB Extraction → Message Decryption 
   → Plain Text Display
   ```

## Features

### Core Functionality
- **Dual-layer Security**: AES encryption combined with LSB steganography
- **Lossless Embedding**: Uses PNG format to preserve image quality
- **Capacity Analysis**: Real-time calculation of image hiding capacity
- **User-friendly Interface**: Responsive design with drag-and-drop support

### Security Features
- **AES-256 Key Derivation**: Uses SHA-256 to create consistent encryption keys
- **Message Delimiters**: Ensures reliable message extraction
- **Input Validation**: Comprehensive server-side validation
- **Error Handling**: Secure error messages without sensitive data exposure

### Technical Features
- **RESTful API**: Clean, documented API endpoints
- **In-memory Processing**: No persistent storage of sensitive data
- **Cross-origin Support**: CORS enabled for frontend integration
- **Comprehensive Logging**: Detailed operation logging for debugging

## Technology Stack

### Backend
- **Java 17+**: Core programming language
- **Spring Boot 3.x**: Web framework and dependency injection
- **Spring Web**: RESTful web services
- **SLF4J + Logback**: Logging framework
- **Java AWT**: Image processing capabilities

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Modern styling with CSS Grid and Flexbox
- **Vanilla JavaScript**: No external dependencies
- **Canvas API**: Image preview and manipulation

### Build & Deployment
- **Maven**: Dependency management and build automation
- **Embedded Tomcat**: Application server
- **Spring Boot DevTools**: Development-time features

## Installation

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- Git

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/stegosecure.git
   cd stegosecure
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   ```
   http://localhost:8080
   ```

### Production Deployment

1. **Build JAR file**
   ```bash
   mvn clean package
   ```

2. **Run the JAR**
   ```bash
   java -jar target/stegosecure-1.0.0.jar
   ```

## Usage

### Encoding Messages

1. Navigate to the application home page
2. Select "Encode Message"
3. Upload a PNG image (carrier image)
4. Enter your secret message
5. Provide a strong encryption key
6. Click "Encode & Download"
7. Save the generated stego-image

### Decoding Messages

1. Select "Decode Message" 
2. Upload the stego-image
3. Enter the exact decryption key used during encoding
4. Click "Decode Message"
5. View the revealed message

### Important Notes

- **Key Management**: Store encryption keys securely - lost keys mean lost messages
- **File Format**: Only PNG images are supported for optimal results
- **Image Capacity**: Check capacity before encoding to ensure message fits
- **Key Sensitivity**: Decryption keys are case-sensitive and must match exactly

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### Health Check
```http
GET /api/test
```
**Response**: Server status and available endpoints

#### Hide Message
```http
POST /api/hide
Content-Type: multipart/form-data

Parameters:
- image: PNG file (multipart)
- message: Secret text (form field)
- key: Encryption key (form field)
```
**Response**: Stego-image file download

#### Reveal Message
```http
POST /api/reveal
Content-Type: multipart/form-data

Parameters:
- image: Stego-image PNG file (multipart)
- key: Decryption key (form field)
```
**Response**: JSON with revealed message

#### Check Capacity
```http
POST /api/capacity
Content-Type: multipart/form-data

Parameters:
- image: PNG file (multipart)
```
**Response**: JSON with capacity information

### Error Responses

All endpoints return consistent error formats:
```json
{
  "status": "error",
  "message": "Descriptive error message",
  "timestamp": 1640995200000
}
```

## Security

### Encryption Details
- **Algorithm**: AES with PKCS5Padding
- **Key Derivation**: SHA-256 hash of user-provided key
- **Key Length**: 128-bit AES keys
- **Mode**: ECB (suitable for short messages with unique keys)

### Steganography Details
- **Technique**: Least Significant Bit (LSB) modification
- **Channels**: RGB channels (3 bits per pixel)
- **Delimiter**: Custom message terminator (`###EOM###`)
- **Capacity**: ~3 bits per pixel minus delimiter overhead

### Security Considerations
- Messages are encrypted before embedding
- Keys are not stored or logged
- Processing occurs entirely in memory
- Original images are not modified permanently
- Error messages avoid sensitive data leakage

### Recommendations
- Use strong, unique encryption keys
- Store keys securely and separately from stego-images
- Consider additional security layers for highly sensitive data
- Regularly update dependencies for security patches

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Add comprehensive tests for new features
- Update documentation for API changes
- Ensure security best practices

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Author**: [Priyesh Rai](https://github.com/yourusername)  
**Contact**: priyeshrai.delhi@gmail.com  
**Project Link**: https://github.com/yourusername/stegosecure
