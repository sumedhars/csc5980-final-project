package edu.msoe.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.msoe.myapplication.R

// this fragment is used for selecting the export date range
class ExportDatePickerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_export_date_picker, container, false)
    }

    // TODO: Implement date picking logic and pass
    //  the selected dates back to ExportManagerFragment.
}
