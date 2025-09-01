package com.example.stegochat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FriendSelectionDialog : BottomSheetDialogFragment() {
    
    private var friends: List<Friend> = emptyList()
    private var onFriendSelectedListener: ((Friend) -> Unit)? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_friend_selection, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.friendsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = FriendAdapter(friends) { friend ->
            onFriendSelectedListener?.invoke(friend)
            dismiss()
        }
    }
    
    fun setFriends(friendsList: List<Friend>) {
        friends = friendsList
    }
    
    fun setOnFriendSelectedListener(listener: (Friend) -> Unit) {
        onFriendSelectedListener = listener
    }
    
    companion object {
        fun newInstance(friends: List<Friend>, onFriendSelected: (Friend) -> Unit): FriendSelectionDialog {
            return FriendSelectionDialog().apply {
                setFriends(friends)
                setOnFriendSelectedListener(onFriendSelected)
            }
        }
    }
    
    private inner class FriendAdapter(
        private val friends: List<Friend>,
        private val onFriendClick: (Friend) -> Unit
    ) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_friend_selection, parent, false)
            return FriendViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
            holder.bind(friends[position])
        }
        
        override fun getItemCount(): Int = friends.size
        
        inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val friendName: TextView = itemView.findViewById(R.id.friendName)
            private val friendStatus: TextView = itemView.findViewById(R.id.friendStatus)
            private val friendContainer: LinearLayout = itemView.findViewById(R.id.friendContainer)
            
            fun bind(friend: Friend) {
                friendName.text = friend.name
                friendStatus.text = if (friend.isOnline) "Online" else "Offline"
                friendStatus.setTextColor(
                    if (friend.isOnline) 
                        itemView.context.getColor(android.R.color.holo_green_dark)
                    else 
                        itemView.context.getColor(android.R.color.darker_gray)
                )
                
                friendContainer.setOnClickListener {
                    onFriendClick(friend)
                }
            }
        }
    }
} 