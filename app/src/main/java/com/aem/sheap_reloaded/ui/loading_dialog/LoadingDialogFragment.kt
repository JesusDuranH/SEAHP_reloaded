package com.aem.sheap_reloaded.ui.loading_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aem.sheap_reloaded.R

class LoadingDialogFragment: DialogFragment() {
    //
    companion object {
        private const val ARG_MESSAGE = "arg_message"

        fun newInstance(message: String): LoadingDialogFragment {
            val fragment = LoadingDialogFragment()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.loading_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message = arguments?.getString(ARG_MESSAGE)
        val loadingTextView = view.findViewById<TextView>(R.id.loading_text)
        loadingTextView.text = message
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false  // Prevent the dialog from being dismissed by pressing the back button
    }
}