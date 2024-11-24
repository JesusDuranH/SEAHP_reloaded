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
                }
                root.setOnClickListener {
                    Toast.makeText(root.context,
                        "Haz seleccionado a $criteria",
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
        return criteriaList.size
    }

    override fun onBindViewHolder(holder: SelectXHolder, position: Int) {
        holder.renderCriteria(criteriaList[position])
    }
}