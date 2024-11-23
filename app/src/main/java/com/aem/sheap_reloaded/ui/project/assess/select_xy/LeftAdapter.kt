package com.aem.sheap_reloaded.ui.project.assess.select_xy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.databinding.ItemChoiceBinding

class LeftAdapter(private val criteriaList: List<Criteria>,
                  private val alternativeList: List<Alternative>,
                  allElement: List<Element>,
                  idMatrix: Long,
                  private val onSelectionChange: (Long) -> Unit
): RecyclerView.Adapter<LeftAdapter.LeftHolder>() {
    //
    private var selectedPosition: Int = -1

    val db = allElement
    val id = idMatrix
    val maxCriteria = criteriaList.size
    val maxAlternative = alternativeList.size

    inner class LeftHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemChoiceBinding.bind(view)

        fun renderCriteria(criteria: Criteria, position: Int){
            //
            with(binding.buttonPanel){
                text = criteria.nameCriteria
                textOn = criteria.idCriteria.toString()
                textOff = criteria.nameCriteria

                isChecked = position == selectedPosition
                setTextColor(
                    if (isChecked) R.color.option_a1.dec()
                    else Color.LTGRAY
                )

                setOnClickListener {
                    if (selectedPosition != position){
                        val previousPosition = selectedPosition
                        selectedPosition = position

                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)

                        onSelectionChange(criteria.idCriteria)
                    } else {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = -1

                        onSelectionChange(selectedPosition.toLong())
                    }
                }
            }
        }

        fun renderAlternative(alternative: Alternative, position: Int){
            //
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeftHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LeftHolder(layoutInflater.inflate(
            R.layout.item_choice, parent, false
        ))
    }

    override fun getItemCount(): Int {
        val size = if (id == 1L || id == 2L) criteriaList.size
        else alternativeList.size
        return size
    }

    override fun onBindViewHolder(holder: LeftHolder, position: Int) {
        if (id == 1L || id == 2L) holder.renderCriteria(criteriaList[position], position)
        else holder.renderAlternative(alternativeList[position], position)
    }
}

/*
class LeftAdapter(private val list: List<String>,
                    private val onSelectionChange: (Int) -> Unit
    ): RecyclerView.Adapter<LeftAdapter.LeftHolder>(){
    //
    private var selectedPosition: Int = -1

    inner class LeftHolder (val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemChoice2Binding.bind(view)

        fun render (item: String, position: Int){
            with(binding.buttonPanel){
                text = "$item - $position"
                textOn = "$item - On"
                textOff = "$item - Off"

                isChecked = position == selectedPosition
                setTextColor(
                    if (isChecked) Color.BLUE
                    else Color.LTGRAY
                )

                setOnClickListener {
                    if (selectedPosition != position){
                        val previousPosition = selectedPosition
                        selectedPosition = position

                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)
                    } else {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = -1
                    }
                    onSelectionChange(selectedPosition)
                }
            }
            //
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeftHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LeftHolder(layoutInflater.inflate(
            R.layout.item_choice_2, parent, false
        ))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: LeftHolder, position: Int) {
        holder.render(list[position], position)
    }
}
*/