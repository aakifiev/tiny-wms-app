package ru.hqr.tinywms

import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.compose.AddBarcodeInfo
import ru.hqr.tinywms.ui.compose.AddStock
import ru.hqr.tinywms.ui.compose.AddressList
import ru.hqr.tinywms.ui.compose.CameraScreen
import ru.hqr.tinywms.ui.compose.HomeScreen
import ru.hqr.tinywms.ui.compose.LoginPage
import ru.hqr.tinywms.ui.compose.ProductInfo
import ru.hqr.tinywms.ui.compose.StartStocktaking
import ru.hqr.tinywms.ui.compose.StockInfoList
import ru.hqr.tinywms.ui.compose.StockList
import ru.hqr.tinywms.ui.signin.SignInScreen
import ru.hqr.tinywms.ui.signup.SignUpScreen
import ru.hqr.tinywms.ui.theme.TinyWmsTheme
import ru.hqr.tinywms.util.mainactivity.startCamera
import ru.hqr.tinywms.util.mainactivity.stopCamera
import ru.hqr.tinywms.util.requestCameraPermission
import ru.hqr.tinywms.view.AddressListViewModel
import ru.hqr.tinywms.view.StockInfoListViewModel
import ru.hqr.tinywms.view.StockListViewModel
import java.util.concurrent.ExecutorService

class MainActivity : FragmentActivity() {

    lateinit var cameraExecutor: ExecutorService;
    val identifier = "[MainActivity]"
    var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

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
        val productListVM = StockListViewModel()
        val stockInfoListVM = StockInfoListViewModel()
        val addressListVM = AddressListViewModel()
        requestCameraPermission()
        startCamera()
        enableEdgeToEdge()
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            TinyWmsTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = NavRoute.SIGN_IN.name) {
                    composable (NavRoute.SIGN_IN.name) {
                        SignInScreen(navController)
                    }
                    composable (NavRoute.SIGN_UP.name) {
                        SignUpScreen(navController)
                    }
                    composable(NavRoute.HOME.name) {
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
                    composable(NavRoute.CAMERA.name) {
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
                    composable(NavRoute.ADDRESS_LIST.name) {
                        AddressList(
                            onStockInfoClick = { barcode ->
                                navController.navigate("stockInfoList/barcode=$barcode/byBarcode=false")
                            },
                            navigateBack = {
                                navController.popBackStack()
                            },
                            drawerState, scope, addressListVM, navController
                        )
                    }
                    composable(NavRoute.STOCK_LIST.name) {
                        StockList(
                            onStockInfoClick = { barcode ->
                                navController.navigate("stockInfoList/barcode=$barcode/byBarcode=true")
                            },
                            navigateBack = {
//                                val route1 = navController.currentDestination?.parent?.nodes
//                                if (route1 == "SIGN_IN") {
//                                    return@StockList
//                                }
                                navController.popBackStack()
                            },
                            drawerState, scope, productListVM, navController
                        )
                    }
                    composable(NavRoute.ADD_STOCK.name) {
                        AddStock(
                            navigateBack = {
                                navController.popBackStack()
                            },
                            drawerState, scope,
                            addressListVM, productListVM,
                            cameraExecutor,
                            navController
                        )
                    }
                    composable(NavRoute.ADD_BARCODE_INFO.name) {
                        AddBarcodeInfo(
                            navigateBack = {
                                navController.popBackStack()
                            },
                            drawerState, scope,
                            addressListVM, productListVM,
                            cameraExecutor,
                            navController
                        )
                    }
                    composable("stockInfoList/barcode={barcode}/byBarcode={byBarcode}") { backStackEntry ->
                        val arguments = requireNotNull(backStackEntry.arguments)
                        val barcode = arguments.getString("barcode")
                        val byBarcode = arguments.getString("byBarcode").toBoolean()
                        StockInfoList(
                            barcode as String, byBarcode,
                            drawerState, scope, stockInfoListVM, navController)
                    }
                    composable("loginPage") {
                        LoginPage(
                            navController,
                            navigateBack = {
                                navController.popBackStack()
                            })
                    }
                    composable(NavRoute.START_STOCKTAKING.name) {
                        StartStocktaking(
                            navigateBack = {
                                navController.popBackStack()
                            },
                            drawerState, scope, addressListVM,
                            cameraExecutor, navController)
                    }
                }
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