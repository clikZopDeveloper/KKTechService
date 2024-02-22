package com.example.kk_services.Fragment.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kk_services.Activity.*
import com.example.kk_services.Adapter.DashboardAdapter
import com.example.kk_services.ApiHelper.ApiController
import com.example.kk_services.ApiHelper.ApiResponseListner
import com.example.kk_services.Model.*
import com.example.kk_services.R
import com.example.kk_services.Utills.GeneralUtilities
import com.example.kk_services.Utills.PrefManager
import com.example.kk_services.Utills.RvStatusClickListner
import com.example.kk_services.Utills.SalesApp
import com.example.kk_services.Utills.Utility
import com.example.kk_services.databinding.FragmentHomeBinding
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class HomeFragment : Fragment(), ApiResponseListner {
    private lateinit var apiClient: ApiController
    private var _binding: FragmentHomeBinding? = null

    var rejected = ""
    var pending = ""
    var accepted = ""


    var pendingComplaint = ""
    var completedComplaint = ""
    var rejectedComplaint = ""
    var processingComplaint = ""

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding.refreshLayout.setOnRefreshListener {
            apiCallDashboard()
            binding.refreshLayout.isRefreshing = false
        }

        apiCallDashboard()




        binding.CardInsert.setOnClickListener {
            callPGURL("https://atulautomotive.online/dealer-signup")

        }

        binding.CardInsert2.setOnClickListener {
            //  callPGURL("https://atulautomotive.online/architect-signup")

        }

        binding.fbAddArchitect.setOnClickListener {
            //  callPGURL("https://atulautomotive.online/architect-signup")

        }


        //    val textView: TextView = binding.textHome
        /*   homeViewModel.text.observe(viewLifecycleOwner) {
               textView.text = it
           }*/

        return root
    }


    fun apiCallDashboard() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        params["username"] = PrefManager.getString(ApiContants.mobileNumber, "")
        params["password"] = PrefManager.getString(ApiContants.password, "")
        // apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.dashboard, params)

    }


    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.logout) {
                val baseResponseBean = apiClient.getConvertIntoModel<BaseResponseBean>(
                    jsonElement.toString(),
                    BaseResponseBean::class.java
                )
                Toast.makeText(activity, baseResponseBean.msg, Toast.LENGTH_SHORT).show()
                PrefManager.clear()
                GeneralUtilities.launchActivity(
                    requireContext() as AppCompatActivity?,
                    LoginActivity::class.java
                )
                requireActivity().finishAffinity()
            }

            if (tag == ApiContants.dashboard) {
                val dashboardBean = apiClient.getConvertIntoModel<DashboardBean>(
                    jsonElement.toString(),
                    DashboardBean::class.java
                )

                Toast.makeText(activity, dashboardBean.msg, Toast.LENGTH_SHORT).show()
                if (dashboardBean.error == false) {

                    if (!dashboardBean.data.allocatedRequest.pending.isNullOrEmpty()) {
                        pending = dashboardBean.data.allocatedRequest.pending
                    } else {
                        pending = "0"
                    }
                    if (!dashboardBean.data.allocatedRequest.accepted.isNullOrEmpty()) {

                        accepted = dashboardBean.data.allocatedRequest.accepted
                    } else {
                        accepted = "0"
                    }
                    if (!dashboardBean.data.allocatedRequest.rejected.isNullOrEmpty()) {
                        rejected = dashboardBean.data.allocatedRequest.rejected
                    } else {
                        rejected = "0"
                    }



                    if (!dashboardBean.data.complaints.pending.isNullOrEmpty()) {
                        pendingComplaint = dashboardBean.data.complaints.pending
                    } else {
                        pendingComplaint = "0"
                    }
                    if (!dashboardBean.data.complaints.completed.isNullOrEmpty()) {

                        completedComplaint = dashboardBean.data.complaints.completed
                    } else {
                        completedComplaint = "0"
                    }
                    if (!dashboardBean.data.complaints.rejected.isNullOrEmpty()) {
                        rejectedComplaint = dashboardBean.data.complaints.rejected
                    } else {
                        rejectedComplaint = "0"
                    }
                    if (!dashboardBean.data.complaints.processing.isNullOrEmpty()) {
                        processingComplaint = dashboardBean.data.complaints.processing
                    } else {
                        processingComplaint = "0"
                    }
                    handleRcComplaint()
                    handleRcDashboard()
                }

            }

        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }
    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(requireActivity(), errorMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        apiCallDashboard()
    }

    fun handleRcDashboard() {
        binding.rcDashboard.layoutManager = GridLayoutManager(requireContext(), 2)
        var mAdapter = DashboardAdapter(requireActivity(), getMenus(), object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {
                if (pos == 0) {
                    startActivity(
                        Intent(
                            context,
                            AddExpensesActivity::class.java
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            context,
                            AllAlocteReqActivity::class.java
                        ).putExtra("Status", status)
                    )
                }

            }
        })
        binding.rcDashboard.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    private fun getMenus(): ArrayList<MenuModelBean> {
        var menuList = ArrayList<MenuModelBean>()

        menuList.add(MenuModelBean(0, "Add Expenses", "", R.drawable.ic_dashbord))
        menuList.add(MenuModelBean(1, "Pending", pending, R.drawable.ic_dashbord))
        menuList.add(MenuModelBean(2, "Accepted", accepted, R.drawable.ic_dashbord))
        menuList.add(
            MenuModelBean(
                3,
                "Rejected",
                rejected,
                R.drawable.ic_dashbord
            )
        )


        return menuList
    }


    fun handleRcComplaint() {
        binding.rcDashComplaint.layoutManager = GridLayoutManager(requireContext(), 2)
        var mAdapter = DashboardAdapter(requireActivity(), getMenusComplaint(), object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {
                startActivity(
                    Intent(
                        context,
                        AllComplaintsActivity::class.java
                    ).putExtra("Status", status)
                )

            }
        })
        binding.rcDashComplaint.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    private fun getMenusComplaint(): ArrayList<MenuModelBean> {
        var menuList = ArrayList<MenuModelBean>()

        menuList.add(MenuModelBean(1, "Pending", pendingComplaint, R.drawable.ic_dashbord))
        menuList.add(MenuModelBean(2, "Processing", processingComplaint, R.drawable.ic_dashbord))
        menuList.add(
            MenuModelBean(
                3,
                "Completed",
                completedComplaint,
                R.drawable.ic_dashbord
            )
        )
        menuList.add(
            MenuModelBean(
                3,
                "Rejected",
                rejectedComplaint,
                R.drawable.ic_dashbord
            )
        )


        return menuList
    }

    fun callPGURL(url: String) {
        Log.d("weburl", url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null)
            startActivity(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
      //  requireActivity().startService(Intent(requireActivity(), LocationService::class.java))
    }
}