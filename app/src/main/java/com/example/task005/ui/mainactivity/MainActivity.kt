package com.example.task005.ui.mainactivity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.task005.R
import com.example.task005.databinding.ActivityMainBinding
import com.example.task005.models.business.Card
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.activity_main,
            null,
            false
        )
        setContentView(binding.root)
        title = getString(R.string.list)

        bindToViewModel()
        showProgress()
        viewModel.getData()
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataApiResult.getSuccess().observe(
            this
        ) {
            it?.let { event ->
                if (event.shouldReadContent()) {
                    setUpUI(event.getContent()!!)
                    hideProgress()
                }
            }
        }
        viewModel.mediatorLiveDataApiResult.getFail().observe(
            this
        ) {
            it?.let { event ->
                if (event.shouldReadContent()) {
                    event.readContent()
                    Toast.makeText(this, "Failed to get data", Toast.LENGTH_LONG).show()
                    hideProgress()
                }
            }
        }
    }

    private fun setUpUI(cards: ArrayList<Card>) {

    }
}