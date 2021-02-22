package updated.mysterium.vpn.database

import androidx.room.Database
import androidx.room.RoomDatabase
import updated.mysterium.vpn.database.dao.NodeDao
import updated.mysterium.vpn.database.entity.NodeEntity

@Database(entities = [NodeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun nodeDao(): NodeDao
}
