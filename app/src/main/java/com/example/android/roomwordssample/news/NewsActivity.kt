package com.example.android.roomwordssample.news

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.ApplicationClass.Companion.cashed
import com.example.android.roomwordssample.Article
import com.example.android.roomwordssample.R
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NewsActivity : AppCompatActivity() {
    lateinit var edit: AutoCompleteTextView
    lateinit var smallLogo: ImageView
    lateinit var rec: RecyclerView
    lateinit var list: List<Article>
    lateinit var tvTotal: TextView
    lateinit var adapter: NewsAdapter
    lateinit var pb:ProgressBar
    lateinit var manager: LinearLayoutManager
    var q:String=""
    var TotalCards=0
    var page = 1
    var isHead = true
    var isLoading = false
    var isLase = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        //Log.d("mLog", "On Save Instance: "+ cashed.describeContents())
//        outState.putParcelable("cache",cashed)
        outState.putString("query",q)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        var query = intent.getStringExtra("query")

        edit = findViewById(R.id.Search_editText);
        smallLogo = findViewById(R.id.NewsLogoSmall)
        tvTotal = findViewById(R.id.TVlistSize)
        rec = findViewById(R.id.News_recyclerView)
        list = listOf()
        pb = findViewById(R.id.progressBar2)


        val sharedPreference =  getSharedPreferences("News", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        val gson = Gson()
        val json: String? = sharedPreference.getString("data", null)
        Log.d("mLog",json.toString())
        if(json != null) {
            cashed = gson.fromJson(json, CachedData::class.java)
        }


        if(savedInstanceState != null){
            q = savedInstanceState.getString("query").toString()
//            Log.d("mLog", "OnCreate Contains Query: $q")
//            savedInstanceState.getParcelable<CachedData>("cache")?.let {
//                cashed = it
//                Log.d("mLog", "OnCreate Contains Cache: ${it.history} and ${it.bookmark}")
//            }
//            savedInstanceState.clear()
            if(q=="") {
                loadHeadlines()
            }
            else{
                isHead=false
                getData(q)
                edit.setText(q)}
        }

        adapter = NewsAdapter(this)
        rec.adapter = adapter
        manager = LinearLayoutManager(this)
        rec.layoutManager = manager
        rec.hasFixedSize()
        rec.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var cards = manager.childCount
                var visible = manager.itemCount
                var first = manager.findFirstVisibleItemPosition()

                if( !isLoading && !isLase && cards+first >= visible && first>=0 && visible>=10 ){
                    page++;
                    MoreData()
                }

            }
        })

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.select_dialog_item, cashed.history)
        edit.threshold = 0
        Log.d("autocmp",cashed.history.toString())

        edit.setAdapter(adapter)

        edit.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buttonClicked(v)
                return@OnEditorActionListener true
            }
            false
        })

        if(query != null && !query.equals("")){
                getData(query)
            q=query
            edit.setText(query)
            isHead=false
        }else{
            loadHeadlines()
        }

    }

    private fun loadHeadlines() {
        isLoading = true
        var quotesApi = RetrofitHeadline.getInstance().create(HeadlineAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {

            try{
                val result = quotesApi.getHeadlineNews()
                if (result != null) {// Checking the results
                    runOnUiThread {
                        // Stuff that updates the UI
                        adapter.setList(result.body()?.articles ?: listOf())
                        rec.adapter?.notifyDataSetChanged()
                        isLoading = false
                        tvTotal.setText("Showing "+adapter.itemCount+" out of "+adapter.itemCount+" results")
                        pb.visibility = View.GONE
                    }
                }else{
                    runOnUiThread{
                        pb.visibility = View.GONE
                    }
                }
            }catch (e:Exception){
                Log.d("myTag",e.localizedMessage)
            }
        }
    }

    private fun MoreData(){
        if(isHead)return
        isLoading = true
        pb.visibility = View.VISIBLE

        val quotesApi = RetrofitHelper.getInstance().create(NewsAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {

            try{
                val result = quotesApi.getNews(q.toString(),"popularity","38f092e004734dedad9354f21cf7f48a",10,page)
                Log.d("myTag"," "+page+result.toString())
                if (result != null) {// Checking the results
                    runOnUiThread {
                        // Stuff that updates the UI
                        adapter.addList(result.body()?.articles ?: listOf())
                        rec.adapter?.notifyDataSetChanged()
                        tvTotal.setText("Showing "+adapter.itemCount+" out of "+result.body()?.totalResults+" results")
                        pb.visibility = View.GONE
                        if(result.body()?.articles?.size ?: 0 < 10){
                            isLase = true
                        }
                    }
                    isLoading = false
                    Log.d("ayush: ", result.body()?.articles.toString())
                }else{
                    runOnUiThread{
                        pb.visibility = View.GONE
                    }
                }
            }catch(e:Exception){
                Log.d("myTag",e.localizedMessage)
                runOnUiThread {
                    pb.visibility = View.GONE
                }
            }
        }
    }

    private fun getData(s: String) {
        isLoading = true
        page = 1
        q=s
        pb.visibility = View.VISIBLE

        val quotesApi = RetrofitHelper.getInstance().create(NewsAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            try{
                val result = quotesApi.getNews(s,"popularity","38f092e004734dedad9354f21cf7f48a",10,page)
                if (result != null) {// Checking the results
                    runOnUiThread {
                        // Stuff that updates the UI
                        TotalCards = result.body()?.totalResults ?: 0
                        adapter.setList(result.body()?.articles ?: listOf())
                        rec.adapter?.notifyDataSetChanged()
                        tvTotal.setText("Showing "+adapter.itemCount+" out of "+result.body()?.totalResults+" results")
                        if(result.body()?.articles?.size ?: 0 < 10){
                            isLase = true
                        }
                        pb.visibility = View.GONE
                    }
                    isLoading = false
                    Log.d("ayush: ", result.body()?.articles.toString())
                }
            }catch(e:Exception){
                Log.d("myToast",e.localizedMessage)
            }
        }
    }

    fun buttonClicked(view: View) {
        if(!edit.text.toString().isEmpty()){
            val sharedPreference =  getSharedPreferences("News", Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            startActivity(Intent(this, NewsActivity::class.java).putExtra("query",edit.text.toString()))
            if(!cashed.history.contains(edit.text.toString()))
                cashed.history.add(edit.text.toString())
                val gson = Gson()
                val json = gson.toJson(cashed)
                editor.putString("data",json)
                editor.commit()
            finish()
        }else{
            Toast.makeText(this,"Empty Search", Toast.LENGTH_SHORT).show()
        }
    }

    fun bookmarks(view: View) {
        var intent = Intent(this, NewsBookMarks::class.java)
        startActivity(intent)
    }
}

