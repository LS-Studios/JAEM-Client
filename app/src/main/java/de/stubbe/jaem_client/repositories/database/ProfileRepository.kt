package de.stubbe.jaem_client.repositories.database

import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.entries.ProfileModel
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao
) {

    suspend fun getProfileByUid(uid: String) = profileDao.getProfileByUid(uid)

    fun getProfileByUidWithChange(uid: String) = profileDao.getProfileByUidWithChange(uid)

    fun getAllProfiles() = profileDao.getAllProfiles()

    suspend fun insertProfile(profile: ProfileModel): Long = profileDao.insert(profile)

    suspend fun updateProfile(profile: ProfileModel) = profileDao.update(profile)

    suspend fun deleteProfile(profile: ProfileModel) = profileDao.delete(profile)

}