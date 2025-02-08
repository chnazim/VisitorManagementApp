package com.bennellin.app.visitormanagementapp.tab.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.general.utils
import com.bennellin.app.visitormanagementapp.tab.activity.DetailViewActivity
import com.bennellin.app.visitormanagementapp.tab.adapter.VisitorAdapterDashboard
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var visitorAdapter: VisitorAdapterDashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerView = view.findViewById(R.id.visitorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        callNVisitorListApi(SharedPreferenceManager.getAuthToken("auth_token"))
        return view
    }

    private fun callNVisitorListApi(authToken: String?) {

        val call = RetrofitInstance.apiService.getNVisilorsList("Bearer $authToken", utils.LIMIT)

        call.enqueue(object : Callback<List<Visitor>> {
            override fun onResponse(call: Call<List<Visitor>>, response: Response<List<Visitor>>) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitorList = response.body() ?: emptyList()
                    visitorAdapter = VisitorAdapterDashboard(
                        visitorList,
                        onPositiveClick = { visitor: Visitor -> performCheckOutApiCall(visitor) },
                        onCallDetailView = { visitor: Visitor ->
                            callDetailView(visitor)
                        })
                    val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    recyclerView.addItemDecoration(divider)
                    recyclerView.adapter = visitorAdapter
                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Visitor>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })


    }

    private fun callDetailView(visitor: Visitor) {
        val intent = Intent(context, DetailViewActivity::class.java)
        intent.putExtra("visitor", visitor)
        startActivity(intent)
    }

    private fun performCheckOutApiCall(visitor: Visitor) {

        val authToken = SharedPreferenceManager.getAuthToken("auth_token")
        Log.d("tag", "idEid: ${visitor.idEidReading}")

        val call =
            RetrofitInstance.apiService.checkOutVisitor("Bearer $authToken", visitor.idEidReading)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    Toast.makeText(context, "Visitor Checked Out Successfully", Toast.LENGTH_SHORT)
                        .show()
                    reloadTheFragment()
                } else {
                    Toast.makeText(
                        context, "Something went wrong, Please try again later", Toast.LENGTH_SHORT
                    ).show()
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    context, "Something went wrong, Please try again later", Toast.LENGTH_SHORT
                ).show()
                println("onFailure Error: ${t.message}")
            }

        })

    }

    private fun reloadTheFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val newFragment = DashboardFragment()

        // Replace the current fragment with the new instance
        transaction.replace(R.id.fragment_container, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = DashboardFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}