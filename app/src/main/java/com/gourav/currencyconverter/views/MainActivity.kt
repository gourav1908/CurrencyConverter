package com.gourav.currencyconverter.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.gourav.currencyconverter.R
import com.gourav.currencyconverter.databinding.ActivityCurrencyBinding
import com.gourav.currencyconverter.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding1: ActivityCurrencyBinding
    lateinit var currencyAdapter: CurrencyAdapter
    var toCurrency = "JPY" //initial

    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding1 = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding1.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initRecyclerView()
        binding1.cardConvert.setOnClickListener {
            binding1.tvError.isVisible = false
            binding1.tvResult.text = null
            mainViewModel.convertCurrency(
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
                        binding1.tvResult.text = event.resultMessage
                    }
                    is MainViewModel.CurrencyEvents.Error -> {
                        binding1.progressBar.isVisible = false
                        binding1.tvError.isVisible = true
                        binding1.tvError.text = event.errorMessage
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
                //Not Implemented
            }

        })
    }
}