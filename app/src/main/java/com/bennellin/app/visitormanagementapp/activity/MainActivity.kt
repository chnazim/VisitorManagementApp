package com.bennellin.app.visitormanagementapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bennellin.app.visitormanagementapp.R
import com.bennellin.app.visitormanagementapp.general.SharedPreferenceManager
import com.bennellin.app.visitormanagementapp.adapters.VisitorAdapter
import com.bennellin.app.visitormanagementapp.models.VisitorData
import com.bennellin.app.visitormanagementapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var itemAdapter: VisitorAdapter


//    @Inject
//    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val itemList = listOf(
            VisitorData(
                R.drawable.baseline_person_24,
                "JhoneDoe",
                "78419924354562",
                "2024-10-07",
                "Gate A"
            ),
            VisitorData(
                R.drawable.baseline_person_24,
                "JhoneDoe",
                "78419924354562",
                "2024-10-07",
                "Gate A"
            ),
            VisitorData(
                R.drawable.baseline_person_24,
                "JhoneDoe",
                "78419924354562",
                "2024-10-07",
                "Gate A"
            ),
            VisitorData(
                R.drawable.baseline_person_24,
                "JhoneDoe",
                "78419924354562",
                "2024-10-07",
                "Gate A"
            ),
            VisitorData(
                R.drawable.baseline_person_24,
                "JhoneDoe",
                "78419924354562",
                "2024-10-07",
                "Gate A"
            ),
            VisitorData(
                R.drawable.baseline_person_24,
                "JhoneDoe",
                "78419924354562",
                "2024-10-07",
                "Gate A"
            ),
        )

        itemAdapter = VisitorAdapter(itemList)
        binding.recyclerView.adapter = itemAdapter
        binding.scanNew.setOnClickListener {
            val intent = Intent(this@MainActivity, GatesListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                SharedPreferenceManager.saveLoggedIn("is_logged_in", false)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}