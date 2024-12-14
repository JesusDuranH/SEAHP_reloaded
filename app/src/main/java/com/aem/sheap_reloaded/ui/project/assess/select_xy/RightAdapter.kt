package com.aem.sheap_reloaded.ui.project.assess.select_xy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.databinding.ItemChoiceBinding

class RightAdapter(private val criteriaList: List<Criteria>,
                   private val alternativeList: List<Alternative>,
                   private val elementList: List<Element>,
                   private val leftPosition: Criteria,
                   private val onSelectionChange: (Criteria) -> Unit
): RecyclerView.Adapter<RightAdapter.RightHolder>() {
    //
    private var selectedPosition: Int = -1

    inner class RightHolder (private val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemChoiceBinding.bind(view)

        fun renderCriteria (criteria: Criteria, position: Int){
            with(binding.buttonPanel){
                text = criteria.nameCriteria
                textOn = criteria.nameCriteria
                textOff = criteria.nameCriteria
                binding.progressIndicator.progress = 100

                if (leftPosition == Criteria() || leftPosition == criteria) {
                    isEnabled = false
                    isChecked = false
                    selectedPosition = -1
                    onSelectionChange(Criteria())
                    binding.progressIndicator.setIndicatorColor(resources.getColor(R.color.option_b6))
                } else {
                    //
                    isChecked = position == selectedPosition

                    if (isChecked){
                        //
                        setTextColor(resources.getColor(R.color.option_a1))
                        setButtonDrawable(R.drawable.ic_check_box_on)
                    } else {
                        //
                        setTextColor(resources.getColor(R.color.option_b1))
                        setButtonDrawable(R.drawable.ic_check_box_off)
                    }

                    val check = elementList.find { it.xElement == leftPosition.idCriteria && it.yElement == criteria.idCriteria && it.scaleElement != 0.0 && it.scaleElement != null }
                    if (check == null) binding.progressIndicator.setIndicatorColor(resources.getColor(R.color.option_c1))
                    else binding.progressIndicator.setIndicatorColor(resources.getColor(R.color.green))

                    setOnClickListener {
                        if (selectedPosition != position){
                            val previousPosition = selectedPosition
                            selectedPosition = position

                            notifyItemChanged(previousPosition)
                            notifyItemChanged(selectedPosition)

                            onSelectionChange(criteria)
                        } else {
                            notifyItemChanged(selectedPosition)
                            selectedPosition = -1

                            onSelectionChange(Criteria())
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