package de.stubbe.jaem_client.data

const val USER_PREFERENCES_NAME = "user_preferences"
const val PROFILE_PICTURE_TRANSITION = "profile_picture_transition"
const val DEEP_LINK_URL = "https://jaem.web.app"
const val SHARING_STARTED_DEFAULT = 5000L
const val SEPARATOR_BYTE = 0xFF.toByte()

const val UID_LENGTH = 36
const val RSA_ENCRYPTION_LENGTH = 256
const val RSA_BLOCK_SIZE = 190
const val SIGNATURE_LENGTH = 64
const val TIMESTAMP_LENGTH = 8
const val MESSAGE_SIZE_BYTES = 8
const val SHORT_BYTES = 2
const val INT_BYTES = 4

const val SHARE_LINK_EXPIRATION_TIME = 10 * 60L