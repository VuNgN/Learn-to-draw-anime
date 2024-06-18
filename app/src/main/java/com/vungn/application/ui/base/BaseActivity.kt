package com.example.ardrawsketch.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.example.ardrawsketch.ui.custom.dialog.WelcomeDialog
import com.vungn.application.util.ads.AdHelper
import com.vungn.application.util.firebase.RemoteKey
import com.vungn.application.util.version.isLollipopPlus
import com.vungn.application.util.version.isRPlus
import com.vungn.application.util.version.isSPlus
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur


/**
 * This is a base activity class that all other activities in the application extend.
 * It provides common functionality for all activities, such as setting up navigation and view binding.
 *
 * @param Binding The type of ViewBinding for the activity. This is used to bind the views in the activity's layout.
 * @param ViewModel The type of ViewModel for the activity. This is used to manage the data for the activity.
 *
 * The class has two constructors:
 * - A default constructor for activities that do not use navigation.
 * - A constructor that takes a navigation resource and a navigation ID for activities that use navigation.
 *
 * The class also includes abstract methods that must be implemented by subclasses:
 * - getViewBinding(): This method should return the ViewBinding for the activity.
 * - getViewModelClass(): This method should return the Class object for the ViewModel.
 * - setupListener(): This method should set up any listeners for the activity.
 * - setupViews(): This method should set up the views for the activity.
 *
 * The class also includes methods for making the status bar transparent and hiding the navigation bar.
 * @author Nguyễn Ngọc Vũ
 */
abstract class BaseActivity<Binding : ViewBinding, ViewModel : androidx.lifecycle.ViewModel> :
    AppCompatActivity {
    @IdRes
    private var navId: Int? = null

    @NavigationRes
    private var navResource: Int? = null
    private var isWelcomeDialogShown = false
    private var welcomeDialog: Dialog? = null
    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        isWelcomeDialogShown = false
        onActivityResult(it)
    }
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isWelcomeDialogShown = false
        onRequestPermissionsResult(permissions)
    }
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        isWelcomeDialogShown = false
        onRequestPermissionResult(granted)
    }

    /**
     * The NavController for the activity's navigation graph.
     */
    protected lateinit var navHostController: NavController

    /**
     * The ViewBinding for the activity.
     */
    protected lateinit var binding: Binding

    /**
     * This method should return the ViewBinding for the activity.
     */
    protected abstract fun getViewBinding(): Binding

    /**
     * The ViewModel for the activity.
     */
    protected lateinit var viewModel: ViewModel

    /**
     * This method should return the Class object for the ViewModel.
     */
    protected abstract fun getViewModelClass(): Class<ViewModel>

    constructor()

    constructor(@IdRes navId: Int, @NavigationRes navResource: Int) {
        this.navId = navId
        this.navResource = navResource
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        viewModel = ViewModelProvider(this)[getViewModelClass()]
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    override fun onResume() {
        super.onResume()
        if (!isWelcomeDialogShown) {
            isWelcomeDialogShown = true
        } else {
            showWelcomeDialog()
            AdHelper(this).openAd(key = RemoteKey.OPENAD_RESUME_STATUS) {
                welcomeDialog?.cancel()
                isWelcomeDialogShown = false
            }
        }
    }

    override fun startActivity(intent: Intent) {
        startActivityForResult.launch(intent)
    }

    protected fun requestMultiplePermissions(permissions: Array<String>) {
        requestMultiplePermissions.launch(permissions)
    }

    protected fun requestPermission(permission: String) {
        requestPermission.launch(permission)
    }

    open fun onActivityResult(result: ActivityResult) {}

    open fun onRequestPermissionsResult(permissions: Map<String, @JvmSuppressWildcards Boolean>) {}

    open fun onRequestPermissionResult(granted: Boolean) {}

    private fun init() {
        setupNav()
        setupViews()
        setupListener()
    }

    /**
     * Set up the navigation for the activity.
     */
    private fun setupNav() {
        if (navId != null && navResource != null) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(navId!!) as NavHostFragment
            navHostController = navHostFragment.navController
            navHostController.setGraph(navResource!!, intent.extras)
        }
    }

    /**
     * This method should set up any listeners for the activity.
     */
    abstract fun setupListener()

    /**
     * This method should set up the views for the activity.
     */
    abstract fun setupViews()

    protected fun makeStatusBarTransparent() {
        if (isLollipopPlus()) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.WHITE
            }
        }
        if (isRPlus()) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    protected fun setStatusBarColor(color: Int) {
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var flags: Int = window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    private fun showWelcomeDialog() {
        if (welcomeDialog == null) {
            initWelcomeDialog()
        }
        if (welcomeDialog?.isShowing == false) {
            welcomeDialog?.show()
        }
    }

    private fun initWelcomeDialog() {
        welcomeDialog = Dialog(this)
        welcomeDialog?.setCancelable(false)
        welcomeDialog?.setContentView(WelcomeDialog(this).apply {
            val windowBackground = window.decorView.background
            val algorithm = getBlurAlgorithm()
            setupWith(
                window.decorView.findViewById(android.R.id.content),
                algorithm
            ).setFrameClearDrawable(
                windowBackground
            ).setBlurRadius(25f)
        })
        welcomeDialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun getBlurAlgorithm(): BlurAlgorithm {
        val algorithm = if (isSPlus()) {
            RenderEffectBlur()
        } else {
            RenderScriptBlur(this)
        }
        return algorithm
    }

    fun setShowWelcomeDialog(isShow: Boolean) {
        isWelcomeDialogShown = isShow
    }
}