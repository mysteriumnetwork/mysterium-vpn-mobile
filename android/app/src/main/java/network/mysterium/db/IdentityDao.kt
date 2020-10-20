package network.mysterium.db

import androidx.room.*
import network.mysterium.ui.IdentityModel

@Dao
interface IdentityDao {
    @Query("SELECT * FROM identity LIMIT 1")
    suspend fun get(): IdentityModel?

    suspend fun set(identity: IdentityModel) {
        delete()
        insert(identity)
    }

    @Insert
    suspend fun insert(identity: IdentityModel)

    @Query("DELETE FROM identity")
    suspend fun delete()
}
