
package de.stubbe.jaem_client.model.enums

enum class SymmetricEncryption(
    val algorithm: String,
    val encrypt: (String) -> String,
    val decrypt: (String) -> String
) {
    AES(
        "AES",
        { it },
        { it }
    )
}

/*
Discovery Service – Arten des Teilens

1. QR-Code
   - Enthält einen Deep Link zur App mit einer serialisierten Version des Profils (Name, Bild, öffentliche Schlüssel).

2. Link
   - Erstellt eine temporäre Instanz des Profils (Name, Bild, öffentliche Schlüssel) auf einem Discovery Service für maximal 10 Minuten.
   - Das neue Gerät scannt den Deep Link, um das Profil abzurufen.

3. Kopieren
   - Funktioniert wie der Link, nur dass die URL in die Zwischenablage kopiert wird.

Schlüsselerstellung

- Asymmetrische Verschlüsselung
  - Wird automatisch bei der Profilerstellung auf einem Endgerät generiert.
- Symmetrische Verschlüsselung
  - Wird bei der Chaterstellung auf einem Endgerät über einen Handshake generiert.

Neuer Chat-Handshake

1. Empfangen der geteilten Profildaten mit einer Liste von asymmetrischen öffentlichen Schlüsseln.
2. Auswahl eines Verschlüsselungsverfahrens, basierend auf den verfügbaren Algorithmen.
3. Chat-Anfrage senden an den gewünschten Nutzer mit dem ausgewählten öffentlichen Schlüssel.
4. Akzeptieren der Anfrage
   - Der Empfänger generiert für alle unterstützten Algorithmen einen symmetrischen Schlüssel.
   - Dieser wird mit dem öffentlichen Schlüssel des Anfragenden verschlüsselt und zurückgesendet.

Neuer Algorithmus-Handling

1. Nutzer können neue Algorithmen von einem Discovery Service abrufen.
2. Generierung neuer Schlüssel für die neuen Algorithmen
   - Jeder Nutzer generiert neue asymmetrische Schlüssel für die neuen Algorithmen.
   - Diese werden an alle verbundenen Discovery Services gesendet.
3. Erster Nutzer mit Update erstellt neue symmetrische Schlüssel
   - Sobald ein Chat-Partner das Update erhält, erstellt er einen neuen symmetrischen Schlüssel für das neue Verfahren.
   - Der neue Schlüssel wird dann an alle Chat-Partner verteilt.

Discovery Service-Anbindung

- Nutzer können mehrere Discovery Services anbinden.
- Suche nach Profilen in allen verbundenen Discovery Services.
- Filterung nach bestimmten Discovery Services ist möglich.
- Profilaktualisierungen werden automatisch an alle verbundenen Discovery Services gesendet.

Daten auf ein neues Gerät übertragen

1. NFC-Übertragung
   - Alle Daten der lokalen Datenbank werden über NFC auf das neue Gerät übertragen.

2. Übertragung über einen Discovery Service
   - Daten werden mit einem temporären Schlüssel verschlüsselt und auf den Discovery Service hochgeladen.
   - Der Schlüssel kann per QR-Code geteilt werden, um die Daten auf dem neuen Gerät zu entschlüsseln.
 */