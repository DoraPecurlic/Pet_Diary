package com.example.petdiary.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petdiary.R
import com.example.petdiary.components.CButton
import com.example.petdiary.components.DontHaveAnAccRow
import com.example.petdiary.ui.theme.AlegreyaFontFamily
import com.example.petdiary.ui.theme.AlegreyaSansFontFamily


@Composable
fun WelcomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
){

    Box(
        modifier = modifier.fillMaxSize()
    ){

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ){
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo3),
                contentDescription = null,
                modifier = Modifier
                    .width(310.dp)
                    .height(210.dp),
                contentScale = ContentScale.Fit
            )


            Text(
                "WELCOME",
                fontSize = 32.sp,
                fontFamily = AlegreyaFontFamily,
                fontWeight = FontWeight(700),
                color = Color.White
            )

            Text(
                "\nFrom vet visits to daily walks, PetDiary ensures you never miss a moment.",
                textAlign = TextAlign.Center,
                fontFamily = AlegreyaSansFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            CButton(text = "Sign In With Email",
                onClick = {
                    navController.navigate("login")
                }
            )

            DontHaveAnAccRow(
                onSignupTap = {
                    navController.navigate("signup")
                }
            )





        }
    }

}


@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun WelcomeScreenPreview(){
    WelcomeScreen(rememberNavController())
}