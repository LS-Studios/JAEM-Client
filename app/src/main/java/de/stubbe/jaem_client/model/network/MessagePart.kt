package de.stubbe.jaem_client.model.network

import de.stubbe.jaem_client.data.SEPARATOR_BYTE
import de.stubbe.jaem_client.model.Attachments
import de.stubbe.jaem_client.model.enums.AttachmentType
import de.stubbe.jaem_client.model.enums.ContentMessageType
import de.stubbe.jaem_client.utils.toByteArray
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Repräsentiert einen Teil einer Nachricht
 */
data class MessagePart(
    val type: ContentMessageType,
    val length: Int,
    val content: ByteArray,
) {
    fun toByteArray(): ByteArray = type.ordinal.toShort().toByteArray() + length.toByteArray() + content

    companion object {
        fun createMessageParts(messageUid: String, message: String, attachments: Attachments?): List<MessagePart> {
            val uidPart = MessagePart(ContentMessageType.UID, messageUid.length, messageUid.toByteArray())

            val textPart = MessagePart(ContentMessageType.MESSAGE, message.length, message.toByteArray())

            if (attachments == null) return listOf(uidPart, textPart)

            val type = when (attachments.type) {
                AttachmentType.FILE -> ContentMessageType.FILE
                AttachmentType.IMAGE_AND_VIDEO -> ContentMessageType.IMAGE_AND_VIDEO
            }

            val attachmentParts = attachments.attachmentPaths.map { path ->
                val file = File(path)
                val fileBytes = Files.readAllBytes(Paths.get(path))
                MessagePart(type, fileBytes.size, file.name.toByteArray() + SEPARATOR_BYTE + fileBytes)
            }

            return listOf(uidPart, textPart) + attachmentParts
        }
    }
}