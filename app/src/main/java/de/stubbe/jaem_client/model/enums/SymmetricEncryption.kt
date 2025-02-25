

package de.stubbe.jaem_client.model.enums

import android.os.Build
import androidx.annotation.RequiresApi
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.math.ec.rfc8032.Ed25519
import java.security.Key
import java.security.KeyPair
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class SymmetricEncryption(
    val algorithm: String,
    val generate: (ByteArray)-> ByteArray,
    val encrypt: (String, ByteArray) -> String,
    val decrypt: (String, ByteArray) -> String
) {
    @RequiresApi(Build.VERSION_CODES.O)
    AES(
        "AES",
        { recipientPublicKey ->
            val random = java.security.SecureRandom()eg
            val keyGen = org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator()
            keyGen.init(org.bouncycastle.crypto.KeyGenerationParameters(random, 256))
            val keyPair: AsymmetricCipherKeyPair = keyGen.generateKeyPair()

            val privateKey = (keyPair.private as Ed25519PrivateKeyParameters).encoded
            val publicKey = (keyPair.public as Ed25519PublicKeyParameters).encoded

            val sharedSecret = privateKey + recipientPublicKey
            val mac = Mac.getInstance("HmacSHA256")
            val salt = ByteArray(32) // Optional: Can be random or fixed
            val info = "Ed25519 Key Exchange".toByteArray()

            mac.init(SecretKeySpec(salt, "HmacSHA256"))
            mac.update(sharedSecret)
            val prk = mac.doFinal()

            mac.init(SecretKeySpec(prk, "HmacSHA256"))
            mac.update(info)
            mac.doFinal().copyOf(32)
        },
        { message, aesKey ->
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv) // Generate random IV

            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)
            val ciphertext = cipher.doFinal(message.toByteArray())

            Base64.getEncoder().encodeToString(iv + ciphertext)
        },
        { message, aesKey ->
            val decodedData = Base64.getDecoder().decode(message)

            val iv = decodedData.copyOfRange(0, 12) // Extract IV
            val ciphertext = decodedData.copyOfRange(12, decodedData.size) // Extract actual ciphertext

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val keySpec = SecretKeySpec(aesKey, "AES")
            val gcmSpec = GCMParameterSpec(128, iv)

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)
            val decryptedBytes = cipher.doFinal(ciphertext)

            String(decryptedBytes)
        }
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
   - Der Schlüssel kann per QR-Code geteilt werden, um die Daten auf dem neuen Gerät

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