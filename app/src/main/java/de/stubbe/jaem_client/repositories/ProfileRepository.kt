package de.stubbe.jaem_client.repositories

import de.stubbe.jaem_client.database.daos.ProfileDao
import de.stubbe.jaem_client.database.entries.ProfileModel

class ProfileRepository(private val profileDao: ProfileDao) {

    suspend fun getProfileById(id: Int) = profileDao.getProfileById(id)

    fun getAllProfiles() = profileDao.getAllProfiles()

    suspend fun insertProfile(profile: ProfileModel) = profileDao.insert(profile)

    suspend fun updateProfile(profile: ProfileModel) = profileDao.update(profile)

    suspend fun deleteProfile(profile: ProfileModel) = profileDao.delete(profile)

}