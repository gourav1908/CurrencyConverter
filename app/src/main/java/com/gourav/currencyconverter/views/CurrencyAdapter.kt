package com.gourav.currencyconverter.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gourav.currencyconverter.R
import com.gourav.currencyconverter.data.models.Rates

class CurrencyAdapter(private var currencyList: List<Rates>) :
    RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {
    private var checkedPosition = 0
    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int, checkedPosition: Int)
        fun onLongItemClick(view: View?, position: Int)
    }

    fun addOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mListener = onItemClickListener
    }

    fun setCurrencies(currencyList: List<Rates>) {
        this.currencyList = ArrayList()
        this.currencyList = currencyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCode.text = currencyList[position].currencyName
        holder.tvAmount.text = currencyList[position].amount.toString()
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvCode: TextView = itemView.findViewById(R.id.tv_code)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val lytSelected: LinearLayout = itemView.findViewById(R.id.lyt_selected)

        init {
            lytSelected.setOnClickListener { v: View ->
                onClick(
                    v
                )
            }
        }

        override fun onClick(v: View?) {
            if (mListener != null) {
                mListener!!.onItemClick(v, position, checkedPosition)
            }
        }

    }
}