package com.android.cast.dlna.demo

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.util.logging.Level

class CastApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        java.util.logging.Logger.getLogger("org.fourthline.cling").level = Level.CONFIG
        com.android.cast.dlna.core.Logger.printThread = true
        com.android.cast.dlna.core.Logger.enabled = true
        com.android.cast.dlna.core.Logger.level = com.android.cast.dlna.core.Level.D
        com.android.cast.dlna.core.Logger.create("CastApplication").i("Application onCreate.")
    }
}

// ---------------------------------------------
// ---- URL
// ---------------------------------------------
const val castVideoMp4Url_20s = "https://video.699pic.com/videos/39/09/13/sUXhxQmpaNf91534390913.mp4"
const val castVideoMp4Url_1min = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
const val castVideoMp4Url_10min = "http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"
const val castVideoM3u8Url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"

//const val castVideoM3u8Url_480x270 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/v2/prog_index.m3u8"
const val castVideoM3u8Url_960x540 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/v5/prog_index.m3u8"
//const val castVideoM3u8Url_1920x1080 = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/v9/prog_index.m3u8"
const val castVideoM3u8Url_1920x1080 = "http://mode95420.cdngold.me:80/68fc652696/d24231d01f/325025"

data class VideoUrl(val url: String, val title: String)

val videoUrlList = mutableListOf(
    VideoUrl(castVideoMp4Url_20s, "20 seconds long video(mp4)"),
    VideoUrl(castVideoMp4Url_1min, "1 minute long video(mp4)"),
    VideoUrl(castVideoMp4Url_10min, "10 minute long video(mp4)"),
    VideoUrl(castVideoM3u8Url, "Standard Apple test flow m3u8 - multiple code rates"),
    VideoUrl(castVideoM3u8Url_960x540, "Standard Apple test flow(m3u8)-960x540"),
    VideoUrl(castVideoM3u8Url_1920x1080, "Sample link from Ibox IPTV"),
)

internal fun AppCompatActivity.replace(id: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(id, fragment).commit()
}

internal fun Fragment.replace(id: Int, fragment: Fragment) {
    childFragmentManager.beginTransaction().replace(id, fragment).commit()
}