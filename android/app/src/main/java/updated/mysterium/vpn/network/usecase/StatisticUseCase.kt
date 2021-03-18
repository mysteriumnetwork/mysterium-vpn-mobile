package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mysterium.SessionFilter
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.common.date.DateUtil
import updated.mysterium.vpn.model.session.Session

class StatisticUseCase(private val nodeRepository: NodeRepository) {

    private companion object {
        const val CURRENT_CONNECTION = "0001-01-01T00:00:00Z"
    }

    suspend fun getLastSessions(): List<Session> {
        val jsonResponse = nodeRepository.getLastSessions(SessionFilter())
        val collectionType = object : TypeToken<Collection<Session?>?>() {}.type
        val sessions: List<Session> = Gson().fromJson(String(jsonResponse), collectionType)
        return sessions.filter { // Get sessions only for last seven days
            DateUtil.dateDiffInDaysFromCurrent(it.timeStarted) < 7
        }.filter { // Not add current connection
            it.timeUpdated != CURRENT_CONNECTION
        }
    }
}
