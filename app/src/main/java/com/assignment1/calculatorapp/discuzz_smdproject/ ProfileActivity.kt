package com.assignment1.calculatorapp.discuzz_smdproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var bioEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var changePasswordButton: Button

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firstNameTextView = findViewById(R.id.firstNameTextView)
        lastNameTextView = findViewById(R.id.lastNameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        bioEditText = findViewById(R.id.bioEditText)
        genderEditText = findViewById(R.id.genderEditText)
        oldPasswordEditText = findViewById(R.id.oldPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        saveButton = findViewById(R.id.saveButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        loadUserInfo()

        saveButton.setOnClickListener {
            saveAdditionalInfo()
        }

        changePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun loadUserInfo() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).get().addOnSuccessListener {
            firstNameTextView.text = it.child("firstName").value.toString()
            lastNameTextView.text = it.child("lastName").value.toString()
            emailTextView.text = it.child("email").value.toString()
            bioEditText.setText(it.child("bio").value?.toString() ?: "")
            genderEditText.setText(it.child("gender").value?.toString() ?: "")
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAdditionalInfo() {
        val userId = auth.currentUser?.uid ?: return
        val bio = bioEditText.text.toString().trim()
        val gender = genderEditText.text.toString().trim()

        val updates = mapOf(
            "bio" to bio,
            "gender" to gender
        )

        database.child("Users").child(userId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Info saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun changePassword() {
        val oldPassword = oldPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val user = auth.currentUser ?: return

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            return
        }

        val email = user.email ?: return
        val credential = EmailAuthProvider.getCredential(email, oldPassword)

        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update password: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}