package io.agora.chatroom.compose.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.neutralColor20
import io.agora.chatroom.theme.neutralColor40
import io.agora.chatroom.theme.neutralColor60
import io.agora.chatroom.theme.neutralColor95
import io.agora.chatroom.uikit.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    iconResource: Int = R.drawable.icon_face,
    hint: String = "",
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconResource),
                modifier = Modifier.size(22.dp),
                contentDescription = "Search bar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = hint, color = ChatroomUIKitTheme.colors.tabUnSelected)
        }
    }
}

@Composable
fun DefaultSearchBar(
    iconResource: Int = R.drawable.icon_magnifier,
    hint: String = stringResource(id = R.string.search),
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = if (isSystemInDarkTheme()) neutralColor20 else neutralColor95,
                shape = RoundedCornerShape(size = 22.dp)
            )
            .height(44.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconResource),
                modifier = Modifier.size(22.dp),
                contentDescription = "Search bar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = hint, color = if (isSystemInDarkTheme()) neutralColor40 else neutralColor60)
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    ChatroomUIKitTheme {
        SearchBar(modifier = Modifier
            .fillMaxWidth()
            .clip(ChatroomUIKitTheme.shapes.medium)
            .border(BorderStroke(1.dp, ChatroomUIKitTheme.colors.tabUnSelected))
            .height(50.dp), hint = "Search")
    }
}