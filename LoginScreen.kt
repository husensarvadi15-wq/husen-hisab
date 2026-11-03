package com.sarvadiverse.husenhisab.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sarvadiverse.husenhisab.ui.theme.PrimaryYellow
import com.sarvadiverse.husenhisab.ui.theme.PureBlack
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val callbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "ઓટો વેરિફિકેશન નિષ્ફળ: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isLoading = false
                Toast.makeText(context, "SMS મોકલવામાં ભૂલ: ${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                isLoading = false
                verificationId = id
                isOtpSent = true
                Toast.makeText(context, "OTP મોકલવામાં આવ્યો છે", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "હુસેન હિસાબ",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryYellow
        )
        Text(
            text = "દૈનિક હિસાબ અને રોજગાર વ્યવસ્થાપન",
            fontSize = 14.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (!isOtpSent) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { if (it.length <= 10) phoneNumber = it },
                label = { Text("મોબાઈલ નંબર (૧૦ આંકડા)", color = Color.Gray) },
                prefix = { Text("+91 ", color = PrimaryYellow) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryYellow,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (phoneNumber.length == 10) {
                        isLoading = true
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber("+91$phoneNumber")
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as Activity)
                            .setCallbacks(callbacks)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } else {
                        Toast.makeText(context, "માન્ય ૧૦ આંકડાનો નંબર દાખલ કરો", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PureBlack, modifier = Modifier.size(24.dp))
                } else {
                    Text("OTP મેળવો", color = PureBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        } else {
            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text("૬ આંકડાનો OTP દાખલ કરો", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryYellow,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val id = verificationId
                    if (id != null && otpCode.length == 6) {
                        isLoading = true
                        val credential = PhoneAuthProvider.getCredential(id, otpCode)
                        auth.signInWithCredential(credential).addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, "ખોટો OTP દાખલ કર્યો છે", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellow)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PureBlack, modifier = Modifier.size(24.dp))
                } else {
                    Text("લોગિન કરો", color = PureBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
