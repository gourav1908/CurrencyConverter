package com.gourav.currencyconverter.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gourav.currencyconverter.R
import com.gourav.currencyconverter.data.models.Rates

class CurrencyAdapter(val context: Context, var currencyList: List<String>) :
    RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {
    private var checkedPosition = 0
    var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int, checkedPosition: Int)
        fun onLongItemClick(view: View?, position: Int)
    }

    fun SetOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mListener = onItemClickListener
    }

    fun setCurrencies(currencyList: List<String>) {
        this.currencyList = ArrayList()
        this.currencyList = currencyList
        notifyDataSetChanged()
    }

    fun changePosition(checkedPosition: Int) {
        this.checkedPosition = checkedPosition
        notifyItemChanged(checkedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_code.text = currencyList[position]
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tv_code: TextView = itemView.findViewById(R.id.tv_code)
        val card_root: CardView = itemView.findViewById(R.id.card_root)
        val lyt_selected: LinearLayout = itemView.findViewById(R.id.lyt_selected)

        init {
            lyt_selected.setOnClickListener { v: View ->
                onClick(
                    v
                )
            }
        }

        fun bind(position: Int) {
            if (checkedPosition == -1) {
                lyt_selected.setBackgroundColor(context.resources.getColor(R.color.white))
                tv_code.setTextColor(context.resources.getColor(R.color.black))
            } else {
                if (checkedPosition == adapterPosition) {
                    lyt_selected.setBackgroundColor(context.resources.getColor(R.color.purple_700))
                    tv_code.setTextColor(context.resources.getColor(R.color.white))
                } else {
                    lyt_selected.setBackgroundColor(context.resources.getColor(R.color.white))
                    tv_code.setTextColor(context.resources.getColor(R.color.black))
                }
            }
        }

        override fun onClick(v: View?) {
            if (mListener != null) {
                mListener!!.onItemClick(v, position, checkedPosition)
            }
        }

    }
}