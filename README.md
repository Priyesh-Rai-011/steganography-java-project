# StegoSecure 🔐

Ever wanted to hide secret messages in images like a proper spy? Well, now you can! StegoSecure lets you embed encrypted text into PNG images so secretly that nobody will even know there's a hidden message in there.

Perfect for your next prank, sharing answers during online exams (just kidding!), or impressing your friends with some cool cryptography skills.

## What's This All About?

This project combines two awesome security techniques:
1. **AES Encryption** - Your message gets scrambled with military-grade encryption
2. **LSB Steganography** - The encrypted message gets hidden in image pixels

So even if someone suspects your cat photo has hidden data, they still can't read it without your secret key!

## Demo 🎬

![StegoSecure Demo](demo/stegosecure-demo.gif)

*Watch me hide "My crush likes me back!" in this innocent-looking meme and then extract it back*

## How It Works (The Cool Technical Stuff)

```
Your Message → AES Encryption → Hide in Image Pixels → Innocent Looking Image
     ↓                                                          ↓
Downloaded Image ← AES Decryption ← Extract from Pixels ← Upload Stego Image
```

### The Architecture (For Those CS Interviews)

```
┌─────────────────────────────────────────────┐
│             Frontend (What You See)         │
│  • Drag & Drop Interface                   │
│  • Real-time Image Preview                 │
│  • Capacity Calculator                     │
│  • Responsive Design                       │
└─────────────────────────────────────────────┘
                      │
                  REST API Calls
                      │
┌─────────────────────────────────────────────┐
│         Spring Boot Backend                 │
│                                            │
│  ┌─────────────────────────────────────────┐ │
│  │        StegoController                  │ │
│  │  (Handles all your requests)           │ │
│  │                                        │ │
│  │  • POST /api/hide    (Hide message)    │ │
│  │  • POST /api/reveal  (Find message)    │ │
│  │  • POST /api/capacity (Check space)    │ │
│  │  • GET /api/test     (Health check)    │ │
│  └─────────────────────────────────────────┘ │
│                      │                      │
│  ┌─────────────────────────────────────────┐ │
│  │         StegoService                    │ │
│  │  (The brain of the operation)           │ │
│  │                                        │ │
│  │  • Orchestrates encryption + hiding    │ │
│  │  • Manages image processing            │ │
│  │  • Handles all the complex stuff       │ │
│  └─────────────────────────────────────────┘ │
│                      │                      │
│  ┌─────────────────────────────────────────┐ │
│  │           Utility Classes               │ │
│  │                                        │ │
│  │  AESUtil              ImageSteganographyUtil │
│  │  • encrypt()          • hideMessage()  │ │
│  │  • decrypt()          • revealMessage() │ │
│  │  • Strong crypto      • Pixel magic    │ │
│  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

## Features That'll Blow Your Mind 🤯

### What Makes This Cool:
- **Double Protection**: Your message is encrypted AND hidden
- **Invisible Changes**: The image looks exactly the same to human eyes
- **Smart Capacity**: Tells you how much text you can hide before you try
- **User-Friendly**: Just drag, drop, type, and download
- **No Sign-up Needed**: Everything runs locally on your machine

### Technical Highlights (For Your Resume):
- Built with Spring Boot (because we're not savages)
- Uses AES-256 encryption (NSA-approved level stuff)
- Implements LSB steganography (the classic technique)
- RESTful API design (because we follow best practices)
- Responsive frontend (works on your phone too)

## Tech Stack (What You'll Learn)

**Backend:**
- **Java 17** - The language that pays the bills
- **Spring Boot** - Makes Java web development actually fun
- **Maven** - Handles all the messy dependency stuff
- **BufferedImage** - For all the pixel manipulation magic

**Frontend:**
- **HTML5/CSS3** - The basics, but done right
- **Vanilla JavaScript** - No bloated frameworks here
- **Canvas API** - For that smooth image preview
- **CSS Grid & Flexbox** - Modern layout techniques

## Getting Started (Super Easy!)

### What You Need:
- Java 17+ (Download from Oracle or use OpenJDK)
- Maven (Usually comes with most IDEs)
- Any text editor or IDE (IntelliJ IDEA recommended)
- A browser (obviously)

### Setup (5 Minutes Max):

1. **Grab the code:**
   ```bash
   git clone https://github.com/Priyesh-Rai-011/stegosecure.git
   cd stegosecure
   ```

2. **Build it:**
   ```bash
   mvn clean compile
   ```

3. **Run it:**
   ```bash
   mvn spring-boot:run
   ```

4. **Open your browser:**
   ```
   http://localhost:8080
   ```

That's it! You're now a steganography wizard 🧙‍♂️

## How to Use (The Fun Part)

### Hiding Your Secrets:

1. **Choose "Encode Message"** from the homepage
2. **Drop a PNG image** - Your meme, profile pic, whatever
3. **Type your secret** - "Pizza party at 9 PM", "The answer is C", etc.
4. **Create a password** - Something you'll remember but others won't guess
5. **Hit "Encode & Download"** - Get your innocent-looking image
6. **Share it anywhere** - Social media, email, print it out!

### Revealing the Secrets:

1. **Choose "Decode Message"**
2. **Upload the secret image** 
3. **Enter the password** - Same one you used to hide it
4. **Click "Decode"** - Watch your message appear like magic!

### Pro Tips:
- Use strong passwords (not "password123")
- Bigger images = more hiding space
- The app tells you how much text you can hide
- Save your passwords somewhere safe!

## API Docs (For the Nerds)

If you want to integrate this into your own projects:

### Hide a Message:
```bash
curl -X POST http://localhost:8080/api/hide \
  -F "image=@your-image.png" \
  -F "message=Your secret here" \
  -F "key=your-password"
```

### Reveal a Message:
```bash
curl -X POST http://localhost:8080/api/reveal \
  -F "image=@stego-image.png" \
  -F "key=your-password"
```

### Check Image Capacity:
```bash
curl -X POST http://localhost:8080/api/capacity \
  -F "image=@your-image.png"
```

## The Science Behind It 🔬

### Encryption (The Boring But Important Stuff):
- Uses **AES encryption** - the same thing banks use
- Your password gets hashed with **SHA-256** 
- Creates a **128-bit key** for encryption
- Even quantum computers would struggle with this

### Steganography (The Cool Stuff):
- Modifies the **least significant bit** of each pixel
- Uses **RGB channels** (3 bits per pixel)
- Adds a special **delimiter** to mark message end
- Changes are invisible to human eyes

### Why This Combo Rocks:
- **Security**: Encrypted before hiding
- **Invisibility**: No visual changes to image
- **Deniability**: Looks like a normal image
- **Reliability**: Built-in error checking

## Common Issues (And How to Fix Them)

**"My image is too small for my message"**
- Use a bigger image or shorter message
- The app calculates capacity for you

**"Wrong password error"**  
- Passwords are case-sensitive
- Make sure you're using the exact same password

**"Only PNG files supported"**
- Convert your JPEGs to PNG first
- PNG is lossless, JPEGs mess up the hidden data

**"Server not starting"**
- Check if Java 17+ is installed
- Make sure port 8080 isn't being used

## Want to Contribute? 

This started as a college project and could use more features! Here's how you can help:

1. **Fork the repo** - Make it your own
2. **Pick an issue** - Or suggest a new feature  
3. **Code it up** - Follow the existing style
4. **Test it** - Make sure it works
5. **Submit a PR** - Let's review it together

### Ideas for New Features:
- Support for other image formats
- Multiple encryption algorithms
- Batch processing
- Mobile app version
- Integration with cloud storage

## Learning Resources 📚

If this project sparked your interest in cryptography and steganography:

- **Cryptography**: "Crypto 101" by Crypto101.io
- **Steganography**: "Hiding in Plain Sight" research papers
- **Spring Boot**: Official Spring Boot documentation
- **Java Security**: Oracle's security programming guide

## Credits & Thanks

- **Inspiration**: Every spy movie ever made
- **Crypto Libraries**: Java's built-in security APIs
- **UI Design**: Notion's clean aesthetic
- **Testing**: My roommates who tried to break it

## License

MIT License - basically, do whatever you want with this code, just don't blame me if you get caught sending secret messages! 😄

---

**Built by**: [Priyesh Rai](https://github.com/Priyesh-Rai-011)  
**Connect**: [LinkedIn](https://www.linkedin.com/in/priyesh-rai-88389b229/) | [Twitter](https://x.com/PriyeshRai_07)  
**Star this repo if it helped you impress someone!** ⭐
