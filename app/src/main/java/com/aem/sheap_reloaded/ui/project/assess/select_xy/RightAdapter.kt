package com.aem.sheap_reloaded.ui.project.assess.select_xy

class RightAdapter {
}

/*
package com.jsus.tests.ui.notifications

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jsus.tests.R
import com.jsus.tests.databinding.ItemChoice2Binding

class RightAdapter(private val list: List<String>,
                   private val leftPosition: Int,
                   private val onSelectionChange: (Int) -> Unit
): RecyclerView.Adapter<RightAdapter.RightHolder>(){
    //
    private var selectedPosition: Int = -1

    inner class RightHolder (private val view: View): RecyclerView.ViewHolder(view){
        //
        private val binding = ItemChoice2Binding.bind(view)

        fun render (item: String, position: Int){
            with(binding.buttonPanel){
                text = "$item - $position"
                textOn = "$item - On"
                textOff = "$item - Off"

                if (leftPosition == -1 || leftPosition == position) {
                    isEnabled = false
                    isChecked = false
                    selectedPosition = -1
                    onSelectionChange(selectedPosition)
                } else {
                    //
                    isChecked = position == selectedPosition
                    setTextColor(
                        if (isChecked) Color.BLUE
                        else Color.DKGRAY
                    )

                    setOnClickListener {
                        if (selectedPosition != position){
                            val previousPosition = selectedPosition
                            selectedPosition = position

                            notifyItemChanged(previousPosition)
                            notifyItemChanged(selectedPosition)
                        } else {
                            notifyItemChanged(selectedPosition)
                            selectedPosition = -1
                        }
                        onSelectionChange(selectedPosition)
                    }
                }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RightHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RightHolder(layoutInflater.inflate(
            R.layout.item_choice_2, parent, false
        ))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RightHolder, position: Int) {
        holder.render(list[position], position)
    }
}
*/