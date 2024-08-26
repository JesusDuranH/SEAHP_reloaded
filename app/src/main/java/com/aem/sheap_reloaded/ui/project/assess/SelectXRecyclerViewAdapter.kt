package com.aem.sheap_reloaded.ui.project.assess

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.databinding.ItemSelectBinding

class SelectXRecyclerViewAdapter (private val criteriaList: List<Criteria>,
                                    private val alternativeList: List<Alternative>):
    RecyclerView.Adapter<SelectXRecyclerViewAdapter.SelectXHolder>(){
    //
    inner class SelectXHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemSelectBinding.bind(view)
        val data = Matrix().get(binding.root.context)

        fun renderCriteria(criteria: Criteria){
            with(binding){
                //
                alternativeButtonSelect.text = "${criteria.nameCriteria} (${criteria.idCriteria})"
                /*if (nums[element.yElement-1][1] < element.columnMax) alternativeButtonSelect.setBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.yellow))
                else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))*/
                alternativeButtonSelect.setOnClickListener {
                    Criteria().setX(criteria, root.context)
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
                    //Criteria().setX(criteria, root.context)
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
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectXHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SelectXHolder(layoutInflater.inflate(
            R.layout.item_select,
            parent, false))
    }

    override fun getItemCount(): Int{
        var size = 0
        if (criteriaList == emptyList<Criteria>()) size = alternativeList.size
        if (alternativeList == emptyList<Alternative>()) size = criteriaList.size
        return size
    }

    override fun onBindViewHolder(holder: SelectXHolder, position: Int) {
        if (criteriaList == emptyList<Criteria>()) holder.renderAlternative(alternativeList[position])
        if (alternativeList == emptyList<Alternative>()) holder.renderCriteria(criteriaList[position])
    }
}