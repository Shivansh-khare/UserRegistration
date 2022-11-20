/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.roomwordssample

import android.Manifest
import android.R
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.roomwordssample.Camera.SnapActivity
import com.example.android.roomwordssample.contacts.*
import com.example.android.roomwordssample.location.LocationForegroundService
import com.example.android.roomwordssample.databinding.ActivityMainBinding
import com.example.android.roomwordssample.news.NewsActivity
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    lateinit var adapter : UsersRecylerAdapter
    lateinit var lst:List<User>
    lateinit var btn1: Button
    lateinit var btn2:Button
    val PERMISSION_ID = 44
    lateinit var binding: ActivityMainBinding
    var drawerLayout: DrawerLayout? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    val wordViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as ApplicationClass).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btn1 = binding.button
        btn2 = binding.button2

        btn2.isEnabled = isServiceRunningInForeground(this, LocationForegroundService::class.java)
        btn1.isEnabled = !isServiceRunningInForeground(this, LocationForegroundService::class.java)

        adapter = UsersRecylerAdapter(wordViewModel)
        val recyclerView = binding.recyclerview

        wordViewModel.allWords.observe(owner = this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let { adapter.setList(it) }
            lst = words
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, UserFormActivity::class.java)
            startActivity(intent)
        }

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = binding.myDrawerLayout
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.ok, R.string.cut)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        // to make the Navigation drawer icon always appear on the action bar

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val navigationView: NavigationView = findViewById(com.example.android.roomwordssample.R.id.nav_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                com.example.android.roomwordssample.R.id.nav_news -> {
                    startActivity(Intent(this, NewsActivity::class.java))
                }
                com.example.android.roomwordssample.R.id.nav_Camera -> {
                    startActivity(Intent(this, SnapActivity::class.java))
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

    }
    fun stop(view: View) {
        btn2.isEnabled = false
        btn1.isEnabled = true

        Log.d("myToast","on click stop")
        StopLocationForeground()

    }
    fun Share(view: View) {
        btn1.isEnabled = false
        btn2.isEnabled = true

        if(checkPermissions())
            StartForegroundLocation()
        else
            requestPermissions()
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartForegroundLocation()
            }
        }
    }

    private fun StartForegroundLocation(){
        Log.d("myToast","on click Start")
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Sharing Live Location")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun StopLocationForeground(){
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        stopService(serviceIntent)
    }

    fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return service.foreground
            }
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

}