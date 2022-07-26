package com.example.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bluetooth.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    var bluetoothAdapter: BluetoothAdapter?=null
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mainViewModel=ViewModelProvider(this)[MainViewModel::class.java]

        setContentView(binding.root)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.locationAvailability.addOnCompleteListener{
                if (!it.result.isLocationAvailable){
                    fusedLocationClient.getCurrentLocation(102,null).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        mainViewModel.permissionRequested.observe(this){
            if (it){
                var error =""
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    error="location"
                }
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)!= PackageManager.PERMISSION_GRANTED){
                    error+=" bluetooth "
                }

                if (!TextUtils.isEmpty(error)){
                    Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
                    openSettings()
                }
                else{
                    mainViewModel.permissionRequested.value=false

                }
            }
        }

    }
    private fun reqPermission(){
        requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.ACCESS_FINE_LOCATION),1000)
    }

    override fun onStart() {
        super.onStart()
        reqPermission()
    }

    override fun onResume() {
        super.onResume()
        if (mainViewModel.permissionRequested.value!!){
           openSettings()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==1000){
            if (checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                mainViewModel.permissionRequested.value=true
                openSettings()
                return
            }

            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                mainViewModel.permissionRequested.value=true
                openSettings()
                return
            }

            mainViewModel.permissionRequested.value=false

            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                val REQUEST_ENABLE_BT=10001

                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }
    private fun openSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
    private fun checkPermission(permission:String):Boolean{
        return ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED
    }
}