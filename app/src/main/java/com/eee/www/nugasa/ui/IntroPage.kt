package com.eee.www.nugasa.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.eee.www.nugasa.R

data class IntroPageData(val drawableId: Int, val titleId: Int, val contentDescriptionId: Int)

@Composable
fun IntroPage(introPageData: IntroPageData) {
    Surface (modifier = Modifier.fillMaxWidth()){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.img_size)),
                painter = painterResource(id = introPageData.drawableId),
                contentDescription = stringResource(id = introPageData.contentDescriptionId)
            )
            Text(
                text = stringResource(id = introPageData.titleId),
                color = colorResource(id = android.R.color.black),
                fontSize = dimensionResource(id = R.dimen.slide_title).value.sp,
                modifier = Modifier.padding(all = dimensionResource(id = R.dimen.slide_title)),
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    lineHeight = 1.2.em
                )
            )
        }
    }
}

@Preview
@Composable
private fun IntroSlidePreview() {
    MaterialTheme {
        IntroPage(
            IntroPageData(
                drawableId = R.drawable.gather_people,
                titleId = R.string.slide_1_title,
                contentDescriptionId = R.string.gather_people_content_description
            )
        )
    }
}
