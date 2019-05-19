package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import cn.edu.cczu.iot161g2.ccdict.R;
import cn.edu.cczu.iot161g2.ccdict.data.TranslationRepository;
import cn.edu.cczu.iot161g2.ccdict.databinding.FragmentTransBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        mBinding.setToText("");
        mBinding.setFromText("");
        mBinding.btnTran2en.setOnClickListener(v -> translate(TranslationRepository.ToLang.EN));
        mBinding.btnTrans2zh.setOnClickListener(v -> translate(TranslationRepository.ToLang.ZH));
        return mBinding.getRoot();
    }

    private void translate(TranslationRepository.ToLang toLang) {
        Log.d(TAG, "From text: " + mBinding.getFromText());

        String fromText = mBinding.getFromText().trim();
        if (fromText.isEmpty()) {
            Toast.makeText(getContext(), "请输入要翻译的文本", Toast.LENGTH_SHORT).show();
            return;
        }

        setButtonsEnabled(false); // 禁用翻译按钮, 防止在网络请求时用户再次点击
        clearEditTextFocus(); // 关闭输入框焦点

        Observable.just("")
                .map(s -> TranslationRepository.getTranslation(fromText, toLang))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trans -> {
                    setButtonsEnabled(true);
                    if (trans != null) {
                        mBinding.setToText(trans);
                    } else {
                        Toast.makeText(getContext(), "获取翻译失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setButtonsEnabled(boolean enabled) {
        mBinding.btnTran2en.setEnabled(enabled);
        mBinding.btnTrans2zh.setEnabled(enabled);
    }

    private void clearEditTextFocus() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mBinding.etFromText.getWindowToken(), 0);
        mBinding.etFromText.clearFocus();
    }
}
