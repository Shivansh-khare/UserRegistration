package com.example.android.roomwordssample.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.ApplicationClass
import com.example.android.roomwordssample.R
import com.example.android.roomwordssample.databinding.ActivityUserFormBinding
import com.google.android.gms.location.*
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class UserFormActivity : AppCompatActivity(), ImageViewHolder.SendImagePickReuest, IPickResult {

    //Remove this variable and use id property of User class to check whether to insert/update
    var isUpdate = false

    private lateinit var activityUserFormBinding: ActivityUserFormBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var adapter: FormAdapter = FormAdapter()
    lateinit var context: Context
    lateinit var recyclerForm: RecyclerView
    lateinit var User: User



    private val UserViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as ApplicationClass).repository)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("User",adapter.usr)
        super.onSaveInstanceState(outState)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserFormBinding = ActivityUserFormBinding.inflate(layoutInflater)
        setContentView(activityUserFormBinding.root)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if(savedInstanceState!=null){
            adapter.usr = savedInstanceState.getParcelable<User>("User")!!
        }

        recyclerForm = activityUserFormBinding.recyclerForm
        context = this



        if(intent.hasExtra("usr")) {
            isUpdate = true
            var x = intent.getLongExtra("usr",0)
            GlobalScope.launch(Dispatchers.IO) {
                UserViewModel.getUser(x).let { adapter.usr = it }
                adapter.notifyDataSetChanged()
            }

        }

        activityUserFormBinding.recyclerForm.adapter = adapter
        activityUserFormBinding.recyclerForm.layoutManager = LinearLayoutManager(this)
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {


            User = adapter.usr

            if(User.image == null){
                Toast.makeText(context,"Please upload any image",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(0)
                adapter.highlight = 0
            }else if(User.Fname == ""){
                Toast.makeText(context,"Please Enter Proper First Name",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(1)
                adapter.highlight = 1
            }else if(User.Lname == ""){
                Toast.makeText(context,"Please Enter Proper Last Name",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(2)
                adapter.highlight = 2
            }else if(User.Address == ""){
                Toast.makeText(context,"Please Enter Proper Address",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(3)
                adapter.highlight = 3
            }else if(User.mob == ""){
                Toast.makeText(context,"Please Enter Proper Mobile Number",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(4)
                adapter.highlight = 4
            }else if(User.email == ""){
                Toast.makeText(context,"Please Enter Proper Email Addres",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(5)
                adapter.highlight = 5
            }else if(User.gender == ""){
                Toast.makeText(context,"Please select any Gender",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(6)
                adapter.highlight = 6
            }else if(User.dob == null){
                Toast.makeText(context,"Please Enter your Date of Birth",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(7)
                adapter.highlight = 7
            }else if(User.industry == ""){
                Toast.makeText(context,"Please Enter your Industry",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(8)
                adapter.highlight = 8
            }else if(User.income == 0.0){
                Toast.makeText(context,"Please Enter your Income",Toast.LENGTH_SHORT).show()
                recyclerForm.scrollToPosition(9)
                adapter.highlight = 9
            }else
            {
                if(checkPermissions()) {
                    GlobalScope.launch {
                        LocationManager.saveUserWithLocation(User, applicationContext, isUpdate, UserViewModel)
                    }
                } else requestPermissions()
                finish()
            }
            adapter.notifyDataSetChanged()
        }

    }


    private fun compress(bitmap: Bitmap, quality:Int):Bitmap {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        val byte = baos.toByteArray()
        val ins = ByteArrayInputStream(byte)
        val bm = BitmapFactory.decodeStream(ins)
        ins.close()
        baos.close()
        return bm
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
    override fun req() {

        PickImageDialog.build(PickSetup()).show(this)


//        val intent = Intent(
//            Intent.ACTION_PICK,
//            MediaStore.Images.Media.INTERNAL_CONTENT_URI
//        )
//        intent.setType("image/*")
//        startActivityForResult(intent, 121)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            121
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 121) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                GlobalScope.launch {
                    LocationManager.saveUserWithLocation(User, applicationContext, isUpdate, UserViewModel)
                }
                finish()
            }
        }
    }

    override fun onPickResult(r: PickResult) {
        if (r.error == null) {

            var bit = compress(r.bitmap,0)

            adapter.usr.image = DatabaseConverters().bitmaptoStr(bit)
            adapter.highlight = -1
            adapter.notifyItemChanged(0)
        } else {
            //Handle possible errors
            Toast.makeText(this, r.error.message, Toast.LENGTH_LONG).show()
        }
    }
}