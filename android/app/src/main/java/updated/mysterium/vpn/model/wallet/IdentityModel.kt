package updated.mysterium.vpn.model.wallet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "identity")
class IdentityModel(
    @PrimaryKey
    val address: String,
    val channelAddress: String,
    var status: IdentityRegistrationStatus
) {
    // Identity is considered as registered if it's status RegisteredConsumer or InProgress since we support fast
    // identity registration flow which means there is no need to wait for actual registration.
    val registered: Boolean
        get() {
            return status == IdentityRegistrationStatus.REGISTERED || status == IdentityRegistrationStatus.IN_PROGRESS
        }

    val registrationFailed: Boolean
        get() {
            return status == IdentityRegistrationStatus.REGISTRATION_ERROR
        }
}
