package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.databinding.FragmentTransBinding;

public class TransFragment extends Fragment {
    private static final String TAG = "TransFragment";

    public TransFragment() {
    }

    public static TransFragment newInstance() {
        return new TransFragment();
    }

    private FragmentTransBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trans, container, false);
        mBinding.btnTran2en.setOnClickListener(v -> translate("en"));
        mBinding.btnTrans2zh.setOnClickListener(v -> translate("zh"));
        return mBinding.getRoot();
    }

    private void translate(String toLang) {
        String fromText = mBinding.getFromText();
    }
}
