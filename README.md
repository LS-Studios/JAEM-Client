# JAEM – Just Another Encrypted Messenger

JAEM is an encrypted messaging app designed for secure, private, and distraction-free communication.

With JAEM, you'll never receive annoying requests from bots or scammers pretending to be someone else—such as your mom—but instead communicate privately, securely, and confidentially. JAEM ensures your privacy through strong end-to-end encryption, and messages are stored only until your communication partner receives them. That means nobody, except you and the person you're messaging, can ever see your conversation.

## Project Team

- **Lead Developer:** [**Lennard Stubbe**](https://github.com/LS-Studios)
- **User Discovery Service:** [**Nick Schefner**](https://github.com/ItsJuzoSuzuya)
- **Server Architecture:** [**Antonio Mikley**](https://github.com/antoniomikley)

## Features

### **End-to-End Encryption**
JAEM ensures privacy with end-to-end encryption, meaning only you and your conversation partner can read your messages.

### **No Spam, No Bots**
JAEM prevents annoying requests from bots or unwanted contacts, ensuring clean and meaningful conversations.

### **Temporary Message Storage**
Messages are only stored temporarily—just until your recipient receives them. Once delivered, messages are permanently deleted.

### **Dynamic Servers**
You can easily select and change backend servers through the settings. This flexibility allows you to dynamically join and leave servers anytime.

### **Themes**
JAEM offers multiple stylish themes:
- **System Default** (Adapts to your device's dark or light mode)
- **Light**
- **Dark**
- **Crypto**

### **Languages**
JAEM supports multiple languages to provide users a comfortable experience:

- **English**
- **German**
- **Korean**
- **Russian**
- **More languages coming soon!**

---

## Useful Links

- [Android Client Repository](https://github.com/LS-Studios/JAEM-Client)
- [Backend Repository](https://github.com/antoniomikley/jaem-server)

---

## Planned Features / To-do

- [ ] Korean names cause error when try to parse from byte array
- [ ] Restrict message deletion to user's own messages only
- [ ] Allow editing of messages
- [ ] Support deleting and editing user profiles
- [ ] Implement attachments in messages
- [ ] Send messages only to servers where both users have joined (requires message delivery service endpoint integration)
- [ ] Fix known messaging bugs and improve error handling when switching servers
- [ ] Implementing more encryption algorithms
