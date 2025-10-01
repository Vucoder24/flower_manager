package com.example.quanlibanhoa.data.dao

import androidx.room.*
import com.example.quanlibanhoa.data.entity.Flower

@Dao
interface FlowerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flower: Flower): Long

    @Update
    suspend fun update(flower: Flower)

    @Delete
    suspend fun delete(flower: Flower)

    @Query("SELECT * FROM flowers ORDER BY tenHoa ASC")
    suspend fun getAllFlowers(): List<Flower>

    @Query("SELECT * FROM flowers WHERE id = :id LIMIT 1")
    suspend fun getFlowerById(id: Int): Flower?
}
