package com.aem.sheap_reloaded.ui.project.create.participant

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.ItemParticipantBinding

class ParticipantRecyclerViewAdapter (private val list: List<User>,
                                      private val listParticipant: List<Participant>,
                                      val listener: OnChangeParticipantListener
): RecyclerView.Adapter<ParticipantRecyclerViewAdapter.CreateUsersHolder>() {
    //
    inner class CreateUsersHolder (val view: View): RecyclerView.ViewHolder(view){
        //
        val binding = ItemParticipantBinding.bind(view)
        val configProject = SEAHP()

        fun render(user: User){
            with(binding){
                projectUserUserSelectItem.text = user.user
                projectNameUserSelectItem.text = user.name
                val ty = checkStatus(user)
                val context = root.context
                if (ty != null) {
                    projectTypeUserSelectItem.text = when (ty){
                        0 -> context.getString(R.string.user_power_0)
                        1 -> context.getString(R.string.user_power_1)
                        2 -> context.getString(R.string.user_power_2)
                        else -> context.getString(R.string.error)
                    }
                } else projectTypeUserSelectItem.text = context.getString(R.string.user_power_na)

                projectSelectType0ButtonUserItem.setOnClickListener {
                    listener.onDataChanged(ty, 0, user, listParticipant[0].project, false)
                }

                projectSelectType1ButtonUserItem.setOnClickListener {
                    listener.onDataChanged(ty, 1, user, listParticipant[0].project, false)
                }

                projectSelectType2ButtonUserItem.setOnClickListener{
                    listener.onDataChanged(ty, 2, user, listParticipant[0].project, false)
                }

                projectSelectTypeDelButtonUserItem.setOnClickListener {
                    listener.onDataChanged(ty, 3, user, listParticipant[0].project, true)
                }

                root.setOnClickListener {
                    Toast.makeText(root.context,
                        "Haz seleccionado a ${user.user}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun checkStatus(user: User): Int? {
            //
            return listParticipant.firstOrNull { it.user.user == user.user}?.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateUsersHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CreateUsersHolder(layoutInflater.inflate(R.layout.item_participant,
            parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CreateUsersHolder, position: Int) {
        holder.render(list[position])
    }
}