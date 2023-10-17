package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.theme.BodyLarge
import io.agora.chatroom.theme.errorColor50
import io.agora.chatroom.theme.neutralColor10
import io.agora.chatroom.theme.neutralColor70
import io.agora.chatroom.theme.neutralColor80
import io.agora.chatroom.theme.neutralColor98
import io.agora.chatroom.theme.primaryColor50
import io.agora.chatroom.theme.primaryColor60
import io.agora.chatroom.uikit.R

var isUserDefaultContent:Boolean = false

@ExperimentalFoundationApi
@Composable
fun TabLayoutWithViewPager(
    isDarkTheme: Boolean? = false,
    tabTitles: List<String>,
    vpContent: @Composable (pageIndex: Int) -> Unit = { isUserDefaultContent = true },
) {

    val itemIndex = remember { mutableStateOf(0) }
    val selectedItemIndex by itemIndex

    val tabItemIndex = remember { mutableStateOf(0) }
    val selectedTabItemIndex by tabItemIndex

    val pagerState = rememberPagerState(
        initialPage = selectedItemIndex,
        initialPageOffsetFraction = 0f
    ) {
        5
    }

    Scaffold(
        modifier = Modifier
            .padding(top = 5.dp)
            .height((LocalConfiguration.current.screenHeightDp / 2).dp),
        content = {
            it.calculateTopPadding()
            it.calculateBottomPadding()
            Column(modifier = Modifier.background(if (isDarkTheme == true) neutralColor10 else neutralColor98)) {
                TabRow(
                    modifier = Modifier.height(50.dp),
                    selectedTabIndex = selectedItemIndex,
                    containerColor = if (isDarkTheme == true) neutralColor10 else neutralColor98,
                    contentColor = if (isDarkTheme == true) neutralColor10 else neutralColor98,
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            content = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = title,
                                        style = BodyLarge,
                                        color = if (isDarkTheme == true) {
                                            if (selectedItemIndex == index) neutralColor98 else neutralColor98
                                        } else {
                                            if (selectedItemIndex == index) neutralColor10 else neutralColor70
                                        }
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.icon_tab_bottom_bg),
                                        contentDescription = "icon",
                                        modifier = Modifier
                                            .width(28.dp).height(10.dp),
                                        tint = if (selectedItemIndex == index) {
                                            if (isDarkTheme == true) primaryColor60 else primaryColor50
                                        } else {
                                            if (isDarkTheme == true) neutralColor10 else neutralColor98
                                        }
                                    )
                                }
                            },
                            selected = selectedItemIndex == index,
                            modifier = Modifier.background(
                                color = if (isDarkTheme == true) neutralColor10 else neutralColor98
                            ),
                            onClick = {
                                tabItemIndex.value = index
                            }
                        )
                    }
                }

//                HorizontalPager(
//                    pageCount = tabTitles.size,
//                    modifier = Modifier.height(200.dp),
//                    state = pagerState
//                ) {
//                    if (isUserDefaultContent) {
//                        DefaultVpContent(selectedItemIndex)
//                    } else {
//                        vpContent(selectedItemIndex)
//                    }
//                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .collect { index ->
                            itemIndex.value = index
                            tabItemIndex.value = index
                        }
                }

                LaunchedEffect(selectedTabItemIndex) {
                    snapshotFlow { selectedTabItemIndex }
                        .collect { index ->
                            itemIndex.value = index
                            pagerState.scrollToPage(index)
                        }
                }

            }
        }
    )
}

@Composable
fun DefaultVpContent(pageIndex:Int){
    LazyColumn() {
        item {
            when (pageIndex) {
                0 -> Text(modifier = Modifier.background(color = errorColor50), text = "Content for Tab 1")
                1 -> Text(modifier = Modifier.background(color = primaryColor50),text = "Content for Tab 2")
                2 -> Text(modifier = Modifier.background(color = neutralColor80),text = "Content for Tab 3")
            }
        }
    }
}


@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun PreviewTabLayoutWithViewPager() {
    val tabTitles = listOf("Tab 1", "Tab 2", "Tab 3")
    TabLayoutWithViewPager(tabTitles = tabTitles)
}