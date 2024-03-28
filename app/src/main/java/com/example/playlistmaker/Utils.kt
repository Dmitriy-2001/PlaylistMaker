import android.content.SharedPreferences
import android.content.res.Resources

object Utils {
    fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }
}
