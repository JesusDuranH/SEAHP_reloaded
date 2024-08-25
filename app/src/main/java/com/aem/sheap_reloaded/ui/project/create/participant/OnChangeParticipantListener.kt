package com.aem.sheap_reloaded.ui.project.create.participant

import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User

interface OnChangeParticipantListener {
    fun onDataChanged(ty: Int?, type: Int, user: User, project: Project, delete: Boolean)
}