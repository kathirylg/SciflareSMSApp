package com.sciflare.smsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sciflare.smsapp.databinding.MessageItemLytBinding
import com.sciflare.smsapp.model.MessageModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MessageListAdapter(private var onItemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private var data = ArrayList<MessageModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun addData(mData: ArrayList<MessageModel>) {
        data = mData
        this.notifyDataSetChanged()
    }


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            MessageItemLytBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], onItemClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(var bindingView: MessageItemLytBinding) : RecyclerView.ViewHolder(bindingView.root) {
        private var formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH)
        fun bind(data: MessageModel, onItemClickListener: (Int) -> Unit) {

            bindingView.msgTitle.text = data.title
            bindingView.msgDesc.text = data.messageBody

            if (data.messageTime.isNotEmpty()){
                val dateString: String = formatter.format(Date(data.messageTime.toLong()))
                bindingView.txtDateTime.text =dateString
            }
            bindingView.msgTitle.setOnClickListener {
                onItemClickListener(adapterPosition)

            }
        }
    }
}
