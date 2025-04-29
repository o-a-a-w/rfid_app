# RFID Access Control System

An Android-based RFID Access Control application for managing secure access to residential buildings. This system uses an Android device's NFC capabilities to read RFID cards/tags and authorize access based on a secure local database.

## Features

- **Secure RFID Authentication**: Validates RFID cards/tags against authorized entries
- **Real-time Access Control**: Instantly grants or denies access with visual and audio feedback
- **Access Logging**: Records all access attempts with timestamps
- **TOTP Admin Security**: Time-based One-Time Password (TOTP) protection for administrative functions
- **Offline Operation**: Functions without internet connectivity
- **Export Capabilities**: Export logs and tag data to CSV format
- **User-friendly Interface**: Clean, intuitive UI design
- **Battery Level Display**: Usefull when app is pinned.

## Installation

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- Android device with NFC capability (Android 7.0+)
- 13.56 MHz RFID cards/tags (ISO/IEC 14443 compliant)

### Build Instructions

1. Clone this repository:
```
git clone https://github.com/o-a-a-w/rfid_app
```
2. Open the project in Android Studio
3. **Important Security Update**: Before building, change the TOTP secret key:
- Open [admin/AdminAuthActivity.kt](app/src/main/java/com/example/rfidaccesscontrol/admin/AdminAuthActivity.kt#L40)
- Locate the `TOTP_SECRET` constant
- Replace the default value with your own secure Base32 encoded secret:
```kotlin
// Generate your own secure key and replace this value
private val TOTP_SECRET = "YOUR_NEW_SECURE_KEY"
```
4. Build the app:
- Select `Build > Build Bundle(s) / APK(s) > Build APK(s)`
- Or run directly on a connected device: `Run > Run 'app'`

## Usage

### Basic Operation

1. Start the app and place an RFID card/tag near the NFC reader
2. The system will validate the tag and display a result screen:
- Green screen with checkmark for authorized tags
- Red screen with X for unauthorized tags

### Administrative Functions

Access the admin area by tapping "ADMIN ACCESS" and entering your TOTP code:

- **View Access Logs**: See all access attempts with timestamps
- **Add New Tag**: Register a new RFID tag with an apartment code
- **Delete Tag**: Remove a tag from the system
- **View All Tags**: List all authorized tags

## Documentation

Detailed documentation is available in the `docs` directory:

- [User Guide](docs/README.md): Complete system usage instructions

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Security Considerations

- The default TOTP secret **MUST** be changed before deployment
- Access logs are stored locally on the device
- Physical access to the device should be restricted
- Consider using device encryption for additional security

## Acknowledgments

- Developed for residential access control systems
- Based on open standards for RFID communication
- Inspired by the need for affordable access control solutions

---

For questions or support, please open an issue on the GitHub repository.