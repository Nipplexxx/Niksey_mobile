package com.example.niksey.ui.screens.phone_book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.niksey.R
import com.example.niksey.database.*
import com.example.niksey.models.CommonModel
import com.example.niksey.ui.screens.base_fragment.BaseFragment
import com.example.niksey.ui.screens.private_messages.SingleChatFragment
import com.example.niksey.utillits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.contacts)
        initRecycleView()
        hideKeyboard()
    }

    private fun initRecycleView() {
        mRecyclerView = view?.findViewById(R.id.contacts_recycle_view)!!
        mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)

        // Set up the adapter with the query for all users
        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefUsers, CommonModel::class.java)
            .build()

        // Adapter to handle displaying user data
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                // Inflate the contact item layout
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(holder: ContactsHolder, position: Int, model: CommonModel) {
                // Bind the user data to the holder
                holder.name.text = if (model.fullname.isEmpty()) model.username else model.fullname
                holder.status.text = model.state
                holder.photo.downloadAndSetImage(model.photoUrl)

                holder.itemView.setOnClickListener {
                    replaceFragment(SingleChatFragment(model))
                }
            }
        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_fullname)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo: CircleImageView = view.findViewById(R.id.contact_photo)
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}
