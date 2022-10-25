
package io.yoath.sports.ui

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import io.yoath.sports.R


class ListAdapter: RecyclerView.Adapter<ListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_cart, parent, false)
        return ViewHolder(convertView = view)
    }

    override fun getItemCount(): Int {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("binding hookup")
    }

    open class ViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView) {
        //-> LEFT (TO)
//        val textTitle = itemView.findViewById<TextView>(R.id.txtTitle)
//        val textDate = itemView.findViewById<TextView>(R.id.txtPublishedDate)
//        val textSource = itemView.findViewById<TextView>(R.id.txtSource)
//        val imgUrl = itemView.findViewById<ImageView>(R.id.imgUrl)
//        val btnSaveIcon = itemView.findViewById<ImageButton>(R.id.btnSaveIcon)

        fun bind(obj: Any, context: Context?) {
            return obj as Unit
        }
    }
}


