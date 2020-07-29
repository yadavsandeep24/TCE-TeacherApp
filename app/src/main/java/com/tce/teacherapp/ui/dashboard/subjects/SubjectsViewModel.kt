package com.tce.teacherapp.ui.dashboard.subjects

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.*
import com.tce.teacherapp.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject

@ExperimentalCoroutinesApi
@UseExperimental(FlowPreview::class)
class SubjectsViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<SubjectViewState>() {

    override fun handleNewData(data: SubjectViewState) {
        data.gradeList?.let {
            setGradeListData(it)
        }
        data.selectedGradePosition?.let {
            setSelectedGradePosition(it)
        }
        data.subjectList?.let {
            setSubjectListData(it)
        }
        data.topicList?.let {
            setTopicListData(it)
        }
        data.chapterLearnData?.let {
            setChapterLearnData(it)
        }
        data.chapterResourceTyeList?.let {
            setChapterResourceTypeData(it)
        }
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<SubjectViewState>> = when (stateEvent) {

            is SubjectStateEvent.GetDivisionEvent -> {
                mainRepository.getGrades(stateEvent = stateEvent)
            }

            is SubjectStateEvent.DivisionSelectionEvent -> {
                mainRepository.setSelectedGrade(
                    stateEvent.grade,
                    stateEvent.position,
                    stateEvent = stateEvent
                )
            }

            is SubjectStateEvent.GetSubjectEvent -> {
                mainRepository.getSubjects(stateEvent.query, stateEvent = stateEvent)
            }

            is SubjectStateEvent.GetTopicEvent -> {
                mainRepository.getTopicList(
                    stateEvent.query,
                    stateEvent.bookId,
                    stateEvent = stateEvent
                )
            }
            is SubjectStateEvent.GetChapterEvent -> {
                mainRepository.getChapterList(
                    stateEvent.query,
                    stateEvent.topicId,
                    stateEvent.bookId,
                    stateEvent = stateEvent
                )
            }
            is SubjectStateEvent.GetTopicResourceEvent ->{
                mainRepository.getTopicResourceList(
                    stateEvent.query,
                    stateEvent.topicId,
                    stateEvent.chapterId,
                    stateEvent = stateEvent
                )
            }
            is SubjectStateEvent.GetChapterResourceTypeEvent ->{
                mainRepository.getChapterResourceType(
                    stateEvent.chapterId,
                    stateEvent.topicId,
                    stateEvent.bookId,
                    stateEvent = stateEvent
                )
            }
            else -> {
                flow {
                    emit(
                        DataState.error<SubjectViewState>(
                            response = Response(
                                message = INVALID_STATE_EVENT,
                                uiComponentType = UIComponentType.None,
                                messageType = MessageType.Error(),
                                serviceTypes = RequestTypes.GENERIC
                            ),
                            stateEvent = stateEvent
                        )
                    )
                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): SubjectViewState {
        return SubjectViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
        scope.launch {
            closePdfRenderer()
            cleared = true
            job.cancel()
        }
    }
    companion object {
        const val FILENAME = "sample.pdf"
    }

    private val job = Job()
    private val executor = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(executor.asCoroutineDispatcher() + job)

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var cleared = false

    private val _pageBitmap = MutableLiveData<Bitmap>()
    val pageBitmap: LiveData<Bitmap>
        get() = _pageBitmap

    private val _previousEnabled = MutableLiveData<Boolean>()
    val previousEnabled: LiveData<Boolean>
        get() = _previousEnabled

    private val _nextEnabled = MutableLiveData<Boolean>()
    val nextEnabled: LiveData<Boolean>
        get() = _nextEnabled

    private val _pageInfo = MutableLiveData<Pair<Int, Int>>()
    val pageInfo: LiveData<Pair<Int, Int>>
        get() = _pageInfo

    init {
        scope.launch {
            openPdfRenderer()
            showPage(0)
            if (cleared) {
                closePdfRenderer()
            }
        }
    }

    fun showPrevious() {
        scope.launch {
            currentPage?.let { page ->
                if (page.index > 0) {
                    showPage(page.index - 1)
                }
            }
        }
    }

    fun showNext() {
        scope.launch {
            pdfRenderer?.let { renderer ->
                currentPage?.let { page ->
                    if (page.index + 1 < renderer.pageCount) {
                        showPage(page.index + 1)
                    }
                }
            }
        }
    }

    private fun openPdfRenderer() {
        val application = mainRepository.getTCEApplication()
        val file = File(application.cacheDir, FILENAME)
        if (!file.exists()) {
            application.assets.open(FILENAME).use { asset ->
                file.writeBytes(asset.readBytes())
            }
        }
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).also {
            pdfRenderer = PdfRenderer(it)
        }
    }

    private fun showPage(index: Int) {
        // Make sure to close the current page before opening another one.
        currentPage?.let { page ->
            currentPage = null
            page.close()
        }
        pdfRenderer?.let { renderer ->
            // Use `openPage` to open a specific page in PDF.
            val page = renderer.openPage(index).also {
                currentPage = it
            }
            // Important: the destination bitmap must be ARGB (not RGB).
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            _pageBitmap.postValue(bitmap)
            val count = renderer.pageCount
            _pageInfo.postValue(index to count)
            _previousEnabled.postValue(index > 0)
            _nextEnabled.postValue(index + 1 < count)
        }
    }

    private fun closePdfRenderer() {
        currentPage?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
    }


}