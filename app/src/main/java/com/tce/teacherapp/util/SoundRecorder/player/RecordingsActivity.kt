package com.example.gabriel.soundrecorder.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gabriel.soundrecorder.util.InjectorUtils
import com.tce.teacherapp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_recordings.*

class RecordingsActivity : AppCompatActivity() {

    private var viewModel: RecordingViewModel? = null
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>
    private var data: ArrayList<String>? = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordings)

        groupAdapter = GroupAdapter<ViewHolder>()

        initUI()
    }

    override fun onStart() {
        super.onStart()

        //Ably the span count and the adapter to the recyclerview
        recordings_recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun initUI() {
        //Get the viewmodel factory
        val factory = InjectorUtils.provideRecordingViewModelFactory()

        //Getting the viewmodel
       // viewModel = ViewModelProviders.of(this, factory).get(RecordingViewModel::class.java)

        updateAdapter()
    }

    private fun updateAdapter() {
        data = viewModel?.getRecordings()
        println("Updating Adapter")
        groupAdapter.clear()

        if(data != null) {
            data!!.forEach {
                println("Data: $it")
              //  groupAdapter.add(Recording(it,this))
            }
        }
    }
}
