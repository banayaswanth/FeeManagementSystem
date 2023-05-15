package com.example.feemanagementsystem.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.adapter.AdapterLoadMember
import com.example.feemanagementsystem.databinding.FragmentFeePendingBinding
import com.example.feemanagementsystem.global.DB
import com.example.feemanagementsystem.global.MyFunction
import com.example.feemanagementsystem.model.AllMember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class FragmentFeePending : BaseFragment() {

    private lateinit var binding:FragmentFeePendingBinding
    private var db: DB?=null
    var adapter:AdapterLoadMember?=null
    var arrayList:ArrayList<AllMember> =ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeePendingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title="Fee Pending"
        db=activity?.let{DB(it)}
        binding.edtPendingSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                myFilter(p0.toString())
            }

        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
    private fun loadData(){
        lifecycleScope.executeAsyncTask(onPreExecute = {
            showDialog("Processing....")
        }, doInBackground = {
            arrayList.clear()
            val sqlQuery = "SELECT * FROM MEMBER WHERE STATUS= 'A' AND REPAYMENT<='"+MyFunction.getCurrentDate()+"'ORDER BY FIRST_NAME"
            db?.fireQuery(sqlQuery)?.use{
                if(it.count>0){
                    it.moveToFirst()
                    do {
                        val list= AllMember(id =  MyFunction.getValue(it,"ID"),
                            firstName = MyFunction.getValue(it,"FIRST_NAME"),
                            lastName = MyFunction.getValue(it,"LAST_NAME"),
                            age = MyFunction.getValue(it,"CLASS"),
                            gender = MyFunction.getValue(it,"GENDER"),
                            weight = MyFunction.getValue(it,"WEIGHT"),
                            mobile = MyFunction.getValue(it,"MOBILE"),
                            address = MyFunction.getValue(it,"ADDRESS"),
                            image = MyFunction.getValue(it,"IMAGE_PATH"),
                            dateOfJoining = MyFunction.returnUserDateFormat(MyFunction.getValue(it,"DATE_OF_JOINING")),
                            expiryDate = MyFunction.returnUserDateFormat(MyFunction.getValue(it,"REPAYMENT")))
                        arrayList.add(list)
                    }while (it.moveToNext())
                }
            }
        }, onPostExecute = {
            if(arrayList.size>0){
                binding.recyclerViewPending.visibility = View.VISIBLE
                binding.txtPendingNDF.visibility = View.GONE

                adapter = AdapterLoadMember(arrayList)
                binding.recyclerViewPending.layoutManager = LinearLayoutManager(activity)
                binding.recyclerViewPending.adapter = adapter

                adapter?.onClick{
                    loadFragment(it)
                }
            }else{
                binding.recyclerViewPending.visibility = View.GONE
                binding.txtPendingNDF.visibility = View.VISIBLE
            }
            closeDialog()
        })
    }
    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: ()-> Unit,
        doInBackground: ()-> R,
        onPostExecute:(R)-> Unit
    ) = launch{
        onPreExecute()
        val result = withContext(Dispatchers.IO){
            doInBackground()
        }
        onPostExecute(result)
    }
    private fun loadFragment(id:String){
        val fragment = FragmentAddMember()
        val args = Bundle()
        args.putString("ID",id)
        fragment.arguments = args
        val fragmentManager: FragmentManager?= fragmentManager
        fragmentManager!!.beginTransaction().replace(R.id.frame_container,fragment,"FragmentAdd").commit()
    }
    private fun myFilter(searchValue:String){
        val temp:ArrayList<AllMember> = ArrayList()
        if(arrayList.size>0){
            for(list in arrayList){
                if(list.firstName.toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT)) ||
                    list.lastName.toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT))
                            ){
                    temp.add(list)
                }
            }
        }
        adapter?.updateList(temp)
    }
}