package com.apps.saneforcetask

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apps.saneforcetask.model.Product
import com.apps.saneforcetask.viewModel.ProductViewModel
import com.apps.saneforcetask.viewState.ProductViewState

@Composable
fun ProductScreen() {
    val viewModel: ProductViewModel = viewModel()
    val productState by viewModel.productState.collectAsState()
    val context = LocalContext.current

    when (val state = productState) {
        is ProductViewState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProductViewState.Empty -> {
            Messages(state.emptyMsg)
        }

        is ProductViewState.ShowProducts -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = state.products, key = { it.productCode }) { product ->
                        ProductListView(
                            product = product,
                            onAddRequest = {
                                viewModel.getProductCount(
                                    productCode = product.productCode,
                                    isAdd = true,
                                    products = state.products
                                )
                            },
                            onMinusRequest = {
                                viewModel.getProductCount(
                                    productCode = product.productCode,
                                    isAdd = false,
                                    products = state.products
                                )
                            },
                            onDeleteRequest = {
                                viewModel.deleteProduct(
                                    productCode = product.productCode,
                                    products = state.products
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(42.dp))
                    }
                }
                Button(
                    onClick = {
                        viewModel.updateProductList(context, products = state.products) {
                            Toast.makeText(context, "Products are updated", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SAVE",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }
            }
        }

        is ProductViewState.Error -> {
            Messages(state.errorMsg)
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchProducts(context)
    }
}

@Composable
private fun Messages(msg: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = msg,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier
                .wrapContentSize()
        )
    }
}

@Composable
fun ProductListView(
    product: Product,
    onAddRequest: () -> Unit,
    onMinusRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 32.dp)
            )

            Button(
                onClick = { onDeleteRequest()},
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "DEL",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = product.convQty,
                modifier = Modifier
                    .padding(end = 18.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
            )

            Button(
                onClick = {onAddRequest() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .size(36.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }

            Text(
                text = product.selectedQty.toString(),
                modifier = Modifier
                    .padding(horizontal = 18.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
            )

            Button(
                onClick = { onMinusRequest() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .size(36.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "-",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }

            Text(
                text = product.productAmount.toString(),
                modifier = Modifier
                    .padding(start = 18.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewProductList() {
    ProductScreen()
}