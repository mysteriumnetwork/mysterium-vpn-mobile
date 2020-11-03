package network.mysterium.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus

@Database(entities = [FavoriteProposal::class, Terms::class, IdentityModel::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProposalDao(): FavoriteProposalDao
    abstract fun termsDao(): TermsDao
    abstract fun identityDao(): IdentityDao
}

class Converters {

    @TypeConverter
    fun toIdentityRegistrationStatus(value: String) = IdentityRegistrationStatus.parse(value)

    @TypeConverter
    fun fromIdentityRegistrationStatus(value: IdentityRegistrationStatus) = value.status

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `identity` (`address` TEXT NOT NULL, `channelAddress` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`address`))")
    }
}
