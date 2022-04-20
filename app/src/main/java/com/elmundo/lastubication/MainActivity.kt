package com.elmundo.lastubication

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.location.*
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCourseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private val CODIGO_SOLICITUD_PERMISO = 100

    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest:LocationRequest?=null

    var callback:LocationCallback?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = FusedLocationProviderClient(this)
        inicializarLocationRequest()

    }
    private fun inicializarLocationRequest(){
        locationRequest= LocationRequest()
        locationRequest?.interval=10000
        locationRequest?.fastestInterval=5000
        locationRequest?.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun validarPermisosUbicacion():Boolean{
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(
            this,
            permisoFineLocation
        ) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(
            this,
            permisoCourseLocation
        ) == PackageManager.PERMISSION_GRANTED
        return hayUbicacionPrecisa && hayUbicacionOrdinaria

    }
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
     /*fusedLocationClient?.lastLocation?.addOnSuccessListener(this, object : OnSuccessListener<Location> {
         override fun onSuccess(location: Location?) {
             if(location !=null){
                 Toast.makeText(applicationContext,location?.latitude.toString()+" - "+location?.longitude,Toast.LENGTH_SHORT).show()
             }
         }

     })*/
        callback= object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for(ubicacion in locationResult?.locations!!){
                 Toast.makeText(applicationContext, ubicacion.latitude.toString()+" , "+ubicacion.longitude.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest as LocationRequest,
            callback as LocationCallback, Looper.myLooper()!!
        )
    }

    private fun pedirPermisos() {
    val deboProveerContexto= ActivityCompat.shouldShowRequestPermissionRationale(this,permisoFineLocation)
        if (deboProveerContexto){
          //mandar mensaje con explicacion adicional
            solicitudPermiso()
        } else{
           solicitudPermiso()
        }


    }
    private fun solicitudPermiso(){
        requestPermissions(arrayOf(permisoFineLocation,permisoCourseLocation),CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
          CODIGO_SOLICITUD_PERMISO ->{
              //Granted=0
              //Denied=-1
              if(grantResults.size > 0  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                  obtenerUbicacion()
                  Toast.makeText(this,"Diste permiso",Toast.LENGTH_SHORT).show()

              } else{
                  Toast.makeText(this,"No diste permiso para acceder a la ubicacion",Toast.LENGTH_SHORT).show()

              }
        }
    }
    }

    private fun detenerActualizacionUbicacion() {
        callback?.let { fusedLocationClient?.removeLocationUpdates(it) }


    }
    override fun onStart() {
        super.onStart()
        if (validarPermisosUbicacion()) {
            obtenerUbicacion()
        } else {
            pedirPermisos()
        }
    }
    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }
}