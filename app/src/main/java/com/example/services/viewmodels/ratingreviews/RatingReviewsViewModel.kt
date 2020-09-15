package com.example.services.viewmodels.ratingreviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.example.services.common.UtilsFunctions
import com.example.services.model.CommonModel
import com.example.services.model.LoginResponse
import com.example.services.model.address.AddressResponse
import com.example.services.model.orders.OrdersDetailResponse
import com.example.services.model.ratnigreviews.RatingReviewListInput
import com.example.services.model.ratnigreviews.ReviewsListResponse
import com.example.services.repositories.ratingreviews.RatingReviewsRepository
import com.example.services.viewmodels.BaseViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap

class RatingReviewsViewModel : BaseViewModel() {
    private var data: MutableLiveData<LoginResponse>? = null
    private var addressDetail = MutableLiveData<AddressResponse>()
    private var deleteAddress = MutableLiveData<CommonModel>()

    private var reviewsList = MutableLiveData<ReviewsListResponse>()
    private var orderDetail = MutableLiveData<OrdersDetailResponse>()
    private var ratingReview = MutableLiveData<CommonModel>()
    private var addImages = MutableLiveData<CommonModel>()
    private var ratingReviewsRepository = RatingReviewsRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            addImages = ratingReviewsRepository.addImages(
                null,
                null
            )
            reviewsList = ratingReviewsRepository.reviewsListList("", "")
            orderDetail = ratingReviewsRepository.getOrderDetail("")
            ratingReview = ratingReviewsRepository.addRatings(
                null,
                null,
                null,
                null
            )
        }
    }

    fun addImagesRes(): LiveData<CommonModel> {
        return addImages
    }

    fun getReviewsRes(): LiveData<ReviewsListResponse> {
        return reviewsList
    }

    fun getOrderDetail(): LiveData<OrdersDetailResponse> {
        return orderDetail
    }

    fun getRatingRes(): LiveData<CommonModel> {
        return ratingReview
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun getReviewsList(id: String, page: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            reviewsList = ratingReviewsRepository.reviewsListList(id, page)
            mIsUpdating.postValue(true)
        }

    }

    fun orderDetail(id: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            orderDetail = ratingReviewsRepository.getOrderDetail(id)
            mIsUpdating.postValue(true)
        }

    }

    fun addRatings(
        params: RatingReviewListInput,
        imagesParts: Array<MultipartBody.Part?>?,
        contributorsMap: HashMap<String, String>,
        mHashMap: HashMap<String, RequestBody>
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            ratingReview =
                ratingReviewsRepository.addRatings(params, imagesParts, contributorsMap, mHashMap)
            mIsUpdating.postValue(true)
        }

    }

    fun addImages(
        imagesParts: Array<MultipartBody.Part?>?,
        mHashMap: HashMap<String, RequestBody>
    ) {
        if (UtilsFunctions.isNetworkConnected()) {
            ratingReview = ratingReviewsRepository.addImages(imagesParts, mHashMap)
            mIsUpdating.postValue(true)
        }

    }


}