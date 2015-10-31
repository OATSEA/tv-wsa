package org.teachervirus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;

import common.utils.FileUtils;
import utils.AppSettings;

public class ChooseInstallationActivity extends AppCompatActivity {

    private static final String TAG = ChooseInstallationActivity.class.getSimpleName();
    private static final int REQ_FILE_PICKER = 1001;
    private RadioButton mDefaultPathRadio,mSelectedPathRadio;
    private AppCompatButton btnGo,btnChangePath,btnRetry;
    private AppCompatTextView txtDefaultPath,txtSelectedPath,txtMsg,txtTitle;
    private String selectedPath="";

    private void initialise(){

        txtDefaultPath=(AppCompatTextView)findViewById(R.id.txtDefaultPath);
        txtSelectedPath=(AppCompatTextView)findViewById(R.id.txtSelectedPath);
        txtMsg=(AppCompatTextView)findViewById(R.id.txtMsg);
        txtTitle = (AppCompatTextView)findViewById(R.id.txtTitle);

        mDefaultPathRadio = (RadioButton)findViewById(R.id.radioDefault);
        mSelectedPathRadio = (RadioButton)findViewById(R.id.radioSelectPath);

        btnRetry=(AppCompatButton)findViewById(R.id.btnRetry);
        btnChangePath=(AppCompatButton)findViewById(R.id.btnChangePath);
        btnGo= (AppCompatButton)findViewById(R.id.btnGO);

        btnChangePath.setOnClickListener(mOnClickListener);
        btnGo.setOnClickListener(mOnClickListener);
        btnRetry.setOnClickListener(mOnClickListener);

        mDefaultPathRadio.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mSelectedPathRadio.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id){

                case R.id.btnGO:
                    if(mDefaultPathRadio.isChecked()){

                        onDirectorySelected(AppSettings.getRootDirPath(ChooseInstallationActivity.this));
                    }else{
                        if(selectedPath.length()>0){
                            onDirectorySelected(selectedPath);
                        }else{
                            Toast.makeText(ChooseInstallationActivity.this, "Please select path", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case R.id.btnChangePath:
                    openFilePicker();
                    break;

                case R.id.btnRetry:
                    if(FileUtils.ensureLighttpdConfigExists()){
                        if(AppSettings.getRootDir(ChooseInstallationActivity.this).exists()){
                            onDirectorySelected(AppSettings.getRootDirPath(ChooseInstallationActivity.this));
                        }else{
                            txtMsg.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            Toast.makeText(ChooseInstallationActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            final int id = buttonView.getId();

            switch (id){
                case R.id.radioDefault:
                    if(isChecked){
                        mSelectedPathRadio.setChecked(false);
                    }
                    break;

                case R.id.radioSelectPath:
                    if(isChecked){
                        mDefaultPathRadio.setChecked(false);
                    }
                    break;
            }
        }
    };



    private void openFilePicker(){
        Intent mIntent = new Intent(ChooseInstallationActivity.this,FilePickerActivity.class);
        mIntent.putExtra("path",selectedPath);
        startActivityForResult(mIntent,REQ_FILE_PICKER);
    }


    private void onDirectorySelected(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        AppSettings.updateRootDirPath(ChooseInstallationActivity.this,path);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setup(boolean retry){
        if(retry){
            txtTitle.setText(AppSettings.getRootDirPath(ChooseInstallationActivity.this)+" not found.");
            txtMsg.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.VISIBLE);
            txtDefaultPath.setVisibility(View.GONE);
            mDefaultPathRadio.setVisibility(View.GONE);
        }else{
            txtDefaultPath.setText(AppSettings.getRootDirPath(ChooseInstallationActivity.this));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_installation);
        initialise();
        setup(getIntent().getBooleanExtra("retry",false));
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean retry = getIntent().getBooleanExtra("retry",false);
        if(retry && AppSettings.getRootDir(ChooseInstallationActivity.this).exists()){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQ_FILE_PICKER:
                if(resultCode == RESULT_OK){

                    selectedPath = data.getStringExtra("path");
                    txtSelectedPath.setText(selectedPath);
                    mSelectedPathRadio.setChecked(true);
                }else{
                    mDefaultPathRadio.setChecked(true);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
