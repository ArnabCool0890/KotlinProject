package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactfetcher.R
import model.Contact

class ContactDetailAdapter(val contactList : ArrayList<Contact>) :
    RecyclerView.Adapter<ContactDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactDetailAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.contac_list_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ContactDetailAdapter.ViewHolder, position: Int) {
       holder.bindItems(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: Contact) {
            val textViewName = itemView.findViewById(R.id.name) as TextView
            val textViewPhoneNumber = itemView.findViewById(R.id.phone_number) as TextView
            textViewName.text = user.name
            textViewPhoneNumber.text = user.number
        }
    }
}