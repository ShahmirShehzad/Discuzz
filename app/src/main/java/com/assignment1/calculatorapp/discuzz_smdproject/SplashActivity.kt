package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TEMP: Force logout to test MainActivity flow
//        FirebaseAuth.getInstance().signOut()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // User is already logged in
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // No user logged in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}
