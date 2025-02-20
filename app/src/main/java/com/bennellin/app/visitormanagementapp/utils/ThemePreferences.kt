import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemePreferences {
    private const val PREF_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"

    fun saveTheme(context: Context, mode: Int) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putInt(KEY_THEME, mode).apply()
    }

    fun getTheme(context: Context): Int {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
