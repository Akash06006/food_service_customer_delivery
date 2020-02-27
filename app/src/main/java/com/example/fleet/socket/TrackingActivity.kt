package com.example.fleet.socket

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.GoogleDirection
import com.example.fleet.R
import com.example.fleet.maps.FusedLocationClass
import com.example.fleet.maps.MapClass
import com.example.fleet.maps.MapInterface
import com.example.fleet.sharedpreference.SharedPrefClass
import com.example.fleet.utils.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Document

open class TrackingActivity : BaseActivity(), OnMapReadyCallback, LocationListener, SocketInterface,
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MapInterface, DialogssInterface,
    GoogleMap.OnInfoWindowClickListener {
    internal var cameraZoom = 16.0f
    private var mAddress = ""
    private var mInterface :DialogssInterface?=null

    private var mGoogleMap : GoogleMap? = null
    private var mFusedLocationClass : FusedLocationClass? = null
    private var mLocation : Location? = null
    private var mLatitude : String? = null
    private var mLongitude : String? = null
    private var utils : Utils? = null
    private var mHandler = Handler()
    private var mMapClass = MapClass()
    private var mContext : Context? = null
    private var sharedPrefClass = SharedPrefClass()
    private var check : Int = 0
    private val points = ArrayList<LatLng>()
    private var mPermissionCheck = false
    private var mGoogleApiClient : GoogleApiClient? = null
    private var mLine : Polyline? = null
    private var socket = SocketClass.socket
    private var dialog : Dialog? = null
    private var locationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var click_settings = 1
    private var click_gps = 1
    private var scan = 0
    private var start = 0
    private var permanent_deny = 0
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val mMarkers = java.util.ArrayList<Marker>()

    companion object {
        var categoryListids : ArrayList<String>? = null

    }

    private val mRunnable = object : Runnable {
        override fun run() {
            if (mFusedLocationClass != null) {
                mLocation = mFusedLocationClass!!.getLastLocation(mContext!!)
                if (mLocation != null) {
                    val mAddress = utils!!.getAddressFromLocation(
                        mContext!!,
                        mLocation!!.latitude,
                        mLocation!!.longitude,
                        "Address"
                    )
                    mLatitude = mLocation!!.latitude.toString() + ""
                    mLongitude = mLocation!!.longitude.toString() + ""
                    // Log.d(TAG, "get_current_address: $mAddress")
                    mLatitude = mLocation!!.latitude.toString() + ""
                    mLongitude = mLocation!!.longitude.toString() + ""
                    val mCameraPosition = CameraPosition.Builder()
                        .target(
                            LatLng(
                                java.lang.Double.parseDouble(mLatitude!!),
                                java.lang.Double.parseDouble(mLongitude!!)
                            )
                        )
                        .zoom(15.5f)
                        .tilt(30f)
                        .build()
                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))

                    mHandler.removeCallbacks(this)
                } else
                    mHandler.postDelayed(this, 500)
            } else
                mHandler.postDelayed(this, 500)
        }
    }

    override fun getLayoutId() : Int {
        return R.layout.fragment_tracking
    }

    override fun initViews() {
        sharedPrefClass = SharedPrefClass()
        categoryListids = ArrayList()
        mContext = this
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mMapClass.setSupportMapFragmentAsync(supportMapFragment!!, this)
        mMapClass.getGeoDataClient(mContext!!)
        mInterface=this
        utils = Utils(this)

        dialog = Dialog(this)
        dialog = mDialogClass.setPermissionDialog(this, this)
        locationDialog = mDialogClass.setDefaultDialog(
            this,
            mInterface!!,
            "GPSCheck",
            getString(R.string.GPS_enabled)
        )

        mFusedLocationClass = FusedLocationClass(mContext)
        //Socket Initialization
        socket.updateSocketInterface(this)
        socket.onConnect()

    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()

    }

    override fun onDestroy() {
        super.onDestroy()
        // mSocket.disconnect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()

        }

    }

    override fun onCameraIdle(cameraPosition : CameraPosition) {
        cameraZoom = cameraPosition.zoom
    }

    override fun onCameraMoveStarted() {
//Empty
    }

    override fun onInfoWindowClick(marker : Marker) {
//Not In Use
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (click_settings > 0) {
            checkPermission()

        }

        if (click_gps > 0) {
            checkGPS()
            click_gps = 0
        }

        super.onResume()
    }

    override fun onMapReady(googleMap : GoogleMap) {
        this.mGoogleMap = googleMap
        mGoogleMap!!.setMinZoomPreference(5f)
        buildGoogleApiClient()
        googleMap.uiSettings.isCompassEnabled = false

        mHandler.postDelayed(mRunnable, 500)
        mPermissionCheck = false
        check = 0

    }

    override fun onAutoCompleteListener(place : Place) {
//Not In Use
    }

    override fun onConnected(bundle : Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient
        )
        if (mLastLocation != null) {
            val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000 //5 seconds
        mLocationRequest.fastestInterval = 3000 //3 seconds
        mLocationRequest.smallestDisplacement = 0.1f //added
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
    }

    override fun onConnectionSuspended(i : Int) {
//Not In Use
    }

    override fun onConnectionFailed(connectionResult : ConnectionResult) {
//Not In Use
    }

    override fun onLocationChanged(location : Location) {
        getCurrentLocation(location)
        val mCameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )         // Sets the center of the map to Mountain View
            .zoom(17.0f)
            .tilt(30f)
            .build()

        mGoogleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))
        mGoogleMap!!.setMinZoomPreference(2.0f)
        mGoogleMap!!.setMaxZoomPreference(17.0f)
        Handler().postDelayed({
            callSocketMethods("updateLocation")
        }, 2000)
    }

    private fun getCurrentLocation(location : Location) {
        mAddress = utils!!.getAddressFromLocation(this, location.latitude, location.longitude, "Address")
        mLongitude = location.longitude.toString() + ""
        mLatitude = location.latitude.toString() + ""


        if (start == 0) {
            mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    //.snippet(points[0].longitude.toString() + "")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            start = 1
        }
    }

    private fun removeMarkers(mMarkers1 : java.util.ArrayList<Marker>) {
        for (marker in mMarkers1) {
            marker.remove()
        }

        mMarkers.clear()

    }

    @SuppressLint("SetTextI18n")
    private fun drawPolyLine(latitudes : JSONArray, longitudes : JSONArray) {
        //mGoogleMap!!.clear()
        val polyline = PolylineOptions().width(5f).color(Color.BLACK).geodesic(true)
        points.clear()
        removeMarkers(mMarkers)



        for (t in 0 until latitudes.length() - 1) {
            val point = LatLng(
                java.lang.Double.parseDouble(latitudes.get(t).toString()),
                java.lang.Double.parseDouble(longitudes.get(t).toString())
            )
            points.add(point)
            polyline.add(point)
            polyline.geodesic(true)

        }

        if (points.size > 0) {
            val mMarker = mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(points[points.size - 1].latitude, points[points.size - 1].longitude))
                    //.snippet(points[0].longitude.toString() + "")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            mMarkers.add(mMarker)
            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location)));
//           val m1 = mGoogleMap!!.addMarker(
//               MarkerOptions()
//                   .position(LatLng(location.latitude, location.longitude))
//                   .anchor(0.5f, 0.5f)
//                   .snippet(location.latitude.toString() + "")
//                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_new))
//           )
//           m1.tag = points[0].longitude
        }
        if (mLine != null) mLine!!.remove()
        mLine = mGoogleMap!!.addPolyline(polyline)







        // List<LatLng> snappedPoints = new ArrayList<>();
        //new GetSnappedPointsAsyncTask().execute(sourcePoints, null, snappedPoints);
        ///
    }

    //SOCKET FUNCTIONALITY
    override fun onSocketCall(onMethadCall : String, vararg jsonObject : Any) {
        val serverResponse = jsonObject[0] as JSONObject
        var methodName = serverResponse.get("method")
        println("serverResponse............" + serverResponse)
        try {
            this.runOnUiThread {
                when (methodName) {
                    "updateLocation" -> try {
                        callSocketMethods("getLocation")
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                    "getLocation" -> try {
                        val innerResponse = serverResponse.get("data") as JSONObject
                        if (innerResponse.get("location_latitude") != null) {
                            val locationLatitude = innerResponse.get("location_latitude") as JSONArray
                            val locationLongitude = innerResponse.get("location_longitude") as JSONArray
                            drawPolyLine(locationLatitude, locationLongitude)
                        }
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }

    }

    private fun callSocketMethods(methodName : String) {
        val object5 = JSONObject()
        when (methodName) {
            "updateLocation" -> try {
                object5.put("methodName", methodName)
                object5.put("latitude", mLatitude)
                object5.put("longitude", mLongitude)
                object5.put("driverId", 1)
                socket.sendDataToServer(methodName, object5)
            } catch (e : Exception) {
                e.printStackTrace()
            }
            "getLocation" -> try {
                object5.put("methodName", methodName)
                object5.put("driverId", 1)
                socket.sendDataToServer(methodName, object5)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onSocketConnect(vararg args : Any) {
        //OnSocket Connect Call It

    }

    override fun onSocketDisconnect(vararg args : Any) {
        // //OnSocket Disconnect Call It

    }



    fun checkPermission() {
        if (!mPermissionCheck) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext != null)
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (dialog != null) {
                            dialog!!.dismiss()
                        }
                        mPermissionCheck = true
                    } else {
                        if (dialog != null) {
                            dialog!!.dismiss()
                        }
                        mPermissionCheck = false

                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
            } else {
                if (dialog != null) {
                    dialog!!.dismiss()
                }
                mPermissionCheck = true
            }
        }
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "GPSCheck" -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                click_gps++
                // locationDialog.dismiss();
                startActivity(intent)
            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        dialog!!.dismiss()
        locationDialog!!.dismiss()
    }

    fun checkGPS() {
        val mLocationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        if (mLocationManager != null) {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        if (!gps_enabled || !network_enabled) {
            checkPermission()
            locationDialog!!.show()
        } else {
            if (locationDialog!!.isShowing()) {
                locationDialog!!.dismiss()
            }
            // locationDialog.dismiss();
            checkPermission()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode : Int, permissions : Array<String>,
        grantResults : IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && permissions.size > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog!!.dismiss()
                    mPermissionCheck = true
                } else {
                    if (scan > 0) {
                        scan = 0
                    } else {
                        val permission1 = permissions[0]
                        val showRationale = shouldShowRequestPermissionRationale(permission1)
                        if (click_settings > 0) {
                            click_settings = 0
                            dialog!!.show()
                        } else {
                            if (!showRationale && permanent_deny > 0) {
                                click_settings++
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", this.packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } else if (permanent_deny == 0 && !showRationale) {
                                dialog!!.show()
                                permanent_deny++
                            } else {
                                permanent_deny++
                                dialog!!.show()
                            }
                        }

                    }
                }
            }
        }
    }

}









