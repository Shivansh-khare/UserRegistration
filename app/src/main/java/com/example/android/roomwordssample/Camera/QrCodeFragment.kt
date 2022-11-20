package com.example.android.roomwordssample.Camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.android.roomwordssample.R

class QrCodeFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private var isStarted:Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        isStarted = true
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(isStarted){
        if(isVisibleToUser){
            Log.d("MLog","QR VISIBLE")
            codeScanner.startPreview()
            codeScanner.startPreview()
            codeScanner.startPreview()
            codeScanner.startPreview()
            codeScanner.startPreview()
        }else{
            Log.d("MLog","QR NOT VISIBLE")
            codeScanner.releaseResources()
        }}
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_code, container, false)
    }

}