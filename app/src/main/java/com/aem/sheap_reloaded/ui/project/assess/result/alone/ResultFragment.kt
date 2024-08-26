package com.aem.sheap_reloaded.ui.project.assess.result.alone

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.Maths
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentAssessResultBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var matrix: Matrix
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var userElement: MutableList<Element>
    private lateinit var configProject: SEAHP
    private lateinit var cakeIsLie: PieChart

    private var _binding: FragmentAssessResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val assessResultViewModel =
            ViewModelProvider(this)[ResultViewModel::class.java]
        _binding = FragmentAssessResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAssessResult
        assessResultViewModel.text.observe(viewLifecycleOwner){
            textView.text = it
        }

        setVar()
        if (!getData()) return root
        set()

        return root
    }

    private fun setVar(){
        //
        val context = requireContext()
        user = User().get(context)
        config = Cipher()
        matrix = Matrix().get(context)
        project = Project().get(context)
        configProject = SEAHP()

        cakeIsLie = binding.cakeIsLie
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            return false
        }
        if (project == Project()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_project))
            return false
        }
        if (matrix == Matrix()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_matrix))
            return false
        }
        return true
    }

    private fun set(){
        //
        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            //
            val isAdmin = Participant().isAdminInThis(user, project)
            val admin = (isAdmin != Participant())
            Log.d("seahp_ResultFragment", "set admin = $admin")
            userElement =  Element().getAllElementOnMatrixByUser(matrix, project, user).toMutableList()
            withContext(Dispatchers.Main){
                //
                loadingDialog.dismiss()
                graphic(Maths().consistencyRatio(userElement), cakeIsLie, "Preferencia Individual")
                cakeIsLie.visibility = View.VISIBLE
            }
        }
    }

    fun graphic(result: Result, cake: PieChart, text: String){

        val entries = listOf(
            PieEntry(40f, "A"),
            PieEntry(30f, "B"),
            PieEntry(20f, "C"),
            PieEntry(10f, "D")
        )
        // Configuración del conjunto de datos
        val dataSet = PieDataSet(result.items, text)
        dataSet.colors = generateColors(result.items.size)
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // Configuración del gráfico
        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.WHITE)

        cake.data = data
        cake.setUsePercentValues(true)
        cake.description.isEnabled = false
        cake.setExtraOffsets(5f, 10f, 5f, 5f)
        cake.dragDecelerationFrictionCoef = 0.95f
        cake.isDrawHoleEnabled = true
        cake.setHoleColor(Color.WHITE)
        cake.setTransparentCircleColor(Color.WHITE)
        cake.setTransparentCircleAlpha(110)
        cake.holeRadius = 58f
        cake.transparentCircleRadius = 61f
        cake.rotationAngle = 0f
        cake.isRotationEnabled = true
        cake.isHighlightPerTapEnabled = true
        cake.animateY(1400, Easing.EaseInOutQuad)

        cake.centerText = result.text
        cake.setCenterTextSize(20f)
        cake.setCenterTextColor(Color.BLACK)

        // Leyenda del gráfico
        val legend = cake.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.xEntrySpace = 7f
        legend.yEntrySpace = 0f
        legend.yOffset = 0f

        // Actualizar el gráfico
        cake.invalidate()
    }

    private fun generateColors(numberOfColors: Int): List<Int> {
        val colors = mutableListOf<Int>()
        for (i in 0 until numberOfColors) {
            colors.add(ColorTemplate.PASTEL_COLORS[i % ColorTemplate.PASTEL_COLORS.size])
        }
        return colors
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}