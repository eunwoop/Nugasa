package com.eee.www.nugasa.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import com.eee.www.nugasa.R

class IntroActivity : ComponentActivity() {
    private val introPageInfo: List<IntroPageData> = listOf(
        IntroPageData(
            R.drawable.gather_people,
            R.string.slide_1_title,
            R.string.gather_people_content_description
        ),
        IntroPageData(
            R.drawable.put_finger,
            R.string.slide_2_title,
            R.string.put_finger_content_description
        ),
        IntroPageData(
            R.drawable.selected,
            R.string.slide_3_title,
            R.string.selected_content_description
        )
    )
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pagerState = rememberPagerState(pageCount = { introPageInfo.size })
            IntroViewPager(pagerState, introPageInfo)
        }
        // making notification bar transparent
        changeStatusBarColor()
    }

    private fun changeStatusBarColor() {
        this.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }
    }
}