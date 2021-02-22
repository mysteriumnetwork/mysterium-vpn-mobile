package updated.mysterium.vpn.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import updated.mysterium.vpn.database.entity.NodeEntity

@Dao
interface NodeDao {

    @Query("SELECT * FROM NodeEntity")
    suspend fun getAllNodes(): List<NodeEntity>

    @Insert
    suspend fun insertAll(nodeList: List<NodeEntity>)

    @Query("DELETE FROM NodeEntity")
    suspend fun deleteAll()
}
