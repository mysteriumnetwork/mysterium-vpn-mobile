package network.mysterium.db

import androidx.room.*

@Entity
data class FavoriteProposal(
        @PrimaryKey val id: String
)

@Dao
interface FavoriteProposalDao {
    @Query("SELECT * FROM favoriteproposal")
    suspend fun getAll(): List<FavoriteProposal>

    @Insert
    suspend fun insert(favoriteProposal: FavoriteProposal)

    @Delete
    suspend fun delete(favoriteProposal: FavoriteProposal)
}
