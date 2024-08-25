package com.aem.sheap_reloaded.ui.project.create.criteria

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.databinding.ItemCriteriaBinding

class CriteriaRecyclerViewAdapter (private val criteria: List<Criteria>):
    RecyclerView.Adapter<CriteriaRecyclerViewAdapter.CriteriaHolder>(){
        //
        inner class CriteriaHolder (val view:View): RecyclerView.ViewHolder(view){
            val binding = ItemCriteriaBinding.bind(view)

            fun render(criteria: Criteria){
                with(binding){
                    projectCriteriaAlternativeName.text = criteria.nameCriteria
                    projectCriteriaAlternativeDesc.text = criteria.descriptionCriteria
                    //checar null despues
                    projectCriteriaAlternativeSub.text =
                        if (criteria.subCriteria?.toInt() == 0) ""
                        else {
                            val sub = criteria.subCriteria?.let { Criteria().getByID(it, criteria.idProject) }
                            Log.d("seahp_CriteriaRecyclerViewAdapter", "render: $criteria SubCriteria: $sub")
                            sub?.let { sub.nameCriteria }
                        }
                    criteriaEditItem.setOnClickListener {
                        //Falta Pantalla de Edicion/Eliminar
                    }

                    root.setOnClickListener {
                        Toast.makeText(root.context,
                            "Haz seleccionado a ${criteria.idCriteria}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriteriaHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CriteriaHolder(layoutInflater.inflate(
            R.layout.item_criteria, parent, false))
    }

    override fun getItemCount(): Int = criteria.size

    override fun onBindViewHolder(holder: CriteriaHolder, position: Int) {
        holder.render(criteria[position])
    }
}