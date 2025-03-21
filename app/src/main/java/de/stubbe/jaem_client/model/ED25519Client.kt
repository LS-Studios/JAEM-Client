package de.stubbe.jaem_client.model

import de.stubbe.jaem_client.model.encryption.SymmetricEncryption
import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.security.PrivateKey
import java.security.PublicKey

class ED25519Client {
    var profileUid: String? = null
    var ed25519PublicKey: Ed25519PublicKeyParameters? = null
    var ed25519PrivateKey: Ed25519PrivateKeyParameters? = null
    var x25519PublicKey: X25519PublicKeyParameters? = null
    var x25519PrivateKey: X25519PrivateKeyParameters? = null
    var rsaPublicKey: PublicKey? = null
    var rsaPrivateKey: PrivateKey? = null

    var encryption: SymmetricEncryption.ED25519 = SymmetricEncryption.ED25519

    constructor(profileUid: String) {
        this.profileUid = profileUid

        val ed25519Keys = encryption.generateSignatureKeys()
        ed25519PublicKey = ed25519Keys.first
        ed25519PrivateKey = ed25519Keys.second

        val x25519Keys = encryption.generateX25519Keys(ed25519PrivateKey!!)
        x25519PublicKey = x25519Keys.first
        x25519PrivateKey = x25519Keys.second

        val rsaKeys = AsymmetricEncryption.RSA.generate()
        rsaPublicKey = rsaKeys.public
        rsaPrivateKey = rsaKeys.private
    }

    constructor(
        profileUid: String,
        ed25519PublicKey: Ed25519PublicKeyParameters,
        x25519PublicKey: X25519PublicKeyParameters,
        rsaPublicKey: PublicKey
    ) {
        this.profileUid = profileUid
        this.ed25519PublicKey = ed25519PublicKey
        this.x25519PublicKey = x25519PublicKey
        this.rsaPublicKey = rsaPublicKey
    }

    constructor(
        profileUid: String,
        ed25519PublicKey: Ed25519PublicKeyParameters,
        ed25519PrivateKey: Ed25519PrivateKeyParameters,
        x25519PublicKey: X25519PublicKeyParameters,
        x25519PrivateKey: X25519PrivateKeyParameters,
        rsaPublicKey: PublicKey,
        rsaPrivateKey: PrivateKey
    ) {
        this.profileUid = profileUid
        this.ed25519PublicKey = ed25519PublicKey
        this.ed25519PrivateKey = ed25519PrivateKey
        this.x25519PublicKey = x25519PublicKey
        this.x25519PrivateKey = x25519PrivateKey
        this.rsaPublicKey = rsaPublicKey
        this.rsaPrivateKey = rsaPrivateKey
    }

    fun copy(
        profileUid: String? = this.profileUid,
        ed25519PublicKey: Ed25519PublicKeyParameters? = this.ed25519PublicKey,
        ed25519PrivateKey: Ed25519PrivateKeyParameters? = this.ed25519PrivateKey,
        x25519PublicKey: X25519PublicKeyParameters? = this.x25519PublicKey,
        x25519PrivateKey: X25519PrivateKeyParameters? = this.x25519PrivateKey,
        rsaPublicKey: PublicKey? = this.rsaPublicKey,
        rsaPrivateKey: PrivateKey? = this.rsaPrivateKey
    ): ED25519Client {
        return ED25519Client(
            profileUid ?: "",
            ed25519PublicKey ?: this.ed25519PublicKey!!,
            ed25519PrivateKey ?: this.ed25519PrivateKey!!,
            x25519PublicKey ?: this.x25519PublicKey!!,
            x25519PrivateKey ?: this.x25519PrivateKey!!,
            rsaPublicKey ?: this.rsaPublicKey!!,
            rsaPrivateKey ?: this.rsaPrivateKey!!
        )
    }

}