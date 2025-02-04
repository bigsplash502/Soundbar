package com.example.soundbar

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.LoadAdError
import com.example.soundbar.ui.theme.SoundBarTheme

class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private var rewardedAd: RewardedAd? = null
    private val adUnitId = "ca-app-pub-3836400919646389/3799814676"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}
        loadRewardedAd()

        // Initialize the MediaPlayer and stream the audio from the URL
        val soundUrl = "https://drive.google.com/uc?export=download&id=11M4jPABxJXX45TwXEXvitVDKzUwFXMiA"
        mediaPlayer = MediaPlayer().apply {
            setDataSource(soundUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
            setOnErrorListener { _, what, extra ->
                Toast.makeText(this@MainActivity, "Error: $what, $extra", Toast.LENGTH_SHORT).show()
                false
            }
        }

        // Connect to the database
        try {
            DatabaseHelper.connect()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to connect to database: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        setContent {
            SoundBarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "SoundBar",
                        modifier = Modifier.padding(innerPadding)
                    )

                    Button(
                        onClick = { showRewardedAd() },
                        enabled = rewardedAd != null
                    ) {
                        Text(text = "Preview Sound (Watch Ad)")
                    }
                }
            }
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
            }
        })
    }

    private fun showRewardedAd() {
        rewardedAd?.let { ad ->
            ad.show(this) { rewardItem: RewardItem ->
                println("User earned reward: ${rewardItem.type} amount: ${rewardItem.amount}")
            }
        } ?: run {
            loadRewardedAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        // Close the database connection when the activity is destroyed
        DatabaseHelper.close()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SoundBarTheme {
        Greeting("SoundBar")
    }
}