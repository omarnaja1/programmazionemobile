package it.omarkiarafederico.skitracker.view.skimap

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import it.omarkiarafederico.skitracker.R
import it.omarkiarafederico.skitracker.databinding.ActivityMapBinding
import it.omarkiarafederico.skitracker.view.selezionecomprensorio.SelezioneComprensorio
import it.omarkiarafederico.skitracker.view.tutorial.WelcomeActivity
import roomdb.LocalDB


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private var selectedFragmentTag = "map"

    override fun onCreate(savedInstanceState: Bundle?) {
        // creazione activity
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // controllo se l'utente ha già visto il tutorial e/o ha già selezionato il comprensorio
        // se non ha fatto almeno una delle due cose, lo redirigo alle varie activities
        // TODO - FORSE METTEREI STA ROBA IN UN ViewModel
        val db = Room.databaseBuilder(this.applicationContext, LocalDB::class.java, "LocalDatabase")
            .allowMainThreadQueries().build()
        var intent: Intent? = null

        // controllo se il tutorial è stato completato e se il comprensorio è stato scelto
        if (db.localDatabaseDao().isTutorialCompletato() != 1)
            intent = Intent(this.applicationContext, WelcomeActivity::class.java)
        else if (db.localDatabaseDao().getIdComprensorio() == null)
            intent = Intent(this.applicationContext, SelezioneComprensorio::class.java)

        // se necessario, apro la activity che serve
        if (intent != null) {
            // svuoto il back stack per evitare bug
            finishAffinity()
            // avvio la activity che serve
            startActivity(intent)
        }

        // ottengo la posizione precisa dell'utente (tramite il gps)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Log.i("SkiTracker GPS Location", "Fine Location Allowed")
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.i("SkiTracker GPS Location", "Coarse Location Allowed")
                } else -> {
                    Log.w("SkiTracker GPS Location", "User denied GPS access authorization")
                }
            }
        }
        Log.i("SkiTracker GPS Location", "Trying to get GPS location.")
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

        // questi sono i miei fragment
        val mapFragment = MappaFragment()
        val infoFragment = InfoPisteFragment()
        val historyFragment = CronologiaFragment()

        // visualizzo il fragment della mappa, che è quello di default
        replaceFragment(mapFragment, "map")

        // implemento la navigazione tra fragment con i bottoni nel bottomNavigationBar
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottomNavMapItem -> replaceFragment(mapFragment, "map")
                R.id.bottomNavInformationItem -> replaceFragment(infoFragment, "info")
                R.id.bottomNavHistoryItem -> replaceFragment(historyFragment, "history")

                else -> {}
            }
            true
        }
    }

    // creazione menu a tendina
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.map_view_menu, menu)
        return true
    }

    // configurazione funzioni del menù a tendina
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) { //nel when, si ricorda che si possono mettere solo le costanti
            R.id.item1 -> { return true }
            R.id.item2 -> { return true }
            R.id.item3 -> { //ritorno alla activity del tutorial
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.item4 -> { //indirizzamento all'activity "ulteriori informazioni"
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // funzione per il cambio di fragment dopo i click su bottom navigation bar
    // basato sull'uso del FragmentManager
    private fun replaceFragment(fragmentDaVisualizzare: Fragment, tag: String) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragmentPrecedente = fragmentManager.findFragmentByTag(selectedFragmentTag)

        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.frame_layout, fragmentDaVisualizzare, tag)
        } else {
            if (fragmentPrecedente != null) {
                fragmentTransaction.hide(fragmentPrecedente)
                fragmentTransaction.show(fragmentDaVisualizzare)
            }
        }

        selectedFragmentTag = tag
        fragmentTransaction.commit()
    }
}