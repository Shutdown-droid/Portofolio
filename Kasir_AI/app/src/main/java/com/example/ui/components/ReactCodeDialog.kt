package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.NeonCyan

const val REACT_COMPONENT_PREVIEW = """// POSWarungApp.jsx - 2026 Functional AI-powered Glassmorphism POS
import React, { useState } from 'react';
import { Plus, Minus, Trash2, Mic, Home, BarChart3, Printer, Bell, Sparkles, X, Download, Share2 } from 'lucide-react';

export default function GlassmorphismPOS() {
  // 1. STORE NAME: Strictly "Kantin SIJA"
  const [storeName] = useState("Kantin SIJA");
  const [activeTab, setActiveTab] = useState('kasir');

  // 2. Interactive Cart (Initialized with 2 default items)
  const [cartItems, setCartItems] = useState([
    { id: 1, name: 'Nasi Telor Dadar', price: 10000, qty: 2 },
    { id: 2, name: 'Es Jeruk Segar', price: 5000, qty: 1 }
  ]);

  // 3. DAILY TOTAL LOGIC: Initialized to 350000
  const [dailyTotal, setDailyTotal] = useState(350000);

  // Dynamic QRIS Trigger (Hidden by default)
  const [showQris, setShowQris] = useState(false);
  const [inputText, setInputText] = useState('');

  // Auto-calculated current cart subtotal
  const cartTotal = cartItems.reduce((acc, item) => acc + (item.price * item.qty), 0);

  const formatRupiah = (val) => new Intl.NumberFormat('id-ID', {
    style: 'currency', currency: 'IDR', maximumFractionDigits: 0
  }).format(val).replace('IDR', 'Rp');

  // '+' and '-' Quantity Controls (removes item if qty hits 0)
  const updateQuantity = (id, delta) => {
    setCartItems(prev => prev
      .map(item => item.id === id ? { ...item, qty: item.qty + delta } : item)
      .filter(item => item.qty > 0)
    );
  };

  // 3. "Cetak Struk" adds cartTotal to dailyTotal, then clears cart
  const handlePrintReceipt = () => {
    if (cartItems.length === 0) return;
    setDailyTotal(prev => prev + cartTotal);
    setCartItems([]);
  };

  const handleAddItem = (e) => {
    e?.preventDefault();
    if (!inputText.trim()) return;
    setCartItems(prev => [...prev, {
      id: Date.now(), name: inputText.trim(), price: 5000, qty: 1
    }]);
    setInputText('');
  };

  return (
    <div className="relative w-full max-w-md h-screen mx-auto overflow-hidden bg-gradient-to-br from-indigo-950 via-purple-900 to-teal-900 text-white font-sans flex flex-col">
      {/* Top Bar with 'Simulasi QRIS' developer trigger */}
      <div className="px-5 pt-3 flex justify-between items-center z-20">
        <span className="text-[11px] font-bold text-white/70">● {storeName} POS</span>
        <button onClick={() => setShowQris(true)} className="px-3 py-1 bg-white/10 border border-cyan-400/30 text-cyan-300 text-xs rounded-full">
          ⚡ Simulasi QRIS
        </button>
      </div>

      {/* 2. QRIS Smart Toast: Buttons say "Gabungkan" and "Pemasukan Baru" */}
      {showQris && (
        <div className="absolute top-12 left-0 right-0 w-[90%] mx-auto z-50 bg-slate-900/90 backdrop-blur-2xl border border-cyan-400/40 rounded-[2rem] p-4 shadow-xl">
          <h4>🔔 Uang Masuk QRIS: Rp 20.000</h4>
          <p className="text-xs text-white/70">Tambahkan ke pesanan ini?</p>
          <div className="flex gap-2 mt-2">
            <button onClick={() => { setShowQris(false); setCartItems(p => [...p, { id: Date.now(), name: 'QRIS', price: 20000, qty: 1 }]); }} className="flex-1 py-2 bg-blue-500 rounded-full font-bold">Gabungkan</button>
            <button onClick={() => { setShowQris(false); setDailyTotal(p => p + 20000); }} className="flex-1 py-2 bg-white/10 border border-white/20 rounded-full">Pemasukan Baru</button>
          </div>
        </div>
      )}

      {/* 1. Header Section (Store Name: Kantin SIJA & Dynamic Total Hari Ini) */}
      <header className="p-5 bg-white/10 backdrop-blur-xl border border-white/20 rounded-[2rem] m-4">
        <p className="opacity-60 text-xs font-semibold">{storeName}</p>
        <h1 className="text-2xl font-bold">Total Hari Ini: {formatRupiah(dailyTotal)}</h1>
      </header>

      {/* 2. Dynamic Cart / Receipt Card */}
      <div className="bg-emerald-500/10 backdrop-blur-xl border border-white/20 shadow-xl rounded-[2rem] p-5 m-4 flex-1 overflow-y-auto">
        {cartItems.map(item => (
          <div key={item.id} className="flex justify-between items-center my-2 p-2 bg-white/5 rounded-xl">
            <div>
              <p className="font-semibold">{item.name}</p>
              <p className="text-xs opacity-60">@{formatRupiah(item.price)}</p>
            </div>
            <div className="flex items-center gap-2">
              <button onClick={() => updateQuantity(item.id, -1)} className="w-6 h-6 bg-white/10 rounded-full">-</button>
              <span className="font-mono font-bold">{item.qty}</span>
              <button onClick={() => updateQuantity(item.id, 1)} className="w-6 h-6 bg-white/10 rounded-full">+</button>
              <span className="w-16 text-right font-mono font-bold">{formatRupiah(item.price * item.qty)}</span>
            </div>
          </div>
        ))}
        <div className="border-b border-dashed border-white/25 my-3" />
        <div className="flex justify-between text-lg font-bold">
          <span>Subtotal:</span>
          <span>{formatRupiah(cartTotal)}</span>
        </div>
        <button onClick={handlePrintReceipt} className="w-full mt-4 py-2.5 bg-gradient-to-r from-emerald-500/30 to-teal-500/30 border border-emerald-400/40 rounded-2xl font-bold">🖨️ Cetak Struk</button>
      </div>

      {/* 4. SOLID GLASSMORPHIC BOTTOM NAV WITH DARK OVERLAY BACKING */}
      <div className="fixed bottom-0 left-0 right-0 z-40 max-w-md mx-auto">
        <div className="absolute inset-0 bg-gradient-to-t from-indigo-950 via-indigo-950/90 to-transparent h-28 pointer-events-none" />
        <div className="relative px-5 pb-4">
          <nav className="w-[85%] mx-auto bg-slate-950/90 backdrop-blur-2xl border border-white/20 shadow-2xl rounded-full p-2 flex justify-around">
            <button onClick={() => setActiveTab('kasir')} className="font-bold">🏠 Kasir</button>
            <button onClick={() => setActiveTab('laporan')}>📊 Laporan</button>
          </nav>
        </div>
      </div>
    </div>
  );
}"""

@Composable
fun ReactCodeDialog(onDismiss: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    val shape = RoundedCornerShape(28.dp)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .fillMaxHeight(0.85f)
                .clip(shape)
                .background(Color(0xFF0F0E26))
                .border(1.dp, Color.White.copy(alpha = 0.3f), shape)
                .padding(18.dp)
                .testTag("react_code_dialog")
        ) {
            Column(modifier = Modifier.fillMaxHeight()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Code",
                            tint = CyanGlow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "React/Tailwind Component (2026)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Tersimpan juga di root file /POSWarungApp.jsx",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                // Code Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF070617))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = REACT_COMPONENT_PREVIEW,
                        color = Color(0xFF67E8F9),
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .verticalScroll(verticalScroll)
                            .horizontalScroll(horizontalScroll)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(REACT_COMPONENT_PREVIEW))
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Salin Kode React",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
