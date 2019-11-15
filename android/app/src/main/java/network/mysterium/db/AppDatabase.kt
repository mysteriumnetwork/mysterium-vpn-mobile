package network.mysterium.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteProposal::class, Terms::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProposalDao(): FavoriteProposalDao
    abstract fun termsDao(): TermsDao
}
