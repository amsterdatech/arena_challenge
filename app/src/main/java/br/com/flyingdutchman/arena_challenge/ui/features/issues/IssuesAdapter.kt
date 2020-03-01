package br.com.flyingdutchman.arena_challenge.ui.features.issues

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.flyingdutchman.arena_challenge.R
import br.com.flyingdutchman.arena_challenge.extensions.*
import kotlinx.android.synthetic.main.issue_list_item.view.*

class IssuesAdapter(private val action: (Issue) -> Unit? = {}) :
    RecyclerView.Adapter<IssuesAdapter.ViewHolder>() {

    private var items: MutableList<Issue> = mutableListOf()


    fun updateItems(news: List<Issue>) {
        val result = DiffUtil.calculateDiff(
            IssuesDiffUtils(
                news,
                items
            )
        )
        items.clear()
        items.addAll(news)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.issue_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = items[position]
        holder.bind(news)

        holder.itemView.custom_view_issue_content_root.setOnClickListener {
            action.invoke(news)
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Issue) {
            itemView.issue_title.text = item.title

            if (item.description.isNotEmpty()) {
                itemView.issue_description.text = item.description
            } else {
                itemView.issue_description.gone()
            }

            itemView.issue_owner_name.text = item.userName

            itemView.issue_avatar.load(item.userAvatar)
            itemView.issue_updated_elapsed_time.text = item.updatedAt.parseIsoDateFormat()
                ?.formatElapsedTime(itemView.context)
        }
    }

    class IssuesDiffUtils(
        private val freshNews: List<Issue>,
        private val oldNews: List<Issue>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldNews.size
        }

        override fun getNewListSize(): Int {
            return freshNews.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val freshItem = freshNews[newItemPosition]
            val oldItem = oldNews[oldItemPosition]
            return freshItem.id == oldItem.id
        }


        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val freshItem = freshNews[newItemPosition]
            val oldItem = oldNews[oldItemPosition]
            return freshItem == oldItem
        }
    }
}
