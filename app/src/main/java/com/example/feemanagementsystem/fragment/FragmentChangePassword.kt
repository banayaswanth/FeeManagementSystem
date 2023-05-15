package com.example.feemanagementsystem.fragment

import android.database.DatabaseUtils
import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.databinding.FragmentChangePasswordBinding
import com.example.feemanagementsystem.global.DB
import com.example.feemanagementsystem.global.MyFunction

class FragmentChangePassword : Fragment() {

private lateinit var binding: FragmentChangePasswordBinding
private var db: DB?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding= FragmentChangePasswordBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Change Password"
        val textView = binding.text
        val shader = LinearGradient(0f, 0f, 0f, textView.textSize, Color.RED, Color.BLUE, Shader.TileMode.CLAMP)
        textView.paint.shader = shader
        val textView2 = binding.text2
        val shader2 = LinearGradient(0f, 0f, 0f, textView.textSize, Color.RED, Color.BLUE, Shader.TileMode.CLAMP)
        textView2.paint.shader = shader2

        db = activity?.let { DB(it) }
        fillOldMobile()
        binding.btnChangePassword.setOnClickListener{
            try{
                if (binding.edtNewPassword.text.toString().trim().isNotEmpty()) {
                    if (binding.edtNewPassword.text.toString()
                            .trim() == binding.edtConfirmPassword.text.toString().trim()
                    ) {
                        val sqlQuery = "UPDATE ADMIN SET PASSWORD=" + DatabaseUtils.sqlEscapeString(
                            binding.edtNewPassword.text.toString().trim()
                        ) + ""
                        db?.executeQuery(sqlQuery)
                        showToast("Password Changed Seccessfully")
                    } else {
                        showToast("Password not matched")
                    }
                }else{
                    showToast("Enter new Password")
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        binding.btnChangeMobile.setOnClickListener{
            try {
                if(binding.edtNewNumber.text.toString().trim().isNotEmpty()) {
                    val sqlQuery =
                        "UPDATE ADMIN SET MOBILE='" + binding.edtNewNumber.text.toString()
                            .trim() + "'"
                    db?.executeQuery(sqlQuery)
                    showToast("Mobile number changed successfully")
                }else{
                    showToast("Enter Nuw Mobile Number")
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    private fun fillOldMobile(){
        try{
            val sqlQuery = "SELECT MOBILE FROM ADMIN"
            db?.fireQuery(sqlQuery)?.use {
                val mobile = MyFunction.getValue(it,"MOBILE")
                if(mobile.trim().isNotEmpty()){
                    binding.edtOldNumber.setText(mobile)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun showToast(value:String){
        Toast.makeText(activity,value,Toast.LENGTH_LONG).show()
    }

}