package ui;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.teachervirus.FilePickerActivity;
import org.teachervirus.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import common.utils.FileUtils;
import common.utils.PreferenceHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigureDirectoryFragment extends Fragment {

    private static final int REQ_DIR = 101;
    private AppCompatButton btnChange,btnChangePath,btnCancel;
    private AppCompatTextView txtSelectPath,txtCurrentPath;
    private RadioButton mSelectedPathRadio;
    private CheckBox mCopyCheckbox,mOverWrightCheckBox;
    private String selectedPath="";
    private ProgressBar mProgressBar;
    private AppCompatTextView txtMsg;

    public ConfigureDirectoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configure_directory, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnChange = (AppCompatButton)view.findViewById(R.id.btnChange);

        mSelectedPathRadio=(RadioButton)view.findViewById(R.id.radioSelectPath);
        mCopyCheckbox=(CheckBox)view.findViewById(R.id.checkCopy);
        mOverWrightCheckBox=(CheckBox)view.findViewById(R.id.checkOverWright);
        txtSelectPath=(AppCompatTextView)view.findViewById(R.id.txtSelectedPath);
        txtCurrentPath = (AppCompatTextView)view.findViewById(R.id.tctCurrentPath);
        btnChangePath=(AppCompatButton)view.findViewById(R.id.btnChangePath);
        btnCancel=(AppCompatButton)view.findViewById(R.id.btnCancel);
        mProgressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        txtMsg=(AppCompatTextView)view.findViewById(R.id.txtMsg);

        mCopyCheckbox.setOnCheckedChangeListener(checkchangeListener);
        mOverWrightCheckBox.setOnCheckedChangeListener(checkchangeListener);
        btnChange.setOnClickListener(mOnClickListener);
        btnChangePath.setOnClickListener(mOnClickListener);
        btnCancel.setOnClickListener(mOnClickListener);

        txtCurrentPath.setText("Current path : " + FileUtils.getPathToRootDir());

        super.onViewCreated(view, savedInstanceState);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int id = v.getId();
            switch (id){

                case R.id.btnChange:
                    if(mSelectedPathRadio.isChecked()){

                        new CopyTask().execute();
                    }
                    break;

                case R.id.btnChangePath:
                    Intent mIntent = new Intent(getContext(), FilePickerActivity.class);
                    mIntent.putExtra("path",selectedPath);
                    startActivityForResult(mIntent, REQ_DIR);
                    break;

                case R.id.btnCancel:
                    getActivity().finish();
                    break;
            }
        }
    };



    private void onDirChanged(){

        if(selectedPath.equals("/mnt/sdcard/")){
            selectedPath = "/mnt/sdcard/TeacherVirus";
        }


        try {
            copyDirectoryOneLocationToAnotherLocation(new File(FileUtils.getPathToRootDir())
            ,new File(selectedPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyDefault(){
        String path = FileUtils.getPathToRootDir();
        if(path.equals("/mnt/sdcard/htdocs")){
            path = "/mnt/sdcard/TeacheVirus";

        }
        if(selectedPath.equals("")){
            selectedPath = path;
        }
        File mDefaultFIle = new File(path);
        if(!mDefaultFIle.exists()){
            mDefaultFIle.mkdirs();
        }
        String[] files = mDefaultFIle.list();
        File dest= new File(selectedPath);
        if(!dest.exists()){
            dest.mkdirs();
        }
        for(String filename : files){

            if (filename.contains("getinfected.php") || filename.contains("loading_spinner.gif")){
                try {

                    copyDirectoryOneLocationToAnotherLocation(new File(mDefaultFIle,filename)
                            ,new File(dest,filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public  void copyDirectoryOneLocationToAnotherLocation(File sourceFolder, File destinationFolder)
            throws IOException {

        if (sourceFolder.isDirectory()) {
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }else{
                if(mCopyCheckbox.isChecked() && mOverWrightCheckBox.isChecked()){
                    destinationFolder.delete();
                    destinationFolder.mkdirs();
                }else if(!mCopyCheckbox.isChecked() && !mOverWrightCheckBox.isChecked()){
                    copyDefault();
                }
            }

            String[] children = sourceFolder.list();
            for (int i = 0; i < sourceFolder.listFiles().length; i++) {
                copyDirectoryOneLocationToAnotherLocation(new File(sourceFolder, children[i]),
                        new File(destinationFolder, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceFolder);
            OutputStream out = new FileOutputStream(destinationFolder);
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private CompoundButton.OnCheckedChangeListener checkchangeListener  = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            final int id = buttonView.getId();
            switch (id){
                case R.id.checkCopy:
                    if(isChecked){
                        mOverWrightCheckBox.setEnabled(true);
                    }else{
                        mOverWrightCheckBox.setEnabled(false);
                    }
                    break;
            }
        }
    };




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQ_DIR){
            if(resultCode == Activity.RESULT_OK){
                selectedPath = data.getStringExtra("path");
                txtSelectPath.setText(selectedPath);
                mSelectedPathRadio.setChecked(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class CopyTask extends AsyncTask<Void,Void,Boolean>{

        private boolean checked;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            txtMsg.setVisibility(View.VISIBLE);
            checked = mCopyCheckbox.isChecked();
            btnChange.setEnabled(false);
            btnChangePath.setEnabled(false);
            btnCancel.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if(selectedPath.length()>0){


                if(checked) {
                    onDirChanged();
                    FileUtils.setServerRootDir(getActivity(),new File(selectedPath));
                }else{
                    copyDefault();
                    FileUtils.setServerRootDir(getActivity(),new File(selectedPath));
                }

                PreferenceHelper.putBoolean(getActivity(), "restart", "restart", true);
                Intent intent = new Intent("reboot");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                getActivity().finish();
                return true;
            }else{

                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressBar.setVisibility(View.GONE);
            txtMsg.setVisibility(View.GONE);
            btnChange.setEnabled(true);
            btnChangePath.setEnabled(true);
            btnCancel.setEnabled(true);
            if(!aBoolean){
                Toast.makeText(getActivity(), "Please select path", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aBoolean);
        }
    }
}
