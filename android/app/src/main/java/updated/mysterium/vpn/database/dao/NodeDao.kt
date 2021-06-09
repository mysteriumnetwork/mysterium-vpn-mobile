package updated.mysterium.vpn.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import updated.mysterium.vpn.database.entity.NodeEntity

@Dao
interface NodeDao {

    @Query("SELECT * FROM NodeEntity WHERE is_saved is 1")
    suspend fun getFavourites(): List<NodeEntity>

    @Query("DELETE FROM NodeEntity WHERE id = :nodeId")
    suspend fun deleteFromFavourite(nodeId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourite(nodeEntity: NodeEntity)

    @Query("SELECT * FROM NodeEntity WHERE id = :nodeId")
    suspend fun getById(nodeId: String): NodeEntity?
}
