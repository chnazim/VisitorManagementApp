package com.bennellin.app.visitormanagementapp.tab.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.FragmentDashboardBinding
import com.bennellin.app.visitormanagementapp.databinding.FragmentReportsBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.general.utils
import com.bennellin.app.visitormanagementapp.tab.adapter.VisitorAdapter
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.bennellin.app.visitormanagementapp.tab.network.models.FilterApiRequestBody
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitPurpose
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitorStatistics
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitorType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var visitorAdapter: VisitorAdapter

    private var fromDate: Calendar = Calendar.getInstance()
    private var toDate: Calendar = Calendar.getInstance()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    //    private var selectedVisitorType: VisitorType? = null
//    private var selectedVisitPurpose: VisitPurpose? = null
    private lateinit var selectedType: String

    private lateinit var binding: FragmentReportsBinding
    val options = arrayOf("Name", "ID Number", "Contact Detail", "Visitor Type", "Visit Purpose")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_reports, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reports, container, false)
        binding.visitorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.visitorRecyclerView.setHasFixedSize(true)

        callNVisitorListApi(SharedPreferenceManager.getAuthToken("auth_token"))

        val todayDate = dateFormat.format(fromDate.time)
        binding.filterFromDate.setText(todayDate)
        binding.filterToDate.setText(todayDate)

        binding.filterFromDate.setOnClickListener { showFromDatePicker() }
        binding.filterToDate.setOnClickListener { showToDatePicker() }

//        getVisitorType(SharedPreferenceManager.getAuthToken("auth_token"))
//        getVisitPurpose(SharedPreferenceManager.getAuthToken("auth_token"))

        val adapter =
            ArrayAdapter(
                requireContext().applicationContext,
                R.layout.spinner_item_layout_filter,
                options
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fieldType.adapter = adapter

        binding.fieldType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = getSelectedOption(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case when nothing is selected
                selectedType = options[0]
            }
        }

        binding.searchButton.setOnClickListener({
            val fromDateStr = binding.filterFromDate.text.toString()
            val toDateStr = binding.filterToDate.text.toString()
            val query = binding.query.text.toString()



            searchVisitors(
                fromDateStr,
                toDateStr,
                selectedType,
                query
            )
        })

        binding.resetButton.setOnClickListener {
            binding.filterFromDate.setText(todayDate)
            binding.filterToDate.setText(todayDate)

            callNVisitorListApi(SharedPreferenceManager.getAuthToken("auth_token"))
        }

        return binding.root
    }

    fun getSelectedOption(position: Int): String {
        return when (position) {
            0 -> "name"
            1 -> "id"
            2 -> "contact"
            3 -> "visitortype"
            4 -> "visitpurpose"
            else -> "Invalid Selection"
        }
    }


    private fun searchVisitors(
        fromDate: String,
        toDate: String,
        selectedType: String,
        query: String
    ) {
        val authToken = SharedPreferenceManager.getAuthToken("auth_token")
        val filterApiRequestBody = FilterApiRequestBody(
            From = fromDate,
            To = toDate,
            FilterFeild = selectedType,
            DataToSearch = query

        )

        val call =
            RetrofitInstance.apiService.filteredVisitors("Bearer $authToken", filterApiRequestBody)

        call.enqueue(object : Callback<List<Visitor>> {
            override fun onResponse(call: Call<List<Visitor>>, response: Response<List<Visitor>>) {
                if (response.isSuccessful) {
                    val visitorList = response.body() ?: emptyList()
                    if (visitorList.isNotEmpty()) {
                        visitorAdapter.updateList(response.body()!!)
                        binding.progressBar.visibility = View.GONE
                    } else {
                        Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Visitor>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callNVisitorListApi(authToken: String?) {

        val call = RetrofitInstance.apiService.getNVisilorsList("Bearer $authToken", utils.LIMIT)

        call.enqueue(object : Callback<List<Visitor>> {
            override fun onResponse(call: Call<List<Visitor>>, response: Response<List<Visitor>>) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitorList = response.body() ?: emptyList()
                    visitorAdapter = VisitorAdapter(visitorList)
                    val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                    binding.visitorRecyclerView.addItemDecoration(divider)
                    binding.visitorRecyclerView.adapter = visitorAdapter
                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Visitor>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })


    }


//    private fun getVisitPurpose(authToken: String?) {
//        val call = RetrofitInstance.apiService.getVisitorPurpose("Bearer $authToken")
//
//        call.enqueue(object : Callback<List<VisitPurpose>> {
//            override fun onResponse(
//                call: Call<List<VisitPurpose>>, response: Response<List<VisitPurpose>>
//            ) {
//                if (response.isSuccessful) {
//                    println("api call successful ${response.body()}")
//                    val visitPurposeList = response.body()?.toMutableList() ?: mutableListOf()
//
//                    val allOption = VisitPurpose(
//                        IdVisitPurpose = -1,
//                        VisitPurpose = "All",
//                        SortOrder = Int.MIN_VALUE, // Ensures it's always first
//                        IsEditable = false
//                    )
//                    visitPurposeList.add(0, allOption)
//
//                    visitPurposeList.sortedBy { it.SortOrder }
//                    val visitorPurpose = visitPurposeList.map { it.VisitPurpose }
//
//                    val adapter = ArrayAdapter(
//                        context!!, R.layout.spinner_item_layout_filter, visitorPurpose
//                    )
//                    adapter.setDropDownViewResource(R.layout.spinner_item_layout_filter)
//                    binding.visitPurpose.adapter = adapter
//
//                    binding.visitPurpose.onItemSelectedListener =
//                        object : AdapterView.OnItemSelectedListener {
//                            override fun onItemSelected(
//                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
//                            ) {
//                                selectedVisitPurpose = visitPurposeList[position]
//                            }
//
//                            override fun onNothingSelected(parent: AdapterView<*>?) {
//                                selectedVisitPurpose = null
//                            }
//
//                        }
//
//                } else {
//                    println("Response Error: ${response.code()} - ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<VisitPurpose>>, t: Throwable) {
//                println("onFailure Error: ${t.message}")
//            }
//
//        })
//
//    }
//
//    private fun getVisitorType(authToken: String?) {
//        val call = RetrofitInstance.apiService.getVisitorType("Bearer $authToken")
//
//        call.enqueue(object : Callback<List<VisitorType>> {
//            override fun onResponse(
//                call: Call<List<VisitorType>>, response: Response<List<VisitorType>>
//            ) {
//                if (response.isSuccessful) {
//                    println("api call successful ${response.body()}")
//                    val visitorTypeList = response.body()?.toMutableList() ?: mutableListOf()
//                    val allOption = VisitorType(
//                        IdVisitorType = -1,
//                        VisitorType = "All",
//                        SortOrder = Int.MIN_VALUE, // Ensures it's always first
//                        IsEditable = false
//                    )
//                    visitorTypeList.add(0, allOption)
//
//                    visitorTypeList.sortedBy { it.SortOrder }
//                    val visitorTypes = visitorTypeList.map { it.VisitorType }
//
//                    val adapter = ArrayAdapter(
//                        context!!, R.layout.spinner_item_layout_filter, visitorTypes
//                    )
//                    adapter.setDropDownViewResource(R.layout.spinner_item_layout_filter)
//                    binding.visitorType.adapter = adapter
//
//                    binding.visitorType.onItemSelectedListener =
//                        object : AdapterView.OnItemSelectedListener {
//                            override fun onItemSelected(
//                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
//                            ) {
//                                selectedVisitorType = visitorTypeList[position]
//                            }
//
//                            override fun onNothingSelected(parent: AdapterView<*>?) {
//                                selectedVisitorType = null
//                            }
//
//                        }
//
//                } else {
//                    println("Response Error: ${response.code()} - ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<VisitorType>>, t: Throwable) {
//                println("onFailure Error: ${t.message}")
//            }
//
//        })
//
//    }


    private fun showFromDatePicker() {
        val year = fromDate.get(Calendar.YEAR)
        val month = fromDate.get(Calendar.MONTH)
        val day = fromDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            context?.let {
                DatePickerDialog(it, { _, selectedYear, selectedMonth, selectedDay ->
                    fromDate.set(selectedYear, selectedMonth, selectedDay)
                    val selectedDate = dateFormat.format(fromDate.time)
                    binding.filterFromDate.setText(selectedDate)

                    // If To Date is before From Date, reset it
                    if (fromDate.before(fromDate)) {
                        toDate.time = fromDate.time
                        binding.filterToDate.setText(selectedDate)
                    }
                }, year, month, day)
            }

        datePickerDialog!!.show()
    }

    private fun showToDatePicker() {
        val year = toDate.get(Calendar.YEAR)
        val month = toDate.get(Calendar.MONTH)
        val day = toDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            context?.let {
                DatePickerDialog(it, { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    // Ensure To Date is after From Date
                    if (selectedCalendar.before(fromDate)) {
                        binding.filterToDate.error = "To Date cannot be before From Date"
                        return@DatePickerDialog
                    }

                    toDate.time = selectedCalendar.time
                    val selectedDate = dateFormat.format(toDate.time)
                    binding.filterToDate.setText(selectedDate)
                }, year, month, day)
            }

        // Restrict To Date to be after or same as From Date
        datePickerDialog!!.datePicker.minDate = fromDate.timeInMillis

        datePickerDialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}