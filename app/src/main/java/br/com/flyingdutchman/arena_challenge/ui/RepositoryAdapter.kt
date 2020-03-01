package br.com.flyingdutchman.arena_challenge.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.flyingdutchman.arena_challenge.R
import br.com.flyingdutchman.arena_challenge.extensions.load
import kotlinx.android.synthetic.main.repo_list_item.view.*

class RepositoryAdapter(private val action: (Repository) -> Unit? = {}) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {
    private var items: MutableList<Repository> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.repo_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = items[position]
        holder.bind(repository)

        holder.itemView.custom_view_repo_content_root.setOnClickListener {
            action.invoke(repository)
        }
    }

    override fun getItemCount(): Int = items.size


    fun updateItems(news: List<Repository>) {
        val result = DiffUtil.calculateDiff(
            RepoDiffUtils(
                news,
                items
            )
        )
        items.clear()
        items.addAll(news)
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Repository) {
            itemView.repo_name.text = item.repoName
            itemView.repo_description.text = item.repoDescription
            itemView.repo_stars_count.text = item.starsCount.toString()
            itemView.repo_pr_count.text = item.forksCount.toString()
            itemView.repo_user_avatar.load(item.ownerAvatar)

        }
    }


    class RepoDiffUtils(
        private val freshNews: List<Repository>,
        private val oldNews: List<Repository>
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