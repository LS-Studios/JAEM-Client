import de.stubbe.jaem_client.model.enums.SymmetricEncryption
import de.stubbe.jaem_client.utils.EncryptionHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EncryptionHelperTest{
    @Test
    fun testSending(){
        val thisDevice = EncryptionHelper(SymmetricEncryption.ED25519)
        val otherDevice = EncryptionHelper(SymmetricEncryption.ED25519, thisDevice.client!!)
        thisDevice.setCommunicationPartner(otherDevice.client!!)

        var message = "Hello World!"

        var encrpyted = thisDevice.encrypt(message.toByteArray())
        assertNotNull(encrpyted)
        var decrypted = otherDevice.decrypt(encrpyted)
        assertNotNull(decrypted)

        assertEquals(message, String(decrypted))
    }
}