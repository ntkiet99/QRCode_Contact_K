package com.example.contactkapp.Activities.ui.qrcode;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;


import com.example.contactkapp.Activities.AccountActivity;
import com.example.contactkapp.Activities.ContactDetailActivity;

import com.example.contactkapp.Activities.Update_Item_Activity;
import com.example.contactkapp.Activities.ui.settings.SettingsViewModel;
import com.example.contactkapp.R;
import com.google.common.collect.BiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class QRCodeFragment extends Fragment {

    private static final int PREQCODE = 3;
    private QRCodeViewModel qrCodeViewModel;
    private Button btnScan;
    private ImageView imvQrCode;
    private CardView qr_code_img_cardview;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;

    String QRCode = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        qrCodeViewModel =
                new ViewModelProvider(this).get(QRCodeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_q_r_code, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnScan = root.findViewById(R.id.btn_open_camera);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(QRCodeFragment.this);
                intentIntegrator.setPrompt("Bật Flash bằng nút tăng âm lượng, và ngược lại!");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
            }
        });

        imvQrCode = (ImageView) root.findViewById(R.id.img_qrcode);

        LoadUserItem();

        qr_code_img_cardview = root.findViewById(R.id.qr_code_img_cardview);
        qr_code_img_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });
        return root;
    }

    private void SaveIamge(Bitmap finalBitmap, String name, String id) {
        try {
            imvQrCode.setDrawingCacheEnabled(true);
            Bitmap b = imvQrCode.getDrawingCache();
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, name, id);
            Toast.makeText(getContext(), "Lưu mã QR thành công", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getContext(), "Lỗi! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Hãy cho phép truy cập bộ nhớ!", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PREQCODE);
            }
        } else {
            try {
                Bitmap bmp = ((BitmapDrawable) imvQrCode.getDrawable()).getBitmap();
                SaveIamge(bmp, currentUser.getEmail(), currentUser.getUid());
            } catch (Exception e) {
            }
        }
    }

    private void LoadUserItem() {
        DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
        Query updateUserItemQuery = userItem.orderByChild("userName").equalTo(currentUser.getEmail());
        updateUserItemQuery.keepSynced(false);
        updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot broccoli : snapshot.getChildren()) {
                    QRCode = broccoli.child("qrcode").getValue(String.class);
                    try {
                        Bitmap bitmap = textToImage(QRCode, 500, 500);
                        if (bitmap != null) {
                            imvQrCode.setImageBitmap(bitmap);
                        }
                    } catch (WriterException e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference userItem = FirebaseDatabase.getInstance().getReference().child("UserItem");
                    Query updateUserItemQuery = userItem.orderByChild("qrcode").equalTo(result.getContents());
                    updateUserItemQuery.keepSynced(false);
                    updateUserItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Intent intent = new Intent(getContext(), ContactDetailActivity.class);
                                intent.putExtra("data", result.getContents());
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Không tìm thấy thông tin!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }
    }

    private Bitmap textToImage(String text, int width, int height) throws WriterException, NullPointerException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.DATA_MATRIX.QR_CODE,
                    width, height, null);
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        int colorWhite = 0xFFFFFFFF;
        int colorBlack = 0xFF000000;

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? colorBlack : colorWhite;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}