package com.example.cryptomonitor.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.cryptomonitor.model.TickerResponse
import com.example.cryptomonitor.viewmodel.CryptoUiState
import com.example.cryptomonitor.viewmodel.CryptoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.cryptomonitor.model.Ticker
import com.example.cryptomonitor.ui.theme.CryptoMonitorTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoMonitorScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crypto Monitor",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is CryptoUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CryptoUiState.Success -> {
                    CryptoContent(
                        ticker = state.ticker,
                        onRefresh = { viewModel.fetchTickerData() },
                        onBack = { viewModel.resetToInitial() }
                    )
                }

                is CryptoUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.fetchTickerData() }
                    )
                }

                is CryptoUiState.Initial -> {
                    InitialContent(
                        onLoadData = { viewModel.fetchTickerData() }
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoContent(
    ticker: TickerResponse,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Card com informações do Bitcoin
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bitcoin (BTC)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Valor atual
                val lastValue = ticker.ticker.last.toDoubleOrNull()
                if (lastValue != null) {
                    val numberFormat = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("pt").setRegion("BR").build())
                    Text(
                        text = numberFormat.format(lastValue),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Data da cotação
                val date = Date(ticker.ticker.date * 1000L)
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                Text(
                    text = "Atualizado em: ${sdf.format(date)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Informações adicionais
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(
                        label = "Máxima",
                        value = formatCurrency(ticker.ticker.high)
                    )
                    InfoItem(
                        label = "Mínima",
                        value = formatCurrency(ticker.ticker.low)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(
                        label = "Compra",
                        value = formatCurrency(ticker.ticker.buy)
                    )
                    InfoItem(
                        label = "Venda",
                        value = formatCurrency(ticker.ticker.sell)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoItem(
                    label = "Volume",
                    value = ticker.ticker.vol,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de refresh
        Button(
            onClick = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Atualizar",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ATUALIZAR COTAÇÃO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp)
        ) {
            Text(
                text = "VOLTAR À TELA INICIAL",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "❌",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Erro ao carregar dados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tentar Novamente")
        }
    }
}

@Composable
fun InitialContent(
    onLoadData: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "₿",
            fontSize = 80.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Crypto Monitor",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Acompanhe a cotação do Bitcoin em tempo real",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLoadData,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Carregar Cotação", fontSize = 16.sp)
        }
    }
}

fun formatCurrency(value: String): String {
    val doubleValue = value.toDoubleOrNull() ?: return value
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("pt").setRegion("BR").build())
    return numberFormat.format(doubleValue)
}


/**
 * Preview da tela inicial — estado [CryptoUiState.Initial].
 * Exibido antes de qualquer busca ser realizada.
 */
@Preview(showBackground = true, name = "Tela Inicial")
@Composable
fun InitialContentPreview() {
    CryptoMonitorTheme {
        InitialContent(onLoadData = {})
    }
}

/**
 * Preview da tela de carregamento — estado [CryptoUiState.Loading].
 * Exibido enquanto a API está sendo consultada.
 */
@Preview(showBackground = true, name = "Carregando")
@Composable
fun LoadingContentPreview() {
    CryptoMonitorTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * Preview da tela de cotação — estado [CryptoUiState.Success].
 * Utiliza dados fictícios para simular a resposta da API.
 */
@Preview(showBackground = true, name = "Cotação - Sucesso")
@Composable
fun CryptoContentPreview() {
    CryptoMonitorTheme {
        CryptoContent(
            ticker = TickerResponse(
                ticker = Ticker(
                    high = "680000.00",
                    low = "620000.00",
                    vol = "12.34567",
                    last = "650000.00",
                    buy = "649000.00",
                    sell = "651000.00",
                    date = System.currentTimeMillis() / 1000L
                )
            ),
            onRefresh = {},
            onBack = {}
        )
    }
}

/**
 * Preview da tela de erro — estado [CryptoUiState.Error].
 * Exibido quando a chamada à API falha.
 */
@Preview(showBackground = true, name = "Erro")
@Composable
fun ErrorContentPreview() {
    CryptoMonitorTheme {
        ErrorContent(
            message = "Falha na conexão com o servidor. Verifique sua internet.",
            onRetry = {}
        )
    }
}

/**
 * Preview do componente [InfoItem] isolado.
 * Demonstra como o componente se comporta com diferentes rótulos e valores.
 */
@Preview(showBackground = true, name = "InfoItem")
@Composable
fun InfoItemPreview() {
    CryptoMonitorTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(label = "Máxima", value = "R$ 680.000,00")
            InfoItem(label = "Mínima", value = "R$ 620.000,00")
        }
    }
}

/**
 * Preview no tema escuro — estado [CryptoUiState.Success].
 * Permite visualizar como a tela se comporta no modo escuro.
 */
@Preview(showBackground = true, name = "Cotação - Tema Escuro", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CryptoContentDarkPreview() {
    CryptoMonitorTheme {
        CryptoContent(
            ticker = TickerResponse(
                ticker = Ticker(
                    high = "680000.00",
                    low = "620000.00",
                    vol = "12.34567",
                    last = "650000.00",
                    buy = "649000.00",
                    sell = "651000.00",
                    date = System.currentTimeMillis() / 1000L
                )
            ),
            onRefresh = {},
            onBack = {}
        )
    }
}