package com.internshala.echo.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.internshala.echo.CurrentSongHelper
import com.internshala.echo.R
import com.internshala.echo.Songs
import com.internshala.echo.databases.Echodatabase
import com.internshala.echo.fragments.SongsPlayingFragment.Staticated.onSongCompletion
import com.internshala.echo.fragments.SongsPlayingFragment.Staticated.processInformation
import com.internshala.echo.fragments.SongsPlayingFragment.Staticated.updateTextViews
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.audioVisualization
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.count
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.currentPosition
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.currentSongHelper
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.endTImeText
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.fab
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.favouriteContent
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.fetchSongs
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.glView
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.loopImageButton
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.mediaPlayer
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.myActivity
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.nextImageButton
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.playPauseImageButton
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.previousImageButton
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.seekBar
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.shuffleImageButton
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.songArtistView
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.songTitleView
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.startTimeText
import com.internshala.echo.fragments.SongsPlayingFragment.Statified.updateSongTime
import kotlinx.android.synthetic.main.fragment_songs_playing.*
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongsPlayingFragment : Fragment() {

    @SuppressLint("StaticFieldLeak")
    object Statified {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTImeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentSongHelper: CurrentSongHelper? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null

        var MY_PREFS_NAME = "ShakeFeature"

        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null

        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null

        var fab: ImageButton? = null
        var favouriteContent: Echodatabase? = null

        var count: Int = 0
        var updateSongTime = object : Runnable {
            override fun run() {
                var getCurrent = mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong() as Long))))
                seekBar?.setProgress(getCurrent?.toInt() as Int)
                seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        mediaPlayer?.seekTo(seekBar?.progress)
                    }
                })


                Handler().postDelayed(this, 1000)

            }

        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            var songArtistUpdated = songArtist
            if (songTitle.equals("unknown", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("unknown", true)) {
                songArtistUpdated = "unknown"
            }
            songArtistView?.setText(songArtist)
            songTitleView?.setText(songTitle)

        }

        fun processInformation(mediaPlayer: MediaPlayer) {

            var finalTime = mediaPlayer?.duration
            var startTime = mediaPlayer?.currentPosition

            seekBar?.max = finalTime

            startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))))
            endTImeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))))
            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun onSongCompletion() {
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else {
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songId = nextSong?.songID as Long

                    updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

                    mediaPlayer?.reset()
                    try {
                        mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformation(mediaPlayer as MediaPlayer)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }


            //  if (favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            //     fab?.setBackgroundResource(R.drawable.favorite_on)
            // } else {
            //     fab?.setBackgroundResource(R.drawable.favorite_off)
            // }

        }


        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PLayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition
            } else if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songID as Long


            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

            mediaPlayer?.reset()
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            //if (favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            //        fab?.setBackgroundResource(R.drawable.favorite_on)
            //  } else {
            //    fab?.setBackgroundResource(R.drawable.favorite_off)
            //}


        }


    }


    var mAcceleration: Float = 0f
    var mAcclerationCurrent: Float = 0f
    var mAcclerationLast: Float = 0f


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_songs_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title = "Now Playing"
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        endTImeText = view?.findViewById(R.id.endTime)
        startTimeText = view?.findViewById(R.id.startTime)
        loopImageButton = view?.findViewById(R.id.loopButton)
        playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        glView = view?.findViewById(R.id.visualizer_view)
        fab = view?.findViewById(R.id.favouriteIcon)
        fab?.alpha = 0.8f

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioVisualization = glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
                Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        audioVisualization?.onPause()
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)

    }

    override fun onDestroyView() {
        audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAcclerationCurrent = SensorManager.GRAVITY_EARTH
        mAcclerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favouriteContent = Echodatabase(myActivity)

        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0

        try {
            //mediaPlayer?.reset()
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("SongId")!!.toLong()
            Statified.currentPosition = arguments!!.getInt("position")
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")



            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            currentSongHelper?.currentPosition = currentPosition

            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)


        } catch (e: Exception) {
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null) {
            Statified.mediaPlayer = FavouritesFragment.Statified.mediaPlayer
        } else {

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(path))
                mediaPlayer?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer?.start()
        }
        processInformation(Statified.mediaPlayer as MediaPlayer)


        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaPlayer?.setOnCompletionListener {
            onSongCompletion()
        }
        clickHandler()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop = true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isLoop = false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        //    if (favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
        //        fab?.setImageResource(R.drawable.favorite_on)
        //   } else {
        //       fab?.setImageResource(R.drawable.favorite_off)
        //   }


    }

    fun clickHandler() {
        count = 0
        fab?.setOnClickListener {
            if (count == 1) {
                Statified.fab?.setImageResource(R.drawable.favorite_off)
                //   Statified.favouriteContent?.deleteFavourite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity, "Removed From Favourites", Toast.LENGTH_SHORT).show()
                count = 0
            } else {
                Statified.fab?.setImageResource(R.drawable.favorite_on)
                //  Statified.favouriteContent?.storeAsFavourite(Statified.currentSongHelper?.songId?.toInt(), Statified.currentSongHelper?.songArtist, currentSongHelper?.songTitle,
                //         Statified.currentSongHelper?.songPath)
                count = 1
                Toast.makeText(Statified.myActivity, "Added to Favourites", Toast.LENGTH_SHORT).show()
            }

        }


        Statified.shuffleImageButton?.setOnClickListener {

            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()




            if (currentSongHelper?.isShuffle as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()

            } else {
                Statified.currentSongHelper?.isLoop = false
                Statified.currentSongHelper?.isShuffle = true
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)


                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()

            }

        }
        Statified.nextImageButton?.setOnClickListener {
            Statified.currentSongHelper?.isPlaying = true

            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        }
        Statified.previousImageButton?.setOnClickListener {
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        }
        Statified.loopImageButton?.setOnClickListener {
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (Statified.currentSongHelper?.isLoop as Boolean) {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLoop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            }


        }
        Statified.playPauseImageButton?.setOnClickListener {
            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

            }
        }
    }


    fun playPrevious() {
        Statified.currentPosition = Statified.currentPosition - 1
        if (Statified.currentPosition == -1) {
            Statified.currentPosition = 0
        }
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.currentSongHelper?.isLoop = false
        var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
        Statified.currentSongHelper?.songArtist = nextSong?.artist
        Statified.currentSongHelper?.songPath = nextSong?.songData
        Statified.currentSongHelper?.songTitle = nextSong?.songTitle
        Statified.currentSongHelper?.songId = nextSong?.songID as Long

        Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        Statified.mediaPlayer?.reset()
        try {
            Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //      if (Statified.favouriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
        //        Statified.fab?.setImageResource(R.drawable.favorite_on)
        //  } else {
        //    Statified.fab?.setImageResource(R.drawable.favorite_off)
        //}

    }

    fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                val x = event?.values?.get(0)
                val y = event?.values?.get(1)
                val z = event?.values?.get(2)

                mAcclerationLast = mAcclerationCurrent
                if (z != null && x != null && y != null) {
                    mAcclerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                }

                val delta = mAcclerationCurrent - mAcclerationLast
                mAcceleration = mAcceleration * 0.9f + delta
                if (mAcceleration > 2) {
                    val prefs = Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("Feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }


}
