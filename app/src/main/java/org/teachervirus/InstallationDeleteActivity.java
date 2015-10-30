package org.teachervirus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RadioButton;

import utils.AppSettings;

public class InstallationDeleteActivity extends AppCompatActivity {

    private static final String TAG = InstallationDeleteActivity.class.getSimpleName();
    private RadioButton radioDelete,radioKeep;
    private AppCompatButton btnContinue;

    private void initialise(){
        radioDelete = (RadioButton)findViewById(R.id.radioDelete);
        radioKeep = (RadioButton)findViewById(R.id.radioKeep);
        btnContinue = (AppCompatButton)findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int id = v.getId();
            switch (id){
                case R.id.btnContinue:
                    continueInstallation();
                    break;
            }
        }
    };

    private void continueInstallation(){
        Intent intent = new Intent();
        if(radioDelete.isChecked()){
            AppSettings.deletePreviousInstallation(InstallationDeleteActivity.this);
            intent.putExtra("deleted",true);
        }else{
            intent.putExtra("deleted",false);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation_delete);
        initialise();
    }
}
