package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.msoe.myapplication.R
import edu.msoe.myapplication.data.Issue
import edu.msoe.myapplication.data.TaskRepository
import edu.msoe.myapplication.data.JiraApiServiceImpl
import edu.msoe.myapplication.ui.viewmodels.TaskListViewModel
import edu.msoe.myapplication.ui.viewmodels.TaskListViewModelFactory

class TaskListFragment : Fragment() {

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(TaskRepository(JiraApiServiceImpl()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        // Setup RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.rvTaskList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TaskListAdapter { issue ->
            val bundle = Bundle().apply {
                putString("issueId", issue.id)
                putString("issueTitle", issue.title)
            }
            findNavController().navigate(R.id.timerFragment, bundle)
        }
        recyclerView.adapter = adapter

        viewModel.issues.observe(viewLifecycleOwner) { issues ->
            adapter.submitList(issues)
        }
        viewModel.fetchIssues(boardId = 1) // Replace with actual board ID

        // Export button for Workflow 2
        val exportButton: Button = view.findViewById(R.id.btnExport)
        exportButton.setOnClickListener {
            findNavController().navigate(R.id.exportManagerFragment)
        }

        return view
    }

    private class TaskListAdapter(
        private val onClick: (Issue) -> Unit
    ) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

        private var issues: List<Issue> = emptyList()

        fun submitList(newIssues: List<Issue>) {
            issues = newIssues
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(issues[position])
        }

        override fun getItemCount(): Int = issues.size

        class TaskViewHolder(
            itemView: View,
            private val onClick: (Issue) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {
            private val titleText: TextView = itemView.findViewById(R.id.tvTaskTitle)
            private val idText: TextView = itemView.findViewById(R.id.tvTaskId)

            fun bind(issue: Issue) {
                titleText.text = issue.title
                idText.text = "ID: ${issue.id}"
                itemView.setOnClickListener { onClick(issue) }
            }
        }
    }
}


