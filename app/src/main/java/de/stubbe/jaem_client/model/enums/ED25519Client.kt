import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.bouncycastle.util.encoders.Hex

class ED25519Client {
    var ed25519PublicKey: Ed25519PublicKeyParameters? = null
    var ed25519PrivateKey:Ed25519PrivateKeyParameters? = null
    var x25519PublicKey: X25519PublicKeyParameters? = null
    var x25519PrivateKey: X25519PrivateKeyParameters? = null

    var encryption:SymmetricEncryption = SymmetricEncryption.ED25519

    constructor() {
        val ed25519Keys = encryption.generateSignatureKeys()
        ed25519PublicKey = ed25519Keys.first
        ed25519PrivateKey = ed25519Keys.second

        val x25519Keys = encryption.generateX25519Keys(ed25519PrivateKey!!)
        x25519PublicKey = x25519Keys.first
        x25519PrivateKey = x25519Keys.second
    }

    constructor(edPublicKey: Ed25519PublicKeyParameters, xPublicKey: X25519PublicKeyParameters) {
        ed25519PublicKey = edPublicKey
        x25519PublicKey = xPublicKey
    }

    constructor(edPublicKey: Ed25519PublicKeyParameters, edPrivateKey:Ed25519PrivateKeyParameters,xPublicKey: X25519PublicKeyParameters, xPrivateKey: X25519PrivateKeyParameters){
        ed25519PublicKey = edPublicKey
        ed25519PrivateKey = edPrivateKey
        x25519PublicKey = xPublicKey
        x25519PrivateKey = xPrivateKey
    }
}