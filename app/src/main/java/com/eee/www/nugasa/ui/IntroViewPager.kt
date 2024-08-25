package com.eee.www.nugasa.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eee.www.nugasa.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroViewPager(pagerState: PagerState, introPageInfo: List<IntroPageData>) {
    val scope = rememberCoroutineScope()
    Box (
        modifier = Modifier.background(Color.White)
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(1.0f),
            verticalAlignment = Alignment.CenterVertically,
            state = pagerState
        ) { page ->
            IntroPage(introPageInfo[page])
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            val context = LocalContext.current
            val colorsActive = colorResource(R.color.dot_active)
            val colorsInactive = colorResource(R.color.dot_inactive)

            // skip button (맨 마지막 페이지에서는 그리지 않는다)
            val skipButtonAlpha = if (isLastPage(pagerState)) 0f else 1f
            Button(
                onClick = {
                    if (skipButtonAlpha != 0f) {
                        context.findActivity()?.finish()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent
                ),
                modifier = Modifier.width(100.dp).alpha(skipButtonAlpha),
                elevation = null,
            ) {
                Text(
                    text = stringResource(id = R.string.skip),
                    color = colorResource(id = R.color.black),
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(Modifier.align(Alignment.CenterVertically)) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) colorsActive else colorsInactive
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.width(100.dp),
                onClick = {
                    if (isLastPage(pagerState)) {
                        context.findActivity()?.finish()
                    } else {
                        scope.launch {
                            // 코루틴 스코프 안에서만 호출이 가능하다고 함..
                            pagerState.scrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = null,
            ) {
                val buttonString =
                    if (isLastPage(pagerState)) stringResource(R.string.start) else stringResource(R.string.next)
                Text(
                    text = buttonString,
                    color = colorResource(id = R.color.black),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun isLastPage(pagerState: PagerState) = pagerState.currentPage == pagerState.pageCount - 1

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewIntroViewPager() {
    val introPageInfo: List<IntroPageData> = listOf(
        IntroPageData(R.drawable.gather_people, R.string.slide_1_title, R.string.gather_people_content_description),
        IntroPageData(R.drawable.put_finger, R.string.slide_2_title, R.string.put_finger_content_description),
        IntroPageData(R.drawable.selected, R.string.slide_3_title, R.string.selected_content_description)
    )
    val pagerState = rememberPagerState(pageCount = { introPageInfo.size })
    IntroViewPager(pagerState = pagerState, introPageInfo)
}