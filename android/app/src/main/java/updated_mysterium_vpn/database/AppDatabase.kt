package updated_mysterium_vpn.database

import androidx.room.Database
import androidx.room.RoomDatabase
import updated_mysterium_vpn.database.dao.NodeDao
import updated_mysterium_vpn.database.entity.NodeEntity

@Database(entities = [NodeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun nodeDao(): NodeDao
}
