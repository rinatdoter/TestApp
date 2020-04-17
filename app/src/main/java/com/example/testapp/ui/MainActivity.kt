package com.example.testapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.testapp.R
import com.example.testapp.extension.showToast
import com.example.testapp.model.Event.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.getViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm = getViewModel(MainVM::class)
        setupViews()
        subscribeToLiveData()
    }

    private fun setupViews() {
        btn_send.setOnClickListener { onSendButtonClick() }
    }

    @SuppressLint("CheckResult")
    private fun onSendButtonClick(){
        /** In real project the case when user choses "dont show again should be handled"*/
        RxPermissions(this)
            .request(Manifest.permission.READ_CALL_LOG)
            .subscribe { isGranted ->
                if(isGranted) vm.getLastCallAndSendToApi()
            }
    }

    private fun subscribeToLiveData(){
        vm.event.observe(this, Observer{
            when(it){
                is CdrFetched -> tv_call_details.text = it.cdr.toString()
                is ShowToast -> showToast(it.message)
                /** In real project these cases should be moved to BaseVM*/
                is ShowLoading -> showLoading()
                is HideLoading -> hideLoading()
            }
        })
    }

    /** In real project these methods should be moved to BaseActivity*/
    private fun showLoading() { layout_progress.visibility = View.VISIBLE }
    private fun hideLoading() { layout_progress.visibility = View.GONE }
}
