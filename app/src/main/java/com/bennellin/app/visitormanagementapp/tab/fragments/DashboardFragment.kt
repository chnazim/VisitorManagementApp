package com.bennellin.app.visitormanagementapp.tab.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.databinding.FragmentDashboardBinding
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.general.utils
import com.bennellin.app.visitormanagementapp.tab.activity.DetailViewActivity
import com.bennellin.app.visitormanagementapp.tab.adapter.VisitorAdapterDashboard
import com.bennellin.app.visitormanagementapp.tab.network.RetrofitInstance
import com.bennellin.app.visitormanagementapp.tab.network.models.FilterApiRequestBody
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitPurpose
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitorType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

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

    private lateinit var visitorAdapter: VisitorAdapterDashboard

    private var fromDate: Calendar = Calendar.getInstance()
    private var toDate: Calendar = Calendar.getInstance()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var selectedVisitorType: VisitorType? = null
    private var selectedVisitPurpose: VisitPurpose? = null

    private lateinit var binding: FragmentDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.visitorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.visitorRecyclerView.setHasFixedSize(true)



        callNVisitorListApi(SharedPreferenceManager.getAuthToken("auth_token"))


        val todayDate = dateFormat.format(fromDate.time)
        binding.filterFromDate.setText(todayDate)
        binding.filterToDate.setText(todayDate)

        binding.filterFromDate.setOnClickListener { showFromDatePicker() }
        binding.filterToDate.setOnClickListener { showToDatePicker() }

        getVisitorType(SharedPreferenceManager.getAuthToken("auth_token"))
        getVisitPurpose(SharedPreferenceManager.getAuthToken("auth_token"))

//        etVisitPurposeDropdown.setOnClickListener {
//            showPopupMenu(etVisitPurposeDropdown, options)
//        }

        binding.searchButton.setOnClickListener({
            val fromDateStr = binding.filterFromDate.text.toString()
            val toDateStr = binding.filterToDate.text.toString()
            val visitPurpose = selectedVisitPurpose?.VisitPurpose ?: ""
            val visitorType = selectedVisitorType?.VisitorType ?: ""
            val name = binding.name.text.toString()
            val emailId = binding.emailId.text.toString()
            val phoneNumber = binding.phoneNumber.text.toString()
            val idNumber = binding.idNumber.text.toString()

            searchVisitors(
                fromDateStr,
                toDateStr,
                visitorType,
                visitPurpose,
                name,
                emailId,
                phoneNumber,
                idNumber
            )
        })

        binding.resetButton.setOnClickListener {
            binding.filterFromDate.setText(todayDate)
            binding.filterToDate.setText(todayDate)
            binding.visitorType.setSelection(0)
            binding.visitPurpose.setSelection(0)
            binding.name.setText("")
            binding.emailId.setText("")
            binding.phoneNumber.setText("")
            binding.idNumber.setText("")
            callNVisitorListApi(SharedPreferenceManager.getAuthToken("auth_token"))
        }


        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        return binding.root

    }


    private fun getVisitPurpose(authToken: String?) {
        val call = RetrofitInstance.apiService.getVisitorPurpose("Bearer $authToken")

        call.enqueue(object : Callback<List<VisitPurpose>> {
            override fun onResponse(
                call: Call<List<VisitPurpose>>, response: Response<List<VisitPurpose>>
            ) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitPurposeList = response.body()?.toMutableList() ?: mutableListOf()

                    val allOption = VisitPurpose(
                        IdVisitPurpose = -1,
                        VisitPurpose = "All",
                        SortOrder = Int.MIN_VALUE, // Ensures it's always first
                        IsEditable = false
                    )
                    visitPurposeList.add(0, allOption)

                    visitPurposeList.sortedBy { it.SortOrder }
                    val visitorPurpose = visitPurposeList.map { it.VisitPurpose }

                    val adapter = ArrayAdapter(
                        context!!, R.layout.spinner_item_layout_filter, visitorPurpose
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_item_layout_filter)
                    binding.visitPurpose.adapter = adapter

                    binding.visitPurpose.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                selectedVisitPurpose = visitPurposeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                selectedVisitPurpose = null
                            }

                        }

                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VisitPurpose>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })

    }

    private fun getVisitorType(authToken: String?) {
        val call = RetrofitInstance.apiService.getVisitorType("Bearer $authToken")

        call.enqueue(object : Callback<List<VisitorType>> {
            override fun onResponse(
                call: Call<List<VisitorType>>, response: Response<List<VisitorType>>
            ) {
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    val visitorTypeList = response.body()?.toMutableList() ?: mutableListOf()
                    val allOption = VisitorType(
                        IdVisitorType = -1,
                        VisitorType = "All",
                        SortOrder = Int.MIN_VALUE, // Ensures it's always first
                        IsEditable = false
                    )
                    visitorTypeList.add(0, allOption)

                    visitorTypeList.sortedBy { it.SortOrder }
                    val visitorTypes = visitorTypeList.map { it.VisitorType }

                    val adapter = ArrayAdapter(
                        context!!, R.layout.spinner_item_layout_filter, visitorTypes
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_item_layout_filter)
                    binding.visitorType.adapter = adapter

                    binding.visitorType.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                selectedVisitorType = visitorTypeList[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                selectedVisitorType = null
                            }

                        }

                } else {
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VisitorType>>, t: Throwable) {
                println("onFailure Error: ${t.message}")
            }

        })

    }

    private fun showPopupMenu(anchor: EditText, options: List<String>) {
        val popupMenu = PopupMenu(context, anchor)
        options.forEachIndexed { index, option ->
            popupMenu.menu.add(0, index, index, option)
        }
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            anchor.setText(item.title) // Set selected option to EditText
            true
        }
        popupMenu.show()
    }

    private fun searchVisitors(
        fromDate: String,
        toDate: String,
        visitorType: String,
        visitorPurpose: String,
        name: String,
        emailId: String,
        phoneNumber: String,
        idNumber: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        val authToken = SharedPreferenceManager.getAuthToken("auth_token")
        val filterApiRequestBody = FilterApiRequestBody(
            FromDate = "",
            ToDate = "",
            VisitPurpose = if (visitorPurpose == "All") {
                ""
            } else {
                visitorPurpose
            },
            VisitorType = if (visitorType == "All") {
                ""
            } else {
                visitorType
            },
            Email = emailId,
            Phone = phoneNumber,
            Name = name,
            IdNumber = idNumber
        )

        val call =
            RetrofitInstance.apiService.filteredVisitors("Bearer $authToken", filterApiRequestBody)

        call.enqueue(object : Callback<List<Visitor>> {
            override fun onResponse(call: Call<List<Visitor>>, response: Response<List<Visitor>>) {
                if (response.isSuccessful && response.body() != null) {
                    visitorAdapter.updateList(response.body()!!)
                    binding.progressBar.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<Visitor>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun callNVisitorListApi(authToken: String?) {

        binding.progressBar.visibility = View.VISIBLE

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
                    binding.visitorRecyclerView.addItemDecoration(divider)
                    binding.visitorRecyclerView.adapter = visitorAdapter
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Something went wrong....", Toast.LENGTH_SHORT).show()
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Visitor>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "Something went wrong with API call....",
                    Toast.LENGTH_SHORT
                ).show()
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
        binding.progressBar.visibility = View.VISIBLE

        val authToken = SharedPreferenceManager.getAuthToken("auth_token")
        Log.d("tag", "idEid: ${visitor.idEidReading}")

        val call =
            RetrofitInstance.apiService.checkOutVisitor("Bearer $authToken", visitor.idEidReading)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    println("api call successful ${response.body()}")
                    Toast.makeText(context, "Visitor Checked Out Successfully", Toast.LENGTH_SHORT)
                        .show()
                    reloadTheFragment()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context, "Something went wrong, Please try again later", Toast.LENGTH_SHORT
                    ).show()
                    println("Response Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    context, "Something went wrong, Please try again later", Toast.LENGTH_SHORT
                ).show()
                println("onFailure Error: ${t.message}")
            }

        })

    }

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