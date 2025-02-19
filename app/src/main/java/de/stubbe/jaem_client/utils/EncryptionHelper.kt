package de.stubbe.jaem_client.utils

/**
 * Hilfsklasse für die Verschlüsselung und Entschlüsselung von Daten.
 */
object EncryptionHelper {

    /**
     * Verschlüsselt die übergebene Daten.
     *
     * @param data: Zu verschlüsselnde Daten
     * @return Verschlüsselte Daten
     */
    fun encrypt(data: String): String {
        return data.reversed()
    }

    /**
     * Entschlüsselt die übergebene Daten.
     *
     * @param data: Zu entschlüsselnde Daten
     * @return Entschlüsselte Daten
     */
    fun decrypt(data: String): String {
        return data.reversed()
    }

}