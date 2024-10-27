package com.kodiiiofc.urbanuniversity.mycontacts.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.kodiiiofc.urbanuniversity.mycontacts.domain.ContactModel
import com.kodiiiofc.urbanuniversity.mycontacts.R
import com.kodiiiofc.urbanuniversity.mycontacts.domain.Callable
import com.kodiiiofc.urbanuniversity.mycontacts.domain.Messenger

class ContactsRecyclerViewAdapter(private val contacts: List<ContactModel>) :
    RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder>() {

    private lateinit var callable: Callable
    private lateinit var messenger: Messenger

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        callable = recyclerView.context as Callable
        messenger = recyclerView.context as Messenger
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameMTV = itemView.findViewById<MaterialTextView>(R.id.name_mtv)
        val phoneMTV = itemView.findViewById<MaterialTextView>(R.id.phone_mtv)
        val callFAB = itemView.findViewById<FloatingActionButton>(R.id.call_fab)
        val smsFAB = itemView.findViewById<FloatingActionButton>(R.id.sms_fab)

        fun onBind(item: ContactModel) {
            callFAB.visibility = View.GONE
            smsFAB.visibility = View.GONE
            nameMTV.text = item.name
            phoneMTV.text = item.phone
            if (item.phone != null) {
                itemView.setOnClickListener {
                    callFAB.visibility = View.VISIBLE
                    smsFAB.visibility = View.VISIBLE
                }
                callFAB.setOnClickListener { callable.call(item.phone) }
                smsFAB.setOnClickListener { messenger.createMessage(item.phone) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(contacts[position])
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}