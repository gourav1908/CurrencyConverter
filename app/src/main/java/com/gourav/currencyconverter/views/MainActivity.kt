package com.gourav.currencyconverter.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.gourav.currencyconverter.R
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.databinding.ActivityCurrencyBinding
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrencyBinding
    private lateinit var currencyAdapter: CurrencyAdapter
    private var toCurrency = "INR" //initial
    private lateinit var currencyList: List<Rates>

    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initRecyclerView()
        binding.cardConvert.setOnClickListener {
            Constants.hideKeyboard(this, binding.cardConvert.rootView)
            binding.tvError.isVisible = false
            mainViewModel.convertCurrency(
                binding.etAmount.text.toString(),
                binding.spinnerFrom.selectedItem.toString(),
                toCurrency
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvents.Success -> {
                        binding.progressBar.isVisible = false
                        binding.tvConvert.isVisible = true
                        binding.tvError.isVisible = false
                        binding.recyclerTo.isVisible = true
                        currencyList = event.resultList
                        currencyAdapter.setCurrencies(currencyList)
                    }
                    is MainViewModel.CurrencyEvents.Error -> {
                        binding.progressBar.isVisible = false
                        binding.tvConvert.isVisible = true
                        binding.tvError.isVisible = true
                        binding.tvError.setTextColor(resources.getColor(R.color.dark_red))
                        binding.tvError.text = event.errorMessage
                    }
                    is MainViewModel.CurrencyEvents.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.tvConvert.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initRecyclerView() {
        currencyList = arrayListOf()
        currencyAdapter = CurrencyAdapter(currencyList)
        currencyAdapter.setCurrencies(currencyList)
        binding.recyclerTo.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            setHasFixedSize(true)
            adapter = currencyAdapter
        }
        currencyAdapter.addOnItemClickListener(object : CurrencyAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int, checkedPosition: Int) {
                /**
                 * Can get click events, Not implemented
                 */
            }

            override fun onLongItemClick(view: View?, position: Int) {
                /**
                 * Not implemented
                 */
            }

        })
    }
}