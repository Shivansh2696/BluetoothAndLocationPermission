package com.example.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.bluetooth.databinding.ActivityPermissionBinding
import com.example.bluetooth.fragments.UnlockFragment

class PermissionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPermissionBinding
    private lateinit var unlockFragment: UnlockFragment
    private lateinit var locationManager: LocationManager
    private var isLocationEnable : Boolean = false
//    private lateinit var btpermission : String
//    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onStart() {
        super.onStart()
        unlockFragment = UnlockFragment()
        replaceFragment(unlockFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.mainFrame,fragment).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isLocationEnable = locationManager.isLocationEnabled
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        askLocationPermission()


    }

//    private fun askBluetoothPermission() {
//        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT),102)
//            }else if (ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
//                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH),103)
//            }
//            else{
//                if (checkBtPermission()){
//                    if (!bluetoothAdapter.isEnabled){
//                        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1000)
//                    }
//                }
//            }
//        }
//    }

    private fun askLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),101)
        }else{
            if (checkLocationPermission()){
                if (!isLocationEnable){
                    openLocationService()
                }
            }
        }
    }

    private fun openLocationService() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Location Service ON")
            .setMessage("Location Service is necessary to use Latch Devices, So Please ON Location")
            .setPositiveButton("OK") { dialog, i ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, i ->
                Toast.makeText(this,"Without Location Permission, you are not Able to use Latch Devices",Toast.LENGTH_LONG).show()
            }
            .create()
        builder.setCancelable(false)
        builder.show()
    }

    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val res: Int = checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

//    private fun checkBtPermission(): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
//            btpermission = Manifest.permission.BLUETOOTH_CONNECT
//        }else{
//            btpermission = Manifest.permission.BLUETOOTH
//        }
//        val res: Int = checkCallingOrSelfPermission(btpermission)
//        return res == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startBluetoothIntent(){
//        if (checkBtPermission()){
//            if (!bluetoothAdapter.isEnabled){
//                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1000)
//            }
//        }
//    }

    private fun startLocationIntent(){
        if (checkLocationPermission()){
            if (!isLocationEnable){
                openLocationService()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101){
           startLocationIntent()
        }

//        if (requestCode == 102){
//           startBluetoothIntent()
//        }
    }

}
