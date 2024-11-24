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

class RightAdapter(private val criteriaList: List<Criteria>,
                   private val alternativeList: List<Alternative>,
                   private val elementList: List<Element>,
                   private val leftPosition: Long?,
                   private val onSelectionChange: (Long?) -> Unit
): RecyclerView.Adapter<RightAdapter.RightHolder>() {
    //
    private var selectedPosition: Int = -1

    inner class RightHolder (private val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemChoiceBinding.bind(view)

        fun renderCriteria (criteria: Criteria, position: Int){
            with(binding.buttonPanel){
                text = criteria.nameCriteria
                textOn = criteria.idCriteria.toString()
                textOff = criteria.nameCriteria

                if (leftPosition == null || leftPosition == criteria.idCriteria) {
                    isEnabled = false
                    isChecked = false
                    selectedPosition = -1
                    onSelectionChange(null)
                } else {
                    //
                    isChecked = position == selectedPosition
                    setTextColor(
                        if (isChecked) resources.getColor(R.color.option_a1)
                        else resources.getColor(R.color.option_b1)
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

                            onSelectionChange(null)
                        }
                    }
                }
            }
        }

        fun renderAlternative(alternative: Alternative, position: Int){
            //
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RightHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RightHolder(layoutInflater.inflate(
            R.layout.item_choice, parent, false
        ))
    }

    override fun getItemCount(): Int {
        var size = 0
        if (criteriaList == emptyList<Criteria>()) size = alternativeList.size
        if (alternativeList == emptyList<Alternative>()) size = criteriaList.size
        return size
    }

    override fun onBindViewHolder(holder: RightHolder, position: Int) {
        if (criteriaList == emptyList<Criteria>()) holder.renderAlternative(alternativeList[position], position)
        if (alternativeList == emptyList<Alternative>()) holder.renderCriteria(criteriaList[position], position)
    }
}