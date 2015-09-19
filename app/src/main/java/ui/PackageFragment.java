package ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.teachervirus.AppController;
import org.teachervirus.Constants;
import org.teachervirus.R;

import java.util.ArrayList;

import adapter.RepositoryListAdapter;

import dialogs.ConfirmDialogFragment;
import model.Repository;


public class PackageFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private ArrayList<Repository> mList = new ArrayList<Repository>();
    private RepositoryListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_repository, container, false);
        prepareView(rootView);
        requestJsonRepository();
        return rootView;
    }

    protected void requestJsonRepository() {
        JsonArrayRequest repositoryRequest = new JsonArrayRequest(Constants.REPOSITORY_URL,
                new RepositoryResponse(),
                new RepositoryErrorListener());
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(repositoryRequest);
    }

    protected void prepareView(View view) {
        mListView = (ListView) view.findViewById(R.id.extension_list);
        mAdapter = new RepositoryListAdapter(getActivity(), mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Repository repo = mList.get(i);
        String fileSize = Formatter.formatFileSize(getActivity(), repo.getSize());
        String repoDetail = String.format(getString(R.string.package_detail), repo.getName(),
                fileSize, repo.getCopyPath());
        ConfirmDialogFragment fragment = ConfirmDialogFragment.
                create(getActivity(), getString(R.string.package_info), repoDetail);
        fragment.show(getFragmentManager(), getClass().getSimpleName());
    }

    private class RepositoryResponse implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray jsonArray) {
            Repository repository;
            mList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject repoJso = jsonArray.getJSONObject(i);
                    repository = new Repository(
                            repoJso.getString("repo_name"),
                            repoJso.getString("repo_url"),
                            repoJso.getString("repo_install_location"),
                            repoJso.getLong("repo_file_size")
                    );
                    mList.add(repository);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RepositoryErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            AppController.toast(getActivity(), "Unable to parse json file: " + volleyError.getMessage());
        }
    }
}
