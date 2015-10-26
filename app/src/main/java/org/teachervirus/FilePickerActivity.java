package org.teachervirus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import adapter.StringViewAdapter;

public class FilePickerActivity extends AppCompatActivity {

    private static final String SELECTED_PATH ="path";

    String sSelectedPath = "";
    ListView lstFolders;
    AppCompatButton btnSave;
    AppCompatButton btnBack;

    ArrayList<String> folders = null;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_PATH, sSelectedPath);
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        initialise();
        sSelectedPath = getIntent().getStringExtra("path");
        setTitle(sSelectedPath);
        FillFolders(sSelectedPath);
    }

    private void initialise(){

        lstFolders=(ListView)findViewById(R.id.lstFolders);
        btnSave = (AppCompatButton)findViewById(R.id.button_save);
        btnBack=(AppCompatButton)findViewById(R.id.button_back);

        btnBack.setOnClickListener(mOnClickListener);
        btnSave.setOnClickListener(mOnClickListener);

        lstFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sFolderName = (String) ((StringViewAdapter) parent
                        .getAdapter()).getItem(position);

                FillFolders(sSelectedPath + "/" + sFolderName);
            }
        });
    }


    private void FillFolders(String path) {

        File storageDir = new File(path.length() == 0 ? "/mnt/" : path);

        if (storageDir.isDirectory()) {
            sSelectedPath = storageDir.getAbsolutePath();
            String[] mStrings = storageDir.list();
            ArrayList<String> folders = new ArrayList<String>();
            for (String s : mStrings) {
                File f = new File(String.format("%s/%s",
                        storageDir.getAbsolutePath(), s));
                if (f.isDirectory() && !f.getName().startsWith("."))
                    folders.add(s);
            }

            lstFolders.setAdapter(new StringViewAdapter(this, folders));

            if (path.length() == 0) {
                setTitle("Select data folder");
            } else
                setTitle(sSelectedPath);
        } else {
            // General.ShowToastLong(lstFolders.getContext(),
            // "not a directory.");
        }


    }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int id = v.getId();

            switch (id){

                case R.id.button_back:

                    try {
                        if (getTitle().toString().equalsIgnoreCase("/mnt")) {
                            setResult(RESULT_CANCELED);
                            finish();
                            return;
                        }
                        sSelectedPath = sSelectedPath.substring(0,
                                sSelectedPath.lastIndexOf("/"));
                    } catch (Exception ex) {
                        sSelectedPath = "";
                    }
                    FillFolders(sSelectedPath);
                    break;

                case R.id.button_save:

                    try{

                        File storageDir = new File(sSelectedPath);
                        sSelectedPath = storageDir.getAbsolutePath();
                        if (storageDir.isDirectory()) {
                            Intent intent = new Intent();
                            intent.putExtra(SELECTED_PATH, sSelectedPath);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(FilePickerActivity.this,"Please select directory",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra(SELECTED_PATH, sSelectedPath);
                            setResult(RESULT_CANCELED, intent);
                            finish();

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                    break;
            }
        }
    };
}
