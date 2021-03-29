package updated.mysterium.vpn.database.preferences

import android.content.Context

class SharedPreferencesManager(private val context: Context) {

    fun setPreferenceValue(key: SharedPreferencesList, value: Boolean) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit().let {
                it.putBoolean(key.prefName, value)
                it.apply()
            }
    }

    fun getPreferenceValue(key: SharedPreferencesList) = context
        .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
        .contains(key.prefName)
}
