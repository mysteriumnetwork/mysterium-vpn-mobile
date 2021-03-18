package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mysterium.SessionFilter
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.common.date.DateUtil
import updated.mysterium.vpn.model.session.Session
import updated.mysterium.vpn.model.session.Spending

class StatisticUseCase(private val nodeRepository: NodeRepository) {

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
        return Gson().fromJson(String(jsonResponse), collectionType)
    }
}
