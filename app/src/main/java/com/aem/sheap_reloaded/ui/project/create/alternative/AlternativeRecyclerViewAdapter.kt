package com.aem.sheap_reloaded.ui.project.create.alternative

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.databinding.ItemAlternativeBinding

class AlternativeRecyclerViewAdapter(private val alternative: List<Alternative>):
    RecyclerView.Adapter<AlternativeRecyclerViewAdapter.AlternativeHolder>(){
        //
        inner class AlternativeHolder (val view: View): RecyclerView.ViewHolder(view){
            val binding = ItemAlternativeBinding.bind(view)

            fun render (alternative: Alternative){
                //
                with(binding){
                    projectAlternativeName.text = alternative.nameAlternative
                    projectAlternativeDesc.text = alternative.descriptionAlternative
                    alternativeEditItem.setOnClickListener {
                        //Falta pantalla de edicion/eliminar
                    }
                    root.setOnClickListener {
                        Toast.makeText(root.context,
                            "Haz seleccionado a ${alternative.idAlternative}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlternativeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AlternativeHolder((layoutInflater.inflate(
            R.layout.item_alternative, parent, false)))
    }

    override fun getItemCount(): Int = alternative.size

    override fun onBindViewHolder(holder: AlternativeHolder, position: Int) {
        holder.render(alternative[position])
    }
}

/*


class ProjectAlternativeRecyclerViewAdapter(private val alternative: List<Alternative>):
    RecyclerView.Adapter<ProjectAlternativeRecyclerViewAdapter.AlternativeHolder>(){


}
* */