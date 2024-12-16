package uz.turgunboyevjurabek.youtubeexample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import uz.turgunboyevjurabek.youtubeexample.ui.theme.YouTubeExampleTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        YouTubePlayerScreen(lifecycleOwner = LocalLifecycleOwner.current, videoId = "FgAL6T_KILw")
                    }
                }
            }
        }
    }
}
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeWebView(url: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onLoadResource(view: WebView?, url: String?) {
                        super.onLoadResource(view, url)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                        settings.loadsImagesAutomatically = true
                    }
                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        println("WebView Error: $description, URL: $failingUrl")
                    }
                }
                loadUrl(url)
            }
        }
    )
}
@Composable
fun YouTubePlayerScreen(
    lifecycleOwner: LifecycleOwner,
    videoId: String
) {
    var isFullscreen by remember { mutableStateOf(false) }
//    FullScreenEffect(isFullscreen = isFullscreen)
    val context = LocalContext.current
    val activity = context as? Activity


    Box(modifier = Modifier.fillMaxWidth()) {
        AndroidView(
            factory = {
                YouTubePlayerView(context = it).apply {
                    lifecycleOwner.lifecycle.addObserver(this)
                    enableBackgroundPlayback(true)
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId = videoId, startSeconds = 0f)
                        }
                    })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) 
        )
        IconButton(
            onClick = {
                isFullscreen = !isFullscreen
                activity?.requestedOrientation =
                    if (isFullscreen) {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE // Landscape holatga o‘tadi
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Portret holatga qaytadi
                    }
                      },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 50.dp, end = 12.dp)
        ) {
            Icon(
                painter = if (isFullscreen) painterResource(id =R.drawable.ic_crop_portrait)  else painterResource(id = R.drawable.ic_crop_rotate),
                contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                tint = Color.White
            )
        }
    }
}
@Composable
fun FullScreenEffect(isFullscreen: Boolean) {
    val activity = LocalContext.current as Activity
    DisposableEffect(isFullscreen) {
        if (isFullscreen) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        onDispose { }
    }
}

