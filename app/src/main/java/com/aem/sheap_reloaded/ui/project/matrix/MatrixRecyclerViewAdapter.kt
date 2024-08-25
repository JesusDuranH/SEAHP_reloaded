package com.aem.sheap_reloaded.ui.project.matrix

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.databinding.ItemMatrixBinding

class MatrixRecyclerViewAdapter(private val matrices: List<Matrix>)
    : RecyclerView.Adapter<MatrixRecyclerViewAdapter.MatrixHolder>() {
    //
    inner class MatrixHolder (val view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemMatrixBinding.bind(view)

        fun render (matrix: Matrix){
            with(binding){
                //
                matrixNameItem.text = matrix.nameMatrix
                matrixDescItem.text = matrix.descriptionMatrix

                root.setOnClickListener {
                    Toast.makeText(
                        root.context,
                        "Haz seleccionado a $matrix",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                matrixAssessItem.setOnClickListener {
                    Matrix().set(matrix, root.context)
                    Log.d("seahp_MatrixRecyclerViewAdapter", "set Matrix: $matrix")
                    /*Navigation.findNavController(view)
                        .navigate(R.id.action_nav_assess_perform_to_select_assess)*/
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatrixHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MatrixHolder(layoutInflater.inflate(
            R.layout.item_matrix,
            parent, false))
    }

    override fun getItemCount(): Int = matrices.size

    override fun onBindViewHolder(holder: MatrixHolder, position: Int) {
        holder.render(matrices[position])
    }
}