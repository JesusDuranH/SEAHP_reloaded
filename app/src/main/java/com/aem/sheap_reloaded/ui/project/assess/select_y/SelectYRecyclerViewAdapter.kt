package com.aem.sheap_reloaded.ui.project.assess.select_y

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.databinding.ItemSelectBinding

class SelectYRecyclerViewAdapter (private val criteriaList: List<Criteria>,
                                  private val alternativeList: List<Alternative>,
                                  //private val allElement: List<Element>
): RecyclerView.Adapter<SelectYRecyclerViewAdapter.SelectYHolder>() {
    //
    inner class SelectYHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemSelectBinding.bind(view)

        fun renderCriteria(criteria: Criteria){
            with(binding){
                //
                alternativeButtonSelect.text = "${criteria.nameCriteria} (${criteria.idCriteria})"
                /*if (nums[element.yElement-1][1] < element.columnMax) alternativeButtonSelect.setBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.yellow))
                else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))*/
                alternativeButtonSelect.setOnClickListener {
                    Criteria().setY(criteria, root.context)
                    //if (data.idMatrix.toInt() == 1) Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_assess_criteria_alternative)
                    //else Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_select_assess_2)
                }
                root.setOnClickListener {
                    Toast.makeText(root.context,
                        "Haz seleccionado a $criteria",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Sin Terminar
        fun renderAlternative(alternative: Alternative){
            with(binding){
                //
                alternativeButtonSelect.text = "${alternative.nameAlternative} (${alternative.nameAlternative})"
                /*if (nums[element.yElement-1][1] < element.columnMax) alternativeButtonSelect.setBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.yellow))
                else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))*/
                alternativeButtonSelect.setOnClickListener {
                    Alternative().setY(alternative, root.context)
                    //if (data.idMatrix.toInt() == 1) Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_assess_criteria_alternative)
                    //else Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_select_assess_2)
                }
                root.setOnClickListener {
                    Toast.makeText(root.context,
                        "Haz seleccionado a $alternative",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        /*fun render(elementB: Element, position: Int){
            with(binding){
                //
                val elementA = ConfigProject().getElement(root.context)

                val scale = allElement.find { it.xElement == elementB.xElement && it.yElement == elementA.yElement && it.idMatrix == elementB.idMatrix}
                if (scale != null){
                    if (elementB.xElement != elementA.yElement) alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))
                } else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.yellow))

                alternativeButtonSelect.text = "${elementB.nameElement} (${elementB.xElement}, ${elementA.yElement})"

                if ((position + 1) == elementA.yElement) alternativeButtonSelect.isEnabled = false

                alternativeButtonSelect.setOnClickListener {
                    //
                    ConfigProject().setElementB(elementB, root.context)
                    Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_2_to_assess_thing_thing)
                }

                root.setOnClickListener {
                    Toast.makeText(root.context,
                        "Haz seleccionado a $elementB",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }*/
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectYHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SelectYHolder(layoutInflater.inflate(
            R.layout.item_select,
            parent, false))
    }

    override fun getItemCount(): Int {
        var size = 0
        if (criteriaList == emptyList<Criteria>()) size = alternativeList.size
        if (alternativeList == emptyList<Alternative>()) size = criteriaList.size
        return size
    }

    override fun onBindViewHolder(holder: SelectYHolder, position: Int) {
        if (criteriaList == emptyList<Criteria>()) holder.renderAlternative(alternativeList[position])
        if (alternativeList == emptyList<Alternative>()) holder.renderCriteria(criteriaList[position])
    }
}