package com.gourav.currencyconverter.views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gourav.currencyconverter.R
import com.gourav.currencyconverter.databinding.ActivityCurrencyBinding
import com.gourav.currencyconverter.databinding.ActivityMainBinding
import com.gourav.currencyconverter.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG: String = "main>>>>"

    //    lateinit var binding: ActivityMainBinding
    lateinit var binding1: ActivityCurrencyBinding
    lateinit var currencyAdapter: CurrencyAdapter
    var toCurrency = "USD"

    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding1 = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding1.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val t1: Long = System.currentTimeMillis()

        initRecyclerView()

        binding1.cardConvert.setOnClickListener {
            mainViewModel.convert(
                binding1.etAmount.text.toString(),
                binding1.spinnerFrom.selectedItem.toString(),
                toCurrency
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvents.Success -> {
                        binding1.progressBar.isVisible = false
                        binding1.tvResult.setTextColor(Color.BLACK)
                        binding1.tvResult.text = event.resultMessage
                    }
                    is MainViewModel.CurrencyEvents.Error -> {
                        binding1.progressBar.isVisible = false
                        binding1.tvResult.setTextColor(Color.RED)
                        binding1.tvResult.text = event.errorMessage
                    }
                    is MainViewModel.CurrencyEvents.Loading -> {
                        binding1.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initRecyclerView() {
        val currencyList = arrayListOf<String>()
        currencyList.addAll(resources.getStringArray(R.array.currency_codes))
        currencyAdapter = CurrencyAdapter(this, currencyList)
        currencyAdapter.setCurrencies(currencyList)
        binding1.recyclerTo.apply {
//            layoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            setHasFixedSize(true)
            adapter = currencyAdapter
        }

        currencyAdapter.SetOnItemClickListener(object : CurrencyAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int, checkedPosition: Int) {
                toCurrency = currencyList[position]
                if (checkedPosition != position) {
                    currencyAdapter.notifyItemChanged(checkedPosition)
                    currencyAdapter.changePosition(position)
                }
            }

            override fun onLongItemClick(view: View?, position: Int) {

            }

        })
    }
}