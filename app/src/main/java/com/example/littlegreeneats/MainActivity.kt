package com.example.littlegreeneats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent as Intent1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val loginBtn: Button = findViewById(R.id.loginBtn)
        val signupPageBtn: TextView = findViewById(R.id.SignupPageBtn)
        val usernameLogin: EditText = findViewById(R.id.usernameLogin);
        val passwordLogin: EditText = findViewById(R.id.passwordLogin);
        loginBtn.setOnClickListener {

            if (usernameLogin.text.trim().isNotEmpty() && passwordLogin.text.trim().isNotEmpty()){
                Toast.makeText(this, "Input Provided", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(this, "Input Required", Toast.LENGTH_LONG).show();

            }


        }
        signupPageBtn.setOnClickListener {
            val intent = Intent1 ( this, SignupPageActivity::class.java)
            startActivity(intent);
            Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show();
        }


    }
}