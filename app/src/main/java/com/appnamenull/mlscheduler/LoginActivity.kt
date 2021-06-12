package com.appnamenull.mlscheduler

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private val RC_SIGN_IN = 1
    private val uidPreferences by lazy { getSharedPreferences("useruid", Context.MODE_PRIVATE) }
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        prefs = getSharedPreferences("firstTime", MODE_PRIVATE)
        println("LOGIN onCreate firstTime : ${prefs?.getBoolean("firstTime", true)}")
        if (prefs!!.getBoolean("firstTime", true)) {
            mAuth = FirebaseAuth.getInstance()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            sign_in_google.setOnClickListener {
                signIn()
            }
            btnNext.setOnClickListener {
                startActivity(Intent(this, TasksActivity::class.java))
                finish()
            }
        }
        else{
            println("LOGIN onResume firstTime false")
            if(checkUsageStatePermission()) {
                val intent = Intent(this, TasksActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    //Obvious
    private fun checkUsageStatePermission() : Boolean{
        println("checkUsageStatePermission start")
        sign_in_google.visibility = View.GONE
        sign_in_anon.visibility = View.GONE
        btnNext.visibility = View.VISIBLE
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow( AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), this.packageName)
        println("checkUsageStatePermission end")
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult( requestCode: Int, resultCode: Int, @Nullable data: Intent? ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else
            Toast.makeText(this, "Please retry", Toast.LENGTH_SHORT).show()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            println("HANDLESIGNINRESULT\tcompletedTask : $completedTask\tcompletedTaskresult : ${completedTask.result}\tcompletedTaskexception : ${completedTask.exception}")
            val acc: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show()
            if(acc != null)
                FirebaseGoogleAuth(acc)
        } catch (e: ApiException) {
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
            FirebaseGoogleAuth(null)
        }
    }

    private fun FirebaseGoogleAuth(acct: GoogleSignInAccount?) {
        if (acct != null) {
            val authCredential = GoogleAuthProvider.getCredential(acct.idToken, null)
            println("FIREBASEGOOGLEAUTH\tauthCredential : $authCredential")
            mAuth!!.signInWithCredential(authCredential)
                .addOnCompleteListener(this ) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                        val user = mAuth!!.currentUser
                        if (user != null) {
                            uidPreferences.edit().putString("useruid", user.uid).apply()
                        }
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
        else
            Toast.makeText(this, "acc failed", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(fUser: FirebaseUser?) {
        println("UPDATE UI")
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null)
            Toast.makeText(this, "${account.displayName} we need Usage Permission.", Toast.LENGTH_LONG).show()

        //CHANGE G BUTTON TO NEXT BUTTON ON GETTING PERMISSION

        //Checks if app's running for first time, thus either gets total usage till 9 days ago or gets usage in time difference

        if(checkUsageStatePermission()) {
            println("UPDATE UI checkUsageStatePermission")
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            println("UPDATE UI startActivity")
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }
}
