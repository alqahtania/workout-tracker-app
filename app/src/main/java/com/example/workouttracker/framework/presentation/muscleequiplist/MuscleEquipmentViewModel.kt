package com.example.workouttracker.framework.presentation.muscleequiplist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.util.LruCache
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.workouttracker.cache.abstraction.MuscleEquipmentDaoService
import com.example.workouttracker.cache.util.GenericErrors
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipment
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipmentPhoto
import com.example.workouttracker.domain.state.DataState
import com.example.workouttracker.domain.state.MessageType
import com.example.workouttracker.domain.state.ResponseMessage
import com.example.workouttracker.domain.state.StateEvent
import com.example.workouttracker.framework.datasource.cache.mappers.MuscleEquipmentMapper
import com.example.workouttracker.framework.presentation.common.BaseViewModel
import com.example.workouttracker.framework.presentation.muscleequiplist.state.MuscleEquipmentStateEvent
import com.example.workouttracker.framework.presentation.muscleequiplist.state.MuscleEquipmentViewState
import com.example.workouttracker.framework.presentation.util.Constants.AUTHORITIES
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MuscleEquipmentViewModel
@ViewModelInject
constructor(
    private val muscleEquipmentDaoService: MuscleEquipmentDaoService,
    private val mapper: MuscleEquipmentMapper,
    @ApplicationContext private val context: Context
) : BaseViewModel<MuscleEquipmentViewState>() {

//    private val _currentMuscle = MutableLiveData<Muscle?>()
//
//    val currentMuscle : LiveData<Muscle?>
//    get() = _currentMuscle


    private val _currentMuscleId = MutableLiveData<String?>()

    var photoPathsMap: Map<String, MuscleEquipmentPhoto> by mutableStateOf(mapOf())


    var memoryCache: LruCache<String, Bitmap>

    init {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    var isImageLoading by mutableStateOf(false)
    val muscleEquipments = Transformations.switchMap(_currentMuscleId) { muscleId ->
        muscleId?.let {
            liveData {
                muscleEquipmentDaoService
                    .searchMuscleEquipByMuscleId(it)
                    .map {
                        val list = mapper.entityListToDomainList(it).reversed()
                        fetchImages(list)
                        list
                    }
                    .catch { error ->
                        setResponseMessage(
                            responseMessage = ResponseMessage(
                                message = "${GenericErrors.ERROR_LOADING_MUSCLE_EQUIPMENTS}\nReason: ${error.message}",
                                messageType = MessageType.Error()
                            )
                        )
                    }
                    .collect {
                        emit(it)
                    }
            }
        }

    }

    fun fetchImages(muscleEquipList: List<MuscleEquipment>) {
        viewModelScope.launch {
            isImageLoading = true

            val photoPathsList = getListOfPhotoFilesPaths()
            addPhotoPath(muscleEquips = muscleEquipList, photoPaths = photoPathsList)
        }
    }

    override fun handleNewData(data: MuscleEquipmentViewState) {
        data.allMuscleEquipmentsList?.let {
            setAllMuscleEquipments(it)
        }
        data.deletedMuscleEquipment?.let {
            setDeletedMuscleEquip(it)
        }
        data.muscleEquipByMuscleIdList?.let {
            setMuscleEquipmentByMuscleId(it)
        }
        data.newInsertedNote?.let {
            setInsertedMuscleEquip(it)
        }
        data.singleMuscleEquipSearch?.let {
            setSingleMuscleEquipSearch(it)
        }
        data.updatedMuscleEquipment?.let {
            setUpdatedMuscleEquip(it)
        }
    }

    override fun handleNewResponseMessage(responseMessage: ResponseMessage) {
        setResponseMessage(responseMessage)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<MuscleEquipmentViewState>?> = when (stateEvent) {
            is MuscleEquipmentStateEvent.InsertNewMuscleEquipmentEvent -> {
                muscleEquipmentDaoService.insertMuscleEquipment(stateEvent.muscleEquipment)
            }
            is MuscleEquipmentStateEvent.DeleteAMuscleEquipmentById -> {
                muscleEquipmentDaoService.deleteAMuscleEquipmentById(stateEvent.muscleEquipmentId)
            }
            is MuscleEquipmentStateEvent.DeleteAMuscleEquipmentEvent -> {
                deleteAllFilesForDeletedMuscleEquip(stateEvent.muscleEquipment)
                muscleEquipmentDaoService.deleteAMuscleEquipment(stateEvent.muscleEquipment)
            }
            is MuscleEquipmentStateEvent.SearchSingleMuscleEquipEvent -> {
                muscleEquipmentDaoService.searchMuscleEquipmentById(stateEvent.muscleEquipId)
            }
            is MuscleEquipmentStateEvent.UpdateAMuscleEquipmentEvent -> {
                muscleEquipmentDaoService.updateAMuscleEquipment(stateEvent.newMuscleEquipment)
            }
            else ->
                emitInvalidStateEvent(stateEvent = stateEvent)
        }

        launchJob(job)
    }

    override fun initNewViewState(): MuscleEquipmentViewState {
        return MuscleEquipmentViewState()
    }

    fun setAllMuscleEquipments(muscleEquipList: ArrayList<MuscleEquipment>) {
        val update = getCurrentViewStateOrNew()
        update.allMuscleEquipmentsList = muscleEquipList
        setViewState(update)
    }

    private fun setDeletedMuscleEquip(muscleEquip: MuscleEquipment) {
        val update = getCurrentViewStateOrNew()
        update.deletedMuscleEquipment = muscleEquip
        setViewState(update)
    }

    private fun setMuscleEquipmentByMuscleId(muscleEquip: ArrayList<MuscleEquipment>) {
        val update = getCurrentViewStateOrNew()
        update.muscleEquipByMuscleIdList = muscleEquip
        setViewState(update)
    }

    private fun setInsertedMuscleEquip(muscleEquip: MuscleEquipment) {
        val update = getCurrentViewStateOrNew()
        update.newInsertedNote = muscleEquip
        setViewState(update)
    }

    private fun setSingleMuscleEquipSearch(muscleEquip: MuscleEquipment) {
        val update = getCurrentViewStateOrNew()
        update.singleMuscleEquipSearch = muscleEquip
        setViewState(update)
    }

    private fun setUpdatedMuscleEquip(muscleEquip: MuscleEquipment) {
        val update = getCurrentViewStateOrNew()
        update.updatedMuscleEquipment = muscleEquip
        setViewState(update)
    }

    fun setNewPhotoForMuscleEquip(muscleEquip: MuscleEquipment) {
        val update = getCurrentViewStateOrNew()
        update.newPhotoForMuscleEquip = muscleEquip
        setViewState(update)
    }

    fun setCurrentMuscleId(muscleId: String) {
        _currentMuscleId.value = muscleId
    }

    fun clearCurrentMuscleId() {
        _currentMuscleId.value = null
    }

    //for updating a muscle
    private var currentEditPosition by mutableStateOf(-1)

    val currentEditItem: MuscleEquipment?
        get() = muscleEquipments.value?.getOrNull(currentEditPosition)

    fun onEditItemSelected(item: MuscleEquipment) {
        currentEditPosition = muscleEquipments.value?.indexOf(item) ?: -1
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    private suspend fun addPhotoPath(muscleEquips: List<MuscleEquipment>, photoPaths: List<File>) {
        withContext(Default) {

            val photoMap = hashMapOf<String, MuscleEquipmentPhoto>()
            val muscleEquipIdsList = muscleEquips.map { it.id }
            if (photoPaths.isNotEmpty() && muscleEquips.isNotEmpty()) {
                for (path in photoPaths) {

                    val muscleEqup = muscleEquipIdsList.find {
                        path.absolutePath.substringAfterLast("/").contains(it)
                    }

                    muscleEqup?.let { muscleEquipId ->


                        val imageBitmap =
                            BitmapFactory.decodeFile(path.absolutePath)

                        imageBitmap?.let {
                            val resizedBitmap = getResizedBitmap(it, 400)
                            val resizedToImageBmp = resizedBitmap.asImageBitmap()
                            val muscleEquipmentPhoto = MuscleEquipmentPhoto(path, resizedToImageBmp)

                            addBitmapToMemoryCache(muscleEquipId, resizedBitmap)

                            photoMap[muscleEquipId] = muscleEquipmentPhoto
                        }

                    }
                }
                photoPathsMap = photoPathsMap + photoMap

            }
            isImageLoading = false
        }

    }

    private suspend fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    private suspend fun getBitmapFromMemCache(key: String): Bitmap? {
        return memoryCache.get(key)
    }

    private suspend fun getResizedBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        Log.d("TAG", "getResizedBitmap: thread ${Thread.currentThread().name} ")
        var width = bitmap.width
        var height = bitmap.height

        val bitmapRation = width.toFloat() / height.toFloat()
        if (bitmapRation > 1) {
            width = maxSize
            height = (width / bitmapRation).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRation).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun addPhotoPathAndMuscleEquipId(muscleEquipId: String, photoFile: File) {
        viewModelScope.launch(IO) {
            isImageLoading = true
            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageBitmap?.let {
                val resizedBitmap = getResizedBitmap(it, 400)
                val resizedToImageBmp = resizedBitmap.asImageBitmap()
                val muscleEquipmentPhoto = MuscleEquipmentPhoto(photoFile, resizedToImageBmp)

                memoryCache.remove(muscleEquipId)
                addBitmapToMemoryCache(muscleEquipId, resizedBitmap)

                photoPathsMap = photoPathsMap + mapOf(muscleEquipId to muscleEquipmentPhoto)

            }
            isImageLoading = false
        }

    }

    fun onEditItemChange(item: MuscleEquipment) {
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id == item.id) {
            "You can only change an item with the same id as currentEditItem"
        }

        val stateEvent = MuscleEquipmentStateEvent.UpdateAMuscleEquipmentEvent(item)
        setStateEvent(stateEvent)
    }


    // Handling picture files


    private suspend fun getListOfPhotoFilesPaths(): List<File> = withContext(IO) {

        return@withContext if (isExternalStorageWritable()) {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()
                ?.toList()
                ?: listOf()
        } else {
            listOf()
        }
    }

    fun createNewPhotoFileName(fileName: String) {
        viewModelScope.launch(IO) {
            if (isExternalStorageWritable()) {
                var oldFilePath: File? = null
                val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                val photoPathList = storageDirectory?.listFiles()
                photoPathList?.let { files ->
                    if (files.isNotEmpty()) {
                        for (p in photoPathList) {
                            if (p.toString().substringAfterLast("/").contains(fileName)) {
                                oldFilePath = p
                            }
                        }
                    }
                }

                val currentFilePath = File.createTempFile(fileName, ".jpg", storageDirectory)
                val imageUri =
                    FileProvider.getUriForFile(
                        context,
                        AUTHORITIES, currentFilePath
                    )

                setNewPhotoDetails(
                    currentMuscleEquipId = fileName,
                    currentPhotoFile = currentFilePath,
                    oldPhotoFile = oldFilePath,
                    imageUri = imageUri

                )

            }
        }

    }

    private suspend fun setNewPhotoDetails(
        currentPhotoFile: File?,
        oldPhotoFile: File?,
        currentMuscleEquipId: String?,
        imageUri: Uri?
    ) {
        withContext(Main) {

            val newPhotoDetail = MuscleEquipmentViewState.PhotoDetail(
                currentPhotoFile = currentPhotoFile,
                oldPhotoFile = oldPhotoFile,
                currentMuscleEquipId = currentMuscleEquipId,
                imageUri = imageUri
            )
            setPhotoDetails(newPhotoDetail)
        }
    }

    fun setPhotoDetails(photoDetails: MuscleEquipmentViewState.PhotoDetail?) {
        val update = getCurrentViewStateOrNew()
        update.newPhotoForMuscleEquip = null
        update.newPhotoDetail = photoDetails
        setViewState(update)
    }

    fun deletePhotoFile(filePath: File?) {
        viewModelScope.launch(IO) {
            if (isExternalStorageWritable()) {
                filePath?.let {
                    it.delete()
                }
            }
        }


    }

    fun deleteOldPhotoFilesForAnEquipExcept(exceptFile: File, muscleEquipId: String) {

        viewModelScope.launch(IO) {

            if (isExternalStorageWritable()) {

                val fileListToDelete = arrayListOf<File>()
                val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                val photoPathList = storageDirectory?.listFiles()
                photoPathList?.let { files ->
                    if (files.isNotEmpty()) {
                        for (p in photoPathList) {
                            if (p.toString().substringAfterLast("/").contains(muscleEquipId)) {
                                fileListToDelete.add(p)
                            }
                        }
                    }
                }

                val removeExceptFileList = fileListToDelete.filter {
                    it.toString() != exceptFile.toString()
                }

                removeExceptFileList.forEach {
                    it.delete()

                }
            }

        }


    }

    private fun deleteAllFilesForDeletedMuscleEquip(muscleEquip: MuscleEquipment) {

        viewModelScope.launch(IO) {
            if (isExternalStorageWritable()) {

                val fileListToDelete = arrayListOf<File>()
                val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                val photoPathList = storageDirectory?.listFiles()
                photoPathList?.let { files ->
                    if (files.isNotEmpty()) {
                        for (p in photoPathList) {
                            if (p.toString().substringAfterLast("/").contains(muscleEquip.id)) {
                                fileListToDelete.add(p)
                            }
                        }
                    }
                }


                if (fileListToDelete.size > 0) {
                    fileListToDelete.forEach {
                        it.delete()

                    }
                }

            }

        }

    }

    // Checks if a volume containing external storage is available
// for read and write.
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
}