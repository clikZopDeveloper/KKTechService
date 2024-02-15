package com.example.kk_services.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kk_services.Model.AllComplaintsBean
import com.example.kk_services.R
import com.example.kk_services.Model.AllContactListBean
import com.example.kk_services.Model.AlocateRequestBean
import com.example.kk_services.Utills.RvAlcateStatusClickListner

import com.example.kk_services.Utills.RvStatusClickListner
import com.example.kk_services.Utills.RvStatusComplClickListner


class AllAlocateReqAdapter(
    var context: Activity,
    var list: List<AlocateRequestBean.Data>,
    var rvClickListner: RvStatusComplClickListner
) : RecyclerView.Adapter<AllAlocateReqAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_alcate_req, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        /*     holder.tvAdd.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(20f))
             holder.tvQtyAdd.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(100f))
             holder.tvQtyMinus.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(100f))
             holder.tvQty.background = RoundView(Color.TRANSPARENT, RoundView.getRadius(20f), true, R.color.orange)
             holder.tvOff.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(20f))
             holder.tvAdd.visibility = View.VISIBLE*/


        holder.tvName.text = list[position].customerName
        holder.tvMobile.text = list[position].mobile

        holder.tvServiceType.text = list[position].serviceType.toString()
        holder.tvDate.text = list[position].createdDate.toString()
        holder.tvID.text = list[position].id.toString()

        // holder.ivImage.setImageDrawable(context.resources.getDrawable(list[position].drawableId))
        if (list[position].status.equals("accepted") || list[position].status.equals("rejected")) {
            holder.tvBtn.visibility = View.GONE
        } else {
            holder.tvBtn.visibility = View.VISIBLE
        }

        holder.tvAccpt.setOnClickListener {
            rvClickListner.clickPos("accepted","accepted",list[position].id)
        }
        holder.tvReject.setOnClickListener {
            rvClickListner.clickPos("rejected","rejected",list[position].id)
        }
        /*holder.itemView.setOnClickListener {
            rvClickListner.clickPos("",list[position].id)
        }*/
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvID: TextView = itemview.findViewById(R.id.tvID)
        val tvName: TextView = itemview.findViewById(R.id.tvName)
        val tvMobile: TextView = itemview.findViewById(R.id.tvMobile)

        val tvServiceType: TextView = itemview.findViewById(R.id.tvServiceType)
        val tvDate: TextView = itemview.findViewById(R.id.tvDate)
        val tvAccpt: TextView = itemview.findViewById(R.id.tvAccpt)
        val tvReject: TextView = itemview.findViewById(R.id.tvReject)
        val tvBtn: LinearLayout = itemview.findViewById(R.id.tvBtn)

    }

}