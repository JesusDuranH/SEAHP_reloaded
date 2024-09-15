package com.aem.sheap_reloaded.ui.project.assess.select_x

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.databinding.ItemSelectBinding

class SelectXRecyclerViewAdapter (private val criteriaList: List<Criteria>,
                                    private val alternativeList: List<Alternative>,
                                    allElement: List<Element>,
                                    idMatrix: Long):
    RecyclerView.Adapter<SelectXRecyclerViewAdapter.SelectXHolder>(){
    //
    val db = allElement
    val id = idMatrix
    val maxCriteria = criteriaList.size
    val maxAlternative = alternativeList.size
    inner class SelectXHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemSelectBinding.bind(view)
        val data = Matrix().get(binding.root.context)

        fun renderCriteria(criteria: Criteria){
            with(binding){
                //
                alternativeButtonSelect.text = criteria.nameCriteria
                var count = 0
                for (item in db){
                    if (item.idMatrix == id && item.xElement == criteria.idCriteria) count++
                }
                if (data.idMatrix == 1L) {
                    if (maxAlternative > count) alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.yellow))
                    else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))
                }
                if (data.idMatrix == 2L) {
                    if (maxCriteria > count) alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.yellow))
                    else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))
                }

                alternativeButtonSelect.setOnClickListener {
                    Criteria().setX(criteria, root.context)
                    if (data.idMatrix.toInt() == 1) Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_assess_criteria_alternative)
                    else Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_select_assess_2)
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
                alternativeButtonSelect.text = alternative.nameAlternative
                var count = 0
                for (item in db){
                    if (item.idMatrix == id && item.xElement == alternative.idAlternative) count++
                }
                if (maxAlternative > count) alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.yellow))
                else alternativeButtonSelect.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green))
                alternativeButtonSelect.setOnClickListener {
                    Alternative().setX(alternative, root.context)
                    Navigation.findNavController(view).navigate(R.id.action_nav_project_select_assess_to_select_assess_2)
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

    override fun getItemCount(): Int {
        val size = if (id == 1L || id == 2L) criteriaList.size
                    else alternativeList.size
        return size
    }

    override fun onBindViewHolder(holder: SelectXHolder, position: Int) {
        if (id == 1L || id == 2L) holder.renderCriteria(criteriaList[position])
        else holder.renderAlternative(alternativeList[position])
    }
}