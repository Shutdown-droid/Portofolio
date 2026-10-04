package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WarungPosDatabase
import com.example.data.repository.TransactionRepository
import com.example.model.OrderItem
import com.example.model.PopularMenuItem
import com.example.model.TransactionRecord
import com.example.util.PdfReceiptManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WarungPosViewModel(
    application: Application,
    private val repository: TransactionRepository
) : AndroidViewModel(application) {

    // Default constructor for ViewModelProvider / Compose viewModel()
    constructor(application: Application) : this(
        application,
        TransactionRepository(WarungPosDatabase.getDatabase(application).transactionDao())
    )

    // 1. Dynamic Store Name
    val storeName: String = "Sistem Kasir AI"

    // Tab Navigation: "kasir" or "laporan"
    private val _activeTab = MutableStateFlow("kasir")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // Persistent Transactions from Room Database
    val transactions: StateFlow<List<TransactionRecord>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Total Revenue observed dynamically from Room for Laporan
    val dailyTotal: StateFlow<Int> = repository.totalRevenue
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 350000
        )
    val dailyRevenueFromDb: StateFlow<Int> = dailyTotal

    // Persistent QRIS Revenue from Room
    val qrisRevenue: StateFlow<Int> = repository.qrisRevenue
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 263000
        )

    // Persistent Cash Revenue from Room
    val cashRevenue: StateFlow<Int> = repository.cashRevenue
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 87000
        )

    // 3. Dynamic QRIS Trigger: Hidden by default (showQris = false)
    private val _showQrisToast = MutableStateFlow(false)
    val showQrisToast: StateFlow<Boolean> = _showQrisToast.asStateFlow()

    private val _qrisAmount = MutableStateFlow(20000)
    val qrisAmount: StateFlow<Int> = _qrisAmount.asStateFlow()

    // 2. Interactive Cart (Initialized with 2 default items)
    private val _orderItems = MutableStateFlow(
        listOf(
            OrderItem(id = 1L, name = "Nasi Telor Dadar", qty = 2, unitPrice = 10000, total = 20000),
            OrderItem(id = 2L, name = "Es Jeruk Segar", qty = 1, unitPrice = 5000, total = 5000)
        )
    )
    val orderItems: StateFlow<List<OrderItem>> = _orderItems.asStateFlow()

    // AI Notification message
    private val _aiNotification = MutableStateFlow<String?>(null)
    val aiNotification: StateFlow<String?> = _aiNotification.asStateFlow()

    // Voice recognition simulation state
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    // Thermal Printer state
    private val _isPrinting = MutableStateFlow(false)
    val isPrinting: StateFlow<Boolean> = _isPrinting.asStateFlow()

    private val _showPrintDialog = MutableStateFlow(false)
    val showPrintDialog: StateFlow<Boolean> = _showPrintDialog.asStateFlow()

    private val _showReactCodeDialog = MutableStateFlow(false)
    val showReactCodeDialog: StateFlow<Boolean> = _showReactCodeDialog.asStateFlow()

    // Popular Menu Data for Laporan
    val popularMenu = listOf(
        PopularMenuItem("Nasi Telor Dadar", 24, "Porsi", 0.85f),
        PopularMenuItem("Gorengan Bakwan/Tahu", 45, "Biji", 0.95f),
        PopularMenuItem("Es Jeruk Segar", 18, "Gelas", 0.65f),
        PopularMenuItem("Nasi Ayam Krispi", 15, "Porsi", 0.55f),
        PopularMenuItem("Kopi Tubruk", 22, "Cangkir", 0.75f)
    )

    init {
        // Prepopulate Room database with initial transactions if first launch
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    // Dismiss QRIS Toast
    fun dismissQrisToast() {
        _showQrisToast.value = false
    }

    // Developer Button: "Simulasi QRIS" (sets showQris to true)
    fun triggerQrisSimulation(amount: Int = 20000) {
        _qrisAmount.value = amount
        _showQrisToast.value = true
        showNotification("🔔 Simulasi QRIS masuk: ${formatRupiah(amount)}")
    }

    // Action 1: [Ya, Gabungkan] -> dismisses toast & saves transaction
    fun mergeQrisPayment() {
        _showQrisToast.value = false
        val currentOrderTotal = calculateOrderTotal()
        val qAmount = _qrisAmount.value

        val timeNow = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date()) + " WIB"
        val nextNum = (transactions.value.size + 1)
        val trxId = "TRX-${String.format("%03d", nextNum)}"
        val newTrx = TransactionRecord(
            id = trxId,
            timestamp = timeNow,
            itemsSummary = "Pesanan Gabung QRIS (${_orderItems.value.size} menu)",
            total = currentOrderTotal,
            method = "QRIS"
        )

        viewModelScope.launch {
            repository.insertTransaction(newTrx)
        }

        showNotification("✨ QRIS ${formatRupiah(qAmount)} berhasil digabungkan!")
    }

    // Action 2: [Tutup] or [Pemasukan Baru]
    fun recordNewQrisIncome() {
        _showQrisToast.value = false
        val qAmount = _qrisAmount.value

        val timeNow = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date()) + " WIB"
        val nextNum = (transactions.value.size + 1)
        val trxId = "TRX-${String.format("%03d", nextNum)}"
        val newTrx = TransactionRecord(
            id = trxId,
            timestamp = timeNow,
            itemsSummary = "Pemasukan Langsung QRIS",
            total = qAmount,
            method = "QRIS"
        )

        viewModelScope.launch {
            repository.insertTransaction(newTrx)
        }

        showNotification("💰 Pemasukan QRIS ${formatRupiah(qAmount)} tersimpan!")
    }

    // 2. Interactive Cart: Update Quantity with '+' or '-'
    // If qty hits 0, removes the item from the list
    fun updateItemQuantity(id: Long, delta: Int) {
        _orderItems.value = _orderItems.value.mapNotNull { item ->
            if (item.id == id) {
                val newQty = item.qty + delta
                if (newQty > 0) {
                    item.copy(qty = newQty, total = item.unitPrice * newQty)
                } else {
                    null // Removed from list when qty reaches 0
                }
            } else {
                item
            }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            showNotification("Transaksi $id telah dihapus dari database.")
        }
    }

    fun removeOrderItem(id: Long) {
        _orderItems.value = _orderItems.value.filter { it.id != id }
    }

    fun clearOrder() {
        _orderItems.value = emptyList()
        showNotification("Struk telah dibersihkan.")
    }

    fun resetDefaultOrder() {
        _orderItems.value = listOf(
            OrderItem(id = 1L, name = "Nasi Telor Dadar", qty = 2, unitPrice = 10000, total = 20000),
            OrderItem(id = 2L, name = "Es Jeruk Segar", qty = 1, unitPrice = 5000, total = 5000)
        )
        showNotification("Struk di-reset ke 2 pesanan default.")
    }

    // Add or update item in cart
    fun addItemToCart(productName: String, unitPrice: Int, quantity: Int = 1) {
        val current = _orderItems.value
        val existingIndex = current.indexOfFirst { it.name.equals(productName, ignoreCase = true) }

        if (existingIndex != -1) {
            val existing = current[existingIndex]
            val newQty = existing.qty + quantity
            val updated = existing.copy(
                qty = newQty,
                total = existing.unitPrice * newQty
            )
            val newList = current.toMutableList()
            newList[existingIndex] = updated
            _orderItems.value = newList
        } else {
            val newItem = OrderItem(
                id = System.currentTimeMillis(),
                name = productName,
                qty = quantity,
                unitPrice = unitPrice,
                total = unitPrice * quantity
            )
            _orderItems.value = current + newItem
        }
    }

    // Voice command processor: Maps spoken item names to predefined product list
    fun processVoiceCommand(spokenText: String) {
        val trimmed = spokenText.trim()
        if (trimmed.isEmpty()) return

        val matchedItems = com.example.model.PredefinedProducts.matchProducts(trimmed)
        if (matchedItems.isNotEmpty()) {
            matchedItems.forEach { (product, qty) ->
                addItemToCart(product.name, product.defaultPrice, qty)
            }
            val summary = matchedItems.joinToString(", ") { "${it.second}x ${it.first.name}" }
            showNotification("🎙️ Suara: Ditambahkan $summary")
        }
    }

    fun setListeningState(listening: Boolean) {
        _isListening.value = listening
        if (listening) {
            showNotification("🎙️ Mendengarkan... Katakan pesanan (misal: '2 Nasi Ayam')")
        }
    }

    // 4. Working Input Field & Shortcuts
    fun parseAndAddOrder(rawText: String) {
        processVoiceCommand(rawText)
    }

    // Voice AI order simulation / fallback
    fun triggerVoiceAI() {
        if (_isListening.value) {
            _isListening.value = false
            return
        }

        viewModelScope.launch {
            _isListening.value = true
            showNotification("🎙️ Mendengarkan suara... (Katakan pesanan)")

            delay(1800) // Simulating voice recognition

            val sampleOrders = listOf(
                "2 Nasi Ayam Krispi",
                "1 Es Jeruk Segar",
                "3 Gorengan",
                "2 Kopi Susu Gula Aren",
                "1 Bakso Urat Malang",
                "1 Nasi Telor Dadar dan 1 Es Teh"
            )
            val recognizedText = sampleOrders.random()
            _isListening.value = false
            processVoiceCommand(recognizedText)
        }
    }

    fun startPrintReceipt() {
        viewModelScope.launch {
            _isPrinting.value = true
            _showPrintDialog.value = true
            delay(1500)
            _isPrinting.value = false
            showNotification("🖨️ Struk pesanan berhasil dicetak!")
        }
    }

    // PDF Receipt Export & Share
    fun shareReceiptAsPdf(context: Context) {
        val items = _orderItems.value
        val total = calculateOrderTotal()
        viewModelScope.launch(Dispatchers.IO) {
            val file = PdfReceiptManager.generateReceiptPdf(
                context = context,
                storeName = storeName,
                items = items,
                totalAmount = total
            )
            withContext(Dispatchers.Main) {
                if (file != null) {
                    PdfReceiptManager.shareReceiptPdf(context, file)
                    showNotification("📤 Membuka menu bagikan struk PDF...")
                } else {
                    showNotification("❌ Gagal membuat file PDF struk.")
                }
            }
        }
    }

    // PDF Receipt Download & Save
    fun downloadReceiptAsPdf(context: Context) {
        val items = _orderItems.value
        val total = calculateOrderTotal()
        viewModelScope.launch(Dispatchers.IO) {
            val file = PdfReceiptManager.generateReceiptPdf(
                context = context,
                storeName = storeName,
                items = items,
                totalAmount = total
            )
            withContext(Dispatchers.Main) {
                if (file != null) {
                    val savedFile = PdfReceiptManager.openOrDownloadReceiptPdf(context, file)
                    val fileName = savedFile?.name ?: file.name
                    showNotification("📄 PDF tersimpan: $fileName")
                } else {
                    showNotification("❌ Gagal membuat file PDF struk.")
                }
            }
        }
    }

    fun dismissPrintDialog() {
        _showPrintDialog.value = false
    }

    fun setShowReactCodeDialog(show: Boolean) {
        _showReactCodeDialog.value = show
    }

    // Dynamic calculation: sum of price * qty
    fun calculateOrderTotal(): Int {
        return _orderItems.value.sumOf { it.total }
    }

    private fun showNotification(msg: String) {
        viewModelScope.launch {
            _aiNotification.value = msg
            delay(3500)
            if (_aiNotification.value == msg) {
                _aiNotification.value = null
            }
        }
    }

    companion object {
        fun formatRupiah(amount: Int): String {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.maximumFractionDigits = 0
            return format.format(amount).replace("Rp", "Rp ")
        }
    }
}
