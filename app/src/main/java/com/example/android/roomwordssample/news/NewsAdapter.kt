package com.example.android.roomwordssample.news

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.roomwordssample.ApplicationClass.Companion.cashed
import com.example.android.roomwordssample.Article
import com.example.android.roomwordssample.R
import com.example.android.roomwordssample.databinding.NewsItemBinding
import com.example.android.roomwordssample.contacts.doubleClickListner
import com.google.gson.Gson





class NewsAdapter(var context:Context) :RecyclerView.Adapter<NewsAdapter.vh>() {
    class vh(var binding: NewsItemBinding) :RecyclerView.ViewHolder(binding.root)
    private var package_name = "com.android.chrome";

    var isBookmarkPage:Boolean = false

    private var list:MutableList<Article> = mutableListOf()

    fun setList(lst:List<Article>){
        list = mutableListOf()
        list.addAll(lst)
    }

    fun addList(lst:List<Article>){
        list.addAll(lst)
    }

    fun getList():List<Article> = list.toList()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vh {
        return vh(NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: vh, position: Int) {
        holder.binding.article = list.get(position)
        val sharedPreference =  context.getSharedPreferences("News", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        holder.itemView.setOnClickListener(object : doubleClickListner() {
            override fun onDoubleClick() {
                if(!isBookmarkPage){
                    if(cashed.bookmark.contains(list.get(position))){
                        Toast.makeText(context,"Already Added",Toast.LENGTH_SHORT)
                    }else{
                        cashed.bookmark.add(list.get(position))
                        val gson = Gson()
                        val json = gson.toJson(cashed)
                        editor.putString("data",json)
                        editor.commit()
                        Toast.makeText(context,"Added to bookmark",Toast.LENGTH_SHORT)
                    }
                }
            }

            override fun onSingleClick() {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(context.resources.getColor(R.color.colorPrimary))
                builder.addDefaultShareMenuItem()

                var customIntent = builder.build()
                customIntent.launchUrl(context,Uri.parse(list.get(position).url))
            }
        })

        Glide.with(holder.itemView.context).load(list.get(position).urlToImage).into(holder.binding.IVNews)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}