package com.internshala.echo.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.activities.MainActivity
import com.internshala.echo.fragments.SongsPlayingFragment

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {

                if (SongsPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongsPlayingFragment.Statified.mediaPlayer?.pause()
                    SongsPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val tm: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {

                    try {
                        MainActivity.Statified.notificationManager?.cancel(1978)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {

                        if (SongsPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                            SongsPlayingFragment.Statified.mediaPlayer?.pause()
                            SongsPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                else -> {

                }
            }
        }
    }


}