package com.internshala.echo.fragments

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.internshala.echo.R
import kotlinx.android.synthetic.main.fragment_favourites.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingsFragment : Fragment() {
    var shakeSwitch: Switch? = null
    var myactivity: Activity? = null
    var trackPosition: Int = 0
    var nowPlayingBottomBar: RelativeLayout? = null
    var songTitle: TextView? = null
    var recyclerView: TextView? = null
    var playPauseButton: ImageButton? = null


    object Statified {
        var MY_PREFS_NAME = "ShakeFeature"
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomBarSetup()
        val prefs = myactivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
        val isAllowed = prefs?.getBoolean("feature", false)
        if (isAllowed as Boolean) {
            shakeSwitch?.isChecked = true
        } else {
            shakeSwitch?.isChecked = false
        }
        shakeSwitch?.setOnCheckedChangeListener({ compoundButton, b ->
            if (b) {
                val editor = myactivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()

            } else {
                val editor = myactivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()

            }
        })

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)
        activity?.title = "Settings"
        shakeSwitch = view.findViewById(R.id.switchshake)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenbarSetScreen)
        songTitle = view.findViewById(R.id.songTitle)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        recyclerView = view.findViewById(R.id.favouriteRecycler)

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    fun bottomBarSetup() {
        try {
            bottomClickHandler()
            songTitle?.setText(SongsPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongsPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongsPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongsPlayingFragment.Staticated.onSongCompletion()
            }

            if (SongsPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun bottomClickHandler() {

        nowPlayingBottomBar?.setOnClickListener({
            FavouritesFragment.Statified.mediaPlayer = SongsPlayingFragment.Statified.mediaPlayer

            val songsPlayingFragment = SongsPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongsPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongsPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongsPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songId", SongsPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongsPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongsPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar", "success")
            songsPlayingFragment.arguments = args

            fragmentManager?.beginTransaction()
                    ?.replace(R.id.details_fragment, songsPlayingFragment)
                    ?.addToBackStack("MainScreenFragment")
                    ?.commit()

        })

        playPauseButton?.setOnClickListener({
            if (SongsPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongsPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongsPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongsPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongsPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })

    }


}