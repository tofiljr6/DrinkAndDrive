package com.example.drinkdrive.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import www.sanju.motiontoast.MotionToast


class SettingsActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var shared: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        shared = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        findViewById<SwitchCompat>(R.id.notify).isChecked =
            shared.getString("notifications", "false") == "true"

        try {
            database = Room.databaseBuilder(
                this,
                AppDatabase::class.java,
                "alcoholDrunk.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        } catch (e: Exception) {
            Log.d("db_D&D", e.message.toString())
        }
    }

    fun deleteHistory(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("CAUTION")
        builder.setMessage("Are you sure to delete all drink history?")
        builder.setPositiveButton("YES") { _, _ ->
            database.alcoholDrunkDAO().deleteAll(Firebase.auth.currentUser!!.uid)
        }
        builder.setNegativeButton("NO") { _, _ -> }
        builder.setIcon(R.drawable.ic_warning_yellow)
        val dialog = builder.create()
        dialog.show()

    }

    fun notifications(view: View) {
        val editor = shared.edit()
        editor.putString("notifications", (view as SwitchCompat).isChecked.toString())
        editor.commit()
    }

    fun deleteUser(view: View) {
        var emailData = ""
        var passwordData = ""
        val user = Firebase.auth.currentUser!!
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete account")
        val layout = LinearLayout(this)
        val email = EditText(this)
        email.inputType = InputType.TYPE_CLASS_TEXT
        email.setHint("Email")
        val password = EditText(this)
        password.inputType =  InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        password.setHint("Password")
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(email)
        layout.addView(password)
        builder.setView(layout)
        builder.setPositiveButton(
            "CONFIRM"
        ) { _, _ ->
            emailData = email.text.toString()
            passwordData = password.text.toString()
            val credential = EmailAuthProvider
                .getCredential(emailData, passwordData)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    database.alcoholDrunkDAO()
                                        .deleteAll(user.uid)
                                    database.alcoholDAO().deleteAll(user.uid)
                                    val editor = shared.edit()
                                    editor.putString("user", "noLogged")
                                    editor.commit()
                                    intent.putExtra("operation",1)
                                    setResult(Activity.RESULT_OK,intent)
                                    finish()
                                }
                            }
                    } else {
                        MotionToast.createColorToast(this,"Error","Invalid email or password",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this,R.font.helvetica_regular))
                    }
                }
        }
        builder.setNegativeButton(
            "CANCEL"
        ) { _, _ -> }
        builder.show()
    }


    fun deleteAlcohols(view: View) {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("CAUTION")
        builder.setMessage("Are you sure to delete your alcohols?")
        builder.setPositiveButton("YES"){ _, _->
            database.alcoholDAO().deleteAll(Firebase.auth.currentUser!!.uid)
            intent.putExtra("operation",2)
            setResult(Activity.RESULT_OK,intent)
        }
        builder.setNegativeButton("NO"){ _, _->}
        builder.setIcon(R.drawable.ic_warning_yellow)
        val dialog=builder.create()
        dialog.show()
    }

    fun changeUserName(view: View) {
        val user = Firebase.auth.currentUser
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change username")
        val username = EditText(this)
        username.inputType = InputType.TYPE_CLASS_TEXT
        username.setHint("Username")
        builder.setView(username)
        builder.setPositiveButton(
            "CONFIRM"
        ) { _, _ ->
            val profileUpdates = userProfileChangeRequest {
                displayName = username.text.toString()
            }
            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MotionToast.createColorToast(this,"Success","You changed username",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this,R.font.helvetica_regular))
                    }
                }
        }
        builder.setNegativeButton(
            "CANCEL"
        ) { _, _ -> }
        builder.show()


    }

    fun changePassword(view: View) {
        val user = Firebase.auth.currentUser!!
        var emailData = ""
        var passwordData = ""
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change password")
        val layout = LinearLayout(this)
        val email = EditText(this)
        email.inputType = InputType.TYPE_CLASS_TEXT
        email.setHint("Email")
        val password = EditText(this)
        password.inputType =  InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        password.setHint("Password")
        val newPassword = EditText(this)
        newPassword.inputType =  InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPassword.setHint("New password")
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(email)
        layout.addView(password)
        layout.addView(newPassword)
        builder.setView(layout)
        builder.setPositiveButton(
            "CONFIRM"
        ) { _, _ ->
            emailData = email.text.toString()
            passwordData = password.text.toString()
            val credential = EmailAuthProvider
                .getCredential(emailData, passwordData)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user!!.updatePassword(newPassword.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    MotionToast.createColorToast(this,"Success","You updated password",
                                        MotionToast.TOAST_SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(this,R.font.helvetica_regular))
                                }
                            }
                    } else {
                        MotionToast.createColorToast(this,"Error","Invalid email or password",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this,R.font.helvetica_regular))
                    }
                }
        }
        builder.setNegativeButton(
            "CANCEL"
        ) { _, _ -> }
        builder.show()
    }
}
