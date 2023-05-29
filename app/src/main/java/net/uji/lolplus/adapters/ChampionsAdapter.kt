package net.uji.lolplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.row_champ.view.*
import net.uji.lolplus.model.Champ

class ChampionsAdapter(val context: Context,
                       val layout: Int
) : RecyclerView.Adapter<ChampionsAdapter.ViewHolder>() {

    private var dataList: List<Champ> = emptyList()
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

    internal fun setCampeones(campeones: List<Champ>) {
        this.dataList = campeones
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Champ){
            // itemview is the layout item
            // to which to put the data of the dataItem object
            itemView.namerow.text = dataItem.name
            itemView.rolerow.text = dataItem.role
            if(dataItem.difficulty=="1"){
                itemView.rowdificulty.text="Low"
            }else if(dataItem.difficulty=="2"){
                itemView.rowdificulty.text="Moderate"
            }else{
                itemView.rowdificulty.text="High"
            }

            val rq = Volley.newRequestQueue(context)
            val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${dataItem.name}.png?image=q_auto,w_140&v=1585730185",
                { response ->
                    itemView.ivrowchamp.setImageBitmap(response)
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