
# Stegochat: Secure Messaging with Steganography

**Stegochat** is a secure Android messaging application that combines traditional chat functionality with **LSB (Least Significant Bit) steganography** to hide secret messages within images. Built with modern Android development practices, it offers a secure and private way to communicate.

[](https://developer.android.com/about/versions/android-14)
[](https://kotlinlang.org/)
[](https://firebase.google.com/)
[](https://www.google.com/search?q=LICENSE)

-----

## 📖 Table of Contents

  - [Key Features](https://www.google.com/search?q=%23-key-features)
  - [Quick Start](https://www.google.com/search?q=%23-quick-start)
      - [Prerequisites](https://www.google.com/search?q=%23prerequisites)
      - [Installation](https://www.google.com/search?q=%23installation)
  - [Usage Guide](https://www.google.com/search?q=%23-usage-guide)
      - [Getting Started](https://www.google.com/search?q=%23getting-started)
      - [Hiding Messages](https://www.google.com/search?q=%23hiding-messages)
      - [Revealing Messages](https://www.google.com/search?q=%23revealing-messages)
  - [Core Components](https://www.google.com/search?q=%23-core-components)
  - [Security Features](https://www.google.com/search?q=%23-security-features)
  - [Screenshots](https://www.google.com/search?q=%23-screenshots)
  - [Contributing](https://www.google.com/search?q=%23-contributing)
  - [License](https://www.google.com/search?q=%23-license)
  - [Troubleshooting](https://www.google.com/search?q=%23-troubleshooting)
  - [Acknowledgments](https://www.google.com/search?q=%23-acknowledgments)

-----

## 🌟 Key Features

### **🔒 Steganography & Encryption**

  - **LSB Steganography**: Seamlessly hide secret text messages within images.
  - **AES-256 Encryption**: All hidden messages are encrypted before embedding for an extra layer of security.
  - **Image Compatibility**: Supports common image formats like PNG, JPEG, and WebP.
  - **Secure Extraction**: Decrypt and reveal hidden messages from stego images.

### **💬 Chat Functionality**

  - **Real-time Messaging**: Instant message delivery powered by Firebase Firestore.
  - **Secure Authentication**: User registration and login managed by Firebase Auth.
  - **Friend Management**: Easily send and accept friend requests.
  - **Deep Linking**: Invite friends directly with custom links.

### **📱 Modern Android Development**

  - **Intuitive UI**: A clean and modern user interface built with Material Design.
  - **Dark/Light Theme**: Automatic theme switching based on device settings.
  - **MVVM Architecture**: A scalable architecture using ViewModel and LiveData.
  - **Kotlin Coroutines**: Efficient handling of asynchronous tasks.

-----

## 🚀 Quick Start

### Prerequisites

Before you begin, make sure you have:

  - **Android Studio** (Arctic Fox or later)
  - **Android SDK** (API 24+)
  - **Kotlin** (1.8+)
  - **Firebase Project**

### Installation

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/DhamodharKamisetty/Stegochat.git
    cd Stegochat
    ```

2.  **Set up Firebase:**

      - Create a new project on the [Firebase Console](https://console.firebase.google.com/).
      - Enable **Authentication**, **Firestore Database**, and **Storage**.
      - Download your `google-services.json` file and place it in the `app/` directory.
      - Follow the steps in `FIREBASE_SETUP_GUIDE.md` to configure security rules.

3.  **Build and Run:**

      - Open the project in Android Studio.
      - Connect a device or start an emulator.
      - Click the "Run" button to build and install the app.

-----

## 🎯 Usage Guide

### Getting Started

1.  Launch the app and create a new account.
2.  Add friends by sending them an invite link or using their invite code.
3.  Start a chat with a friend from your list.

### Hiding Messages

1.  Go to the "Hide Message" tab.
2.  Select a friend to send the message to.
3.  Choose a high-quality image from your gallery.
4.  Type your secret message and tap "Hide Message".
5.  The app will generate a stego image that you can share with your friend.

### Revealing Messages

1.  On the "Reveal Message" tab, select a stego image from your gallery.
2.  Choose the correct sender from your friends list.
3.  Tap "Reveal Message" to extract and view the hidden text.

-----

## 🛠️ Core Components

  - `LSBSteganography.kt`: The heart of the application, containing the core LSB algorithm.
  - `FirebaseDatabaseHelper.kt`: Manages all database operations with Firestore.
  - `FriendchatActivity.kt`: The main chat interface where messages are exchanged.
  - `HidemessageActivity.kt`: Handles the process of hiding messages in images.

-----

## 🔐 Security Features

  - **End-to-End Encryption**: Hidden messages are encrypted on the sender's device and can only be decrypted by the intended recipient.
  - **Robust Key Management**: Secure cryptographic keys are generated and stored to protect your conversations.
  - **Firebase Security Rules**: Ensures that users can only access their own data and authorized chats.

-----

## 🤝 Contributing

We welcome contributions\! Please feel free to fork the repository and submit a pull request with your improvements.

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature-name`).
3.  Commit your changes (`git commit -m 'Add your feature'`).
4.  Push to the branch (`git push origin feature/your-feature-name`).
5.  Open a Pull Request.

-----


## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details.

-----

## 🆘 Troubleshooting

If you encounter issues, please check the following:

  - **Firebase Connection**: Make sure `google-services.json` is correctly placed and that your Firebase project is properly configured.
  - **Permissions**: Ensure all required Android permissions (e.g., storage, camera) have been granted.
  - **Steganography**: For best results, use PNG images and avoid highly compressed images, as they may cause data loss.

For more detailed guides, check the `Documentation` folder.

-----

## 🙏 Acknowledgments

A special thank you to:

  - The **Firebase Team** for providing a robust and easy-to-use backend service.
  - The **Android Community** for endless resources and support.
  - All the open-source projects that made this possible.

**Made with ❤️ for secure communication**
