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
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.adapter.AdapterLoadMember
import com.example.feemanagementsystem.databinding.FragmentAddMemberBinding
import com.example.feemanagementsystem.databinding.FragmentAllMemberBinding
import com.example.feemanagementsystem.global.DB
import com.example.feemanagementsystem.global.MyFunction
import com.example.feemanagementsystem.model.AllMember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class FragmentAllMember : BaseFragment() {

    private val TAG="FragmentAllMember"
    var db:DB?=null
    var adapter:AdapterLoadMember?=null
    var arrayList:ArrayList<AllMember> =ArrayList()
    private lateinit var binding: FragmentAllMemberBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAllMemberBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title="Dashboard"
        db= activity?.let { DB(it) }

        binding.rdGroupMember.setOnCheckedChangeListener{ radioGroup, id ->
            when(id){
                R.id.rdActiveMember->{
                    loadData("A")
                }
                R.id.rdInActiveMember->{
                    loadData("D")
                }
            }
        }

        binding.imgAddMember.setOnClickListener{
            loadFragment("")
        }
        binding.edtAllMemberSearch.addTextChangedListener(object : TextWatcher {
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
        loadData("A")
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
private fun loadData(memberStatus:String){
    lifecycleScope.executeAsyncTask(onPreExecute = {
        showDialog("Processing....")
    }, doInBackground = {
        arrayList.clear()
            val sqlQuery = "SELECT * FROM MEMBER WHERE STATUS= '$memberStatus'"
        db?.fireQuery(sqlQuery)?.use{
            if(it.count>0){
                it.moveToFirst()
                do {
                    val list= AllMember(id =  MyFunction.getValue(it,"ID"),
                        firstName = MyFunction.getValue(it,"FIRST_NAME"),
                        lastName = MyFunction.getValue(it,"LAST_NAME"),
                        age = MyFunction.getValue(it,"CLASS"),
                        gender = MyFunction.getValue(it,"GENDER"),
                        weight = MyFunction.getValue(it,"REGISTER_NUMBER"),
                        mobile = MyFunction.getValue(it,"MOBILE"),
                        address = MyFunction.getValue(it,"SECTION"),
                        image = MyFunction.getValue(it,"IMAGE_PATH"),
                        dateOfJoining = MyFunction.returnUserDateFormat(MyFunction.getValue(it,"DATE_OF_JOINING")),
                        expiryDate = MyFunction.returnUserDateFormat(MyFunction.getValue(it,"REPAYMENT")))
                    arrayList.add(list)
                }while (it.moveToNext())
                 }
        }
    }, onPostExecute = {
        if(arrayList.size>0){
            binding.recyclerViewMember.visibility = View.VISIBLE
            binding.txtAllMemberNDF.visibility = View.GONE

            adapter = AdapterLoadMember(arrayList)
            binding.recyclerViewMember.layoutManager = LinearLayoutManager(activity)
            if (binding.recyclerViewMember.adapter == null) {
                binding.recyclerViewMember.adapter = adapter
            }

        adapter?.onClick{
loadFragment(it)
        }
        }else{
            binding.recyclerViewMember.visibility = View.GONE
            binding.txtAllMemberNDF.visibility = View.VISIBLE
        }
        closeDialog()
    })
}
    private fun loadFragment(id:String){
        val fragment = FragmentAddMember()
        val args = Bundle()
        args.putString("ID",id)
        fragment.arguments = args
        val fragmentManager:FragmentManager?= fragmentManager
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