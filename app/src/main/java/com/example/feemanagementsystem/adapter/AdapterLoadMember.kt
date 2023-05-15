package com.example.feemanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.databinding.AllMemberListResBinding
import com.example.feemanagementsystem.model.AllMember

class AdapterLoadMember(var arrayList:ArrayList<AllMember>): Adapter<AdapterLoadMember.MyViewHolder>() {
    private var onClick:((String)->Unit)?=null
    fun onClick(onClick:((String)->Unit)){
        this.onClick = onClick


    }

    class MyViewHolder(val binding: AllMemberListResBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AllMemberListResBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return arrayList.size;
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        with(holder){
            with(arrayList[position]){
                binding.txtAdapterName.text = this.firstName+" "+this.lastName
                binding.txtAdapterClass.text =  "Age : "+this.age
                binding.txtAdapterReg.text= "Registration : "+this.weight
                binding.txtAdapterMobile.text= "Mobile : "+this.mobile
                binding.txtSection.text=this.address
                binding.txtRePayment.text="Repayment : "+this.expiryDate
                if(this.image.isNotEmpty()){
                    Glide.with(holder.itemView.context)
                        .load(this.image)
                        .into(binding.imgAdapterPic)
                }else{
                    if(this.gender == "Male"){
                        Glide.with(holder.itemView.context)
                            .load(R.drawable.boy)
                            .into(binding.imgAdapterPic)
                    }else{
                        Glide.with(holder.itemView.context)
                            .load(R.drawable.girl)
                            .into(binding.imgAdapterPic)
                    }
                }
                binding.layoutMemberList.setOnClickListener {
                    onClick?.invoke(this.id)
                }
            }
        }
    }

    fun updateList(list: ArrayList<AllMember>){
        arrayList=list
        notifyDataSetChanged()
    }
}