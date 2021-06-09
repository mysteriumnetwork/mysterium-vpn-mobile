package updated.mysterium.vpn.database.preferences

import android.content.Context

class SharedPreferencesManager(private val context: Context) {

    fun setPreferenceValue(key: SharedPreferencesList, value: Int) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit().let {
                it.putInt(key.prefName, value)
                it.apply()
            }
    }

    fun setPreferenceValue(key: SharedPreferencesList, value: Boolean) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit().let {
                it.putBoolean(key.prefName, value)
                it.apply()
            }
    }

    fun setPreferenceValue(key: SharedPreferencesList, value: String) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit().let {
                it.putString(key.prefName, value)
                it.apply()
            }
    }

    fun containsPreferenceValue(key: SharedPreferencesList) = context
        .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
        .contains(key.prefName)

    fun getIntPreferenceValue(key: SharedPreferencesList, defValue: Int = 1) = context
        .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
        .getInt(key.prefName, defValue)

    fun getStringPreferenceValue(key: SharedPreferencesList, defValue: String? = null) = context
        .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
        .getString(key.prefName, defValue)

    fun getBoolPreferenceValue(key: SharedPreferencesList, defValue: Boolean = false) = context
        .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
        .getBoolean(key.prefName, defValue)

    fun removePreferenceValue(key: SharedPreferencesList) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit()
            .remove(key.prefName)
            .apply()
    }
}
