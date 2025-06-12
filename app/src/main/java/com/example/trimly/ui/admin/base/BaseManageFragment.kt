package com.example.trimly.ui.admin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

abstract class BaseManageFragment<T> : Fragment() {

    protected lateinit var recyclerView: RecyclerView
    protected lateinit var fab: FloatingActionButton
    protected lateinit var adapter: BaseAdapter<T>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(getRecyclerViewId())
        fab = view.findViewById(getFabId())

        setupRecyclerView()
        setupFab()
        loadData()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = createAdapter()
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        fab.setOnClickListener {
            showAddDialog()
        }
    }

    fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    fun refreshData() {
        loadData()
    }

    abstract fun getLayoutId(): Int
    abstract fun getRecyclerViewId(): Int
    abstract fun getFabId(): Int
    abstract fun createAdapter(): BaseAdapter<T>
    abstract fun loadData()
    abstract fun showAddDialog()
} 