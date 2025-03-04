package de.stubbe.jaem_client.model

import de.stubbe.jaem_client.model.enums.AttachmentType

/**
 * Model für die Speicherung von Anhängen
 *
 * @param type: Typ des Anhangs
 * @param attachmentPaths: Liste von pfaden zu den Anhängen
 */
data class Attachments(
    val type: AttachmentType,
    val attachmentPaths: List<String>
)
