package de.stubbe.jaem_client.model

data class DialogActionModel(
    val text: String,
    val onClick: () -> Unit
)
