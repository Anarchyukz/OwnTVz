package tv.own.owntv.features.settings

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.widthIn
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.ui.components.OwnTVButton
import tv.own.owntv.ui.components.OwnTVButtonStyle
import tv.own.owntv.ui.components.OwnTVIcon
import tv.own.owntv.ui.components.roundedPanel
import tv.own.owntv.ui.theme.OwnTVTheme

private val vpnProviders = listOf(
    "Custom WireGuard",
    "NordVPN",
    "Surfshark",
    "ExpressVPN",
    "Proton VPN",
    "IPVanish",
    "CyberGhost",
    "Private Internet Access",
    "Mullvad",
    "Windscribe",
    "hide.me",
    "PureVPN",
    "AirVPN",
    "IVPN",
)

@Composable
fun VpnSettingsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val colors = OwnTVTheme.colors

    var provider by remember { mutableStateOf("Custom WireGuard") }
    var config by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showProviders by remember { mutableStateOf(false) }

    fun refreshState() {
        kotlinx.coroutines.MainScope().launch {
            connected = NativeVpnManager.isConnected()
        }
    }

    fun connectNow() {
        busy = true
        message = null
        kotlinx.coroutines.MainScope().launch {
            val result = NativeVpnManager.connect(config)
            busy = false
            connected = result.isSuccess
            message = result.exceptionOrNull()?.message
                ?: if (result.isSuccess) context.getString(R.string.settings_vpn_connected) else context.getString(R.string.settings_vpn_failed)
        }
    }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        if (it.resultCode == Activity.RESULT_OK) connectNow()
    }

    val configPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                ?: error(context.getString(R.string.settings_vpn_read_failed))
        }.onSuccess {
            config = it
            NativeVpnManager.saveConfig(provider, it)
            message = context.getString(R.string.settings_vpn_config_loaded)
        }.onFailure {
            message = it.message
        }
    }

    LaunchedEffect(Unit) {
        NativeVpnManager.initialize(context)
        provider = NativeVpnManager.savedProvider()
        config = NativeVpnManager.savedConfig()
        connected = NativeVpnManager.isConnected()
    }

    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .roundedPanel()
            .focusGroup()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Header(stringResource(R.string.settings_vpn_title), onBack)
        Spacer(Modifier.height(8.dp))

        GroupLabel(stringResource(R.string.settings_vpn_provider))
        Row2(
            icon = OwnTVIcon.NETWORK,
            title = provider,
            desc = stringResource(R.string.settings_vpn_provider_hint),
            chip = stringResource(R.string.settings_vpn_change),
            primaryChip = false,
            onClick = { showProviders = true },
        )

        Spacer(Modifier.height(12.dp))
        GroupLabel(stringResource(R.string.settings_vpn_configuration))
        Row2(
            icon = OwnTVIcon.NETWORK,
            title = stringResource(R.string.settings_vpn_import),
            desc = if (config.isBlank()) {
                stringResource(R.string.settings_vpn_no_config)
            } else {
                stringResource(R.string.settings_vpn_config_ready)
            },
            chip = stringResource(R.string.settings_vpn_choose_file),
            primaryChip = !config.isBlank(),
            onClick = {
                configPicker.launch(arrayOf("text/plain", "application/octet-stream"))
            },
        )

        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.settings_vpn_provider_note),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OwnTVButton(
                label = if (busy) stringResource(R.string.settings_vpn_working)
                else if (connected) stringResource(R.string.settings_vpn_disconnect)
                else stringResource(R.string.settings_vpn_connect),
                onClick = {
                    if (!busy && connected) {
                        busy = true
                        kotlinx.coroutines.MainScope().launch {
                            val result = NativeVpnManager.disconnect()
                            busy = false
                            connected = result.isFailure.not()
                            message = result.exceptionOrNull()?.message
                                ?: context.getString(R.string.settings_vpn_disconnected)
                        }
                    } else if (!busy) {
                        if (config.isBlank()) {
                            message = context.getString(R.string.settings_vpn_no_config)
                        } else {
                            val intent: Intent? = VpnService.prepare(context)
                            if (intent == null) connectNow() else vpnPermissionLauncher.launch(intent)
                        }
                    }
                },
                style = if (connected) OwnTVButtonStyle.SECONDARY else OwnTVButtonStyle.PRIMARY,
            )
        }

        Spacer(Modifier.height(12.dp))
        Text(
            if (connected) stringResource(R.string.settings_vpn_status_connected)
            else stringResource(R.string.settings_vpn_status_disconnected),
            style = MaterialTheme.typography.bodyMedium,
            color = if (connected) colors.primary else colors.onSurfaceVariant,
        )

        message?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.settings_vpn_privacy),
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }

    if (showProviders) {
        tv.own.owntv.ui.components.OwnTVPopup(
            onDismissRequest = { showProviders = false },
            fontScale = .50f,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(min = 420.dp),
            ) {
                Text(
                    stringResource(R.string.settings_vpn_provider),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                )
                Spacer(Modifier.height(12.dp))
                vpnProviders.forEach { item ->
                    Row2(
                        icon = OwnTVIcon.NETWORK,
                        title = item,
                        desc = null,
                        chip = null,
                        primaryChip = false,
                        onClick = {
                            provider = item
                            if (config.isNotBlank()) NativeVpnManager.saveConfig(item, config)
                            showProviders = false
                        },
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
