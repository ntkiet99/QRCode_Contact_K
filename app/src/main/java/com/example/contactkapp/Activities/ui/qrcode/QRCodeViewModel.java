package com.example.contactkapp.Activities.ui.qrcode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRCodeViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public QRCodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("QR Code nè!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}