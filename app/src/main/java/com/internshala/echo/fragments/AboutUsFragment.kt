package com.internshala.echo.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.internshala.echo.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AboutUsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AboutUsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AboutUsFragment : Fragment() {
    var myactivity: Activity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_about_us, container, false)
        activity?.title = "About Us"
        return view

    }


}
