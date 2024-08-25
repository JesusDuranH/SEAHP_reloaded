package com.aem.sheap_reloaded

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aem.sheap_reloaded.code.things.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import com.aem.sheap_reloaded.code.things.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
import com.aem.sheap_reloaded.databinding.ActivityMainBinding
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.net.ssl.SSLContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        Security.addProvider(BouncyCastleProvider())
        /*binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        System.setProperty("https.protocols", "TLSv1.2")
        try {
            Log.d("DB", "TSL Init")
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (ex: Exception){
            ex.printStackTrace()
        }
        getWritePermission()
        getReadPermission()
        //verificarProveedorBouncyCastle()
    }

    private fun getWritePermission() {
        if (ContextCompat.checkSelfPermission
                (this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun getReadPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    fun verificarProveedorBouncyCastle() {
        val proveedores = Security.getProviders()
        var bouncyCastlePresente = false
        for (proveedor in proveedores) {
            if (proveedor.name.equals("BC", ignoreCase = true)) {
                bouncyCastlePresente = true
                break
            }
        }
        if (bouncyCastlePresente) {
            Toast.makeText(this, "BouncyCastle está configurado como proveedor de seguridad.",
                Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "BouncyCastle no está configurado como proveedor de seguridad.",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}