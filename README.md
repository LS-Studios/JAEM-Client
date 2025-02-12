# Projektbeschreibung

**Projektname:** [Hier den Projektnamen einfügen]  
**Autor:** [Dein Name]  
**Version:** 1.0  
**Letzte Aktualisierung:** [Datum]

---

## Screens
Das Projekt enthält folgende Screens:
1. **Schlüssel Eingabe / Generierung**  
   - Benutzer können einen Schlüssel manuell eingeben oder automatisch generieren.
   
2. **Chat Overview**  
   - Übersicht über bestehende Chats mit Möglichkeit zur Auswahl eines Chats.

3. **Chat Create Screen**  
   - Erstellen eines neuen Chats, ggf. mit individuellen Einstellungen.

4. **Chat Screen**  
   - Chat-Interaktion mit Nachrichtenanzeige, Verschlüsselung und Nutzeroptionen.

---

## Architektur
Das Projekt basiert auf dem **MVVM (Model-View-ViewModel)**-Architekturpattern.  
- **Model:** Verwaltet die Daten und deren Verarbeitung.  
- **ViewModel:** Stellt Daten für die UI bereit und verarbeitet Benutzerinteraktionen.  
- **View:** Präsentiert die Daten und aktualisiert sich über LiveData oder State Management.  

Dank MVVM bleibt der App-Status auch bei **Bildschirmrotation oder Zustandsänderungen** erhalten.

---

## Verwendete Technologien
### **Mehrsprachigkeit: String-Resources**
- Alle Texte sind in **String-Resources** (`strings.xml`) gespeichert, um einfache Lokalisierung für verschiedene Sprachen zu ermöglichen.

### **Design: Custom Theme für Light & Dark Mode**
- Die App verwendet **eigene Farbpaletten**, um das UI sowohl im **Light Mode** als auch im **Dark Mode** optimal anzupassen.

### **Datenbank: Room**
- **Room Database** wird verwendet, um Chat-Daten und Einstellungen persistent zu speichern.
- Ermöglicht **schnellen Zugriff** auf gespeicherte Daten und vermeidet Datenverlust.

### **Datenspeicherung: DataStore (Preferences & Proto)**

Für die Speicherung von App-Einstellungen und Zustandsinformationen wird **Jetpack DataStore** verwendet. DataStore ersetzt `SharedPreferences` und bietet eine **sichere, effiziente und asynchrone** Möglichkeit, kleine Datenmengen zu speichern.

#### **Einsatzbereiche in der App**
- **Einstellungen speichern** (z. B. Dark/Light Theme, Spracheinstellungen)
- **Benutzerpräferenzen verwalten** (z. B. Auto-Logout, Benachrichtigungen)
- **App-Zustand erhalten** (z. B. letzte geöffnete Ansicht)

#### **Arten von DataStore**
1. **Preferences DataStore**  
   - Speichert Key-Value-Paare (ähnlich wie `SharedPreferences`).
   - Wird für einfache Einstellungen und Flags verwendet.

2. **Proto DataStore**  
   - Speichert strukturierte Daten in **Protobuf-Format**.
   - Wird für **komplexe Objekte** mit spezifischen Datentypen verwendet.
   - Bietet **Typensicherheit** und **Schema-Versionierung**.

#### **Warum Proto DataStore?**
- **Bessere Performance**: Binärserialisierung ist effizienter als JSON oder XML.
- **Typensicherheit**: Die gespeicherten Werte entsprechen definierten Strukturen.
- **Einfache Migration**: Protobuf-Dateien unterstützen Versionierung und Upgrades.

---

## Entwickler
- **Lennard Stubbe** (Lead Developer)
- **Nick Schefner** (User discovery service)
- **Antonio Mikley** (Server architecture)

---

## Daten-Klassen
Die wichtigsten Daten-Klassen, die in der Anwendung verwendet werden:

### **Profile**
Repräsentiert das Benutzerprofil eines Nutzers.  
```kotlin
data class Profile(
    val id: Int,               // Eindeutige ID des Profils
    val name: String,          // Name des Benutzers
    val image: String,         // URL oder Pfad zum Profilbild
    val description: String    // Beschreibung oder Status des Benutzers
)
```

### **Message**
Repräsentiert eine Nachricht innerhalb eines Chats.  
```kotlin
data class Message(
    val id: Int,               // Eindeutige ID der Nachricht
    val senderId: Int,         // ID des Absenders (Profile)
    val receiverId: Int,       // ID des Empfängers (Profile)
    val chatId: Int,           // Zugehörige Chat-ID
    val content: String,       // Inhalt der Nachricht
    val sendTime: Long,        // Zeitstempel des Sendens
    val deliveryTime: Long?    // Zeitstempel der Zustellung (nullable)
)
```

### **Chat**
Repräsentiert einen Chat zwischen zwei Nutzern.  
```kotlin
data class Chat(
    val id: Int,               // Eindeutige ID des Chats
    val userIds: List<Int>,    // IDs der am Chat teilnehmenden Benutzer
)
```

---

## Weitere Klassen
- **Encryption-Helper**  
  - Implementiert die **Verschlüsselung** und **Entschlüsselung** von Nachrichten mit sicherem Algorithmus.
  - Schützt Chat-Daten vor unbefugtem Zugriff.

---

## Farbsets für Light und Dark Theme
### **Light Theme**
- **Primärfarbe:** `#2196F3` (Blau)
- **Sekundärfarbe:** `#FFC107` (Gelb)
- **Hintergrund:** `#FFFFFF` (Weiß)
- **Textfarbe:** `#212121` (Schwarzgrau)

### **Dark Theme**
- **Primärfarbe:** `#90CAF9` (Helles Blau)
- **Sekundärfarbe:** `#FFD54F` (Helles Gelb)
- **Hintergrund:** `#121212` (Dunkelgrau)
- **Textfarbe:** `#FFFFFF` (Weiß)
