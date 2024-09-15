package com.aem.sheap_reloaded.ui.project.assess.p2p

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.databinding.FragmentAssessP2pBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class P2PFragment: Fragment() {
    private lateinit var matrix: Matrix
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var criteriaX: Criteria
    private lateinit var criteriaY: Criteria
    private lateinit var alternativeX: Alternative
    private lateinit var alternativeY: Alternative
    private lateinit var textView: TextView

    private var _binding: FragmentAssessP2pBinding? = null
    private val binding get() = _binding!!
    private var itExist: Element? = null
    private var setValue: Double = 1.0
    private var textX = ""
    private var textY = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //
        _binding = FragmentAssessP2pBinding.inflate(inflater, container, false)

        val root: View = binding.root

        textView = binding.textAssessThingThing

        setVar()
        if (!getData()) return root
        set(textView)
        buttonOperation()
        return root
    }

    private fun setVar(){
        //
        val context = requireContext()
        user = User().get(context)
        matrix = Matrix().get(context)
        project = Project().get(context)

        criteriaX = Criteria()
        criteriaY = Criteria()
        alternativeX = Alternative()
        alternativeY = Alternative()
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            disable()
            return false
        }
        if (project == Project()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_project))
            disable()
            return false
        }
        if (matrix == Matrix()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_matrix))
            return false
        }
        return true
    }

    private fun disable(){
        //
        binding.assessButtonSaveNExit.isEnabled = false
        binding.assessButtonNext.isEnabled = false
        binding.seekBarAssess.isEnabled = false
    }

    private fun set(textView: TextView){
        //
        val context = requireContext()
        with(binding){
            //
            val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
            loadingDialog.show(childFragmentManager, "loadingDialog")
            CoroutineScope(Dispatchers.IO).launch {
                //
                when (matrix.idMatrix){
                    2L -> {
                        criteriaX = Criteria().getX(context)
                        criteriaY = Criteria().getY(context)
                        Log.d("seahp_P2PFragment", "get Criteria X: $criteriaX")
                        Log.d("seahp_P2PFragment", "get Criteria Y: $criteriaY")
                        textX = criteriaX.nameCriteria
                        textY = criteriaY.nameCriteria
                        itExist = Element().toEvaluate(matrix, project, user,
                            criteriaX.idCriteria, criteriaY.idCriteria)
                    }
                    else ->{
                        alternativeX = Alternative().getX(context)
                        alternativeY = Alternative().getY(context)
                        Log.d("seahp_P2PFragment", "get Alternative X: $alternativeX")
                        Log.d("seahp_P2PFragment", "get Alternative Y: $alternativeY")
                        textX = alternativeX.nameAlternative
                        textY = alternativeY.nameAlternative
                        itExist = Element().toEvaluate(matrix, project, user,
                            alternativeX.idAlternative, alternativeY.idAlternative)
                    }
                }
                Log.d("seahp_P2PFragment", "set search itExist: $itExist")
                withContext(Dispatchers.Main){
                    loadingDialog.dismiss()
                    setValue = if (itExist != Element()) itExist!!.scaleElement!!
                                else 1.0
                    Log.d("seahp_P2PFragment", "set setValue: $setValue")

                    textOptionA.text = textY
                    textOptionB.text = textX

                    setValue = if (setValue < 1) (-1 / setValue) else setValue
                    Log.d("seahp_P2PFragment", "set setProgess: $setValue")

                    setSeekBar(textView, textX, textY, setValue)
                    textView.text = setText(setValue.toInt(), textX, textY)
                }
            }
        }
    }

    private fun setSeekBar(textView: TextView, elementX: String, elementY: String, progress: Double){
        //
        val seekBar = binding.seekBarAssess
        val stepValues = listOf(-9, -8, -7, -6, -5, -4, -3, -2, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        seekBar.max = stepValues.size - 1
        seekBar.progress = stepValues.indexOf(progress.toInt())
        Log.d("seahp_P2PFragment", "setSeekBar progress: ${seekBar.progress}")
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val selectedStepValue = stepValues[progress]
                setValue = selectedStepValue.toDouble()
                textView.text = setText(selectedStepValue, elementX, elementY)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }
        })
    }

    private fun setText (op: Int, elementX: String, elementY: String): String{
        val context = requireContext()
        return when (op){
            -9 -> context.getString(R.string.fundamental_scale_9) + " $elementX = $setValue"
            -8 -> context.getString(R.string.fundamental_scale_8) + " $elementX = $setValue"
            -7 -> context.getString(R.string.fundamental_scale_7) + " $elementX = $setValue"
            -6 -> context.getString(R.string.fundamental_scale_6) + " $elementX = $setValue"
            -5 -> context.getString(R.string.fundamental_scale_5) + " $elementX = $setValue"
            -4 -> context.getString(R.string.fundamental_scale_4) + " $elementX = $setValue"
            -3 -> context.getString(R.string.fundamental_scale_3) + " $elementX = $setValue"
            -2 -> context.getString(R.string.fundamental_scale_2) + " $elementX = $setValue"
            1 -> context.getString(R.string.fundamental_scale_1) + " $setValue"
            2 -> context.getString(R.string.fundamental_scale_2) + " $elementY = $setValue"
            3 -> context.getString(R.string.fundamental_scale_3) + " $elementY = $setValue"
            4 -> context.getString(R.string.fundamental_scale_4) + " $elementY = $setValue"
            5 -> context.getString(R.string.fundamental_scale_5) + " $elementY = $setValue"
            6 -> context.getString(R.string.fundamental_scale_6) + " $elementY = $setValue"
            7 -> context.getString(R.string.fundamental_scale_7) + " $elementY = $setValue"
            8 -> context.getString(R.string.fundamental_scale_8) + " $elementY = $setValue"
            9 -> context.getString(R.string.fundamental_scale_9) + " $elementY = $setValue"
            else -> context.getString(R.string.error)
        }
    }

    private fun buttonOperation(){
        binding.assessButtonSaveNExit.setOnClickListener {
            if (itExist == Element()) saveValue(true)
            else updateVal(true)
        }
        binding.assessButtonNext.setOnClickListener {
            if (itExist == Element()) saveValue(false)
            else updateVal(false)
        }
    }

    fun saveValue(saveNExit: Boolean){
        //
        val name = "$textY - $textX"
        if (setValue < 0) setValue = -1 / setValue

        val mirrorName = "$textX - $textY"
        val mirrorValue = 1 / setValue

        var newElement: Element
        var mirrorElement: Element

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            //
            when (matrix.idMatrix){
                2L -> {
                    newElement = Element().create(criteriaX.idCriteria, criteriaY.idCriteria,
                        name, null, setValue, matrix, project, user, 2)
                    mirrorElement = Element().create(criteriaY.idCriteria, criteriaX.idCriteria,
                        mirrorName, null, mirrorValue, matrix, project, user, 2)
                }
                else ->{
                    newElement = Element().create(alternativeX.idAlternative, alternativeY.idAlternative,
                        name, null, setValue, matrix, project, user, 2)
                    mirrorElement = Element().create(alternativeY.idAlternative, alternativeX.idAlternative,
                        mirrorName, null, mirrorValue, matrix, project, user, 2)
                }
            }
            withContext(Dispatchers.Main){
                //
                Log.d("seahp_P2PFragment", "saveValue newElement: $newElement")
                Log.d("seahp_P2PFragment", "saveValue mirrorElement: $mirrorElement")
                loadingDialog.dismiss()
                getOut(saveNExit)
            }
        }
    }

    fun updateVal(saveNExit: Boolean){
        //
        if (itExist!!.scaleElement != setValue){
            //
            if (setValue < 0) setValue = -1 / setValue
            var updateValue: Element
            var mirrorUpdate: Element

            val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
            loadingDialog.show(childFragmentManager, "loadingDialog")
            CoroutineScope(Dispatchers.IO).launch {
                //
                updateValue = Element().updateScale(itExist!!, setValue, false)
                val mirrorValue = 1 / setValue

                mirrorUpdate = Element().updateScale(itExist!!, mirrorValue, true)

                withContext(Dispatchers.Main){
                    //
                    Log.d("seahp_P2PFragment", "updateVal updateValue: $updateValue")
                    Log.d("seahp_P2PFragment", "updateVal mirrorUpdate: $mirrorUpdate")
                    loadingDialog.dismiss()
                    getOut(saveNExit)
                }
            }
        } else {
            //
            Log.d("seahp_P2PFragment", "updateVal Nothing Update")
            getOut(saveNExit)
        }
    }

    private fun next(){
        //
        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            when (matrix.idMatrix){
                2L -> {
                    Log.d("seahp_P2PFragment", "N next list:")
                    val criteriaList = Criteria().listByProject(project)

                    val max = criteriaList.size - 1
                    Log.d("seahp_P2PFragment", "N max: $max")

                    Log.d("seahp_P2PFragment", "N criteriaX: $criteriaX")
                    val positionX = criteriaList.indexOf(criteriaX)
                    Log.d("seahp_P2PFragment", "N criteriaY: $criteriaY")
                    val positionY = criteriaList.indexOf(criteriaY)
                    Log.d("seahp_P2PFragment", "N position: ($positionX, $positionY)")

                    if (positionY != -1){
                        //
                        var nextPositionX = positionX
                        var nextPositionY = positionY

                        if (nextPositionY == max) {
                            nextPositionX++
                            nextPositionY = nextPositionX + 1
                        }
                        else nextPositionY++

                        if (criteriaList[nextPositionY] == criteriaList[nextPositionX]) nextPositionY++
                        Log.d("seahp_P2PFragment", "N next position: ($nextPositionX, $nextPositionY)")

                        if (nextPositionX == max && nextPositionY == (max + 1)) {
                            withContext(Dispatchers.Main){
                                Log.d("seahp_P2PFragment", "N End Evaluation")
                                loadingDialog.dismiss()
                                getOut(true)
                            }
                        } else {
                            Log.d("seahp_P2PFragment", "N next criteriaX: ${criteriaList[nextPositionX]}")
                            Log.d("seahp_P2PFragment", "N next criteriaY: ${criteriaList[nextPositionY]}")
                            Criteria().setY(criteriaList[nextPositionY], requireContext())
                            Criteria().setX(criteriaList[nextPositionX], requireContext())

                            withContext(Dispatchers.Main){
                                loadingDialog.dismiss()
                                set(textView)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getOut(saveNExit: Boolean){
        //
        if (saveNExit) findNavController().navigate(R.id.action_nav_project_assess_thing_thing_to_select_assess,
            null,
            NavOptions.Builder().setPopUpTo(R.id.nav_project_select_assess_x, true).build())
        else next()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}