package net.uji.lolplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.row_skills.view.*
import net.uji.lolplus.model.Skill

class SkillsAdapter(val context: Context,
                    val layout: Int
) : RecyclerView.Adapter<SkillsAdapter.ViewHolder>() {

    private var dataList: List<Skill> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setHabilidades(habilidades: List<Skill>) {
        this.dataList = habilidades
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Skill){
            // itemview is the layout item
            // to which to put the data of the dataItem object
            itemView.tvkey.text = dataItem.key
            itemView.tvskillname.text = dataItem.name
            itemView.tvdescription.text = dataItem.description

            val rq = Volley.newRequestQueue(context)
            val imageRequest = ImageRequest(dataItem.img,
                { response ->
                    itemView.ivskill.setImageBitmap(response)
                }, 0, 0, null, null,
                { error ->
                    // Manejar el error aquí
                }
            )
            rq.add(imageRequest)
            itemView.tag = dataItem
        }

    }
}