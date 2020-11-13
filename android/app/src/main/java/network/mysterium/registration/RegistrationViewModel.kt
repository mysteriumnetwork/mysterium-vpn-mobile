package network.mysterium.registration

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mysterium.RegisterIdentityRequest
import network.mysterium.db.AppDatabase
import network.mysterium.service.core.NodeRepository
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import java.math.BigDecimal
import java.math.RoundingMode

class RegistrationViewModel(private val nodeRepository: NodeRepository, private val db: AppDatabase) : ViewModel() {

    val topupAmount = MutableLiveData<BigDecimal>()
    val registrationFee = MutableLiveData(BigDecimal(0))
    val totalAmount = MediatorLiveData<BigDecimal>()
    val identity = MutableLiveData<IdentityModel>()
    val balance = MutableLiveData(BigDecimal(0))
    val balanceSufficient = MediatorLiveData<Boolean>()
    val progress = MutableLiveData(RegistrationProgress.NOT_STARTED)
    val token = MutableLiveData("")

    init {
        val sumTotal = fun(): BigDecimal {
            val t = topupAmount.value ?: BigDecimal(0)
            val r = registrationFee.value ?: BigDecimal(0)
            return t.plus(r)
        }
        totalAmount.apply {
            addSource(registrationFee) { value = sumTotal() }
            addSource(topupAmount) { value = sumTotal() }
        }

        val isSufficient = fun(): Boolean {
            val balance = this.balance.value ?: return false
            val total = this.totalAmount.value ?: return false
            return balance >= total
        }
        balanceSufficient.apply {
            addSource(balance) { value = isSufficient() }
            addSource(totalAmount) { value = isSufficient() }
        }
    }

    suspend fun load() {
        loadIdentity()
        loadRegistrationFees()
        initListeners()
    }

    private suspend fun loadIdentity() {
        try {
            // Load node identity and its registration status.
            val nodeIdentity = nodeRepository.getIdentity()
            val identity = IdentityModel(
                    address = nodeIdentity.address,
                    channelAddress = nodeIdentity.channelAddress,
                    status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
            )
            this.identity.value = identity
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load account identity", e)
        }
    }

    private suspend fun loadRegistrationFees() {
        val fees = nodeRepository.identityRegistrationFees()
        registrationFee.value = BigDecimal.valueOf(fees.fee).setScale(0, RoundingMode.HALF_UP)
    }

    private suspend fun initListeners() {
        nodeRepository.registerIdentityRegistrationChangeCallback { statusString ->
            CoroutineScope(Dispatchers.Main).launch {
                handleRegistrationStatusChange(IdentityRegistrationStatus.parse(statusString))
            }
        }
        nodeRepository.registerBalanceChangeCallback { balance ->
            CoroutineScope(Dispatchers.Main).launch {
                handleBalanceChange(balance)
            }
        }
    }

    private suspend fun handleRegistrationStatusChange(status: IdentityRegistrationStatus) {
        Log.i(TAG, "Identity registration status changed: ${this.identity.value?.status} → $status")
        val identity = this.identity.value ?: return
        identity.status = status
        this.identity.value = identity
        if (identity.registered) {
            if (progress.value != RegistrationProgress.DONE) {
                progress.value = RegistrationProgress.DONE
            }
            db.identityDao().set(identity)
        }
    }

    private fun handleBalanceChange(balance: Double) {
        Log.i(TAG, "Balance changed: ${this.balance.value} → $balance")
        if (this.balance.value?.compareTo(BigDecimal(balance)) == 0) {
            return
        }
        this.balance.value = BigDecimal.valueOf(balance)
    }

    suspend fun registered(): Boolean {
        val id = db.identityDao().get()
        return id?.registered ?: false
    }

    suspend fun register() {
        val identity = this.identity.value ?: return
        Log.i(TAG, "Registering identity ${identity.address}")
        try {
            val req = RegisterIdentityRequest().apply {
                identityAddress = identity.address
            }
            nodeRepository.registerIdentity(req)
            progress.value = RegistrationProgress.IN_PROGRESS
        } catch (e: Exception) {
            Log.i(TAG, "Failed to register identity ${identity.address}", e)
            progress.value = RegistrationProgress.NOT_STARTED
            throw e
        }
    }

    suspend fun registerWithReferralToken() {
        val identity = this.identity.value ?: return
        val token = this.token.value ?: return
        Log.i(TAG, "Registering identity ${identity.address} with referral token $token")
        try {
            val req = RegisterIdentityRequest().apply {
                this.identityAddress = identity.address
                this.token = token
            }
            nodeRepository.registerIdentity(req)
            progress.value = RegistrationProgress.IN_PROGRESS
        } catch (e: Exception) {
            Log.i(TAG, "Failed to register identity ${identity.address} with token $token", e)
            progress.value = RegistrationProgress.NOT_STARTED
            throw e
        }
    }

    companion object {
        private const val TAG = "RegistrationViewModel"
    }

}
