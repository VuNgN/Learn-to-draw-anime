package com.vungn.application.ui.activity

import com.example.ardrawsketch.base.BaseActivity
import com.vungn.application.R
import com.vungn.application.databinding.ActivityMainBinding
import com.vungn.application.ui.viewmodel.ActivityMainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, ActivityMainViewModel>(R.id.nav_host, R.navigation.nav_main) {
    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<ActivityMainViewModel> =
        ActivityMainViewModel::class.java

    override fun setupListener() {}

    override fun setupViews() {}
}