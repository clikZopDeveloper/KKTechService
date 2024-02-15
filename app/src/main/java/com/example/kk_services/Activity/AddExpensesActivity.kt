package com.example.kk_services.Activity

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil

import com.example.kk_services.ApiHelper.ApiController
import com.example.kk_services.ApiHelper.ApiResponseListner
import com.example.kk_services.Model.DashboardBean

import com.example.kk_services.R
import com.example.kk_services.Utills.*
import com.example.kk_services.databinding.ActivityAddExpensesBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


class AddExpensesActivity : AppCompatActivity(), ApiResponseListner,
    GoogleApiClient.OnConnectionFailedListener,
    ConnectivityListener.ConnectivityReceiverListener {
    private lateinit var binding: ActivityAddExpensesBinding

    val imgList: MutableList<File> = ArrayList()
    private lateinit var apiClient: ApiController

    private var calendar: Calendar? = null
    var myReceiver: ConnectivityListener? = null
    var fromDate = ""


    var projectType = "TA"
    var activity: Activity = this
    val PERMISSION_CODE = 12345
    val CAMERA_PERMISSION_CODE1 = 123
    var SELECT_PICTURES1 = 1
    var file2: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_expenses)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        myReceiver = ConnectivityListener()
        binding.igToolbar.tvTitle.text = "Add Expenses"
        binding.igToolbar.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.igToolbar.ivMenu.setOnClickListener { finish() }
        binding.igToolbar.ivLogout.visibility = View.GONE
        binding.igToolbar.switchDayStart.visibility = View.GONE

       // leadStatus = intent.getStringExtra("leadStatus")!!
          requestPermission()
    //    typeMode()

        calendar = Calendar.getInstance();
        val hour: Int = calendar!!.get(Calendar.HOUR_OF_DAY)
        val min: Int = calendar!!.get(Calendar.MINUTE)

        //   callCityListAdapter()

        binding.apply {

            btnUplaodImages.setOnClickListener {
                openCameraDialog(SELECT_PICTURES1,CAMERA_PERMISSION_CODE1)
            }

            btnSubmit.setOnClickListener {
                apiAddExpenses()
            }

            typeMode()
            editDate.setOnClickListener(View.OnClickListener {
                val c = Calendar.getInstance()
                val year = c[Calendar.YEAR]
                val month = c[Calendar.MONTH]
                val day = c[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                    this@AddExpensesActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        //  dob.setText(dateofnews);

                    //    val dateofnews = "${dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year}"
                        val dateofnews = "${ year.toString()+ "-"+(monthOfYear + 1).toString()  + "-" + dayOfMonth.toString() }"

                        //   val dateofnews = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year

                        editDate.setText(dateofnews)
                    },
                    year, month, day
                )
                datePickerDialog.show()
            })


        }
    }

    fun typeMode() {
        binding.radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId == R.id.rbTA) {
                    projectType = "TA"
                } else if (checkedId == R.id.rbDA) {
                    projectType = "DA"

                }else if (checkedId == R.id.rbHotels) {
                    projectType = "Hotels"

                }else if (checkedId == R.id.rbOthers) {
                    projectType = "Others"

                }
            }
        })
    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.CreateExpense) {
                val updateLeadBean = apiClient.getConvertIntoModel<DashboardBean>(
                    jsonElement.toString(),
                    DashboardBean::class.java
                )

                Toast.makeText(this, updateLeadBean.msg, Toast.LENGTH_SHORT).show()
                finish()
            }


        }catch (e:Exception){
            Log.d("error>>",e.localizedMessage)
        }

    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()

        Utility.showSnackBar(activity, errorMessage)
        Log.d("error",errorMessage)

    }

    fun ClickPicCamera(CAMERA_PERMISSION_CODE: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_PERMISSION_CODE)
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES),
            PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==PERMISSION_CODE){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission is Granted",Toast.LENGTH_SHORT).show()

            }
            else{
                requestPermission()
            }
        }
    }

    private fun uploadImage(SELECT_PICTURES: Int) {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , SELECT_PICTURES
            )
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_PICTURES);
        }
    }

    fun openCameraDialog(SELECT_PICTURES: Int,CAMERA_PERMISSION_CODE: Int) {
        val dialog: Dialog = GeneralUtilities.openBootmSheetDailog(R.layout.dialog_camera, R.style.AppBottomSheetDialogTheme,
            this
        )
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val llInternalPhoto = dialog.findViewById<View>(R.id.llInternalPhoto) as LinearLayout
        val llClickPhoto = dialog.findViewById<View>(R.id.llClickPhoto) as LinearLayout

        llInternalPhoto.setOnClickListener { dialog.dismiss()
            requestPermission()
            uploadImage(SELECT_PICTURES)
        }

        llClickPhoto.setOnClickListener { dialog.dismiss()
            requestPermission()
            ClickPicCamera(CAMERA_PERMISSION_CODE)

        }
        ivClose.setOnClickListener { dialog.dismiss() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURES1){
            if (data?.getClipData() != null) { // if multiple images are selected
                var count = data.clipData?.itemCount
                binding.tvImageCount.visibility=View.VISIBLE
                binding.tvImageCount.text="$count Images"
                Log.d("wewwe", "$count")
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    val picturePath: String = GeneralUtilities.getPath(
                        applicationContext, imageUri
                    )
                    file2 = File(picturePath)
                    //val custImg = CustProdImgBean(file2)
                    imgList.add(file2!!)
                 //   Log.d("MultiPicturePath", picturePath)

                    //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                }

            } else if (data?.getData() != null) {   // if single image is selected
                binding.tvImageCount.visibility=View.GONE
                var imageUri: Uri = data.data!!
                val picturePath: String = GeneralUtilities.getPath(
                    applicationContext, imageUri)
                file2 = File(picturePath)
                val myBitmap = BitmapFactory.decodeFile(file2!!.absolutePath)
                binding.btnAadharFront.setImageBitmap(myBitmap)
                imgList.add(file2!!)

                Log.d("SinglePicturePath", picturePath)
                //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
            }

        }
         if (requestCode == CAMERA_PERMISSION_CODE1) {
            try {
                Toast.makeText(this@AddExpensesActivity,"sdfsd",Toast.LENGTH_SHORT).show()

                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.btnAadharFront.setImageBitmap(imageBitmap)
                val tempUri =GeneralUtilities.getImageUri(applicationContext, imageBitmap)
                file2= File(GeneralUtilities.getRealPathFromURII(this,tempUri))
                imgList.add(file2!!)
                Log.e("Path", file2.toString())

                //Toast.makeText(getContext(), ""+picturePath, Toast.LENGTH_SHORT).show();
            } catch (e: java.lang.Exception) {
                Log.e("Path Error", e.toString())
                Toast.makeText(applicationContext, "" + e, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun apiAddExpenses() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("title", binding.editTitle.text.toString())
        builder.addFormDataPart("amount",  binding.editAmount.text.toString())
        builder.addFormDataPart("exp_date",  binding.editDate.text.toString())
        builder.addFormDataPart("category", projectType)
        builder.addFormDataPart("comments",  binding.editComments.text.toString())

         for (i in 0 until imgList.size) {
          builder.addFormDataPart("files[]", imgList.get(i).name,
              RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imgList.get(i)))
      }

        Log.d("requestParms", Gson().toJson(builder))
        apiClient.progressView.showLoader()
        apiClient.makeCallMultipart(ApiContants.CreateExpense, builder.build())

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
