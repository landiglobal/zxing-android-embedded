package com.landiglobal.scanner.scan;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.landiglobal.scanner.demo.R;

import java.util.Arrays;
import java.util.Collection;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScanFragment extends Fragment implements BarcodeCallback {
    DecoratedBarcodeView barcodeView;

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public ScanFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed_scanning, container, false);
        rootView.findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });
        barcodeView = rootView.findViewById(R.id.barcode_view);
        barcodeView.setStatusText("");
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.CODABAR, BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(new Intent());
        barcodeView.decodeContinuous(this);

        rootView.findViewById(R.id.iv_camera_switcher).setOnClickListener((v)->{
            barcodeView.pause();
            Intent intent = new Intent();
            if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }
            intent.putExtra(Intents.Scan.CAMERA_ID,  cameraId);
            barcodeView.initializeFromIntent(intent);
            barcodeView.resume();
        });
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(barcodeView != null) {
            if (isVisibleToUser) {
                barcodeView.resume();
            } else {
                barcodeView.pauseAndWait();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    /**
     * Barcode was successfully scanned.
     *
     * @param result the result
     */
    @Override
    public void barcodeResult(BarcodeResult result) {
        if(!TextUtils.isEmpty(result.getText())){
            requireActivity().finish();
        }
    }

}

