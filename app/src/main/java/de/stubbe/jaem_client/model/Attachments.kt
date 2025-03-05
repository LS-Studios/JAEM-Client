package de.stubbe.jaem_client.model

import de.stubbe.jaem_client.model.enums.AttachmentType
import kotlinx.serialization.Serializable

/**
 * Model für die Speicherung von Anhängen
 *
 * @param type: Typ des Anhangs
 * @param attachmentPaths: Liste von pfaden zu den Anhängen
 */
@Serializable
data class Attachments(
    val type: AttachmentType,
    val attachmentPaths: List<String>
)
