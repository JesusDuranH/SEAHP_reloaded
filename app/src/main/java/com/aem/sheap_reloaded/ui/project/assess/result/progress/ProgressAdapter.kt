package com.aem.sheap_reloaded.ui.project.assess.result.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Result
import com.aem.sheap_reloaded.databinding.ItemProgressBinding

class ProgressAdapter(private val resultList: List<Result>
): RecyclerView.Adapter<ProgressAdapter.ProgressHolder>() {
    //
    inner class ProgressHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemProgressBinding.bind(view)
        fun render(result: Result){
            //
            with(binding){
                textProgressName.text = result.participant.user.user
                progressIndicator.progress = result.result.toInt()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProgressHolder(layoutInflater.inflate(
            R.layout.item_progress, parent, false
        ))
    }

    override fun getItemCount(): Int = resultList.size

    override fun onBindViewHolder(holder: ProgressHolder, position: Int) {
        holder.render(resultList[position])
    }
}