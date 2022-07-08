package alex.sin.photos.view

import alex.sin.photos.R
import alex.sin.photos.currentVisibleListSize
import alex.sin.photos.photos
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PhotoPreview(val name: String, val url: String, val id: Int)


fun getSmallString(s: String) = if (s.length >= 30) "${s.substring(0, 30)}..." else s


class PhotosAdapter(private val onClick: (PhotoPreview) -> Unit)
    : RecyclerView.Adapter<PhotosAdapter.PhotosHolder>() {

    class PhotosHolder(root: View) : RecyclerView.ViewHolder(root) {
        val name: TextView = root.findViewById(R.id.name)
        private val url: TextView = root.findViewById(R.id.url)
        val card: LinearLayout = root.findViewById(R.id.card)

        fun bind(contact: PhotoPreview) {
            name.text = contact.name
            url.text = getSmallString(contact.url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        val holder = PhotosHolder(LayoutInflater.from(parent.context).inflate(R.layout.photo_layout, parent, false))

        holder.card.setOnClickListener {
            onClick(photos[holder.absoluteAdapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) = holder.bind(photos[position])

    override fun getItemCount(): Int = currentVisibleListSize
}
