package updated_mysterium_vpn.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import updated_mysterium_vpn.database.entity.NodeEntity

@Dao
interface NodeDao {

    @Query("SELECT * FROM NodeEntity")
    suspend fun getAllNodes(): List<NodeEntity>

    @Insert
    suspend fun insertAll(nodeList: List<NodeEntity>)

    @Query("DELETE FROM NodeEntity")
    suspend fun deleteAll()
}
