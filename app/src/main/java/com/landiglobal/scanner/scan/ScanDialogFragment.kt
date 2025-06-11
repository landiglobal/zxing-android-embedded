package com.landiglobal.scanner.scan

import android.app.Dialog
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.landiglobal.scanner.demo.R
import java.util.Arrays

/**
 * A placeholder fragment containing a simple view.
 */
class ScanDialogFragment : DialogFragment(), BarcodeCallback {
    var barcodeView: DecoratedBarcodeView? = null
    private var cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_tabbed_scanning, container, false)
        rootView.findViewById<View?>(R.id.button_back).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
            }
        })
        barcodeView = rootView.findViewById<DecoratedBarcodeView?>(R.id.barcode_view)
        barcodeView!!.setStatusText("")
        val formats: MutableCollection<BarcodeFormat?> =
            Arrays.asList<BarcodeFormat?>(BarcodeFormat.CODABAR, BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView!!.getBarcodeView().setDecoderFactory(DefaultDecoderFactory(formats))
        barcodeView!!.initializeFromIntent(Intent().apply {
            putExtra(Intents.Scan.CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_BACK)
        })
        barcodeView!!.decodeContinuous(this)
        rootView.findViewById<View?>(R.id.iv_camera_switcher).setOnClickListener {
            barcodeView!!.pause()
            val intent = Intent()
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
            }
            intent.putExtra(Intents.Scan.CAMERA_ID, cameraId)
            barcodeView!!.initializeFromIntent(intent)
            barcodeView!!.resume()
        }
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        return dialog
    }

    override fun onStart() {
        super.onStart()

        getDialog()!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (barcodeView != null) {
            if (isVisibleToUser) {
                barcodeView!!.resume()
            } else {
                barcodeView!!.pauseAndWait()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView!!.pauseAndWait()
    }

    override fun onResume() {
        super.onResume()
        barcodeView!!.resume()
    }

    /**
     * Barcode was successfully scanned.
     *
     * @param result the result
     */
    override fun barcodeResult(result: BarcodeResult) {
        if (!TextUtils.isEmpty(result.getText())) {
            dismiss()
        }
    }

    companion object {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): ScanDialogFragment {
            return ScanDialogFragment()
        }
    }
}

