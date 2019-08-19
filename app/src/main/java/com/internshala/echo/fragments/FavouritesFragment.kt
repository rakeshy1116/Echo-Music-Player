package com.internshala.echo.fragments

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.activities.MainActivity
import com.internshala.echo.adapters.FavouritesAdapter
import com.internshala.echo.databases.Echodatabase
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FavouritesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FavouritesFragment : Fragment() {

    var myActivity: Activity? = null
    var noFavourites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favouriteContent: Echodatabase? = null

    var refreshList: ArrayList<Songs>? = null
    var getListfromDatabase: ArrayList<Songs>? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favouriteContent = Echodatabase(myActivity)
        display_favourites_by_searching()
        bottomBarSetup()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_favourites, container, false)
        activity?.title = "Favourites"
        favouriteContent = Echodatabase(myActivity)
        noFavourites = view?.findViewById(R.id.noFavourites)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenbarFavScreen)
        songTitle = view.findViewById(R.id.songTitle)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        recyclerView = view.findViewById(R.id.favouriteRecycler)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }


    fun getSongsFromPhone(): ArrayList<Songs> {
        val arrayList = ArrayList<Songs>()
        val contentResolver = myActivity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))


            }
        }

        return arrayList
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


    fun display_favourites_by_searching() {
//        if (favouriteContent?.checkSize() as Int > 0) {
//
//            refreshList = ArrayList<Songs>()
//            getListfromDatabase = favouriteContent?.queryDBList()
//            val fetchListFromDevice = getSongsFromPhone()
//            if (fetchListFromDevice != null) {
//                for (i in 0..fetchListFromDevice.size - 1) {
//                    for (j in 0..getListfromDatabase?.size as Int - 1) {
//                        if (getListfromDatabase?.get(j)?.songID == fetchListFromDevice?.get(i)?.songID) {
//                            refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
//                        }
//                    }
//                }
//            }
//
//
//            if (refreshList == null) {
//                recyclerView?.visibility = View.INVISIBLE
//                noFavourites?.visibility = View.VISIBLE
//            } else {
//                val favouritesAdapter = FavouritesAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
//                val mLayoutManager = LinearLayoutManager(activity)
//                recyclerView?.layoutManager = mLayoutManager
//                recyclerView?.itemAnimator = DefaultItemAnimator()
//                recyclerView?.adapter = favouritesAdapter
//                recyclerView?.setHasFixedSize(true)
//            }
//        } else {
        recyclerView?.visibility = View.INVISIBLE
        noFavourites?.visibility = View.VISIBLE

    }


}
