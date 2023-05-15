package com.example.feemanagementsystem.fragment

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast

class MyJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}
