package updated.mysterium.vpn.network.usecase

import network.mysterium.terms.Terms
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.model.terms.FullVersionTerm

class TermsUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    private companion object {
        const val SHORT_VERSION_LABEL = "**SHORT VERSION IN HUMAN LANGUAGE:**"
        const val FULL_VERSION_LABEL = "**FULL VERSION:**"
        const val QUOTES = "**"
    }

    private val fullTermsData by lazy {
        Terms.endUserMD()
    }

    fun isTermsAccepted() = sharedPreferencesManager.containsPreferenceValue(SharedPreferencesList.TERMS)

    fun userAcceptTerms() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.TERMS,
        value = true
    )

    fun getShortTerms(): List<String> {
        val startPosition = fullTermsData.indexOf(SHORT_VERSION_LABEL)
        val termsWithoutStartedMetaInfo = fullTermsData.removeRange(
            startIndex = 0,
            endIndex = startPosition + SHORT_VERSION_LABEL.length
        )
        val endPosition = termsWithoutStartedMetaInfo.indexOf(QUOTES)
        val shortVersionTermsData = termsWithoutStartedMetaInfo.removeRange(
            startIndex = endPosition,
            endIndex = termsWithoutStartedMetaInfo.length - 1
        )
        return shortVersionTermsData
            .replace("\n", "")
            .replace("\r", "")
            .split('-')
            .drop(1)
    }

    fun getFullTerms(): List<FullVersionTerm> {
        val startPosition = fullTermsData.indexOf(FULL_VERSION_LABEL)
        val fullTerms = fullTermsData.removeRange(
            startIndex = 0,
            endIndex = startPosition + FULL_VERSION_LABEL.length
        )
        val endPosition = fullTerms.indexOf(QUOTES)
        val fullVersionTermsData = fullTerms.removeRange(
            startIndex = endPosition,
            endIndex = fullTerms.length - 1
        )
        var indexOfTerm = 1
        val fullTermsList = mutableListOf<FullVersionTerm>()
        while (fullVersionTermsData.indexOf("$indexOfTerm\\. ") != -1) {
            val startTermPosition = fullVersionTermsData.indexOf("$indexOfTerm\\. ")
            val endTermPosition = fullVersionTermsData.indexOf("${indexOfTerm + 1}\\. ")
            val term = if (endTermPosition != -1) {
                fullVersionTermsData.subSequence(startTermPosition, endTermPosition)
            } else {
                fullVersionTermsData.subSequence(startTermPosition, fullVersionTermsData.length - 1)
            }
            val termsWithoutIndex = term.toString().replace("$indexOfTerm\\. ", "")
            val title = termsWithoutIndex.subSequence(0, termsWithoutIndex.indexOf("\n"))
            val content = termsWithoutIndex.subSequence(termsWithoutIndex.indexOf("\n"), termsWithoutIndex.length - 1)
            fullTermsList.add(
                FullVersionTerm(
                    index = indexOfTerm,
                    title = title.toString(),
                    content = content.toString()
                )
            )
            indexOfTerm++
        }
        return fullTermsList
    }
}
