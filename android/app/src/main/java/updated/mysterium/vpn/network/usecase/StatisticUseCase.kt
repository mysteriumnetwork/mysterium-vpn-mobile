package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mysterium.SessionFilter
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.model.session.Session

class StatisticUseCase(private val nodeRepository: NodeRepository) {

    suspend fun getLastSessions(): List<Session> {
        val jsonResponse = nodeRepository.getLastSessions(SessionFilter())
        val collectionType = object : TypeToken<Collection<Session?>?>() {}.type
        return Gson().fromJson(String(jsonResponse), collectionType)
    }
}
