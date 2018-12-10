package com.selme.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.google.firebase.storage.FirebaseStorage
import com.selme.R
import com.selme.view.adapter.DashboardAdapter
import com.selme.presenter.service.DataMapper
import com.selme.model.dto.PostDTO
import com.selme.presenter.interfaces.PostDTOCallback
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : Fragment(), PostDTOCallback {

    private var dashboardAdapter: DashboardAdapter? = null
    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        progressBar = view.progressBar_dashboard
        swipeRefreshLayout = view.swipeRefresh_dashboard

        val storageRef = FirebaseStorage.getInstance().reference
        val dataMapper = DataMapper(storageRef)
        swipeRefreshLayout?.setOnRefreshListener { dataMapper.toPostDto(this, false) }
        dataMapper.toPostDto(this, false)

        initRecyclerView(view)

        return view
    }

    private fun initRecyclerView(view: View) {
        view.dashboard_recycler_view.layoutManager = LinearLayoutManager(context)
        dashboardAdapter = DashboardAdapter()
        view.dashboard_recycler_view.adapter = dashboardAdapter

        val itemDecoration = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        view.dashboard_recycler_view.addItemDecoration(itemDecoration)
    }


    override fun toDto(dto: List<PostDTO>) {
        if(dto.isEmpty()){
            progressBar!!.visibility = View.GONE
            swipeRefreshLayout!!.isRefreshing = false
            return
        }

        dashboardAdapter!!.clearItems()

        val sortedList = dto.sortedByDescending { it.createdDate }
        dashboardAdapter!!.setItems(sortedList)

        progressBar!!.visibility = View.GONE
        swipeRefreshLayout!!.isRefreshing = false
    }
}
