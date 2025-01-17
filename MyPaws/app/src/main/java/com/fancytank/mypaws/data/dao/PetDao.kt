package com.fancytank.mypaws.data.dao

import androidx.room.*
import com.fancytank.mypaws.data.entity.Pet

@Dao
interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPet(pet: Pet): Long

    @Query("SELECT * FROM pets WHERE userId = :userId")
    fun getPetsByUserId(userId: Long): List<Pet>

    @Delete
    fun deletePet(pet: Pet)
}