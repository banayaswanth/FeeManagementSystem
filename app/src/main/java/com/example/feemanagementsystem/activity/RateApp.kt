package com.example.feemanagementsystem.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import com.example.feemanagementsystem.R

class RateApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_app)
        showRateFeedbackDialog()
    }
    private fun showRateFeedbackDialog() {

    }
}