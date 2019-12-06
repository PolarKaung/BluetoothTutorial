package com.henry.bluetoothtutorial

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var btAdapter : BluetoothAdapter? = null
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private val requestENABLEBLUETOOTH = 1

    companion object {
        const val EXTRA_ADDRESS:String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) {
            Toast.makeText(this, resources.getString(R.string.bt_unavailable), Toast.LENGTH_SHORT).show()
            return
        } else {
            if (!btAdapter!!.isEnabled)  {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent,requestENABLEBLUETOOTH)
            }
        }

        btnRefresh.setOnClickListener {
            pairedDeviceList()
        }
    }

    private fun pairedDeviceList() {
        pairedDevices = btAdapter!!.bondedDevices
        val deviceList : ArrayList<BluetoothDevice> = ArrayList()
        val nameList : ArrayList<String> = ArrayList()

        if (pairedDevices.isNotEmpty()) {
            pairedDevices.forEach { device ->
                deviceList.add(device)
                nameList.add(device.name)
                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.no_paired_devices), Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
        lvDevices.adapter = adapter

        lvDevices.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device : BluetoothDevice = deviceList[position]
            val address : String = device.address
            val name = device.name

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, arrayOf(name,address))
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestENABLEBLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (btAdapter!!.isEnabled) {
                    Toast.makeText(this, "Bluetooth has been enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Bluetooth has not been enabled", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth enabling has been cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
