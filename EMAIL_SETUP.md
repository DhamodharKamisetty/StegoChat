# Email Setup Guide for StegoChat OTP Functionality

## Overview
This guide explains how to configure email functionality for OTP (One-Time Password) sending in the StegoChat app.

## Prerequisites
1. A Gmail account
2. App password for Gmail (2-factor authentication must be enabled)

## Step 1: Enable 2-Factor Authentication on Gmail
1. Go to your Google Account settings
2. Navigate to Security
3. Enable 2-Step Verification
4. Generate an App Password for your Android app

## Step 2: Generate App Password
1. Go to Google Account settings
2. Navigate to Security > 2-Step Verification
3. Click on "App passwords"
4. Select "Mail" and "Android"
5. Copy the generated 16-character password

## Step 3: Update Email Configuration
Open `app/src/main/java/com/example/stegochat/EmailHelper.kt` and update these lines:

```kotlin
private const val EMAIL_FROM = "your-email@gmail.com" // Replace with your Gmail
private const val EMAIL_PASSWORD = "your-app-password" // Replace with your app password
```

## Step 4: Alternative Email Services
If you prefer to use other email services, update the configuration:

### For Outlook/Hotmail:
```kotlin
private const val EMAIL_HOST = "smtp-mail.outlook.com"
private const val EMAIL_PORT = "587"
```

### For Yahoo:
```kotlin
private const val EMAIL_HOST = "smtp.mail.yahoo.com"
private const val EMAIL_PORT = "587"
```

## Step 5: Testing
1. Build and run the app
2. Navigate to Forgot Password screen
3. Enter a valid email address
4. Click "Send OTP"
5. Check the recipient's email for the OTP

## Troubleshooting

### Common Issues:

1. **Authentication Failed**
   - Ensure you're using an App Password, not your regular Gmail password
   - Verify 2-factor authentication is enabled

2. **Email Not Received**
   - Check spam/junk folder
   - Verify email address is correct
   - Ensure internet connection is stable

3. **Build Errors**
   - Make sure all dependencies are synced
   - Clean and rebuild the project

## Security Notes
- Never commit your actual email credentials to version control
- Consider using environment variables or secure storage for production
- The current implementation is for development/testing purposes

## Production Considerations
For production apps, consider:
- Using Firebase Authentication
- Implementing server-side OTP generation
- Using third-party email services (SendGrid, Mailgun, etc.)
- Adding rate limiting for OTP requests
- Implementing proper error handling and logging 