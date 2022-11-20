package com.example.android.roomwordssample.contacts

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.databinding.RecyclerviewItemBinding
import java.util.*

public class UsersRecylerAdapter(var wordViewModel: UserViewModel) : RecyclerView.Adapter<UsersRecylerAdapter.viewHolder>() {

    private var list: List<User>? = null

    fun setList(lst: List<User>){
        list = lst
        notifyDataSetChanged()
    }

    class viewHolder(var biding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(biding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    companion object{
        @JvmStatic
        @BindingAdapter("app:profileImage")
        fun loadImg(view:ImageView, bitmap: Bitmap){
            view.setImageBitmap(bitmap)
        }
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var curUser : User = list?.get(position) as User
        holder.biding.user = curUser
        holder.biding.converter = DatabaseConverters()
        holder.itemView.setOnClickListener{
            Log.d("helo",
                (curUser.lon).toString()+(curUser.lat).toString()
            )
            val uri: String = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f",curUser.lat ?: 0,
                 curUser.lon ?: 0
            )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            holder.itemView.context.startActivity(intent)
        }
        holder.biding.del.setOnClickListener {
            curUser.id?.let { it1 -> wordViewModel.delete(it1) }
        }
        holder.biding.update.setOnClickListener {
            Log.d("hello", curUser.toString())
            val newxtIntent = Intent(holder.itemView.context, UserFormActivity::class.java).apply{
                putExtra("usr",curUser.id)
            }
            holder.itemView.context.startActivity(newxtIntent)
        }
    }

    override fun getItemCount(): Int {
        if(list == null)return 0;
        return list!!.size
    }
}