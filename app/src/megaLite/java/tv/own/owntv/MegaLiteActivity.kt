package tv.own.owntv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinInject
import org.koin.androidx.compose.koinViewModel
import tv.own.owntv.core.epg.EpgSourceStore
import tv.own.owntv.core.theme.AccentColor
import tv.own.owntv.core.theme.ThemeMode
import tv.own.owntv.features.epg.EpgScreen
import tv.own.owntv.features.live.LiveScreen
import tv.own.owntv.features.movies.MoviesScreen
import tv.own.owntv.features.series.SeriesScreen
import tv.own.owntv.features.settings.SettingsViewModel
import tv.own.owntv.ui.theme.OwnTVTheme

private val Black = Color(0xFF07080A)
private val Panel = Color(0xFF111318)
private val Raised = Color(0xFF191C22)
private val Line = Color(0xFF2B3038)
private val Muted = Color(0xFF8C949F)
private val Orange = Color(0xFFF59E0B)

private enum class Page { HOME, LIVE, SERIES, MOVIES, EPG, SETUP }

class MegaLiteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OwnTVTheme(
                themeMode = ThemeMode.DARK,
                accent = AccentColor.AMBER,
                systemInDarkTheme = true,
            ) {
                MegaLiteApp(onExit = { finish() })
            }
        }
    }
}

@Composable
private fun MegaLiteApp(onExit: () -> Unit) {
    var page by remember { mutableStateOf(Page.HOME) }
    var setupTab by remember { mutableStateOf(0) }

    BackHandlerCompat(enabled = page != Page.HOME) { page = Page.HOME }
    BackHandlerCompat(enabled = page == Page.HOME) { onExit() }

    Column(
        modifier = Modifier.fillMaxSize().background(Black),
    ) {
        MegaTopBar(page, onHome = { page = Page.HOME })
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 42.dp, vertical = 24.dp)) {
            when (page) {
                Page.HOME -> MegaHome(
                    onPage = { page = it },
                    onSetup = { setupTab = it; page = Page.SETUP },
                )
                Page.LIVE -> LiveScreen(
                    onFullscreen = {},
                    onChildFocused = {},
                    previewEnabled = true,
                    modifier = Modifier.fillMaxSize(),
                )
                Page.SERIES -> SeriesScreen(
                    onFullscreen = {},
                    onChildFocused = {},
                    modifier = Modifier.fillMaxSize(),
                )
                Page.MOVIES -> MoviesScreen(
                    onFullscreen = {},
                    onChildFocused = {},
                    modifier = Modifier.fillMaxSize(),
                )
                Page.EPG -> MegaEpgScreen(onHome = { page = Page.HOME })
                Page.SETUP -> MegaSetupScreen(
                    initialTab = setupTab,
                    onDone = { page = Page.HOME },
                )
            }
        }
    }
}

@Composable
private fun MegaTopBar(page: Page, onHome: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(78.dp)
            .background(Color(0xFF0B0D10))
            .padding(horizontal = 42.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(38.dp).background(Orange, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center,
            ) { Text("O", color = Black, fontSize = 21.sp, fontWeight = FontWeight.Black) }
            Column {
                Text("OwnTVz", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("MEGA LITE", color = Orange, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
        }
        if (page != Page.HOME) {
            FocusButton("HOME", onClick = onHome, modifier = Modifier.width(130.dp))
        } else {
            Text("LEAN • FAST • SIMPLE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        }
    }
}

@Composable
private fun MegaHome(
    onPage: (Page) -> Unit,
    onSetup: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            HeroCard(
                modifier = Modifier.weight(1.6f),
                title = "Your TV. Simplified.",
                subtitle = "A clean, fast interface with the original playback engine underneath.",
                onClick = { onPage(Page.LIVE) },
            )
            StatusCard(modifier = Modifier.weight(1f))
        }

        Text("WATCH", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HomeCard("LIVE TV", "Channels", { onPage(Page.LIVE) })
            HomeCard("SERIES", "Shows & episodes", { onPage(Page.SERIES) })
            HomeCard("MOVIES", "Films", { onPage(Page.MOVIES) })
            HomeCard("EPG", "Programme guide", { onPage(Page.EPG) })
        }

        Text("QUICK SETUP", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HomeCard("ADD PLAYLIST", "M3U / service URL", { onSetup(0) }, Modifier.weight(1f))
            HomeCard("ADD EPG", "XMLTV guide URL", { onSetup(1) }, Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))
        Text(
            "The old sidebar and old Mega Lite launcher UI are not used in this build.",
            color = Color(0xFF5E6670),
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun HeroCard(modifier: Modifier, title: String, subtitle: String, onClick: () -> Unit) {
    FocusPanel(modifier.height(178.dp).then(modifier), onClick) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text("MEGA LITE", color = Orange, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(title, color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun StatusCard(modifier: Modifier) {
    Box(modifier.height(178.dp).then(modifier).background(Panel, RoundedCornerShape(18.dp)).border(1.dp, Line, RoundedCornerShape(18.dp)).padding(22.dp)) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text("BUILT AROUND THE CORE", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text("Playback, playlists, EPG and content handling stay intact. Only the presentation layer is being replaced.", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RowScope.HomeCard(title: String, detail: String, onClick: () -> Unit, modifier: Modifier = Modifier.weight(1f)) {
    FocusPanel(modifier.height(126.dp).then(modifier), onClick) {
        Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(detail, color = if (title.startsWith("ADD")) Orange else Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun MegaSetupScreen(initialTab: Int, onDone: () -> Unit) {
    var tab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val vm: SettingsViewModel = koinViewModel()
    val epgStore: EpgSourceStore = koinInject()
    val scope = rememberCoroutineScope()
    var playlistName by remember { mutableStateOf("") }
    var playlistUrl by remember { mutableStateOf("") }
    var playlistEpg by remember { mutableStateOf("") }
    var epgName by remember { mutableStateOf("UK EPG") }
    var epgUrl by remember { mutableStateOf("https://epgshare01.online/epgshare01/epg_ripper_UK1.xml.gz") }
    var message by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("QUICK SETUP", color = Orange, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FocusButton("PLAYLIST", { tab = 0 }, Modifier.width(180.dp))
            FocusButton("EPG", { tab = 1 }, Modifier.width(180.dp))
        }
        if (tab == 0) {
            Text("Add your playlist without opening the old Settings screen.", color = Muted, fontSize = 13.sp)
            MegaInput("NAME", playlistName, { playlistName = it })
            MegaInput("M3U / SERVICE URL", playlistUrl, { playlistUrl = it })
            MegaInput("OPTIONAL PLAYLIST EPG URL", playlistEpg, { playlistEpg = it })
            FocusButton("ADD PLAYLIST", {
                if (playlistUrl.isBlank()) {
                    message = "Enter a playlist URL first."
                } else {
                    vm.addM3u(
                        name = playlistName.ifBlank { "Playlist" },
                        url = playlistUrl,
                        epgUrl = playlistEpg,
                    )
                    message = "Playlist import started."
                }
            }, Modifier.width(220.dp))
        } else {
            Text("Add a standalone XMLTV source.", color = Muted, fontSize = 13.sp)
            MegaInput("NAME", epgName, { epgName = it })
            MegaInput("XMLTV URL", epgUrl, { epgUrl = it })
            FocusButton("ADD EPG", {
                if (epgUrl.isBlank()) {
                    message = "Enter an EPG URL first."
                } else {
                    scope.launch {
                        epgStore.add(epgName.ifBlank { "EPG" }, epgUrl)
                        message = "EPG source added."
                    }
                }
            }, Modifier.width(220.dp))
        }
        if (message.isNotBlank()) Text(message, color = Orange, fontSize = 13.sp)
        Spacer(Modifier.weight(1f))
        FocusButton("DONE", onDone, Modifier.width(150.dp))
    }
}

@Composable
private fun MegaInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
                .background(Panel, RoundedCornerShape(12.dp))
                .border(1.dp, Line, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 13.dp),
        )
    }
}

@Composable
private fun MegaEpgScreen(onHome: () -> Unit) {
    val liveVm: tv.own.owntv.features.live.LiveViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    EpgScreen(
        onBack = onHome,
        onFullscreen = {},
        onPlayChannel = { channel, _ ->
            scope.launch { liveVm.ensurePlayingByIdAsync(channel.id, false) }
        },
        onPlayCatchup = { channel, programme ->
            liveVm.playCatchupProgramme(channel, programme)
        },
        onAddEpg = onHome,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun FocusPanel(modifier: Modifier, onClick: () -> Unit, content: @Composable () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .border(2.dp, if (focused) Orange else Line, RoundedCornerShape(18.dp))
            .background(if (focused) Raised else Panel, RoundedCornerShape(18.dp))
            .focusable()
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.DirectionCenter)) {
                    onClick()
                    true
                } else false
            },
    ) { content() }
}

@Composable
private fun FocusButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) =
    FocusPanel(modifier.height(48.dp), onClick) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        }
    }

@Composable
private fun BackHandlerCompat(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}
