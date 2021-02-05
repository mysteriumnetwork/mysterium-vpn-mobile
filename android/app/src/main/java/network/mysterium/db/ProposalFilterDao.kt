package network.mysterium.db

import androidx.room.*
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.QualityLevel

@Entity
data class SavedProposalFilter(
        @PrimaryKey val id: Int = 1,
        val countryCode: String,
        val countryName: String,
        val pricePerHour: Double,
        val pricePerGiB: Double,
        var qualityLevel: QualityLevel,
        val nodeType: NodeType,
        val qualityIncludeUnreachable: Boolean,
) {
    fun pricesWithinBound(defaultHourMax: Double, defaultPerGibMax: Double): Boolean {
        return pricePerHour <= defaultHourMax && pricePerGiB <= defaultPerGibMax
    }
}

@Dao
interface ProposalFilterDao {
    @Query("SELECT * FROM SavedProposalFilter LIMIT 1")
    suspend fun get(): SavedProposalFilter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(filter: SavedProposalFilter)
}
