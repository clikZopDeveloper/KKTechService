package com.example.kk_services.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kk_services.Adapter.AllAlocateReqAdapter
import com.example.kk_services.ApiHelper.ApiController
import com.example.kk_services.ApiHelper.ApiResponseListner
import com.example.kk_services.Model.AlocateRequestBean
import com.example.kk_services.R
import com.example.kk_services.Utills.*

import com.example.kk_services.databinding.ActivityAlocateReqBinding

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class AllAlocteReqActivity : AppCompatActivity(), ApiResponseListner,
    GoogleApiClient.OnConnectionFailedListener,
    ConnectivityListener.ConnectivityReceiverListener {
    private lateinit var binding: ActivityAlocateReqBinding
    private lateinit var apiClient: ApiController
    var myReceiver: ConnectivityListener? = null
    var activity: Activity = this
    private  var editReamrk : TextInputEditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alocate_req)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        myReceiver = ConnectivityListener()

        binding.igToolbar.tvTitle.text = "Allocate Request"
        binding.igToolbar.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.igToolbar.ivMenu.setOnClickListener { finish() }
        binding.igToolbar.ivLogout.visibility = View.GONE
        binding.igToolbar.switchDayStart.visibility = View.GONE

        intent.getStringExtra("Status")?.let { apiAllAlocateReq(it) }


    }

    fun apiAllAlocateReq(status: String) {


        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["status"] = status
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getAllocationRequest, params)

    }
    fun apiAccept(status: String, id: Int) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["status"] = status
        params["id"] = id.toString()
        params["comment"] = editReamrk?.text.toString()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getUpdateAllocateRequest, params)

    }

    fun handleAlocateReq(data: List<AlocateRequestBean.Data>) {
        binding.rcOfficeTeam.layoutManager = LinearLayoutManager(this)
        var mAdapter = AllAlocateReqAdapter(this, data, object :
            RvStatusComplClickListner {
            override fun clickPos(status: String,workstatus: String,amt: String, id: Int) {
                 if (workstatus.equals("rejected")){
                    dialogRemark(status,workstatus,id)
                }else{
                     dialog(status,id)

                 }
            }
        })
        binding.rcOfficeTeam.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }
    fun dialog(status: String,ids: Int) {

        val builder = AlertDialog.Builder(this@AllAlocteReqActivity)
        builder.setMessage("Are you sure you want to start service?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Delete selected note from database

                apiAccept(status,ids)
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }
    override fun success(tag: String?, jsonElement: JsonElement?) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.getAllocationRequest) {
                val officeTeamBean = apiClient.getConvertIntoModel<AlocateRequestBean>(
                    jsonElement.toString(),
                    AlocateRequestBean::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (officeTeamBean.error==false) {
                    handleAlocateReq(officeTeamBean.data)
                }

            }
            if (tag == ApiContants.getUpdateAllocateRequest) {
                val officeTeamBean = apiClient.getConvertIntoModel<AlocateRequestBean>(
                    jsonElement.toString(),
                    AlocateRequestBean::class.java
                )

                if (officeTeamBean.error==false) {
                     Toast.makeText(this, officeTeamBean.msg, Toast.LENGTH_SHORT).show()
                    finish()
                }

            }
        }catch (e:Exception){
            Log.d("error>>",e.localizedMessage)
        }
    }
    fun dialogRemark(status: String, workstatus: String, ids: Int) {
        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
            .create()
        val dialog = layoutInflater.inflate(R.layout.dialog_reamrk,null)

        builder.setView(dialog)

        builder.setCanceledOnTouchOutside(false)
        builder.show()
        /*    val dialog: Dialog = GeneralUtilities.openBootmSheetDailog(
                R.layout.dialog_update_client, R.style.AppBottomSheetDialogTheme,
                this
            )*/
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        editReamrk = dialog.findViewById<TextInputEditText>(R.id.editReamrk) as TextInputEditText

        val btnSubmit = dialog.findViewById<TextView>(R.id.btnSubmit) as TextView

        ivClose.setOnClickListener {  builder.dismiss() }
        btnSubmit.setOnClickListener {
            builder.dismiss()
            if (editReamrk?.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter Remark",Toast.LENGTH_SHORT).show()
            }else{
                apiAccept(status,ids)
            }

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
    override fun onDestroy() {
        super.onDestroy()
        // Start the LocationService when the app is closed
   //     startService(Intent(this, LocationService::class.java))
    }
}
