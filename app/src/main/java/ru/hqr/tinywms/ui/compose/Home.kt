package ru.hqr.tinywms.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.component.BottomNavigationBar
import ru.hqr.tinywms.ui.component.CustomModalNavigationDrawer

@Composable
fun Home(
    navController: NavHostController,
    drawerState: DrawerState,
    selectedDestination: MutableIntState
) {

//    lateinit var cameraExecutor: ExecutorService;
    val identifier = "[HomeScreen]"
    val scope = rememberCoroutineScope()

    CustomModalNavigationDrawer(
        drawerState = drawerState,
        scope = scope,
        navController = navController
    )
    {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController, selectedDestination)
            }
        ) { padding ->
            val pages =
                listOf(
                    HomeButton("Ваш аккаунт", NavRoute.ACCOUNT.name),
                    HomeButton("Каталог товаров", NavRoute.STOCK_LIST.name),
                    HomeButton("Открыть склад", NavRoute.ADDRESS_LIST.name),
                    HomeButton("Сформировать отчет", NavRoute.HOME.name),
                    HomeButton("Сканировать штрихкод", NavRoute.HOME.name),
                    HomeButton("Написать в техподдержку", NavRoute.HOME.name)
                )
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Red),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .padding(start = 50.dp),
                        horizontalAlignment = AbsoluteAlignment.Left
                    ) {
                        Text(text = "Доброе утро, Антон!", color = Color.White)
                        Spacer(modifier = Modifier.padding(20.dp))
                        Text(text = "1 333", fontSize = 50.sp, color = Color.White)
                        Text(text = "товара на складе", color = Color.White)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(150.dp),
                        modifier = Modifier
//                            .background(color = Color.Green)
                            .background(color = MaterialTheme.colorScheme.background)
//                        .fillMaxWidth()
//                        .width(350.dp)
//                        .height(350.dp),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
//                    verticalItemSpacing = 10.dp,
                    ) {
                        items(pages) { page ->
                            Button(
                                onClick = { navController.navigate(page.route) },
                                shape = RoundedCornerShape(15.dp),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
//                                .width(350.dp)
//                                .fillMaxHeight()
                                    .height(150.dp),
                                colors = ButtonColors(
                                    containerColor = Color.LightGray,
                                    contentColor = Color.Red,
                                    disabledContentColor = Color.Black,
                                    disabledContainerColor = Color.Gray
                                )
                            ) {
                                Text(page.label)
                            }
                        }
//                }
//                Row(
//                    modifier = Modifier
//                        .padding(10.dp)
//                ) {

                    }
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                ) {
                    Button(
                        modifier = Modifier
//
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonColors(
                            containerColor = Color.Yellow,
                            contentColor = Color.Black,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.Gray
                        ),
                        onClick = { navController.navigate(NavRoute.START_STOCKTAKING.name) }
                    ) {
                        Text("НАЧАТЬ ИНВЕНТАРИЗАЦИЮ")
                    }
                }
            }
        }
    }
}

data class HomeButton(
    val label: String,
    val route: String
)