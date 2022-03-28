package com.example.littlegreeneats

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.littlegreeneats.databinding.ActivitySignupPageBinding
import com.google.firebase.auth.FirebaseAuth

class SignupPageActivity : AppCompatActivity() {




    //ViewBinding
    private lateinit var binding: ActivitySignupPageBinding
    //ProgressDialog
    private lateinit var progressDialog:ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email=""
    private var password=""
    private var username=""
    private var phone=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating Account in...")
        progressDialog.setCanceledOnTouchOutside(false)


        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click, begin signup
        binding.signupBtn.setOnClickListener {
            //  before logging in, validate data
            validateData()
        }


    }
    private fun validateData(){
        //get data
        email = binding.emailSignup.text.toString().trim()
        password = binding.passwordSignup.text.toString().trim()
        username = binding.usernameSignup.text.toString().trim()
        phone= binding.phoneSignup.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.emailSignup.error = "invalid email format"
        }else if (TextUtils.isEmpty(password)){
            //no passwod entered
            binding.passwordSignup.error = "Please enter password"
        }else if (TextUtils.isEmpty(username)){
            //no passwod entered
            binding.usernameSignup.error = "Please enter username"
        }else if (TextUtils.isEmpty(phone)){
            //no passwod entered
            binding.phoneSignup.error = "Please enter phone"
        }
        else{
            firebaseSignup()
        }


    }

    private fun firebaseSignup() {
        //show progress
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //signup success
                progressDialog.dismiss()
                //get currentu user
                val firebaseUser=firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Account created with email $email", Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //signup failure
                progressDialog.dismiss()
                Toast.makeText(this, "Signup failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()//go back to the previous activity, when back button ogf actionbar clicked
        return super.onSupportNavigateUp()
    }
}