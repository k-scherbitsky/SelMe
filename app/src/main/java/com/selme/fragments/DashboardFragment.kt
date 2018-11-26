package com.selme.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.google.common.collect.Lists
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.selme.R
import com.selme.adapter.DashboardAdapter
import com.selme.dto.DataMapper
import com.selme.dto.PostDTO
import com.selme.entity.PostEntity
import com.selme.interfaces.PostDTOCallback

import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.Date
import java.util.stream.Collectors

class DashboardFragment : Fragment(), PostDTOCallback {

    private var recyclerView: RecyclerView? = null
    private var dashboardAdapter: DashboardAdapter? = null
    private var dataMapper: DataMapper? = null
    private var storageRef: StorageReference? = null
    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onStart() {
        super.onStart()

        val view = view
        storageRef = FirebaseStorage.getInstance().reference
        dataMapper = DataMapper(storageRef)

        recyclerView = view!!.findViewById(R.id.dashboard_recycler_view)
        progressBar = view.findViewById(R.id.progressBar_dashboard)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh_dashboard)

        swipeRefreshLayout!!.setOnRefreshListener { dataMapper!!.toPostDto(this) }

        dataMapper!!.toPostDto(this)

        initRecyclerView()

    }

    private fun initRecyclerView() {
        val itemDecoration = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        dashboardAdapter = DashboardAdapter(recyclerView)
        recyclerView!!.adapter = dashboardAdapter
        recyclerView!!.addItemDecoration(itemDecoration)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }


    override fun toDto(dto: List<PostDTO>) {
        dashboardAdapter!!.clearItems()

        val sortedList = dto.sortedByDescending { it.createdDate }

        dashboardAdapter!!.setItems(sortedList)
        progressBar!!.visibility = View.INVISIBLE
        swipeRefreshLayout!!.isRefreshing = false
    }
}
