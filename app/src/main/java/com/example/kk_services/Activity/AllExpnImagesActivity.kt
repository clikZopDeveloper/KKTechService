package com.example.kk_services.Activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kk_services.databinding.ActivityAllcontactBinding
import com.example.kk_services.Adapter.AllExpensesImgAdapter
import com.example.kk_services.ApiHelper.ApiController
import com.example.kk_services.ApiHelper.ApiResponseListner
import com.example.kk_services.Model.AllExpnesesImageBean
import com.example.kk_services.R

import com.example.kk_services.Utills.ConnectivityListener
import com.example.kk_services.Utills.GeneralUtilities
import com.example.kk_services.Utills.RvStatusClickListner
import com.example.kk_services.Utills.SalesApp
import com.example.kk_services.Utills.Utility
import com.example.kk_services.databinding.ActivityAllexpImgBinding

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class AllExpnImagesActivity : AppCompatActivity(), ApiResponseListner,
    GoogleApiClient.OnConnectionFailedListener,
    ConnectivityListener.ConnectivityReceiverListener {
    private lateinit var binding: ActivityAllexpImgBinding
    private lateinit var apiClient: ApiController
    var myReceiver: ConnectivityListener? = null
    var activity: Activity = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_allexp_img)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        myReceiver = ConnectivityListener()

        binding.igToolbar.tvTitle.text = "All Expenses Image"
        binding.igToolbar.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.igToolbar.ivMenu.setOnClickListener { finish() }
        binding.igToolbar.ivLogout.visibility = View.GONE
        binding.igToolbar.switchDayStart.visibility = View.GONE

        intent.getStringExtra("id")?.let {  apiAllContact(it) }

    }

    fun apiAllContact(id: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["exp_id"] = id
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getExpenseImgs, params)

    }

    fun handleAllExpImg(data: List<AllExpnesesImageBean.Data.Img>) {
        binding.rcOfficeTeam.layoutManager = LinearLayoutManager(this)
        var mAdapter = AllExpensesImgAdapter(this, data, object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {

            }
        })
        binding.rcOfficeTeam.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }
    override fun success(tag: String?, jsonElement: JsonElement?) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.getExpenseImgs) {
                val officeTeamBean = apiClient.getConvertIntoModel<AllExpnesesImageBean>(
                    jsonElement.toString(),
                    AllExpnesesImageBean::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (officeTeamBean.error==false) {
                    handleAllExpImg(officeTeamBean.data.imgs)
                }

            }
        }catch (e:Exception){
            Log.d("error>>",e.localizedMessage)
        }



    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(this, errorMessage)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        GeneralUtilities.unregisterBroadCastReceiver(this, myReceiver)
    }

    override fun onResume() {
        GeneralUtilities.registerBroadCastReceiver(this, myReceiver)
        SalesApp.setConnectivityListener(this)
        super.onResume()
    }

    override fun onNetworkConnectionChange(isconnected: Boolean) {
        ApiContants.isconnectedtonetwork = isconnected
        GeneralUtilities.internetConnectivityAction(this, isconnected)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

}
