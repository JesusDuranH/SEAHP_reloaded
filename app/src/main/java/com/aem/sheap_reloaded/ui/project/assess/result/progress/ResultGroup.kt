package com.aem.sheap_reloaded.ui.project.assess.result.progress

import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Result

class ResultGroup(
    val participant: Participant,
    val results: List<Result>
){
    override fun toString(): String {
        return "\nResultGroup(${participant.user.user}, ${participant.project.nameProject}, $results)"
    }
}
