package it.omarkiarafederico.skitracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay


class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // inizializzazione mappa
        val map : MapView = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.isTilesScaledToDpi = true
        Configuration.getInstance().userAgentValue = "skitracker"

        // creo un controller della mappa per impostare una posizione iniziale
        val mapController = map.controller
        val startPoint = GeoPoint(46.370066950988, 10.659417137504)
        mapController.setCenter(startPoint)
        mapController.animateTo(startPoint, 16.0, 1200)

        // aggiungo la possibilità di poter ruotare la mappa con due dita
        val mRotationGestureOverlay = RotationGestureOverlay(map)
        mRotationGestureOverlay.setEnabled(true)
        map.getOverlays().add(mRotationGestureOverlay)
    }
}