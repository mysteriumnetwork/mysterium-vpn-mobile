package updated.mysterium.vpn.database.preferences

import android.content.Context
import android.util.Log
import updated.mysterium.vpn.common.extensions.TAG

class SharedPreferencesManager(private val context: Context) {

    fun setPreferenceValue(key: SharedPreferencesList, value: Int) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit().let {
                it.putInt(key.prefName, value)
                it.apply()
            }
    }

    fun setPreferenceValue(key: SharedPreferencesList, value: Long) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit().let {
                it.putLong(key.prefName, value)
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

    fun getIntPreferenceValue(key: SharedPreferencesList, defValue: Int = 1): Int {
        return try {
            context
                .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
                .getInt(key.prefName, defValue)
        } catch (exception: Exception) {
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            defValue
        }
    }

    fun getLongPreferenceValue(key: SharedPreferencesList, defValue: Long = 0): Long {
        return try {
            context
                .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
                .getLong(key.prefName, defValue)
        } catch (exception: Exception) {
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            defValue
        }
    }

    fun getStringPreferenceValue(key: SharedPreferencesList, defValue: String? = null): String? {
        return try {
            context
                .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
                .getString(key.prefName, defValue)
        } catch (exception: Exception) {
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            defValue
        }
    }

    fun getBoolPreferenceValue(key: SharedPreferencesList, defValue: Boolean = false): Boolean {
        return try {
            context
                .getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
                .getBoolean(key.prefName, defValue)
        } catch (exception: Exception) {
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            defValue
        }
    }

    fun removePreferenceValue(key: SharedPreferencesList) {
        context.getSharedPreferences(key.prefName, Context.MODE_PRIVATE)
            .edit()
            .remove(key.prefName)
            .apply()
    }
}
