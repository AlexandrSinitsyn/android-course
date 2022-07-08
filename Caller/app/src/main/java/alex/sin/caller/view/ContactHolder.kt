package alex.sin.caller.view

import alex.sin.caller.R
import alex.sin.caller.domain.Contact
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val contactList: List<Contact>,
    private val onNameClick: (Contact) -> Unit,
    private val onNumberClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    class ContactHolder(root: View) : RecyclerView.ViewHolder(root) {

        val name = root.findViewById<TextView>(R.id.name)
        val number = root.findViewById<TextView>(R.id.number)

        fun bind(contact: Contact) {
            name.text = contact.name
            number.text = contact.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val holder = ContactHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        )

        holder.name.setOnClickListener {
            onNameClick(contactList[holder.absoluteAdapterPosition])
        }
        holder.number.setOnClickListener {
            onNumberClick(contactList[holder.absoluteAdapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(
        holder: ContactHolder,
        position: Int
    ) = holder.bind(contactList[position])

    override fun getItemCount(): Int = contactList.size
}