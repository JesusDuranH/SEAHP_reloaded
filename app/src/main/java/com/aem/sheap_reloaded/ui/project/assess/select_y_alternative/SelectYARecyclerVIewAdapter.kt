package com.aem.sheap_reloaded.ui.project.assess.select_y_alternative

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.databinding.ItemAssessAlternativeBinding

class SelectYARecyclerVIewAdapter(private val elements: List<Alternative>,
                                  private val crieriaX: Criteria,
                                  private val all: List<Element>,
): RecyclerView.Adapter<SelectYARecyclerVIewAdapter.AssessAlternativeHolder>() {
    //
    inner class AssessAlternativeHolder(val view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemAssessAlternativeBinding.bind(view)

        fun render(element: Alternative){
            with(binding){
                //
                projectItemTextCriteriaName.text = "${element.nameAlternative} (${crieriaX.idCriteria} , ${element.idAlternative})"
                projectItemTextinputLayoutValue.hint = "${crieriaX.nameCriteria} - ${element.nameAlternative} Value:"
                val text = all.find { it.yElement == element.idAlternative }?.scaleElement?.toString() ?: ""
                projectItemTextinputEdittextValue.setText(text)
                //val text = "(${position + 1}, ${rowElement.yElement})"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessAlternativeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AssessAlternativeHolder(layoutInflater.inflate(
            R.layout.item_assess_alternative,
            parent, false))
    }

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: AssessAlternativeHolder, position: Int) {
        holder.render(elements[position])
    }
}