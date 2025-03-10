package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.entries.ProfileEntity
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao
) {

    suspend fun getProfileByUid(uid: String) = profileDao.getProfileByUid(uid)

    fun getProfileByUidWithChange(uid: String) = profileDao.getProfileByUidWithChange(uid)

    fun getAllProfiles() = profileDao.getAllProfiles()

    suspend fun insertProfile(profile: ProfileEntity): Long = profileDao.insert(profile)

    suspend fun updateProfile(profile: ProfileEntity) = profileDao.update(profile)

    suspend fun deleteProfile(profile: ProfileEntity) = profileDao.delete(profile)

}