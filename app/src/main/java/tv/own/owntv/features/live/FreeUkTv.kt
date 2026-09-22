package tv.own.owntv.features.live

import android.content.Context

/** Built-in curated free UK live-TV source. The playlist contains only direct public CDN URLs. */
object FreeUkTv {
    const val SOURCE_NAME = "🇬🇧 Free UK TV"
    const val GROUP_NAME = "🇬🇧 Free UK TV"
    const val PLAYLIST_URL = "https://raw.githubusercontent.com/Anarchyukz/OwnTVz/24ea038e735e5f8e76dc9f970d04e46697f57cfe/app/src/main/assets/free_uk_tv.m3u"
    const val TV_LICENCE_PREF = "uk_live_tv_licence_acknowledged"

    fun isAcknowledged(context: Context): Boolean =
        context.getSharedPreferences("owntv_legal", Context.MODE_PRIVATE)
            .getBoolean(TV_LICENCE_PREF, false)

    fun setAcknowledged(context: Context, value: Boolean) {
        context.getSharedPreferences("owntv_legal", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(TV_LICENCE_PREF, value)
            .apply()
    }
}
