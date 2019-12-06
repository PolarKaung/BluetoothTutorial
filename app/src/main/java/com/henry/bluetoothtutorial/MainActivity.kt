package com.henry.bluetoothtutorial

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var BtAdapter : BluetoothAdapter? = null
    lateinit var pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS:String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BtAdapter = BluetoothAdapter.getDefaultAdapter()
        if (BtAdapter == null) {
            Toast.makeText(this, resources.getString(R.string.bt_unavailable), Toast.LENGTH_SHORT).show()
            return
        } else {
            if (!BtAdapter!!.isEnabled)  {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
            }
        }

        btnRefresh.setOnClickListener {
            pairedDeviceList()
        }
    }

    private fun pairedDeviceList() {
        pairedDevices = BtAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if (!pairedDevices.isEmpty()) {
            pairedDevices.forEach { device ->
                list.add(device)
                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(this, "No Paired Devices Found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        lvDevices.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (BtAdapter!!.isEnabled) {
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
