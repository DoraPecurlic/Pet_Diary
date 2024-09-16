package com.example.petdiary.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.petdiary.R
import com.example.petdiary.components.CButton
import com.example.petdiary.components.CTextField
import com.example.petdiary.ui.theme.AlegreyaFontFamily
import com.example.petdiary.ui.theme.AlegreyaSansFontFamily
import com.example.petdiary.utilities.AuthHelper
import com.example.petdiary.viewModel.RegisterViewModel


@Composable
fun RegisterScreen(
    navController: NavHostController,
    context: Context,
    registerViewModel: RegisterViewModel?

){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val stayLoggedIn = remember { mutableStateOf(AuthHelper.isRememberMeChecked(context)) }


    Surface(
        color = Color(0xFF253334),
        modifier = Modifier.fillMaxSize()
    ){
        Box(modifier =  Modifier.fillMaxSize()){

            Image(painter = painterResource(id = R.drawable.bg1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .align(Alignment.BottomCenter)
            )



            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ){

                // Logo
                Image(painter = painterResource(id = R.drawable.logo3),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 54.dp)
                        .height(100.dp)
                )



                Text(text = "Sign Up",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = AlegreyaFontFamily,
                        fontWeight = FontWeight(500),
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )


                Text("Sign Up to track your pet's adventures and needs.",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = AlegreyaSansFontFamily,
                        color = Color(0xB2FFFFFF)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )



                CTextField(
                    hint = "Email Address",
                    value = email,
                    onValueChange = { newValue -> email = newValue }
                )

                CTextField(
                    hint = "Password",
                    value = password,
                    onValueChange = { newValue -> password = newValue },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(24.dp))

                CButton(
                    text = "Sign Up",
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            registerViewModel?.register(context, email, password,stayLoggedIn.value, navController)
                        } else {
                            errorMessage = "Please fill out all fields."
                        }
                    }
                )


                Spacer(modifier = Modifier.height(3.dp))


                Row(
                    modifier = Modifier.padding(top=12.dp, bottom = 52.dp)
                ){
                    Text("Already have an account? ",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = AlegreyaSansFontFamily,
                            color = Color.White
                        )
                    )

                    Text("Sign In",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = AlegreyaSansFontFamily,
                            fontWeight = FontWeight(800),
                            color = Color.White
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate("login")
                        }
                    )


                }




            }

        }
    }
}






@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen( navController = rememberNavController(), context = LocalContext.current, registerViewModel = null)
}