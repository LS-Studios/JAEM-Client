package de.stubbe.jaem_client.model

import android.Manifest
import android.os.Build

enum class JAEMFileType {
    CAMERA,
    IMAGE,
    IMAGE_AND_VIDEO,
    STORAGE;

    fun getPermissions(): List<String> {
        val list: MutableList<String> = mutableListOf()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            when (this) {
                CAMERA -> list.add(Manifest.permission.CAMERA)
                IMAGE -> list.add(Manifest.permission.READ_MEDIA_IMAGES)
                IMAGE_AND_VIDEO -> list.addAll(listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
                STORAGE -> list.addAll(listOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
            }
        }
        return list.toList()
    }

}