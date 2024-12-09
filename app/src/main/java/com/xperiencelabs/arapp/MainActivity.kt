package com.xperiencelabs.arapp

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var engineButton: ExtendedFloatingActionButton
    private lateinit var motorButton: ExtendedFloatingActionButton
    private lateinit var bikeModelNode: ArModelNode
    private lateinit var engineModelNode: ArModelNode
    private lateinit var webView: WebView
    private lateinit var detailButton: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        placeButton = findViewById(R.id.place)
        engineButton = findViewById(R.id.engineButton)
        motorButton = findViewById(R.id.motorButton)
        sceneView = findViewById(R.id.sceneView)
        detailButton = findViewById(R.id.detail)
        webView = findViewById(R.id.webView)

        // Load bike model
        bikeModelNode = ArModelNode(sceneView.engine).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/bike.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            ) {
                sceneView.planeRenderer.isVisible = true
            }
            isVisible = false
        }

        // Load engine model
        engineModelNode = ArModelNode(sceneView.engine).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/engine.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            )
            isVisible = false
        }

        sceneView.addChild(bikeModelNode)
        sceneView.addChild(engineModelNode)

        // Set up place button
        placeButton.setOnClickListener {
            bikeModelNode.isVisible = true
            bikeModelNode.anchor()
            sceneView.planeRenderer.isVisible = false
            placeButton.isGone = true
            engineButton.isGone = false
            motorButton.isGone = false
        }

        // Engine button to show engine model
        engineButton.setOnClickListener {
            bikeModelNode.isVisible = false
            engineModelNode.isVisible = true
        }

        // Motor button to show bike model
        motorButton.setOnClickListener {
            engineModelNode.isVisible = false
            bikeModelNode.isVisible = true
        }

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        val sketchfabUrl = "https://sketchfab.com/models/ad2416e341cb4beca3f86b0b00e84749/embed"

        detailButton.setOnClickListener {
            // Hide AR Scene and show WebView
            sceneView.isGone = true
            webView.isVisible = true
            webView.loadUrl(sketchfabUrl)

            engineButton.isGone = true
            motorButton.isGone = true
            detailButton.isGone = true

            // Set fullscreen flags for immersive experience
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    override fun onBackPressed() {
        if (webView.isVisible) {
            // Exit fullscreen and return to AR scene
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            webView.isGone = true
            sceneView.isVisible = true

            engineButton.isVisible = true
            motorButton.isVisible = true
            detailButton.isVisible = true
        } else {
            super.onBackPressed()
        }
    }
}
