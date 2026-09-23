package tv.own.owntv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

class MegaLiteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MegaLiteHome(
                onOpenFullApp = { destination ->
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra(EXTRA_MEGA_LITE_DESTINATION, when (destination) {
                            "TV GUIDE" -> "guide"
                            "SERIES" -> "series"
                            "MOVIES" -> "movies"
                            else -> null
                        })
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
    val items = listOf(
        MegaLiteItem("TV GUIDE", "▦"),
        MegaLiteItem("SERIES", "▶"),
        MegaLiteItem("MOVIES", "▰"),
    )
    var focusedIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A0C))
            .padding(horizontal = 72.dp, vertical = 42.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "OwnTVz",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "MEGA LITE",
                    color = Color(0xFFF59E0B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Choose what you want to watch",
                color = Color(0xFF9EA3AA),
                fontSize = 16.sp,
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                items.forEachIndexed { index, item ->
                    MegaLiteCard(
                        item = item,
                        focused = focusedIndex == index,
                        modifier = Modifier.weight(1f),
                        onFocused = { focusedIndex = index },
                        onSelected = { onOpenFullApp(item.title) },
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Use ◀ ▶ and OK",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color(0xFF777C83),
                fontSize = 13.sp,
            )
        }
    }
}

private data class MegaLiteItem(val title: String, val symbol: String)

@Composable
private fun MegaLiteCard(
    item: MegaLiteItem,
    focused: Boolean,
    modifier: Modifier,
    onFocused: () -> Unit,
    onSelected: () -> Unit,
) {
    val borderColor by animateColorAsState(
        if (focused) Color(0xFFF59E0B) else Color(0xFF25282D),
        label = "megaLiteBorder",
    )
    val background = if (focused) Color(0xFF1A1B1F) else Color(0xFF111317)

    Box(
        modifier = modifier
            .height(250.dp)
            .border(
                width = if (focused) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp),
            )
            .background(background, RoundedCornerShape(18.dp))
            .onFocusChanged { if (it.isFocused) onFocused() }
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp &&
                    (it.key == Key.Enter || it.key == Key.DirectionCenter)
                ) {
                    onSelected()
                    true
                } else {
                    false
                }
            }
            .focusable(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = item.symbol,
                color = if (focused) Color(0xFFF59E0B) else Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
