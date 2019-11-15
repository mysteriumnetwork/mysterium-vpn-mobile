package network.mysterium.db

import androidx.room.*

@Entity
data class Terms(
        @PrimaryKey val version: String
)

@Dao
interface TermsDao {
    @Query("SELECT * FROM terms LIMIT 1")
    suspend fun get(): Terms?

    @Insert
    suspend fun insert(terms: Terms)

    @Query("DELETE FROM terms")
    suspend fun delete()
}
