package ru.hqr.tinywms

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.hqr.tinywms.ui.compose.CameraScreen
import ru.hqr.tinywms.ui.compose.HomeScreen
import ru.hqr.tinywms.ui.compose.LoginPage
import ru.hqr.tinywms.ui.compose.ProductInfo
import ru.hqr.tinywms.ui.compose.StockInfoList
import ru.hqr.tinywms.ui.compose.StockList
import ru.hqr.tinywms.ui.theme.TinyWmsTheme
import ru.hqr.tinywms.util.mainactivity.startCamera
import ru.hqr.tinywms.util.mainactivity.stopCamera
import ru.hqr.tinywms.util.requestCameraPermission
import java.util.concurrent.ExecutorService

class MainActivity : ComponentActivity() {

    lateinit var cameraExecutor: ExecutorService;
    val identifier = "[MainActivity]"
    var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

//    var shouldShowCamera by remember { mutableStateOf(true) }

    // MARK: Camera Permissions
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("identifier", "Camera Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("identifier", "Camera Permission denied")
            shouldShowCamera.value = false
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermission()
        startCamera()
        enableEdgeToEdge()
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            TinyWmsTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "stockList") {
                    composable("home") {
                        HomeScreen(
                            executorHs = cameraExecutor,
                            popBackStack = {
                                navController.popBackStack()
                            },
                            toProductInfo = { barcode ->
                                navController.navigate("productInfo/barcode=$barcode")
                            }
                        )
                    }
                    composable("camera") {
                        CameraScreen(
                            executorHs = cameraExecutor,
                            popBackStack = {
                                navController.popBackStack()
                            },
                            toProductInfo = { barcode ->
                                navController.navigate("productInfo/barcode=$barcode")
                            }
                        )
                    }
                    composable("productInfo/barcode={barcode}") { backStackEntry ->
                        val arguments = requireNotNull(backStackEntry.arguments)
                        val barcode = arguments.getString("barcode")
                        ProductInfo(barcode as String)
                    }
                    composable("stockList") {
                        StockList(
                            onStockInfoClick = { barcode ->
                                navController.navigate("stockInfoList/barcode=$barcode")
                            },
                            navigateBack = {
                                navController.popBackStack()
                            },
                            drawerState, scope, navController
                        )
                    }
                    composable("stockInfoList/barcode={barcode}") { backStackEntry ->
                        val arguments = requireNotNull(backStackEntry.arguments)
                        val barcode = arguments.getString("barcode")
                        StockInfoList(barcode as String, drawerState, scope, navController)
                    }
                    composable("loginPage") {
                        LoginPage(
                            navController,
                            navigateBack = {
                                navController.popBackStack()
                            })
                    }
                }
//                Column (modifier = Modifier.fillMaxSize()) {
//                    val navController = rememberNavController()
//                    NavHost(navController, startDestination = "home") {
//
//                    }
//                    Spacer(modifier = Modifier.height(40.dp))
//                    Box(modifier = Modifier
//                        .fillMaxWidth(fraction = 0.9f)
//                        .height(36.dp)
//                        .clip(shape = RoundedCornerShape(6.dp))) {
//                        Button(enabled = true, modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight(), onClick = {
//                                navController.popBackStack()
//                        }) {
//                            Text("Like")
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Box(modifier = Modifier
//                        .fillMaxWidth(fraction = 0.9f)
//                        .height(36.dp)
//                        .clip(shape = RoundedCornerShape(6.dp))) {
//                        Button(enabled = true, modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight(), onClick = { }) {
//                            Text("Like 2")
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Box(modifier = Modifier
//                        .fillMaxWidth(fraction = 0.9f)
//                        .height(360.dp)
//                        .clip(shape = RoundedCornerShape(6.dp))) {
//                        Camera(
//                            executor = cameraExecutor,
//                            onError = {
//                            Log.i(identifier, "There was as error with the camera: $it")
//                        },
//                                    onCatchBarcode = { navController.navigate("") })
//                    }
//
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestCameraPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCamera()
    }
}