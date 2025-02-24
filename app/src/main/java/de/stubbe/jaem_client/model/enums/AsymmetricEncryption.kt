package de.stubbe.jaem_client.model.enums

enum class AsymmetricEncryption(
    val algorithm: String,
    val encrypt: (String) -> String,
    val decrypt: (String) -> String
) {
    ED25519(
        "Ed25519",
        { it },
        { it }
    )
}