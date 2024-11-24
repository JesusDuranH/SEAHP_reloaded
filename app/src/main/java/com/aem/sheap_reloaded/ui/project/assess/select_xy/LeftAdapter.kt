package com.aem.sheap_reloaded.ui.project.assess.select_xy

import android.content.res.Resources.Theme
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.databinding.ItemChoiceBinding

class LeftAdapter(private val criteriaList: List<Criteria>,
                  private val alternativeList: List<Alternative>,
                  allElement: List<Element>,
                  idMatrix: Long,
                  private val onSelectionChange: (Criteria) -> Unit
): RecyclerView.Adapter<LeftAdapter.LeftHolder>() {
    //
    private var selectedPosition: Int = -1

    val db = allElement
    val idDB = idMatrix
    val maxCriteria = criteriaList.size
    val maxAlternative = alternativeList.size

    inner class LeftHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemChoiceBinding.bind(view)
        val data = Matrix().get(binding.root.context)

        fun renderCriteria(criteria: Criteria, position: Int){
            //
            with(binding.buttonPanel){
                text = criteria.nameCriteria
                textOn = criteria.nameCriteria
                textOff = criteria.nameCriteria
                isChecked = (position == selectedPosition)

                var count = 0
                for (item in db){
                    if (item.idMatrix == idDB && item.xElement == criteria.idCriteria) count++
                }
                Log.d("seahp_LeftAdapter", "renderCriteria count: $count")
                Log.d("seahp_LeftAdapter", "renderCriteria maxCriteria: $maxCriteria")
                if (data.idMatrix == 1L) {
                    val progression : Double = (count.toDouble()/maxAlternative.toDouble())*100
                    Log.d("seahp_LeftAdapter", "progression Alternative: $progression")
                    binding.progressIndicator.progress = progression.toInt()
                }

                if (data.idMatrix == 2L) {
                    val progression : Double = (count.toDouble()/maxCriteria.toDouble())*100
                    Log.d("seahp_LeftAdapter", "progression Criteria: $progression")
                    binding.progressIndicator.progress = progression.toInt()
                }

                if (isChecked){
                    //
                    setTextColor(resources.getColor(R.color.option_a1))
                    setButtonDrawable(R.drawable.ic_check_box_on)
                    binding.progressIndicator.setIndicatorColor(
                        if (binding.progressIndicator.progress == 100){
                            resources.getColor(R.color.green)
                        } else {
                            resources.getColor(R.color.option_c1)
                        }
                    )
                } else {
                    //
                    setTextColor(resources.getColor(R.color.option_b1))
                    setButtonDrawable(R.drawable.ic_check_box_off)
                    binding.progressIndicator.setIndicatorColor(resources.getColor(R.color.option_a1))
                }

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
        val size = if (idDB == 1L || idDB == 2L) criteriaList.size
        else alternativeList.size
        return size
    }

    override fun onBindViewHolder(holder: LeftHolder, position: Int) {
        if (idDB == 1L || idDB == 2L) holder.renderCriteria(criteriaList[position], position)
        else holder.renderAlternative(alternativeList[position], position)
    }
}