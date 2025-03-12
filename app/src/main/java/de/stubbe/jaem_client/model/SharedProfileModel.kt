package de.stubbe.jaem_client.model

import de.stubbe.jaem_client.datastore.ServerUrlModel

data class SharedProfileModel(
    val serverUrl: ServerUrlModel,
    val sharedCode: String,
    val timestamp: Long
)