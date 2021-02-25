package updated.mysterium.vpn.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import updated.mysterium.vpn.database.entity.NodeEntity

@Dao
interface NodeDao {

    @Query("SELECT * FROM NodeEntity")
    suspend fun getAllNodes(): List<NodeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(nodeList: List<NodeEntity>)

    @Query("DELETE FROM NodeEntity WHERE is_saved is 0")
    suspend fun deleteAllUnsaved()

    @Query("SELECT * FROM NodeEntity WHERE is_saved is 1")
    suspend fun getFavourites(): List<NodeEntity>

    @Query("DELETE FROM NodeEntity WHERE id = :nodeId")
    suspend fun deleteFromFavourite(nodeId: String)
}
