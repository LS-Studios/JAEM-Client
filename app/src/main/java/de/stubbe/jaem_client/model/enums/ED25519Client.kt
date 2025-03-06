import de.stubbe.jaem_client.model.enums.AsymmetricEncryption
import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.security.PrivateKey
import java.security.PublicKey

class ED25519Client {
    var ed25519PublicKey: Ed25519PublicKeyParameters? = null
    var ed25519PrivateKey: Ed25519PrivateKeyParameters? = null
    var x25519PublicKey: X25519PublicKeyParameters? = null
    var x25519PrivateKey: X25519PrivateKeyParameters? = null
    var rsaPublicKey: PublicKey? = null
    var rsaPrivateKey: PrivateKey? = null

    var encryption: SymmetricEncryption = SymmetricEncryption.ED25519

    constructor() {
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
        ed25519PublicKey: Ed25519PublicKeyParameters,
        x25519PublicKey: X25519PublicKeyParameters,
        rsaPublicKey: PublicKey
    ) {
        this.ed25519PublicKey = ed25519PublicKey
        this.x25519PublicKey = x25519PublicKey
        this.rsaPublicKey = rsaPublicKey
    }

    constructor(
        ed25519PublicKey: Ed25519PublicKeyParameters,
        ed25519PrivateKey: Ed25519PrivateKeyParameters,
        x25519PublicKey: X25519PublicKeyParameters,
        x25519PrivateKey: X25519PrivateKeyParameters,
        rsaPublicKey: PublicKey,
        rsaPrivateKey: PrivateKey
    ) {
        this.ed25519PublicKey = ed25519PublicKey
        this.ed25519PrivateKey = ed25519PrivateKey
        this.x25519PublicKey = x25519PublicKey
        this.x25519PrivateKey = x25519PrivateKey
        this.rsaPublicKey = rsaPublicKey
        this.rsaPrivateKey = rsaPrivateKey
    }

    fun updateKeys(
        client: ED25519Client
    ) {
        this.ed25519PublicKey = client.ed25519PublicKey
        this.ed25519PrivateKey = client.ed25519PrivateKey
        this.x25519PublicKey = client.x25519PublicKey
        this.x25519PrivateKey = client.x25519PrivateKey
        this.rsaPublicKey = client.rsaPublicKey
        this.rsaPrivateKey = client.rsaPrivateKey
    }
}