package com.example.littlegreeneats

import android.app.ProgressDialog
import android.content.pm.SigningInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.littlegreeneats.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception
import android.content.Intent as Intent1

class MainActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient

//  constants
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }
//    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog:ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth : FirebaseAuth
    private var email = ""
    private var password = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //configure the google SingIn
        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //don't worry if shows in red, will resolve when build first time
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //Googl SignIn Button, Click to begin
        binding.googleSignInBtn.setOnClickListener {
            //begin Google Signin
            Log.d(TAG, "onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        //handle click, open register activity
        binding.SignupPageBtn.setOnClickListener {
            val intent = Intent1 ( this, SignupPageActivity::class.java)
            startActivity(intent)
        }


        //handle click, begin login
        binding.loginBtn.setOnClickListener {
        //  before logging in, validate data
            validateData()
        }


    }
    private fun validateData(){
        //get data
        email =binding.usernameLogin.text.toString().trim()
        password =binding.passwordLogin.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.usernameLogin.error = "invalid email format"
        }else if (TextUtils.isEmpty(password)){
            //no passwod entered
            binding.passwordLogin.error = "Please enter password"
        }
        else{
        firebaseLogin()
        }
    }


    private fun firebaseLogin(){
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                //get user info
                val firebaseUser =firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "LoggedIn as $email", Toast.LENGTH_SHORT).show()
                startActivity(Intent1 ( this, HomePageActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun checkUser(){
        //if user is already logged in go to profile activity
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user is already logged in
            startActivity(Intent1 ( this, HomePageActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: android.content.Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from launching the Intent from GoogleSignInApi.getsigninintent(..);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
    //gGoogle SignIn success, now auth with firebase
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            } catch (e: Exception) {
                //failed google sign in
                Log.d(TAG, "onActivityResult: ${e.message}")

            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    //login success
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")

                    //get loggedin user
                    val firebaseUser = firebaseAuth.currentUser
                    //get user info
                    val uid = firebaseUser!!.uid
                    val email = firebaseUser!!.email

                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid:$uid")
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")


                    //check if user is now existing
                    if (authResult.additionalUserInfo!!.isNewUser){
                        //user is new ~ Accoount created
                        Log.d(TAG, "firebaseAuthWithGoogleAccount: Account creasted \n$email")
                        Toast.makeText(this, "Account created... \n$email",Toast.LENGTH_SHORT).show()

                    }
                    else{
                        //Existing user ~ LoggedIn
                        Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user \n$email")
                        Toast.makeText(this, "LoggedIn... \n$email",Toast.LENGTH_SHORT).show()
                    }

                    //start profile activity
                    startActivity(android.content.Intent(this,HomePageActivity::class.java))
                    finish()

                }.addOnFailureListener { e ->
                    //login failed
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Logged in failed due to ${e.message}")
                    Toast.makeText(this, "Login Failed due to ${e.message}",Toast.LENGTH_SHORT).show()
                }
    }
}


