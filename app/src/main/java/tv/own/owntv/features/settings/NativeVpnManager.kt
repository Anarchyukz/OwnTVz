package tv.own.owntv.features.settings

import android.content.Context
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import tv.own.owntv.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

/**
 * OwnTV's native WireGuard controller.
 *
 * The official WireGuard Android tunnel library owns the Android VpnService and wireguard-go
 * userspace backend. OwnTV only supplies a parsed WireGuard configuration and a tunnel name.
 */
object NativeVpnManager {
    private const val PREFS = "native_vpn"
    private const val KEY_CONFIG = "wireguard_config"
    private const val KEY_PROVIDER = "provider"

    private lateinit var backend: GoBackend
    private lateinit var appContext: Context

    private val tunnel = object : Tunnel {
        override fun getName(): String = "owntv-vpn"
        override fun onStateChange(newState: Tunnel.State) = Unit
    }

    fun initialize(context: Context) {
        if (!::backend.isInitialized) {
            appContext = context.applicationContext
            backend = GoBackend(appContext)
        }
    }

    fun savedConfig(): String =
        if (::appContext.isInitialized) {
            appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_CONFIG, "") ?: ""
        } else ""

    fun savedProvider(): String =
        if (::appContext.isInitialized) {
            appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_PROVIDER, appContext.getString(R.string.settings_vpn_provider_custom_wireguard)) ?: appContext.getString(R.string.settings_vpn_provider_custom_wireguard)
        } else ""

    fun saveConfig(provider: String, config: String) {
        appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PROVIDER, provider)
            .putString(KEY_CONFIG, config)
            .apply()
    }

    suspend fun isConnected(): Boolean = withContext(Dispatchers.IO) {
        if (!::backend.isInitialized) return@withContext false
        backend.getState(tunnel) == Tunnel.State.UP
    }

    suspend fun connect(configText: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (configText.isBlank()) throw IllegalArgumentException()
            val config = Config.parse(ByteArrayInputStream(configText.toByteArray(Charsets.UTF_8)))
            backend.setState(tunnel, Tunnel.State.UP, config)
            Unit
        }
    }

    suspend fun disconnect(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (::backend.isInitialized) {
                backend.setState(tunnel, Tunnel.State.DOWN, null)
            }
        }
    }
}
