package com.henry.bluetoothtutorial

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_control.*
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object {
        var myUUID: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000")
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        var address: String? = null
        var name: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        name = intent.getStringArrayExtra(MainActivity.EXTRA_ADDRESS)[0]
        address = intent.getStringArrayExtra(MainActivity.EXTRA_ADDRESS)[1]
        ConnectToDevice(this).execute()
        txvAddress.text = name

        btnLEDon.setOnClickListener {
            sendCommand("a")
        }
        btnLEDoff.setOnClickListener {
            sendCommand("b")
        }
        btnDisconnect.setOnClickListener {
            disconnect()
        }
    }

    private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void,Void,String>() {

        private var connectSuccess: Boolean = true
        private val context:Context = c

        override fun doInBackground(vararg params: Void?): String? {
            try {
                if (bluetoothSocket != null || isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Connecting...", "Please wait")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                isConnected = true
            }
            progress.dismiss()
        }

    }
}