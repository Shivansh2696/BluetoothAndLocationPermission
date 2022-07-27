package com.example.bluetooth.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.example.bluetooth.R
import com.example.bluetooth.databinding.FragmentUnlockBinding

class UnlockFragment : Fragment() {

    private lateinit var binding : FragmentUnlockBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var btpermission : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentUnlockBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        binding.btUnlock.setOnClickListener{
            if (!checkBtPermission()){
                askBluetoothPermission()
            }else{
                startBluetoothIntent()
            }
        }
    }

    private fun askBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT),102)
            }else if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH),103)
            }
            else{
                if (checkBtPermission()){
                    if (!bluetoothAdapter.isEnabled){
                        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1000)
                    }
                }
            }
        }
    }

    private fun checkBtPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            btpermission = Manifest.permission.BLUETOOTH_CONNECT
        }else{
            btpermission = Manifest.permission.BLUETOOTH
        }
        val res: Int = checkCallingOrSelfPermission(requireContext(),btpermission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    private fun startBluetoothIntent(){
        if (checkBtPermission()){
            if (!bluetoothAdapter.isEnabled){
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1000)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 102){
            startBluetoothIntent()
        }
        if(requestCode == 103){
            startBluetoothIntent()
        }
    }
}