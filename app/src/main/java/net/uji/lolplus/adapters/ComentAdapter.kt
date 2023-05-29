package net.uji.lolplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.row_comment.view.*
import net.uji.lolplus.R
import net.uji.lolplus.model.Comment

class ComentAdapter(val context: Context,
                    val layout: Int,
                    val listener: OnLongClickListenerComent
) : RecyclerView.Adapter<ComentAdapter.ViewHolder>() {

    private var dataList: List<Comment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item,listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setComentarios(comentarios: List<Comment>) {
        this.dataList = comentarios
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Comment,listener: OnLongClickListenerComent){
            itemView.tvcomment.text = dataItem.comment
            itemView.tvcommentdate.text=dataItem.date
            itemView.tvnickcomment.text=dataItem.user.nick

            if(dataItem.user.champfav=="noone"){
                itemView.ivcomment.setImageResource(R.drawable.noone)
            }else{

                val rq = Volley.newRequestQueue(context)
                val imageRequest = ImageRequest("https://opgg-static.akamaized.net/images/lol/champion/${dataItem.user.champfav}.png?image=q_auto,w_140&v=1585730185",
                    { response ->
                        itemView.ivcomment.setImageBitmap(response)
                    }, 0, 0, null, null,
                    { error ->
                        // Manejar el error aquí
                    }
                )
                rq.add(imageRequest)
            }

            itemView.tag = dataItem
            itemView.setOnLongClickListener{
                listener.OnLongClickComentario(dataItem)
            }
        }
    }

    interface OnLongClickListenerComent{
        fun OnLongClickComentario(comentario:Comment):Boolean{
            return true
        }
    }
}