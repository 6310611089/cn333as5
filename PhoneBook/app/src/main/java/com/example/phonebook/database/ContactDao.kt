package com.example.phonebook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

    @Query("SELECT * FROM PhoneBookDbModel")
    fun getAllSync(): List<PhoneBookDbModel>

    @Query("SELECT * FROM PhoneBookDbModel WHERE id IN (:contactIds)")
    fun getContactsByIdsSync(contactIds: List<Long>): List<PhoneBookDbModel>

    @Query("SELECT * FROM PhoneBookDbModel WHERE id LIKE :id")
    fun findByIdSync(id: Long): PhoneBookDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneBookDbModel: PhoneBookDbModel)

    @Insert
    fun insertAll(vararg phoneBookDbModel: PhoneBookDbModel)

    @Query("DELETE FROM PhoneBookDbModel WHERE id LIKE :id")
    fun delete(id: Long)

    @Query("DELETE FROM PhoneBookDbModel WHERE id IN (:contactIds)")
    fun delete(contactIds: List<Long>)
}