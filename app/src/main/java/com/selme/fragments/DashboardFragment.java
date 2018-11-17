package com.selme.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.selme.R;
import com.selme.adapter.DashboardAdapter;
import com.selme.dto.DataMapper;
import com.selme.dto.PostDTO;
import com.selme.entity.PostEntity;
import com.selme.interfaces.PostDTOCallback;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DashboardFragment extends Fragment implements PostDTOCallback {

    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;
    private DataMapper dataMapper;
    private StorageReference storageRef;
    private ProgressBar progressBar;

    public DashboardFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        storageRef = FirebaseStorage.getInstance().getReference();

        recyclerView = view.findViewById(R.id.dashboard_recycler_view);
        progressBar = view.findViewById(R.id.progressBar_dashboard);

        dataMapper = new DataMapper(storageRef);
        dataMapper.toPostDto(this);

        initRecyclerView();

    }

    private void initRecyclerView() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dashboardAdapter = new DashboardAdapter();
        recyclerView.setAdapter(dashboardAdapter);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }


    @Override
    public void toDto(List<PostDTO> dto) {
        dashboardAdapter.setItems(dto);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
