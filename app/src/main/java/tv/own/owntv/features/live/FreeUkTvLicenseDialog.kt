package tv.own.owntv.features.live

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.ui.components.OwnTVButton
import tv.own.owntv.ui.components.OwnTVButtonStyle
import tv.own.owntv.ui.components.dialogPanel
import tv.own.owntv.ui.components.modalScrim
import tv.own.owntv.ui.components.trapAllFocusExit
import tv.own.owntv.ui.theme.OwnTVTheme

@Composable
fun FreeUkTvLicenseDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = OwnTVTheme.colors
    var checked by remember { mutableStateOf(false) }
    val checkFocus = remember { FocusRequester() }
    val acceptFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) { runCatching { checkFocus.requestFocus() } }
    BackHandler { onDismiss() }

    tv.own.owntv.ui.components.OwnTVPopup(onDismissRequest = onDismiss) {
        tv.own.owntv.ui.theme.PopupFontTheme(fontScale = 0.78f) {
            Column(
                Modifier.fillMaxSize().modalScrim().trapAllFocusExit().focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    Modifier.dialogPanel(width = 560.dp, corner = 18.dp, padding = 24.dp),
                ) {
                    Text(
                        stringResource(R.string.free_uk_tv_licence_required),
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.free_uk_tv_licence_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(18.dp))
                    OwnTVButton(
                        label = stringResource(if (checked) R.string.free_uk_tv_licence_checked else R.string.free_uk_tv_licence_unchecked),
                        onClick = { checked = !checked },
                        style = if (checked) OwnTVButtonStyle.PRIMARY else OwnTVButtonStyle.SECONDARY,
                        modifier = Modifier.fillMaxWidth().focusRequester(checkFocus),
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OwnTVButton(
                            stringResource(R.string.common_ok),
                            onClick = { if (checked) onAccept() },
                            style = if (checked) OwnTVButtonStyle.PRIMARY else OwnTVButtonStyle.SECONDARY,
                            modifier = Modifier.weight(1f).focusRequester(acceptFocus),
                        )
                        OwnTVButton(
                            stringResource(R.string.common_cancel),
                            onClick = onDismiss,
                            style = OwnTVButtonStyle.SECONDARY,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
