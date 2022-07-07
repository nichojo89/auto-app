package com.example.carapihelloworld

import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class TelemetryFragment : Fragment() {
    private lateinit var mCarPropertyManager: CarPropertyManager
    var mGearTextView: TextView? = null
    var mSpeedTextView: TextView? = null
    var mBatteryTextView: TextView? = null
    var mFuelDoorTextView: TextView? = null

    init {
        mCarPropertyManager =
            Car.createCar(context).getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerCarPropertyManagerCBs()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_telemetry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mGearTextView = getView()?.findViewById<TextView>(R.id.gear_textview)
        mSpeedTextView = getView()?.findViewById<TextView>(R.id.speed_textview)
        mBatteryTextView = getView()?.findViewById<TextView>(R.id.battery_textview)
        mFuelDoorTextView = getView()?.findViewById<TextView>(R.id.fuel_door_textview)
    }

    private fun registerCarPropertyManagerCBs() {
        val f =  CarPropertyEventCallback()
        Log.d(TAG, "Test CarPropertyManager callbacks:")
        mCarPropertyManager.registerCallback(val x = CarPropertyEventCallback() {
            fun onChangeEvent(carPropertyValue: CarPropertyValue) {
                Log.d(
                    TAG,
                    "GEAR_SELECTION: onChangeEvent(" + carPropertyValue.getValue().toString() + ")"
                )
                val gear: String = carPropertyValue.getValue().toString()
                mGearTextView.setText("The vehicle is in " + gear + "th gear")
            }

            fun onErrorEvent(propId: Int, zone: Int) {
                Log.d(TAG, "GEAR_SELECTION: onErrorEvent($propId, $zone)")
            }
        }, VehiclePropertyIds.GEAR_SELECTION, CarPropertyManager.SENSOR_RATE_NORMAL)
        mCarPropertyManager.registerCallback(object : CarPropertyEventCallback() {
            fun onChangeEvent(carPropertyValue: CarPropertyValue) {
                Log.d(
                    TAG,
                    "PERF_VEHICLE_SPEED: onChangeEvent(" + carPropertyValue.getValue()
                        .toString() + ")"
                )
                mSpeedTextView.setText(
                    "The vehicles speed is " + carPropertyValue.getValue().toString()
                        .toString() + "mph"
                )
            }

            fun onErrorEvent(propId: Int, zone: Int) {
                Log.d(TAG, "PERF_VEHICLE_SPEED: onErrorEvent($propId, $zone)")
            }
        }, VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_NORMAL)
        mCarPropertyManager.registerCallback(object : CarPropertyEventCallback() {
            fun onChangeEvent(carPropertyValue: CarPropertyValue) {
                Log.d(
                    TAG,
                    "EV_BATTERY_LEVEL: onChangeEvent(" + carPropertyValue.getValue()
                        .toString() + ")"
                )
                mBatteryTextView.setText(
                    "The cars battery charge is " + carPropertyValue.getValue().toString()
                        .toString() + " volts"
                )
            }

            fun onErrorEvent(propId: Int, zone: Int) {
                Log.d(TAG, "EV_BATTERY_LEVEL: onErrorEvent($propId, $zone)")
            }
        }, VehiclePropertyIds.EV_BATTERY_LEVEL, CarPropertyManager.SENSOR_RATE_ONCHANGE)
        mCarPropertyManager.registerCallback(object : CarPropertyEventCallback() {
            fun onChangeEvent(carPropertyValue: CarPropertyValue) {
                Log.d(
                    TAG,
                    "FUEL_DOOR_OPEN: onChangeEvent(" + carPropertyValue.getValue().toString() + ")"
                )
                val open: Boolean =
                    java.lang.Boolean.valueOf(carPropertyValue.getValue().toString())
                if (open) {
                    mFuelDoorTextView.setText("Your fuel door is open")
                } else {
                    mFuelDoorTextView.setText("Your fuel door is closed")
                }
            }

            fun onErrorEvent(propId: Int, zone: Int) {
                Log.d(TAG, "FUEL_DOOR_OPEN: onErrorEvent($propId, $zone)")
            }
        }, VehiclePropertyIds.FUEL_DOOR_OPEN, CarPropertyManager.SENSOR_RATE_ONCHANGE)
    }

    companion object {
        private const val TAG = "CarApiHelloWorld"
        private const val REQUEST_CODE_ASK_PERMISSIONS = 1
    }
}