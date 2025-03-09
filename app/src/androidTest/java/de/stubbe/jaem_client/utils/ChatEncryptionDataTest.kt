
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ChatEncryptionDataTest{
    @Test
    fun testSending(){
        val thisDevice = ChatEncryptionData(SymmetricEncryption.ED25519)
        val otherDevice = ChatEncryptionData(SymmetricEncryption.ED25519, thisDevice.client!!)
        thisDevice.setCommunicationPartner(otherDevice.client!!)

        var message = "Hello World!"

        var encrpyted = thisDevice.encrypt(message.toByteArray())
        assertNotNull(encrpyted)
        var decrypted = otherDevice.decrypt(encrpyted)
        assertNotNull(decrypted)

        assertEquals(message, String(decrypted))
    }
}