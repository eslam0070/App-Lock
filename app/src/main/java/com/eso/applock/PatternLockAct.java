package com.eso.applock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.eso.applock.databinding.ActivityPatternLockBinding;
import com.eso.applock.model.Password;
import com.eso.applock.services.BackgroundManager;
import com.eso.applock.utils.Utils;

import java.util.List;

public class PatternLockAct extends AppCompatActivity {

    ActivityPatternLockBinding binding;
    Password utilsPassword;
    String userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatternLockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        BackgroundManager.getInstance().init(this).startService();
        initIconApp();
        utilsPassword = new Password(this);
        binding.statusPassword.setText(utilsPassword.STATUS_FIRST_STEP);
        if (utilsPassword.getPassword() == null){
            binding.normalLayout.setVisibility(View.GONE);
            binding.stepView.setVisibility(View.VISIBLE);
            binding.stepView.setStepsNumber(2);
            binding.stepView.go(0,true);
        }else {
            binding.normalLayout.setVisibility(View.VISIBLE);
            binding.stepView.setVisibility(View.GONE);

            int backColor = ResourcesCompat.getColor(getResources(),R.color.blue,null);
            binding.root.setBackgroundColor(backColor);
            binding.statusPassword.setTextColor(Color.WHITE);
        }

        initPatternListener();
    }

    private void initIconApp() {
        if (getIntent().getStringExtra("broadcast_receiver") != null){
            String current_App = new Utils(this).getLastApp();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = getPackageManager().getApplicationInfo(current_App,0);
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
            binding.appIcon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
        }
    }

    private void initPatternListener() {
        binding.patternView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String pwd = PatternLockUtils.patternToString(binding.patternView,pattern);
                if (pwd.length() < 4){
                    binding.statusPassword.setText(utilsPassword.SHEMA_FAILED);
                    binding.patternView.clearPattern();
                    return;
                }
                if (utilsPassword.getPassword() == null){
                    if (utilsPassword.isFirstStep()){
                        userPassword = pwd;
                        utilsPassword.setFirstStep(false);
                        binding.statusPassword.setText(utilsPassword.STATUS_NEXT_STEP);
                        binding.stepView.go(1,true);
                    }else {
                        if (userPassword.equals(pwd)){
                            utilsPassword.setPassword(userPassword);
                            binding.statusPassword.setText(utilsPassword.STATUS_PASSWORD_CORRECT);
                            binding.stepView.done(true);
                            startAct();
                        }else {
                            binding.statusPassword.setText(utilsPassword.STATUS_PASSWORD_INCORRECT);
                        }
                    }
                }else {
                    if (utilsPassword.isCorrect(pwd)){
                        binding.statusPassword.setText(utilsPassword.STATUS_PASSWORD_CORRECT);
                        startAct();
                    }else {
                        binding.statusPassword.setText(utilsPassword.STATUS_PASSWORD_INCORRECT);
                    }
                }
                binding.patternView.clearPattern();
            }

            @Override
            public void onCleared() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (utilsPassword.getPassword() == null && !utilsPassword.isFirstStep()){
            binding.stepView.go(0,true);
            utilsPassword.setFirstStep(true);
            binding.statusPassword.setText(utilsPassword.STATUS_FIRST_STEP);
        }else {
            startCurrentHomePackage();
            finish();
            super.onBackPressed();
        }
    }

    private void startCurrentHomePackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName,activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
        new Utils(this).clearLastApp();
    }

    private void startAct() {
        if (getIntent().getStringExtra("broadcast_receiver") == null){
            startActivity(new Intent(PatternLockAct.this,MainActivity.class));
        }
        finish();
    }
}
