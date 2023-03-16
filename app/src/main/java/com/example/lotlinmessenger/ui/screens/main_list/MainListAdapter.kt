package com.example.lotlinmessenger.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.models.CommonModel
import com.example.lotlinmessenger.ui.screens.groups.GroupChatFragment
import com.example.lotlinmessenger.ui.screens.single_chat.SingleChatFragment
import com.example.lotlinmessenger.utillits.TYPE_CHAT
import com.example.lotlinmessenger.utillits.TYPE_GROUP
import com.example.lotlinmessenger.utillits.downloadAndSetImage
import com.example.lotlinmessenger.utillits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView


class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView? = view.findViewById<TextView>(R.id.main_list_item_name)
        val itemLastMessage: TextView = view.findViewById<TextView>(R.id.main_list_last_message)
        val itemPhoto: CircleImageView = view.findViewById<CircleImageView>(R.id.main_list_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)

        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            when(listItems[holder.adapterPosition].type){
                TYPE_CHAT ->replaceFragment(SingleChatFragment(listItems[holder.adapterPosition]))
                TYPE_GROUP -> replaceFragment(GroupChatFragment(listItems[holder.adapterPosition]))
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName?.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item:CommonModel){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}