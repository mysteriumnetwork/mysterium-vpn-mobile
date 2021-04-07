package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mysterium.ListOrdersRequest
import mysterium.SessionFilter
import network.mysterium.payment.Order
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.common.date.DateUtil
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.model.session.Session
import updated.mysterium.vpn.model.session.Spending

class StatisticUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    private companion object {
        const val MAX_SPENDINGS_INDEX = 99
    }

    suspend fun getLastSessions(): List<Session> {
        val jsonResponse = nodeRepository.getLastSessions(SessionFilter())
        val collectionType = object : TypeToken<Collection<Session?>?>() {}.type
        val sessions: List<Session> = Gson().fromJson(String(jsonResponse), collectionType)
        return sessions.filter { // Get sessions only for last seven days
            DateUtil.dateDiffInDaysFromCurrent(it.createdAt) < 7
        }
    }

    suspend fun getSpendings(): List<Spending> {
        val jsonResponse = nodeRepository.getLastSessions(SessionFilter())
        val collectionType = object : TypeToken<Collection<Spending?>?>() {}.type
        val spendings: List<Spending> = Gson().fromJson(String(jsonResponse), collectionType)
        return if (spendings.size <= MAX_SPENDINGS_INDEX) {
            spendings
        } else {
            spendings.subList(0, MAX_SPENDINGS_INDEX) // Get only last 100 spendings
        }
    }

    suspend fun getTopUps(): List<Order> {
        val listOrdersRequest = ListOrdersRequest().apply {
            identityAddress = sharedPreferencesManager.getStringPreferenceValue(
                SharedPreferencesList.IDENTITY_ADDRESS
            )
        }
        return nodeRepository.listOrders(listOrdersRequest)
    }
}
