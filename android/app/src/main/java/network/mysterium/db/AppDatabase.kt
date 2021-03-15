package network.mysterium.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.QualityLevel
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus

@Database(entities = [FavoriteProposal::class, Terms::class, IdentityModel::class, SavedProposalFilter::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProposalDao(): FavoriteProposalDao
    abstract fun termsDao(): TermsDao
    abstract fun identityDao(): IdentityDao
    abstract fun proposalFilterDao(): ProposalFilterDao
}

class Converters {

    @TypeConverter
    fun toIdentityRegistrationStatus(value: String) = IdentityRegistrationStatus.parse(value)

    @TypeConverter
    fun fromIdentityRegistrationStatus(value: IdentityRegistrationStatus) = value.status

    @TypeConverter
    fun toNodeType(value: String) = NodeType.parse(value)

    @TypeConverter
    fun fromNodeType(value: NodeType) = value.nodeType

    @TypeConverter
    fun toQualityLevel(value: Int) = QualityLevel.parse(value)

    @TypeConverter
    fun fromQualityLevel(value: QualityLevel) = value.level

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `identity` (`address` TEXT NOT NULL, `channelAddress` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`address`))")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `SavedProposalFilter` (
            `id` TEXT PRIMARY KEY NOT NULL, 
            `countryCode` TEXT NOT NULL, 
            `countryName` TEXT NOT NULL, 
            `pricePerHour` REAL NOT NULL, 
            `pricePerGiB` REAL NOT NULL, 
            `qualityLevel` INT NOT NULL, 
            `nodeType` TEXT NOT NULL,
            `qualityIncludeUnreachable` INT NOT NULL
        )
        """.trimIndent())
    }
}
