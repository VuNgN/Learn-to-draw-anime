package com.vungn.application.ui.fragment.home

import com.vungn.application.databinding.FragmentHomeBinding
import com.vungn.application.ui.base.BaseFragment
import com.vungn.application.ui.viewmodel.FragmentHomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, FragmentHomeViewModel>() {
    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun getViewModelClass(): Class<FragmentHomeViewModel> = FragmentHomeViewModel::class.java

    override fun setupListener() {}

    override fun setupViews() {}
}