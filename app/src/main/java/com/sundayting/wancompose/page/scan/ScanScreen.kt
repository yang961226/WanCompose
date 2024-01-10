package com.sundayting.wancompose.page.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.helper.PermissionCheckHelper

object ScanScreen : WanComposeDestination {
    override val route: String
        get() = "扫描页面"


    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
    ) {

        val needPermission = Manifest.permission.CAMERA
//        val needPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Manifest.permission.READ_MEDIA_IMAGES
//        } else {
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        }

        val context = LocalContext.current

        var permissionStatus by remember {
            mutableStateOf(PermissionCheckHelper.PermissionStatus.Denied)
        }

        val requestPermissionLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                permissionStatus =
                    PermissionCheckHelper.checkPermissionAfterRequest(context, needPermission)
            }

        val toSettingLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
                permissionStatus =
                    PermissionCheckHelper.checkPermissionAfterRequest(context, needPermission)
            }

        LaunchedEffect(context, needPermission) {
            requestPermissionLauncher.launch(needPermission)
        }

        ConstraintLayout(
            modifier
                .background(Color.Black)
                .pointerInput(Unit) {}
        ) {

            if (permissionStatus == PermissionCheckHelper.PermissionStatus.Granted) {
                ScanContent()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .constrainAs(createRef()) {
                            centerTo(parent)
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            when (permissionStatus) {
                                PermissionCheckHelper.PermissionStatus.PermanentDenied -> {
                                    toSettingLauncher.launch(Intent().apply {
                                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        data = Uri.fromParts("package", context.packageName, null)
                                    })
                                }

                                PermissionCheckHelper.PermissionStatus.Granted -> {
                                    requestPermissionLauncher.launch(needPermission)
                                }

                                else -> {

                                }

                            }
                        }
                ) {

                    when (permissionStatus) {
                        PermissionCheckHelper.PermissionStatus.Denied -> {
                            Text(
                                text = stringResource(id = R.string.scan_need_camera_permission),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = stringResource(id = R.string.click_for_apply),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White.copy(0.5f)
                                )
                            )
                        }

                        PermissionCheckHelper.PermissionStatus.PermanentDenied -> {
                            Text(
                                text = stringResource(id = R.string.camera_permission_permanent_denied),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = stringResource(id = R.string.to_setting),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White.copy(0.5f)
                                )
                            )
                        }

                        else -> {}
                    }
                }
            }

            Image(
                painterResource(id = R.drawable.ic_close),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                modifier = Modifier
                    .statusBarsPadding()
                    .constrainAs(createRef()) {
                        top.linkTo(parent.top, 10.dp)
                        start.linkTo(parent.start, 10.dp)
                    }
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) { navController.popBackStack() }
                    .background(Color.LightGray.copy(0.5f))
                    .padding(2.dp)
                    .size(20.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )


        }

    }

    @Composable
    private fun ScanContent(
        modifier: Modifier = Modifier,
    ) {

        ConstraintLayout(
            modifier.fillMaxSize()
        ) {

            val context = LocalContext.current
            val previewView = remember(context) {
                PreviewView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            }

            val cameraProviderFuture =
                remember(context) {
                    ProcessCameraProvider.getInstance(context)
                }

            val viewLifecycleOwner = LocalLifecycleOwner.current

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView },
            )

            var camera by remember {
                mutableStateOf<Camera?>(null)
            }
            LaunchedEffect(context, viewLifecycleOwner) {
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            // Unbind use cases before rebinding
                            cameraProvider.unbindAll()

                            // Bind use cases to camera
                            cameraProvider.bindToLifecycle(
                                viewLifecycleOwner, cameraSelector, preview
                            ).also {
                                camera = it
                                it.cameraControl.enableTorch(false)
                            }

                        } catch (_: Exception) {
                        }

                    },
                    ContextCompat.getMainExecutor(context)
                )
            }

            var isTorchOpen by remember { mutableStateOf(false) }

            Image(
                painterResource(id = if (isTorchOpen) R.drawable.ic_torch_open else R.drawable.ic_torch_close),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                modifier = Modifier
                    .statusBarsPadding()
                    .constrainAs(createRef()) {
                        bottom.linkTo(parent.bottom, 35.dp)
                        start.linkTo(parent.start, 35.dp)
                    }
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
                        camera?.let {
                            isTorchOpen = !isTorchOpen
                            it.cameraControl.enableTorch(isTorchOpen)
                        }
                    }
                    .background(Color.LightGray.copy(0.75f))
                    .padding(5.dp)
                    .size(35.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

    }

    fun NavController.navigateToScanScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }
}