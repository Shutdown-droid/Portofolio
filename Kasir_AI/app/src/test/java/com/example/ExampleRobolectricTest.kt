package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.WarungPosDatabase
import com.example.data.repository.TransactionRepository
import com.example.model.TransactionRecord
import com.example.viewmodel.WarungPosViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: WarungPosDatabase
  private lateinit var repository: TransactionRepository
  private lateinit var app: Application

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    app = context as Application
    db = Room.inMemoryDatabaseBuilder(context, WarungPosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = TransactionRepository(db.transactionDao())
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val appName = app.getString(R.string.app_name)
    assertEquals("Warung AI POS", appName)
  }

  @Test
  fun `verify store name is Sistem Kasir AI`() {
    val vm = WarungPosViewModel(app, repository)
    assertEquals("Sistem Kasir AI", vm.storeName)
  }

  @Test
  fun `room database insertion and flow observation`() = runBlocking {
    val record = TransactionRecord("TRX-TEST-1", "12:00 WIB", "Nasi Telor (2)", 20000, "QRIS")
    repository.insertTransaction(record)

    val list = repository.allTransactions.first()
    assertEquals(1, list.size)
    assertEquals("TRX-TEST-1", list[0].id)
    assertEquals(20000, list[0].total)
    assertEquals("QRIS", list[0].method)

    val revenue = repository.totalRevenue.first()
    assertEquals(20000, revenue)
  }

  @Test
  fun `room database prepopulate initial transactions`() = runBlocking {
    repository.prepopulateIfEmpty()
    val list = repository.allTransactions.first()
    assertTrue(list.isNotEmpty())
    val totalRevenue = repository.totalRevenue.first()
    assertEquals(350000, totalRevenue)
  }

  @Test
  fun `verify initial cart calculation in viewmodel with 2 default items`() {
    val vm = WarungPosViewModel(app, repository)
    // Initialized with 2 default items: Nasi Telor Dadar (2x 10000 = 20000) + Es Jeruk Segar (1x 5000 = 5000) = 25000
    assertEquals(2, vm.orderItems.value.size)
    assertEquals(25000, vm.calculateOrderTotal())
    // QRIS Toast is hidden by default
    assertFalse(vm.showQrisToast.value)
  }

  @Test
  fun `verify interactive cart plus and minus buttons`() {
    val vm = WarungPosViewModel(app, repository)
    val firstItem = vm.orderItems.value[0] // Nasi Telor (qty 2, unitPrice 10000)

    // Click '+' button
    vm.updateItemQuantity(firstItem.id, 1)
    assertEquals(3, vm.orderItems.value.first { it.id == firstItem.id }.qty)
    assertEquals(30000, vm.orderItems.value.first { it.id == firstItem.id }.total)
    assertEquals(35000, vm.calculateOrderTotal())

    // Click '-' button twice
    vm.updateItemQuantity(firstItem.id, -1)
    assertEquals(2, vm.orderItems.value.first { it.id == firstItem.id }.qty)

    vm.updateItemQuantity(firstItem.id, -1)
    assertEquals(1, vm.orderItems.value.first { it.id == firstItem.id }.qty)

    // Click '-' once more: qty hits 0 -> item is removed from array
    vm.updateItemQuantity(firstItem.id, -1)
    assertEquals(1, vm.orderItems.value.size)
    assertFalse(vm.orderItems.value.any { it.id == firstItem.id })
    assertEquals(5000, vm.calculateOrderTotal())
  }

  @Test
  fun `verify dynamic qris trigger and dismiss actions`() {
    val vm = WarungPosViewModel(app, repository)
    // Hidden by default
    assertFalse(vm.showQrisToast.value)

    // Developer triggers Simulasi QRIS
    vm.triggerQrisSimulation(20000)
    assertTrue(vm.showQrisToast.value)

    // Dismiss closes toast
    vm.dismissQrisToast()
    assertFalse(vm.showQrisToast.value)
  }

  @Test
  fun `verify input field adds item with default price 5000 and qty 1`() {
    val vm = WarungPosViewModel(app, repository)
    vm.parseAndAddOrder("Kopi")
    val added = vm.orderItems.value.last()
    assertTrue(added.name.contains("Kopi", ignoreCase = true))
    assertEquals(1, added.qty)
    assertEquals(5000, added.unitPrice)
    assertEquals(5000, added.total)
  }

  @Test
  fun `verify qris merge action saves to room`() = runBlocking {
    val vm = WarungPosViewModel(app, repository)
    vm.triggerQrisSimulation(20000)
    assertTrue(vm.showQrisToast.value)

    vm.mergeQrisPayment()
    assertFalse(vm.showQrisToast.value)

    val list = repository.allTransactions.first()
    assertTrue(list.any { it.total == 25000 && it.method == "QRIS" })
  }

  @Test
  fun `verify voice command maps spoken item name to predefined product list and updates cart`() {
    val vm = WarungPosViewModel(app, repository)
    val initialItemCount = vm.orderItems.value.size
    val initialTotal = vm.calculateOrderTotal()

    // Speak "2 Nasi Ayam Krispi" (unitPrice 15000)
    vm.processVoiceCommand("2 Nasi Ayam Krispi")

    assertEquals(initialItemCount + 1, vm.orderItems.value.size)
    val ayamItem = vm.orderItems.value.first { it.name == "Nasi Ayam Krispi" }
    assertEquals(2, ayamItem.qty)
    assertEquals(15000, ayamItem.unitPrice)
    assertEquals(30000, ayamItem.total)
    assertEquals(initialTotal + 30000, vm.calculateOrderTotal())
  }

  @Test
  fun `verify voice command increments quantity if item already exists in cart`() {
    val vm = WarungPosViewModel(app, repository)
    // "Es Jeruk Segar" is in the initial cart with qty 1 (price 5000)
    val initialEsJeruk = vm.orderItems.value.first { it.name == "Es Jeruk Segar" }
    assertEquals(1, initialEsJeruk.qty)

    // User speaks "Tambah 2 Es Jeruk Segar"
    vm.processVoiceCommand("Tambah 2 Es Jeruk Segar")

    val updatedEsJeruk = vm.orderItems.value.first { it.name == "Es Jeruk Segar" }
    assertEquals(3, updatedEsJeruk.qty)
    assertEquals(15000, updatedEsJeruk.total)
  }

  @Test
  fun `verify compound voice command adds multiple mapped products`() {
    val vm = WarungPosViewModel(app, repository)
    val prevCount = vm.orderItems.value.size

    // Spoken compound order
    vm.processVoiceCommand("1 Bakso Urat Malang dan 3 Gorengan")

    val bakso = vm.orderItems.value.find { it.name == "Bakso Urat Malang" }
    val gorengan = vm.orderItems.value.find { it.name == "Gorengan Gurih" }

    assertTrue(bakso != null)
    assertEquals(1, bakso?.qty)
    assertEquals(13000, bakso?.unitPrice)

    assertTrue(gorengan != null)
    assertEquals(3, gorengan?.qty)
    assertEquals(1000, gorengan?.unitPrice)
  }

  @Test
  fun `verify generate receipt pdf creates valid file with cart items`() {
    val vm = WarungPosViewModel(app, repository)
    val items = vm.orderItems.value
    val total = vm.calculateOrderTotal()

    val pdfFile = com.example.util.PdfReceiptManager.generateReceiptPdf(
      context = app,
      storeName = vm.storeName,
      items = items,
      totalAmount = total,
      transactionId = "TRX-PDF-TEST-001"
    )

    assertTrue(pdfFile != null)
    assertTrue(pdfFile?.exists() == true)
    assertTrue((pdfFile?.length() ?: 0L) > 0)
    assertTrue(pdfFile?.name?.endsWith(".pdf") == true)
  }

  @Test
  fun `verify download receipt pdf persists to storage`() {
    val vm = WarungPosViewModel(app, repository)
    val pdfFile = com.example.util.PdfReceiptManager.generateReceiptPdf(
      context = app,
      storeName = vm.storeName,
      items = vm.orderItems.value,
      totalAmount = vm.calculateOrderTotal()
    )
    assertTrue(pdfFile != null)

    val saved = com.example.util.PdfReceiptManager.openOrDownloadReceiptPdf(app, pdfFile!!)
    assertTrue(saved != null)
    assertTrue(saved?.exists() == true)
    assertTrue((saved?.length() ?: 0L) > 0)
  }
}
