package com.aem.sheap_reloaded.ui.project.assess.result.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Result
import com.aem.sheap_reloaded.databinding.ItemProgressBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class ProgressAdapter(private val resultGroup: List<ResultGroup>
): RecyclerView.Adapter<ProgressAdapter.ProgressHolder>() {
    //
    inner class ProgressHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemProgressBinding.bind(view)
        fun render(resultItem: ResultGroup){
            //
            with(binding){
                val getProgress = resultItem.results.find { it.id == 0L }
                val progress = getProgress?.result ?: 0L
                val getPercent = resultItem.results.find { it.id == 1L }
                val percent = getPercent?.result ?: 0.0

                val thing = if (percent > 0) {
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.HALF_UP
                    "${df.format(percent).toDouble()}%"
                } else "Error"

                textProgressName.text = resultItem.participant.user.user
                progressIndicator.progress = progress.toInt()
                textPercent.text = thing
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProgressHolder(layoutInflater.inflate(
            R.layout.item_progress, parent, false
        ))
    }

    override fun getItemCount(): Int = resultGroup.size

    override fun onBindViewHolder(holder: ProgressHolder, position: Int) {
        holder.render(resultGroup[position])
    }
}