package com.dev.base.mvp.view.widget;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.dev.base.R;

/**
 * author:  ljy
 * date:    2018/5/2
 * description:
 */

public class PhotoDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View contentView = inflater.inflate(R.layout.layout_popupwindow, container, false);
        Button mBtnCamera = (Button) contentView.findViewById(R.id.btn_item_camera);
        Button mBtnAlbum = (Button) contentView.findViewById(R.id.btn_item_album);
        mBtnCamera.setOnClickListener((View.OnClickListener) getActivity());
        mBtnAlbum.setOnClickListener((View.OnClickListener) getActivity());
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(true);

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.windowAnimations = R.style.PopupAnimation;//弹入弹出动画
        window.setAttributes(params);
        params.dimAmount = 0.4f;//背景透明度
        window.setBackgroundDrawable(null);//设置为无边框

    }

}
