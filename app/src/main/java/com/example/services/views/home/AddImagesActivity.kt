package com.example.services.views.home

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services.databinding.ActivityImagesBinding
import com.example.services.R
import com.example.services.callbacks.ChoiceCallBack
import com.example.services.common.UtilsFunctions
import com.example.services.constants.GlobalConstants
import com.example.services.model.CommonModel
import com.example.services.model.orders.OrdersDetailResponse
import com.example.services.model.ratnigreviews.RatingData
import com.example.services.model.ratnigreviews.RatingReviewListInput
import com.example.services.utils.BaseActivity
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.utils.Utils
import com.example.services.viewmodels.ratingreviews.RatingReviewsViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.AddRatingReviewsListAdapter
import com.uniongoods.adapters.ImagesListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddImagesActivity : BaseActivity(), DialogssInterface, ChoiceCallBack {
    lateinit var reviewsBinding: ActivityImagesBinding
    lateinit var reviewsViewModel: RatingReviewsViewModel
    var reviewsAdapter: AddRatingReviewsListAdapter? = null
    var cartObject = JsonObject()
    var count = 0
    var orderId = ""
    var mLoadMoreViewCheck = true
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager1: LinearLayoutManager
    var suborders: ArrayList<OrdersDetailResponse.Suborders>? = null
    var imagesList = ArrayList<String>()
    val ratingData = RatingReviewListInput()
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888
    private var companyId = ""
    private var profileImage = ""
    var imagesListAdapter: ImagesListAdapter? = null
    private var imagesParts: Array<MultipartBody.Part?>? = null
    // var ratingReviewInput = ArrayList<RatingReviewListInput>()
    override fun getLayoutId(): Int {
        return R.layout.activity_images
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
        /*   confirmationDialog = mDialogClass.setDefaultDialog(
               this,
               this,
               "Rating",
               getString(R.string.warning_rate_cancel)
           )
           confirmationDialog?.show()*/
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            "Rating" -> {
                confirmationDialog?.dismiss()
                finish()
            }
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {

            "Rating" -> confirmationDialog?.dismiss()

        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initViews() {

        reviewsBinding = viewDataBinding as ActivityImagesBinding
        reviewsViewModel = ViewModelProviders.of(this).get(RatingReviewsViewModel::class.java)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager1 = LinearLayoutManager(this)
        reviewsBinding.commonToolBar.imgRight.visibility = View.GONE
        reviewsBinding.commonToolBar.imgRight.setImageResource(R.drawable.ic_cart)
        reviewsBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.rating)
        reviewsBinding.reviewsViewModel = reviewsViewModel
        orderId = intent.extras?.get("orderId").toString()
        val from = intent.extras?.get("from").toString()


        imagesListAdapter = ImagesListAdapter(null, this, imagesList, this)
        reviewsBinding.rvImages.setHasFixedSize(true)
        linearLayoutManager1.orientation = RecyclerView.HORIZONTAL
        reviewsBinding.rvImages.layoutManager = linearLayoutManager1
        reviewsBinding.rvImages.adapter = imagesListAdapter
        reviewsBinding.rvImages.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
        //ratingReviewInput[0].orderId = orderId
        // reviewsBinding.btnSubmit.visibility = View.INVISIBLE
        /*reviewsBinding.btnSubmit.setBackgroundTintList(
            ColorStateList.valueOf(
                Color.parseColor(
                    GlobalConstants.COLOR_CODE
                )
            )*//*mContext.getResources().getColorStateList(R.color.colorOrange)*//*
        )*/

        // initRecyclerView()

        //UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        reviewsViewModel.addImagesRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            showToastSuccess(message)
                            finish()
                            //ratingReviewInput
                            // var ratingReviewInput = RatingReviewListInput("orderId", null)

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            //reviewsBinding.rvReviews.visibility = View.GONE
                            //reviewsBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })

        reviewsViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "imgAddImage" -> {
                        if (checkAndRequestPermissions()) {
                            confirmationDialog =
                                mDialogClass.setUploadConfirmationDialog(this, this, "gallery")
                        }
                    }
                    "imgBack" -> {
                        onBackPressed()
                    }
                    "btnSubmit" -> {
                        if (imagesList.size <= 0) {
                            showToastError("Please add at least one image")
                        } else {
                            val mHashMap = HashMap<String, RequestBody>()
                            mHashMap["companyId"] =
                                Utils(this).createPartFromString(GlobalConstants.COMPANY_ID)
                            mHashMap["mediaType"] = Utils(this).createPartFromString("photo")
                            //   mHashMap["review"] =Utils(this).createPartFromString(reviewsBinding.edtDescription.getText().toString())

                            // mHashMap["rating"] = Utils(this).createPartFromString("0")
                            //  mHashMap["ratingData"] =Utils(this).createPartFromString(ratingData.ratingData.toString())

                            if (imagesList.size > 0) {
                                imagesParts = arrayOfNulls<MultipartBody.Part>(imagesList.count())
                                for (i in 0 until imagesList.count()) {
                                    val f1 = File(imagesList[i])
                                    imagesParts!![i] = Utils(this).prepareFilePart("media", f1)

                                }
                            }

                            if (UtilsFunctions.isNetworkConnected()) {
                                startProgressDialog()
                                reviewsViewModel.addImages(
                                    imagesParts,
                                    mHashMap
                                )
                            }
                        }

                    }
                }
            })
        )


    }


    override fun photoFromCamera(mKey: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri =
                        FileProvider.getUriForFile(this, packageName + ".fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            profileImage = absolutePath
        }
    }

    override fun photoFromGallery(mKey: String) {
        val i = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun videoFromCamera(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            profileImage = picturePath
            setImage(picturePath)
            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            setImage(profileImage)            // val extras = data!!.extras
            // val imageBitmap = extras!!.get("data") as Bitmap
            //getImageUri(imageBitmap)
        }

    }

    private fun setImage(path: String) {
        imagesList.add(path)
        if (imagesList.size < 4) {
            reviewsBinding.imgAddImage.visibility = View.VISIBLE
        } else {
            reviewsBinding.imgAddImage.visibility = View.GONE
        }
        imagesListAdapter?.notifyDataSetChanged()
        /*  Glide.with(this)
              .load(path)
              .placeholder(R.drawable.user)
              .into(profileBinding.imgProfile)*/
    }

    fun removeImage(pos: Int) {
        imagesList.removeAt(pos)
        if (imagesList.size < 4) {
            reviewsBinding.imgAddImage.visibility = View.VISIBLE
        } else {
            reviewsBinding.imgAddImage.visibility = View.GONE
        }
        imagesListAdapter?.notifyDataSetChanged()
        /*  Glide.with(this)
              .load(path)
              .placeholder(R.drawable.user)
              .into(profileBinding.imgProfile)*/
    }


}
