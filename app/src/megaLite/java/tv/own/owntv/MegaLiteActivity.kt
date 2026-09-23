package tv.own.owntv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text

private val MegaBlack = Color(0xFF07080A)
private val MegaPanel = Color(0xFF101216)
private val MegaPanelRaised = Color(0xFF171A20)
private val MegaLine = Color(0xFF292D34)
private val MegaMuted = Color(0xFF8B929C)
private val MegaOrange = Color(0xFFF59E0B)
private val MegaOrangeSoft = Color(0xFF2A1D08)

class MegaLiteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MegaLiteHome(
                onOpenFullApp = { destination ->
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra(
                            EXTRA_MEGA_LITE_DESTINATION,
                            when (destination) {
                                "LIVE TV" -> "guide"
                                "SERIES" -> "series"
                                "MOVIES" -> "movies"
                                "ADD PLAYLIST" -> "playlist"
                                "ADD EPG" -> "epg"
                                else -> null
                            },
                        )
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    startActivity(intent)
                },
            )
        }
    }
}

@Composable
private fun MegaLiteHome(onOpenFullApp: (String) -> Unit) {
    val firstFocus = remember { FocusRequester() }
    var focusedTitle by remember { mutableStateOf("LIVE TV") }

    LaunchedEffect(Unit) {
        firstFocus.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MegaBlack)
            .padding(horizontal = 64.dp, vertical = 34.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MegaHeader()

            Spacer(Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                MegaHeroCard(
                    modifier = Modifier
                        .weight(1.55f)
                        .height(176.dp),
                    title = "Your TV, simplified",
                    subtitle = "Live channels, series and movies in one place.",
                    focused = focusedTitle == "HERO",
                    onFocused = { focusedTitle = "HERO" },
                    onSelected = { onOpenFullApp("LIVE TV") },
                )

                MegaStatusCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(176.dp),
                )
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "WATCH",
                color = MegaMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MegaWatchCard(
                    item = MegaLiteItem("LIVE TV", "LIVE", "Watch live channels"),
                    modifier = Modifier
                        .weight(1f)
                        .height(190.dp)
                        .then(if (focusedTitle == "LIVE TV") Modifier.focusRequester(firstFocus) else Modifier),
                    focused = focusedTitle == "LIVE TV",
                    onFocused = { focusedTitle = "LIVE TV" },
                    onSelected = { onOpenFullApp("LIVE TV") },
                )
                MegaWatchCard(
                    item = MegaLiteItem("SERIES", "SERIES", "Browse your shows"),
                    modifier = Modifier
                        .weight(1f)
                        .height(190.dp),
                    focused = focusedTitle == "SERIES",
                    onFocused = { focusedTitle = "SERIES" },
                    onSelected = { onOpenFullApp("SERIES") },
                )
                MegaWatchCard(
                    item = MegaLiteItem("MOVIES", "MOVIES", "Find something to watch"),
                    modifier = Modifier
                        .weight(1f)
                        .height(190.dp),
                    focused = focusedTitle == "MOVIES",
                    onFocused = { focusedTitle = "MOVIES" },
                    onSelected = { onOpenFullApp("MOVIES") },
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "QUICK SETUP",
                color = MegaMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MegaSetupCard(
                    title = "ADD PLAYLIST",
                    detail = "Connect your TV service",
                    symbol = "+",
                    focused = focusedTitle == "ADD PLAYLIST",
                    modifier = Modifier.weight(1f),
                    onFocused = { focusedTitle = "ADD PLAYLIST" },
                    onSelected = { onOpenFullApp("ADD PLAYLIST") },
                )
                MegaSetupCard(
                    title = "ADD EPG",
                    detail = "Add programme guide data",
                    symbol = "▤",
                    focused = focusedTitle == "ADD EPG",
                    modifier = Modifier.weight(1f),
                    onFocused = { focusedTitle = "ADD EPG" },
                    onSelected = { onOpenFullApp("ADD EPG") },
                )
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "MEGA LITE",
                    color = Color(0xFF5F6670),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                )
                Text(
                    text = "D-pad to navigate  •  OK to select",
                    color = Color(0xFF626974),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun MegaHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(MegaOrange, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "O",
                    color = MegaBlack,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            Column {
                Text(
                    text = "OwnTVz",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "MEGA LITE",
                    color = MegaOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }
        }

        Row(
            modifier = Modifier
                .background(MegaPanel, RoundedCornerShape(20.dp))
                .border(1.dp, MegaLine, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Color(0xFF55C271), RoundedCornerShape(50)),
            )
            Text(
                text = "READY",
                color = Color(0xFFB9C1CB),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
            )
        }
    }
}

@Composable
private fun MegaHeroCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    focused: Boolean,
    onFocused: () -> Unit,
    onSelected: () -> Unit,
) {
    MegaFocusablePanel(
        modifier = modifier,
        focused = focused,
        onFocused = onFocused,
        onSelected = onSelected,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "WELCOME BACK",
                color = MegaOrange,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.7.sp,
            )
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    color = MegaMuted,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun MegaStatusCard(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(MegaPanel, RoundedCornerShape(18.dp))
            .border(1.dp, MegaLine, RoundedCornerShape(18.dp))
            .padding(22.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "SETUP STATUS",
                color = MegaMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MegaStatusRow("Playlist", "Add when ready")
                MegaStatusRow("EPG", "Optional")
            }
        }
    }
}

@Composable
private fun MegaStatusRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = MegaMuted, fontSize = 12.sp)
    }
}

private data class MegaLiteItem(
    val title: String,
    val tag: String,
    val detail: String,
)

@Composable
private fun MegaWatchCard(
    item: MegaLiteItem,
    modifier: Modifier,
    focused: Boolean,
    onFocused: () -> Unit,
    onSelected: () -> Unit,
) {
    MegaFocusablePanel(
        modifier = modifier,
        focused = focused,
        onFocused = onFocused,
        onSelected = onSelected,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .background(if (focused) MegaOrangeSoft else Color(0xFF191C21), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = item.tag,
                    color = if (focused) MegaOrange else MegaMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.detail,
                    color = MegaMuted,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun MegaSetupCard(
    title: String,
    detail: String,
    symbol: String,
    focused: Boolean,
    modifier: Modifier,
    onFocused: () -> Unit,
    onSelected: () -> Unit,
) {
    MegaFocusablePanel(
        modifier = modifier.height(82.dp),
        focused = focused,
        onFocused = onFocused,
        onSelected = onSelected,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = symbol,
                color = if (focused) MegaOrange else Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = detail,
                    color = MegaMuted,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun MegaFocusablePanel(
    modifier: Modifier,
    focused: Boolean,
    onFocused: () -> Unit,
    onSelected: () -> Unit,
    content: @Composable () -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (focused) MegaOrange else MegaLine,
        label = "megaLiteBorder",
    )
    val borderWidth by animateDpAsState(
        targetValue = if (focused) 2.dp else 1.dp,
        label = "megaLiteBorderWidth",
    )
    val background = if (focused) MegaPanelRaised else MegaPanel

    Box(
        modifier = modifier
            .border(borderWidth, borderColor, RoundedCornerShape(18.dp))
            .background(background, RoundedCornerShape(18.dp))
            .onFocusChanged { if (it.isFocused) onFocused() }
            .onKeyEvent {
                if (
                    it.type == KeyEventType.KeyUp &&
                    (it.key == Key.Enter || it.key == Key.DirectionCenter)
                ) {
                    onSelected()
                    true
                } else {
                    false
                }
            }
            .focusable(),
    ) {
        content()
    }
}
