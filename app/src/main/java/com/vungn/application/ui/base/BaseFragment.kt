package com.vungn.application.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.ardrawsketch.ui.custom.dialog.LoadingDialog
import com.vungn.application.util.ads.InterAd
import com.vungn.application.util.firebase.RemoteKey
import icepick.Icepick

/**
 * This is a base fragment class that all other fragments in the application extend.
 * It provides common functionality for all fragments, such as setting up navigation and view binding.
 *
 * @param Binding The type of ViewBinding for the fragment. This is used to bind the views in the fragment's layout.
 * @param ViewModel The type of ViewModel for the fragment. This is used to manage the data for the fragment.
 *
 * The class includes abstract methods that must be implemented by subclasses:
 * - getViewBinding(): This method should return the ViewBinding for the fragment.
 * - getViewModelClass(): This method should return the Class object for the ViewModel.
 * - setupListener(): This method should set up any listeners for the fragment.
 * - setupViews(): This method should set up the views for the fragment.
 *
 * The class also includes a method for navigating back in the navigation stack.
 * @author Nguyễn Ngọc Vũ
 */
abstract class BaseFragment<Binding : ViewBinding, ViewModel : androidx.lifecycle.ViewModel> :
    Fragment() {
    protected var loadingDialog: Dialog? = null

    /**
     * The ViewBinding for the fragment.
     */
    protected lateinit var binding: Binding

    /**
     * This method should return the ViewBinding for the fragment.
     */
    protected abstract fun getViewBinding(): Binding

    /**
     * The ViewModel for the fragment.
     */
    protected lateinit var viewModel: ViewModel

    /**
     * This method should return the Class object for the ViewModel.
     */
    protected abstract fun getViewModelClass(): Class<ViewModel>

    /**
     * The NavController for the fragment's navigation graph.
     */
    protected lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        retainInstance = true
        init()
        setupViews()
        setupListener()
    }

    private fun init() {
        binding = getViewBinding()
        viewModel = ViewModelProvider(requireActivity())[getViewModelClass()]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null && !savedInstanceState.isEmpty) {
            Icepick.restoreInstanceState(this, savedInstanceState)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        InterAd.loadAd(requireActivity())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    /**
     * Handle the back button press.
     */
    open fun onBackPressed() {
        if (navController.previousBackStackEntry == null) {
            finishActivity(!requireActivity().isTaskRoot)
        } else {
            popBack()
        }
    }

    /**
     * This method should set up any listeners for the fragment.
     */
    abstract fun setupListener()

    /**
     * This method should set up the views for the fragment.
     */
    abstract fun setupViews()

    /**
     * Finish the activity.
     */
    protected fun finishActivity(isShowAd: Boolean = true) {
        if (!isShowAd) {
            requireActivity().finish()
            return
        }
        showInterAdAndThen(RemoteKey.INTER_BACK_STATUS) {
            requireActivity().finish()
        }
    }

    /**
     * Navigate back in the navigation stack.
     */
    protected fun popBack(isShowAd: Boolean = true) {
        if (!isShowAd) {
            navController.popBackStack()
            return
        }
        showInterAdAndThen(RemoteKey.INTER_BACK_STATUS) {
            navController.popBackStack()
        }
    }

    protected fun showAdAndNavigate(action: NavDirections) {
        showInterAdAndThen(RemoteKey.INTER_HOME_STATUS) {
            navController.navigate(action)
        }
    }

    protected fun showInterAdAndThen(key: String, action: () -> Unit) {
        InterAd.showInterstitial(requireActivity(), key = key, onClose = {
            loadingDialog?.cancel()
            action()
        }, onAdNotLoaded = {
            InterAd.loadAd(requireActivity(), onLoaded = {
                InterAd.showInterstitial(requireActivity(), key = key, onClose = {
                    loadingDialog?.cancel()
                    action()
                }, onAdNotLoaded = {
                    loadingDialog?.cancel()
                    action()
                })
            }, onLoading = {
                showLoadingDialog()
            })
        })
    }

    protected fun showLoadingDialog() {
        if (loadingDialog?.isShowing == true) return
        if (loadingDialog != null) {
            loadingDialog?.show()
            return
        }
        loadingDialog = Dialog(requireActivity())
        loadingDialog?.setContentView(LoadingDialog(requireActivity()))
        loadingDialog?.setCancelable(false)
        loadingDialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        loadingDialog?.show()
    }
}