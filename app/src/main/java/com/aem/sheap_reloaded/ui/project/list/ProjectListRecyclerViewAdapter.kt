package com.aem.sheap_reloaded.ui.project.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.ItemProjectBinding

class ProjectListRecyclerViewAdapter (private val participants: List<Participant>,
                                      private val projects: List<Project>
): RecyclerView.Adapter<ProjectListRecyclerViewAdapter.ProjectHolder>() {
    //
    inner class ProjectHolder (val view: View):RecyclerView.ViewHolder(view){

        private val binding= ItemProjectBinding.bind(view)

        fun render(participant: Participant){
            with(binding){
                val dataProject =
                    projects.find { participant.project.idProject == it.idProject } ?: Project()

                projectNameItem.text = dataProject.nameProject
                projectDescItem.text = dataProject.descriptionProject

                projectEditItem.setOnClickListener {
                    val context = root.context
                    if (participant.type == 0){
                        Toast.makeText(context,
                            context.getString(R.string.user_power_no_access),
                            Toast.LENGTH_SHORT).show()
                    } else {
                        SEAHP().setStatus(true, context)
                        SEAHP().setProject(dataProject, context)
                        //Navigation.findNavController(view).navigate(R.id.action_nav_project_list_to_edit)
                    }
                }

                root.setOnClickListener {
                    SEAHP().setProject(dataProject, root.context)
                    //Navigation.findNavController(view).navigate(R.id.action_nav_project_list_to_assess)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProjectHolder(layoutInflater.inflate(
            R.layout.item_project, parent, false))
    }

    override fun getItemCount(): Int = participants.size

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) {
        holder.render(participants[position])
    }
}