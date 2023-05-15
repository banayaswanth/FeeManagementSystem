package com.example.feemanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.databinding.FragmentAddMemberBinding
import com.example.feemanagementsystem.databinding.FragmentAddUpdateFeeBinding
import com.example.feemanagementsystem.global.DB
import com.example.feemanagementsystem.global.MyFunction


class FragmentAddUpdateFee : Fragment() {
    private lateinit var binding: FragmentAddUpdateFeeBinding
    var db: DB?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddUpdateFeeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title="Fee"
        db= activity?.let { DB(it) }
        binding.btnMonthlyFee.setOnClickListener {
            if(validate()){
                saveData()
            }
        }
        fillData()
    }

        private fun validate():Boolean {
            if (binding.edtOneMonth.text.toString().trim().isEmpty()) {
                showToast("Enter one month fee")
                return false
            } else if (binding.edtThreeMonth.text.toString().trim().isEmpty()) {
                showToast("Enter Three month fee")
                return false
            }else if (binding.edtSixMonth.text.toString().trim().isEmpty()) {
                showToast("Enter Six month fee")
                return false
            }else if (binding.edtNineMonth.text.toString().trim().isEmpty()) {
                showToast("Enter Nine month fee")
                return false
            }else if (binding.edtTwelveMonth.text.toString().trim().isEmpty()) {
                showToast("Enter Twelve month fee")
                return false
            }
            return true
            }
           private fun saveData(){
                try{
                    val sqlQuery = "INSERT OR REPLACE INTO FEE(ID,ONE_MONTH,THREE_MONTH,SIX_MONTH,NINE_MONTH,TWELVE_MONTH)VALUES"+
                            "('1','"+binding.edtOneMonth.text.toString().trim()+"','"+binding.edtThreeMonth.text.toString().trim()+"',"+
                            "'"+binding.edtSixMonth.text.toString().trim()+"','"+binding.edtNineMonth.text.toString().trim()+"',"+
                            "'"+binding.edtTwelveMonth.text.toString().trim()+"')"
                    db?.executeQuery(sqlQuery)
                    showToast("Monthly Payment data saved successfully")
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
    private fun fillData(){
        try{
            val sqlQuery="SELECT * FROM WHERE ID = '1'"
            db?.fireQuery(sqlQuery)?.use{
                if(it.count>0) {
                    it.moveToFirst()
                    binding.edtOneMonth.setText(MyFunction.getValue(it, "ONE_MONTH"))
                    binding.edtThreeMonth.setText(MyFunction.getValue(it, "THREE_MONTH"))
                    binding.edtSixMonth.setText(MyFunction.getValue(it, "SIX_MONTH"))
                    binding.edtNineMonth.setText(MyFunction.getValue(it, "NINE_MONTH"))
                    binding.edtTwelveMonth.setText(MyFunction.getValue(it, "TWELVE_MONTH"))
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }

}