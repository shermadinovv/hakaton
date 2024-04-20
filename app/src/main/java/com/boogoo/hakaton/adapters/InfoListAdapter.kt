package com.boogoo.hakaton.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView



import com.boogoo.hakaton.databinding.UserssInfoItemBinding

import com.boogoo.hakaton.model.UserInfo
import com.squareup.picasso.Picasso

class InfoListAdapter (private val infoModelList : List<UserInfo>) :
    RecyclerView.Adapter<InfoListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: UserssInfoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: UserInfo) {
            binding.apply {
                infoSubtitleUni.text = model.uni
                infoTitleName.text = model.name
                Picasso.get().load(model.pic).into(binding.im)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserssInfoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return infoModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(infoModelList[position])
    }


}