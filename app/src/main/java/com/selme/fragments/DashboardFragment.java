package com.selme.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.selme.R;
import com.selme.adapter.DashboardAdapter;
import com.selme.entity.PostEntity;

import java.util.Arrays;
import java.util.Collection;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;

    public DashboardFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();

        recyclerView = view.findViewById(R.id.dashboard_recycler_view);

        initRecyclerView();

        loadPosts();

    }

    private void loadPosts() {
        Collection<PostEntity> entities = getPosts();
        dashboardAdapter.setItems(entities);
    }

    private Collection<PostEntity> getPosts() {
        return Arrays.asList(
                new PostEntity("Ps4 vs PC", "Who is win?",
                        "https://stylus.ua/thumbs/390x390/ed/94/144363.jpeg",
                        "https://www.cyberpowerpc.com/images/cs/p400/whtw_400.png"),
                new PostEntity("Lorem", "Who is win?",
                        "https://www.w3schools.com/css/img_mountains.jpg",
                        "https://www.w3schools.com/w3css/img_fjords.jpg")
                );
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dashboardAdapter = new DashboardAdapter();
        recyclerView.setAdapter(dashboardAdapter);
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



}
