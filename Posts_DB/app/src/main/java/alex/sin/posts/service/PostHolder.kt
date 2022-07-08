package alex.sin.posts.service

import alex.sin.posts.R
import alex.sin.posts.domain.Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val postList: MutableList<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(root: View) : RecyclerView.ViewHolder(root) {

        val title = root.findViewById<TextView>(R.id.postTitle)
        val body = root.findViewById<TextView>(R.id.postBody)
        val deleteButton = root.findViewById<TextView>(R.id.postDelete)

        fun bind(contact: Post) {
            title.text = contact.title
            body.text = contact.body
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val holder = PostHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        )

        holder.deleteButton.setOnClickListener {
            onClick(postList[holder.absoluteAdapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(
        holder: PostHolder,
        position: Int
    ) = holder.bind(postList[position])

    override fun getItemCount(): Int = postList.size


    fun insertItem(item: Post, position: Int) {
        postList.add(position, item)
        notifyItemInserted(position)
    }

    fun eraseItem(position: Int) {
        postList.removeAt(position)
        notifyItemRemoved(position)
    }
}
