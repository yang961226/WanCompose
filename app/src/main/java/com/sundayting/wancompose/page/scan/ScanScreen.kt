package com.sundayting.wancompose.page.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.helper.LocalVibratorHelper
import com.sundayting.wancompose.common.helper.PermissionCheckHelper
import com.sundayting.wancompose.theme.WanColors

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

        val vibratorHelper = LocalVibratorHelper.current

        ConstraintLayout(
            modifier.fillMaxSize()
        ) {

            val context = LocalContext.current

            val viewLifecycleOwner = LocalLifecycleOwner.current

            val lifecycleCameraController = remember(context) {
                LifecycleCameraController(context).apply {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                }
            }

            LaunchedEffect(
                viewLifecycleOwner
            ) {
                lifecycleCameraController.unbind()
                lifecycleCameraController.bindToLifecycle(viewLifecycleOwner)
            }

            val barcodeScanner = remember {
                BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )
            }

            DisposableEffect(Unit) {
                onDispose {
                    barcodeScanner.close()
                }
            }

            var rect by remember {
                mutableStateOf<Rect?>(null)
            }

            LaunchedEffect(lifecycleCameraController, context) {
                val mainExecutor = ContextCompat.getMainExecutor(context)
                lifecycleCameraController.setImageAnalysisAnalyzer(
                    mainExecutor,
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        COORDINATE_SYSTEM_VIEW_REFERENCED,
                        mainExecutor
                    ) { result: MlKitAnalyzer.Result? ->
                        if (result == null) {
                            rect = null
                            return@MlKitAnalyzer
                        }
                        val barcodeList = result.getValue(barcodeScanner) ?: return@MlKitAnalyzer
                        if (barcodeList.isEmpty()) {
                            rect = null
                            return@MlKitAnalyzer
                        }
                        rect = barcodeList.first().boundingBox?.toComposeRect() ?: Rect.Zero
                        barcodeList.forEach {

                            Log.d("临时测试", "结果：${it.boundingBox}")
                        }
                    }
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PreviewView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            MATCH_PARENT,
                            MATCH_PARENT
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                update = {
                    it.controller = lifecycleCameraController
                }
            )

            AnimatedVisibility(
                visible = rect != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                rect?.let {
                    Canvas(Modifier.fillMaxSize()) {
                        drawCircle(
                            WanColors.TopColor,
                            center = it.center,
                            radius = 25.dp.toPx()
                        )
                    }
                }
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
                        lifecycleCameraController.cameraControl?.let {
                            vibratorHelper.vibrateClick()
                            isTorchOpen = !isTorchOpen
                            it.enableTorch(isTorchOpen)
                        }
                    }
                    .background(Color.LightGray.copy(0.75f))
                    .padding(5.dp)
                    .size(35.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            ScanLight(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .constrainAs(createRef()) {
                        centerTo(parent)
                    })
        }

    }

    fun NavController.navigateToScanScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }
}

@Composable
private fun ScanLight(
    modifier: Modifier = Modifier,
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetY by infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = 375.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0f at 0
                1f at 500
                1f at 2500
                0f at 3000
            },
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(modifier.height(400.dp)) {
        Box(
            Modifier
                .offset {
                    IntOffset(0, offsetY.roundToPx())
                }
                .graphicsLayer {
                    this.alpha = alpha
                }
                .fillMaxWidth()
                .height(25.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xFF428DC2)
                        )
                    )
                )
        )
    }


}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun PreviewScanLight() {
    ScanLight(
        Modifier
            .fillMaxWidth()
    )
}