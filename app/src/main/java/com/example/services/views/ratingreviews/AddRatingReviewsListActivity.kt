package com.example.services.views.ratingreviews

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
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
import com.example.services.databinding.ActivityReviewsListBinding
import com.example.services.R
import com.example.services.callbacks.ChoiceCallBack
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.orders.OrdersDetailResponse
import com.example.services.model.ratnigreviews.RatingData
import com.example.services.model.ratnigreviews.RatingReviewListInput
import com.example.services.utils.BaseActivity
import com.example.services.utils.DialogClass
import com.example.services.utils.DialogssInterface
import com.example.services.utils.Utils
import com.example.services.viewmodels.home.RatingInfo
import com.example.services.viewmodels.ratingreviews.RatingReviewsViewModel
import com.google.gson.JsonObject
import com.uniongoods.adapters.AddRatingReviewsListAdapter
import com.uniongoods.adapters.ImagesListAdapter
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddRatingReviewsListActivity : BaseActivity(), DialogssInterface, ChoiceCallBack {
    lateinit var reviewsBinding: ActivityReviewsListBinding
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
        return R.layout.activity_reviews_list
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        confirmationDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "Rating",
            getString(R.string.warning_rate_cancel)
        )
        confirmationDialog?.show()
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

        reviewsBinding = viewDataBinding as ActivityReviewsListBinding
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

        if (from.equals("list")) {
            if (UtilsFunctions.isNetworkConnected()) {
                reviewsViewModel.orderDetail(orderId)
                startProgressDialog()
            }
        } else {
            val comapnyName = intent.extras?.get("name").toString()
            val image = intent.extras?.get("image").toString()
            companyId = intent.extras?.get("id").toString()
            reviewsBinding.txtRestname.setText(comapnyName)
            val mJsonObject = intent.getSerializableExtra("data") as? RatingInfo
            // val mJsonObject = JSONObject(intent.extras?.get("data")?.toString())

            Glide.with(this).load(image)
                .into(reviewsBinding.imgRest)


            if (mJsonObject != null) {
                val restRating = mJsonObject?.rating//mJsonObject.get("rating").toString()
                val review = mJsonObject?.review//mJsonObject.get("review").toString()
                if (!TextUtils.isEmpty(restRating)) {
                    reviewsBinding.rBar.setRating(mJsonObject?.rating!!.toFloat()/*restRating?.toFloat()*/)
                }
                reviewsBinding.rbQuality.setRating(mJsonObject?.foodQuality!!.toFloat()/*restRating?.toFloat()*/)
                reviewsBinding.rbMoneyValue.setRating(mJsonObject?.foodQuantity!!.toFloat()/*restRating?.toFloat()*/)
                reviewsBinding.rbPackaging.setRating(mJsonObject?.packingPres!!.toFloat()/*restRating?.toFloat()*/)
                reviewsBinding.edtReviews.setText(review)
            }


/*mHashMap["foodQuality"] =
                Utils(this).createPartFromString(reviewsBinding.rbQuality.getRating().toString())
            mHashMap["foodQuantity"] =
                Utils(this).createPartFromString(reviewsBinding.rbMoneyValue.getRating().toString())
            mHashMap["packingPres"] =
                Utils(this).createPartFromString(reviewsBinding.rbPackaging.getRating().toString())*/


            reviewsBinding.txtItemsRating.visibility = View.GONE
            reviewsBinding.rvReviews.visibility = View.GONE

        }
        imagesListAdapter = ImagesListAdapter(this, null, imagesList, this)
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

        UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        reviewsViewModel.getOrderDetail().observe(this,
            Observer<OrdersDetailResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    mLoadMoreViewCheck = true
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //ratingReviewInput
                            // var ratingReviewInput = RatingReviewListInput("orderId", null)
                            reviewsBinding.txtRestname.setText(response.data?.company?.companyName)
                            companyId = response.data?.companyId.toString()
                            Glide.with(this).load(response.data?.company?.logo1)
                                .into(reviewsBinding.imgRest)
                            ratingData.orderId = response.data?.id
                            suborders?.clear()
                            suborders = response.data?.suborders
                            if (suborders?.size!! > 0) {
                                for (item in suborders!!) {
                                    val rating = RatingData()
                                    rating.rating = "1"
                                    rating.review = ""
                                    rating.name = item.service?.name
                                    rating.icon = item.service?.icon
                                    rating.serviceId = item.service?.id
                                    rating.price = item.service?.price
                                    ratingData.ratingData?.add(rating)
                                }
                                initRecyclerView()
                                reviewsBinding.rvReviews.visibility = View.VISIBLE
                                reviewsBinding.tvNoRecord.visibility = View.GONE
                                reviewsAdapter?.notifyDataSetChanged()
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            //reviewsBinding.rvReviews.visibility = View.GONE
                            //reviewsBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })

        reviewsViewModel.getRatingRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    mLoadMoreViewCheck = true
                    val message = response.message
                    when {
                        response.code == 200 -> {

                            showToastSuccess(message)
                            finish()
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
/*["review": "Niceeeee", "ratingData": [], "rating": "5.0", "companyId": "6b9a100c-f8ca-484a-a29d-ecb4bd5e7b00", "orderId": "", "foodQuality": "0.0", "foodQuantity": "0.0", "packingPres": "0.0", "images": []]*/
                        val mHashMap = HashMap<String, RequestBody>()
                        mHashMap["foodQuality"] =
                            Utils(this).createPartFromString(reviewsBinding.rbQuality.getRating().toString())
                        mHashMap["foodQuantity"] =
                            Utils(this).createPartFromString(reviewsBinding.rbMoneyValue.getRating().toString())
                        mHashMap["packingPres"] =
                            Utils(this).createPartFromString(reviewsBinding.rbPackaging.getRating().toString())

                        mHashMap["companyId"] =
                            Utils(this).createPartFromString(companyId)
                        mHashMap["orderId"] =
                            Utils(this).createPartFromString(orderId.toString())
                        mHashMap["rating"] =
                            Utils(this).createPartFromString(reviewsBinding.rBar.getRating().toString())
                        mHashMap["review"] =
                            Utils(this).createPartFromString(reviewsBinding.edtReviews.getText().toString())

                        // mHashMap["rating"] = Utils(this).createPartFromString("0")
                        //  mHashMap["ratingData"] =Utils(this).createPartFromString(ratingData.ratingData.toString())


                        if (imagesList.size > 0) {
                            imagesParts = arrayOfNulls<MultipartBody.Part>(imagesList.count())
                            for (i in 0 until imagesList.count()) {
                                val f1 = File(imagesList[i])
                                imagesParts!![i] = Utils(this).prepareFilePart("images", f1)

                            }
                        }
                        // val contributorsMap: HashMap<String, String> = HashMap()
                        val ratingDataArray = ArrayList<JsonObject>()
                        if (ratingData.ratingData.size > 0) {
                            for (i in 0 until ratingData.ratingData.count()) {
                                val obj = JsonObject()
                                obj.addProperty("rating", ratingData.ratingData[i].rating)
                                obj.addProperty("serviceId", ratingData.ratingData[i].serviceId)
                                ratingDataArray.add(obj)

                                /* contributorsMap["rating[${i}]"] =
                                     ratingData.ratingData[i].rating.toString()
                                 contributorsMap["serviceId[${i}]"] =
                                     ratingData.ratingData[i].serviceId.toString()*/

                            }
                        }

                        mHashMap["ratingData"] =
                            Utils(this).createPartFromString(ratingDataArray.toString())

                        if (UtilsFunctions.isNetworkConnected()) {
                            startProgressDialog()
                            reviewsViewModel.addRatings(
                                ratingData,
                                imagesParts,
                                /*contributorsMap,*/
                                mHashMap
                            )
                        }

                    }
                }
            })
        )

        reviewsBinding.rBar.setOnRatingBarChangeListener(object :
            RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                // Toast.makeText(this, "Given rating is: $p1", Toast.LENGTH_SHORT).show()
                // showToastSuccess("Given rating is: $p1")
            }
        })
    }

    fun updateRating(position: Int, rating: String) {
        //ratingData.ratingData.get(position).review = etReview!!.getText().toString().trim()
        ratingData.ratingData.get(position).rating = rating// ratingBar!!.rating.toString()
        //  reviewsBinding.btnSubmit.visibility = View.INVISIBLE
        reviewsAdapter?.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        reviewsAdapter = AddRatingReviewsListAdapter(this, ratingData, this)
        reviewsBinding.rvReviews.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        reviewsBinding.rvReviews.layoutManager = linearLayoutManager
        reviewsBinding.rvReviews.adapter = reviewsAdapter
        reviewsBinding.rvReviews.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun addRating(position: Int) {
        confirmationDialog = Dialog(this, R.style.transparent_dialog_borderless)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.add_rating_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(true)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val serviceImage = confirmationDialog?.findViewById<ImageView>(R.id.iv_service_image)
        val serviceName = confirmationDialog?.findViewById<TextView>(R.id.tv_service_name)
        val ratingBar = confirmationDialog?.findViewById<RatingBar>(R.id.rb_ratings)
        val etReview = confirmationDialog?.findViewById<EditText>(R.id.et_review)
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)

        etReview!!.setText(ratingData.ratingData.get(position).review)
        ratingBar!!.setRating(ratingData.ratingData.get(position).rating!!.toFloat())
        serviceName?.setText(ratingData.ratingData.get(position).name)
        Glide.with(this)
            .load(ratingData.ratingData.get(position).icon)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .placeholder(R.drawable.ic_category)
            .into(serviceImage!!)
        //btnSubmit?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(GlobalConstants.COLOR_CODE))/*mContext.getResources().getColorStateList(R.color.colorOrange)*/)

        btnSubmit?.setOnClickListener {
            ratingData.ratingData.get(position).review = etReview!!.getText().toString().trim()
            ratingData.ratingData.get(position).rating = ratingBar!!.rating.toString()
            reviewsBinding.btnSubmit.visibility = View.INVISIBLE
            reviewsAdapter?.notifyDataSetChanged()
            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
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
