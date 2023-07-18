package com.example.emotioneye

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class ResultDialog : DialogFragment() {
    private var okBtn: Button? = null
    private var resultTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resultdialog, container, false)
        var resultText = ""

        okBtn = view.findViewById(R.id.result_ok_button)
        resultTextView = view.findViewById(R.id.result_text_view)

        val bundle = arguments
        resultText = bundle?.getString(LCOFaceDetection.RESULT_TEXT).toString()
        resultTextView?.text = resultText

        okBtn?.setOnClickListener {
            dismiss()
        }

        return view
    }
}
