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
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
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
                        "TV Licence required",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "UK law requires you to be covered by a valid TV Licence when watching live TV on any channel or streaming service. OwnTVz cannot check whether you have a licence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(18.dp))
                    OwnTVButton(
                        label = if (checked) "☑  I confirm that I am covered by a valid TV Licence" else "☐  I confirm that I am covered by a valid TV Licence",
                        onClick = { checked = !checked },
                        style = if (checked) OwnTVButtonStyle.PRIMARY else OwnTVButtonStyle.SECONDARY,
                        modifier = Modifier.fillMaxWidth().focusRequester(checkFocus),
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OwnTVButton(
                            "Continue",
                            onClick = { if (checked) onAccept() },
                            style = if (checked) OwnTVButtonStyle.PRIMARY else OwnTVButtonStyle.SECONDARY,
                            modifier = Modifier.weight(1f).focusRequester(acceptFocus),
                        )
                        OwnTVButton(
                            "Cancel",
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
