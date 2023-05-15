package com.example.feemanagementsystem.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment


@Suppress("DEPRECATION")
abstract class BaseFragment : Fragment() {
    var mProgress: ProgressDialog? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProgress = ProgressDialog(activity)
    }

    fun showDialog(msg: String?) {
        try {
            mProgress!!.setMessage(msg)
            mProgress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mProgress!!.isIndeterminate = false
            mProgress!!.setCancelable(false)
            mProgress!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeDialog() {
        if (mProgress != null) {
            mProgress!!.dismiss()
        }
    }
}