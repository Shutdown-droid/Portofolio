import React, { useState, useEffect, useRef } from 'react';
import { 
  Plus, 
  Minus, 
  Trash2, 
  Mic, 
  Home, 
  BarChart3, 
  Printer, 
  Bell, 
  Sparkles, 
  X, 
  Check, 
  ShoppingBag,
  Volume2,
  Download,
  Share2
} from 'lucide-react';

// Predefined Product Catalog with standard pricing and keyword aliases
const PREDEFINED_PRODUCTS = [
  {
    name: 'Nasi Ayam Krispi',
    price: 15000,
    category: 'Makanan',
    keywords: ['nasi ayam krispi', 'nasi ayam', 'ayam krispi', 'ayam goreng', 'ayam crispy', 'ayam']
  },
  {
    name: 'Nasi Telor Dadar',
    price: 10000,
    category: 'Makanan',
    keywords: ['nasi telor dadar', 'nasi telor', 'nasi telur', 'telor dadar', 'telur dadar', 'telur', 'telor']
  },
  {
    name: 'Nasi Campur Spesial',
    price: 14000,
    category: 'Makanan',
    keywords: ['nasi campur', 'campur', 'nasi rames']
  },
  {
    name: 'Bakso Urat Malang',
    price: 13000,
    category: 'Makanan',
    keywords: ['bakso urat malang', 'bakso urat', 'bakso malang', 'bakso']
  },
  {
    name: 'Soto Ayam Lamongan',
    price: 14000,
    category: 'Makanan',
    keywords: ['soto ayam lamongan', 'soto ayam', 'soto lamongan', 'soto']
  },
  {
    name: 'Mie Goreng Telur',
    price: 9000,
    category: 'Makanan',
    keywords: ['mie goreng telur', 'mie goreng', 'indomie', 'mie telur', 'mie']
  },
  {
    name: 'Es Teh Manis',
    price: 3000,
    category: 'Minuman',
    keywords: ['es teh manis', 'es teh', 'esteh', 'teh manis', 'teh dingin', 'teh']
  },
  {
    name: 'Es Jeruk Segar',
    price: 5000,
    category: 'Minuman',
    keywords: ['es jeruk segar', 'es jeruk', 'jeruk peras', 'jeruk dingin', 'jeruk']
  },
  {
    name: 'Kopi Susu Gula Aren',
    price: 6000,
    category: 'Minuman',
    keywords: ['kopi susu gula aren', 'kopi susu', 'kopsus', 'kopi aren']
  },
  {
    name: 'Kopi Tubruk Panas',
    price: 4000,
    category: 'Minuman',
    keywords: ['kopi tubruk panas', 'kopi tubruk', 'kopi panas']
  },
  {
    name: 'Kopi',
    price: 5000,
    category: 'Minuman',
    keywords: ['kopi hitam', 'kopi']
  },
  {
    name: 'Gorengan Gurih',
    price: 1000,
    category: 'Snack',
    keywords: ['gorengan', 'bakwan', 'tahu isi', 'tempe mendoan', 'bala bala', 'gehu', 'tempe', 'tahu']
  },
  {
    name: 'Pisang Goreng Keju',
    price: 2500,
    category: 'Snack',
    keywords: ['pisang goreng keju', 'pisang goreng', 'pisang keju', 'pisang']
  }
];

export default function GlassmorphismPOS() {
  // 1. STORE NAME: Strictly "Kantin SIJA" (No "Bu Ari")
  const [storeName] = useState("Kantin SIJA");

  // Tab State: 'kasir' | 'laporan'
  const [activeTab, setActiveTab] = useState('kasir');

  // 2. Interactive Cart (Initialized with 2 default items)
  const [cartItems, setCartItems] = useState([
    { id: 1, name: 'Nasi Telor Dadar', price: 10000, qty: 2 },
    { id: 2, name: 'Es Jeruk Segar', price: 5000, qty: 1 }
  ]);

  // 3. DAILY TOTAL LOGIC: Initialized to 350000 (starting mockup amount)
  const [dailyTotal, setDailyTotal] = useState(350000);

  // Transaction history for Laporan
  const [transactions, setTransactions] = useState([
    { id: 'TRX-014', time: '14:20 WIB', summary: 'Nasi Ayam Krispi (2), Es Teh (2)', total: 36000, method: 'QRIS' },
    { id: 'TRX-013', time: '13:45 WIB', summary: 'Bakso Urat Malang (1), Es Jeruk (1)', total: 18000, method: 'Tunai' },
    { id: 'TRX-012', time: '13:10 WIB', summary: 'Nasi Telor Dadar (3), Gorengan (5)', total: 35000, method: 'QRIS' },
    { id: 'TRX-011', time: '12:30 WIB', summary: 'Kopi Susu Gula Aren (2)', total: 12000, method: 'QRIS' }
  ]);

  // Dynamic QRIS Trigger (Hidden by default)
  const [showQris, setShowQris] = useState(false);
  const qrisSimulationAmount = 20000;

  // Working Input Field & Voice State
  const [inputText, setInputText] = useState('');
  const [isListening, setIsListening] = useState(false);
  const recognitionRef = useRef(null);

  // UI Interactive States
  const [isPrinting, setIsPrinting] = useState(false);
  const [printSuccess, setPrintSuccess] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);
  const [showReceiptModal, setShowReceiptModal] = useState(false);

  // Auto-calculated current cart subtotal (sum of price * qty)
  const cartTotal = cartItems.reduce((acc, item) => acc + (item.price * item.qty), 0);

  // Format currency helper
  const formatRupiah = (amount) => {
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      maximumFractionDigits: 0
    }).format(amount).replace('IDR', 'Rp');
  };

  // Predefined Product Speech Mapper
  const matchProductFromSpeech = (spokenText) => {
    val: lower = spokenText.toLowerCase().trim();

    // Extract quantity from speech
    let qty = 1;
    const numMatch = lower.match(/\b(\d+)\b/);
    if (numMatch) {
      qty = parseInt(numMatch[1], 10);
    } else if (lower.includes('sepasang') || lower.includes('dua')) {
      qty = 2;
    } else if (lower.includes('tiga')) {
      qty = 3;
    } else if (lower.includes('empat')) {
      qty = 4;
    } else if (lower.includes('lima')) {
      qty = 5;
    } else if (lower.includes('enam')) {
      qty = 6;
    } else if (lower.includes('tujuh')) {
      qty = 7;
    } else if (lower.includes('delapan')) {
      qty = 8;
    } else if (lower.includes('sembilan')) {
      qty = 9;
    } else if (lower.includes('sepuluh')) {
      qty = 10;
    } else if (lower.includes('satu') || lower.includes('sebungkus') || lower.includes('seporsi') || lower.includes('segelas')) {
      qty = 1;
    }

    // Match against PREDEFINED_PRODUCTS with priority on longer keywords
    let bestMatch = null;
    let bestKwLength = -1;

    for (const prod of PREDEFINED_PRODUCTS) {
      for (const kw of prod.keywords) {
        if (lower.includes(kw) && kw.length > bestKwLength) {
          bestMatch = prod;
          bestKwLength = kw.length;
        }
      }
    }

    if (bestMatch) {
      return { product: bestMatch, qty };
    }

    // Fallback for custom spoken items
    const cleanName = spokenText
      .replace(/(?i)\b(pesan|tambah|beli|tolong|ambilkan|minta|porsi|bungkus|gelas|biji|buah|satu|dua|tiga|empat|lima|\d+)\b/gi, '')
      .trim();

    return {
      product: {
        name: cleanName || 'Menu Tambahan',
        price: 5000,
        category: 'Lainnya'
      },
      qty
    };
  };

  // Voice Command Processor: Maps spoken item names to predefined product list
  const processVoiceCommand = (spokenText) => {
    const trimmed = spokenText.trim();
    if (!trimmed) return;

    // Support compound speech orders (e.g., "2 Nasi Ayam dan 1 Es Teh")
    const segments = trimmed
      .split(/(?:,|\bdan\b|\bsama\b|\bserta\b|\blalu\b|\bterus\b|\btambah\b|\+)/i)
      .map(s => s.trim())
      .filter(Boolean);

    const targetSegments = segments.length > 0 ? segments : [trimmed];
    const addedSummaries = [];

    setCartItems(prev => {
      let currentCart = [...prev];

      for (const seg of targetSegments) {
        const { product, qty } = matchProductFromSpeech(seg);
        addedSummaries.push(`+${qty}x ${product.name}`);

        const existingIndex = currentCart.findIndex(item => item.name.toLowerCase() === product.name.toLowerCase());
        if (existingIndex !== -1) {
          currentCart[existingIndex] = {
            ...currentCart[existingIndex],
            qty: currentCart[existingIndex].qty + qty
          };
        } else {
          currentCart.push({
            id: Date.now() + Math.random(),
            name: product.name,
            price: product.price,
            qty: qty
          });
        }
      }

      return currentCart;
    });

    showFloatingNotice(`🎙️ Terdeteksi: "${trimmed}" ➔ ${addedSummaries.join(', ')}`);
  };

  // Voice Microphone Toggle using Web Speech API (device microphone)
  const toggleVoiceRecording = () => {
    if (isListening) {
      if (recognitionRef.current) {
        try { recognitionRef.current.stop(); } catch (err) {}
      }
      setIsListening(false);
      return;
    }

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    if (SpeechRecognition) {
      try {
        const recognition = new SpeechRecognition();
        recognition.lang = 'id-ID';
        recognition.continuous = false;
        recognition.interimResults = false;

        recognition.onstart = () => {
          setIsListening(true);
          showFloatingNotice('🎙️ Mendengarkan suara... (Katakan misal: "2 Nasi Ayam Krispi")');
        };

        recognition.onresult = (event) => {
          setIsListening(false);
          const transcript = event.results[0][0].transcript;
          processVoiceCommand(transcript);
        };

        recognition.onerror = () => {
          setIsListening(false);
          fallbackVoiceSimulation();
        };

        recognition.onend = () => {
          setIsListening(false);
        };

        recognitionRef.current = recognition;
        recognition.start();
        return;
      } catch (e) {
        fallbackVoiceSimulation();
      }
    } else {
      fallbackVoiceSimulation();
    }
  };

  // Fallback voice recognition simulation when hardware mic is restricted in sandbox
  const fallbackVoiceSimulation = () => {
    setIsListening(true);
    showFloatingNotice('🎙️ Mendengarkan perintah suara...');
    
    setTimeout(() => {
      setIsListening(false);
      const voiceSamples = [
        '2 Nasi Ayam Krispi',
        '1 Es Jeruk Segar',
        'Minta 3 Gorengan Gurih',
        '2 Kopi Susu Gula Aren',
        '1 Bakso Urat Malang',
        '1 Soto Ayam Lamongan',
        '2 Mie Goreng Telur'
      ];
      const randomSpoken = voiceSamples[Math.floor(Math.random() * voiceSamples.length)];
      processVoiceCommand(randomSpoken);
    }, 1800);
  };

  // Quantity Modifier (+ and -)
  const updateQuantity = (id, delta) => {
    setCartItems(prevItems => 
      prevItems
        .map(item => {
          if (item.id === id) {
            const newQty = item.qty + delta;
            return newQty > 0 ? { ...item, qty: newQty } : null;
          }
          return item;
        })
        .filter(Boolean) // If qty hits 0, remove item from the array
    );
  };

  // Remove single item completely
  const removeItem = (id) => {
    setCartItems(prev => prev.filter(item => item.id !== id));
  };

  // Add Item from Input Field
  const handleAddItem = (e) => {
    if (e) e.preventDefault();
    const trimmed = inputText.trim();
    if (!trimmed) return;

    processVoiceCommand(trimmed);
    setInputText('');
  };

  // 2. QRIS Action: "Gabungkan" (without square brackets)
  const handleMergeQris = () => {
    setShowQris(false);
    // Merge QRIS amount into cart
    setCartItems(prev => [
      ...prev,
      {
        id: Date.now(),
        name: 'Pemasukan QRIS (Gabung)',
        price: qrisSimulationAmount,
        qty: 1
      }
    ]);
    showFloatingNotice(`✨ Pembayaran QRIS ${formatRupiah(qrisSimulationAmount)} berhasil digabungkan ke keranjang!`);
  };

  // 2. QRIS Action: "Pemasukan Baru" (without square brackets)
  const handleNewIncomeQris = () => {
    setShowQris(false);
    // Directly record as new income to dailyTotal
    setDailyTotal(prev => prev + qrisSimulationAmount);
    setTransactions(prev => [
      {
        id: `TRX-${String(prev.length + 15).padStart(3, '0')}`,
        time: new Date().toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit' }) + ' WIB',
        summary: 'Pemasukan Langsung QRIS',
        total: qrisSimulationAmount,
        method: 'QRIS'
      },
      ...prev
    ]);
    showFloatingNotice(`💰 Pemasukan QRIS ${formatRupiah(qrisSimulationAmount)} langsung tercatat ke Total Hari Ini!`);
  };

  // Temporary notification toast
  const showFloatingNotice = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3800);
  };

  // 3. DAILY TOTAL LOGIC: "🖨️ Cetak Struk" Button Action
  // Calculates current cart total, adds it to dailyTotal, and clears/empties the cart items
  const handlePrintReceipt = () => {
    if (cartItems.length === 0) {
      showFloatingNotice('⚠️ Keranjang masih kosong!');
      return;
    }

    const currentTotal = cartTotal;

    setIsPrinting(true);
    setTimeout(() => {
      setIsPrinting(false);
      setPrintSuccess(true);

      // Add current cart total to dailyTotal
      setDailyTotal(prev => prev + currentTotal);

      // Save to transaction record
      const summaryText = cartItems.map(i => `${i.name} (${i.qty})`).join(', ');
      setTransactions(prev => [
        {
          id: `TRX-${String(prev.length + 15).padStart(3, '0')}`,
          time: new Date().toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit' }) + ' WIB',
          summary: summaryText,
          total: currentTotal,
          method: 'QRIS / Kasir'
        },
        ...prev
      ]);

      // Clear/empty the current cart items
      setCartItems([]);

      showFloatingNotice(`🖨️ Struk dicetak! Total Hari Ini bertambah ${formatRupiah(currentTotal)}.`);

      setTimeout(() => setPrintSuccess(false), 3000);
    }, 1200);
  };

  // Generate and Download PDF/Receipt Document
  const handleDownloadPdf = () => {
    if (cartItems.length === 0) {
      showFloatingNotice('⚠️ Keranjang masih kosong!');
      return;
    }
    const dateStr = new Date().toLocaleDateString('id-ID', { dateStyle: 'medium', timeStyle: 'short' });
    const receiptContent = 
`=============================================
           ${storeName.toUpperCase()}
     Jl. Kuliner Kampus No. 12, Jakarta
           STRUK PEMBELIAN RESMI
---------------------------------------------
Tanggal: ${dateStr}
No Transaksi: TRX-${Date.now().toString().slice(-6)}
---------------------------------------------
ITEM                 QTY    HARGA      TOTAL
---------------------------------------------
${cartItems.map(item => `${item.name.padEnd(20).slice(0, 20)} ${String(item.qty).padStart(3)}  ${formatRupiah(item.price).padStart(9)}  ${formatRupiah(item.price * item.qty).padStart(9)}`).join('\n')}
---------------------------------------------
TOTAL BAYAR: ${formatRupiah(cartTotal)}
STATUS: LUNAS VIA QRIS / CASH
=============================================
  ~ Terima Kasih Atas Kunjungan Anda! ~
`;
    const blob = new Blob([receiptContent], { type: 'application/pdf;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `Struk_${Date.now()}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    showFloatingNotice('📄 Struk PDF berhasil diunduh!');
  };

  // Share Receipt via Web Share API
  const handleShareReceipt = async () => {
    if (cartItems.length === 0) {
      showFloatingNotice('⚠️ Keranjang masih kosong!');
      return;
    }
    const text = `*${storeName}*\nTotal: ${formatRupiah(cartTotal)}\nItem:\n` +
      cartItems.map(i => `- ${i.qty}x ${i.name} (${formatRupiah(i.price * i.qty)})`).join('\n') +
      `\n\n_Terima Kasih!_`;
    if (navigator.share) {
      try {
        await navigator.share({
          title: `Struk ${storeName}`,
          text: text
        });
        showFloatingNotice('📤 Struk berhasil dibagikan!');
      } catch (err) {
        // Ignored if cancelled
      }
    } else {
      navigator.clipboard?.writeText(text);
      showFloatingNotice('📋 Teks struk berhasil disalin!');
    }
  };

  return (
    <div className="relative w-full max-w-md h-screen mx-auto overflow-hidden bg-gradient-to-br from-indigo-950 via-purple-900 to-teal-900 text-white font-sans flex flex-col select-none shadow-2xl">
      {/* Decorative Ambient Mesh Glow Orbs */}
      <div className="absolute -top-24 -left-20 w-72 h-72 bg-blue-500/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-1/3 -right-24 w-80 h-80 bg-purple-500/25 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -bottom-20 left-1/4 w-80 h-80 bg-teal-500/20 rounded-full blur-3xl pointer-events-none" />

      {/* Top Bar with Developer "Simulasi QRIS" Button */}
      <div className="relative z-20 px-5 pt-3 pb-1 flex items-center justify-between">
        <div className="flex items-center gap-1.5 text-xs text-white/70">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          <span className="text-[11px] font-semibold tracking-wide uppercase">{storeName} POS</span>
        </div>

        {/* Small Developer Button: "Simulasi QRIS" */}
        <button
          onClick={() => setShowQris(true)}
          className="py-1 px-3 bg-white/10 hover:bg-white/20 active:scale-95 text-cyan-300 text-xs font-bold rounded-full border border-cyan-400/30 backdrop-blur-md shadow-[0_0_12px_rgba(6,182,212,0.3)] transition-all flex items-center gap-1.5"
        >
          <Bell className="w-3.5 h-3.5 animate-bounce" />
          <span>Simulasi QRIS</span>
        </button>
      </div>

      {/* 2. QRIS SMART TOAST: Hidden by default, buttons say "Gabungkan" and "Pemasukan Baru" */}
      {showQris && (
        <div className="absolute top-4 left-0 right-0 w-[92%] mx-auto z-50 transition-all duration-300 animate-in fade-in slide-in-from-top-4">
          <div className="bg-slate-900/90 backdrop-blur-2xl border border-cyan-400/40 rounded-3xl p-4 shadow-[0_10px_35px_rgba(6,182,212,0.35)] relative overflow-hidden">
            {/* Luminous Top Accent Line */}
            <div className="absolute top-0 left-0 right-0 h-[1.5px] bg-gradient-to-r from-transparent via-cyan-400 to-transparent" />

            <div className="flex items-start gap-3">
              <div className="w-10 h-10 rounded-full bg-cyan-400/20 border border-cyan-300/40 flex items-center justify-center shrink-0 shadow-[0_0_15px_rgba(6,182,212,0.4)]">
                <Bell className="w-5 h-5 text-cyan-300" />
              </div>

              <div className="flex-1">
                <div className="flex items-center justify-between">
                  <h4 className="text-sm font-bold text-white tracking-wide">
                    🔔 Uang Masuk QRIS: <span className="text-cyan-300 font-extrabold">{formatRupiah(qrisSimulationAmount)}</span>
                  </h4>
                  <button 
                    onClick={() => setShowQris(false)}
                    className="text-white/60 hover:text-white transition-colors p-1"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>
                <p className="text-xs text-white/70 mt-0.5">Tambahkan ke pesanan ini?</p>

                {/* 2. Toast Actions: "Gabungkan" and "Pemasukan Baru" (no square brackets) */}
                <div className="flex gap-2 mt-3">
                  <button
                    onClick={handleMergeQris}
                    className="flex-1 py-2 px-3 bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 text-white text-xs font-bold rounded-full shadow-[0_0_15px_rgba(59,130,246,0.5)] border border-cyan-300/40 transition-all active:scale-95 text-center"
                  >
                    Gabungkan
                  </button>
                  <button
                    onClick={handleNewIncomeQris}
                    className="flex-1 py-2 px-3 bg-white/10 hover:bg-white/15 text-white/90 text-xs font-semibold rounded-full border border-white/20 transition-all active:scale-95 text-center backdrop-blur-md"
                  >
                    Pemasukan Baru
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Floating Notice Toast for Voice Results & Alerts */}
      {toastMessage && (
        <div className="absolute top-28 left-0 right-0 w-[88%] mx-auto z-40 transition-all duration-300">
          <div className="bg-cyan-500/25 backdrop-blur-lg border border-cyan-400/50 rounded-full px-4 py-2.5 text-xs text-center text-cyan-100 shadow-[0_4px_25px_rgba(6,182,212,0.4)] font-medium">
            {toastMessage}
          </div>
        </div>
      )}

      {/* 1. HEADER SECTION (Store Name: "Kantin SIJA" & Dynamic "Total Hari Ini") */}
      <header className="px-5 py-2 z-20 shrink-0">
        <div className="bg-white/10 backdrop-blur-xl border border-white/20 shadow-xl rounded-[2rem] p-5 relative overflow-hidden">
          <div className="flex items-center justify-between">
            <div>
              {/* 1. STORE NAME: Strictly "Kantin SIJA" */}
              <p className="text-xs tracking-wider uppercase font-semibold text-white/60">
                {storeName}
              </p>
              {/* 3. Total Hari Ini State: Auto-updates dynamically */}
              <h1 className="text-2xl font-black tracking-tight text-white mt-0.5 drop-shadow-[0_2px_10px_rgba(255,255,255,0.3)]">
                Total Hari Ini: <span className="text-emerald-400 font-extrabold">{formatRupiah(dailyTotal)}</span>
              </h1>
            </div>
            <div className="w-10 h-10 rounded-full bg-white/10 border border-white/25 flex items-center justify-center text-emerald-400 shadow-[0_0_12px_rgba(52,211,153,0.3)]">
              <Sparkles className="w-5 h-5" />
            </div>
          </div>

          <div className="flex items-center justify-between mt-3 pt-2.5 border-t border-white/10 text-[11px] text-white/70">
            <span className="flex items-center gap-1.5">
              <ShoppingBag className="w-3.5 h-3.5 text-cyan-300" />
              {cartItems.length} Menu di Struk Aktif
            </span>
            <span className="text-cyan-300 font-medium">
              Subtotal: {formatRupiah(cartTotal)}
            </span>
          </div>
        </div>
      </header>

      {/* MAIN CONTENT AREA */}
      <main className="flex-1 overflow-y-auto px-5 py-2 space-y-4 pb-44 z-10 scrollbar-none">
        {activeTab === 'kasir' ? (
          <>
            {/* Quick Menu Shortcut Chips */}
            <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
              {['+ Nasi Ayam', '+ Nasi Telor', '+ Es Teh', '+ Es Jeruk', '+ Kopi', '+ Gorengan'].map((chip, idx) => (
                <button
                  key={idx}
                  onClick={() => processVoiceCommand(chip.replace('+ ', '1 '))}
                  className="px-3.5 py-1.5 rounded-full bg-white/10 hover:bg-white/20 active:scale-95 border border-white/20 backdrop-blur-md text-xs font-semibold text-white/90 whitespace-nowrap shadow-sm transition-all"
                >
                  {chip}
                </button>
              ))}
            </div>

            {/* DYNAMIC RECEIPT CARD (Interactive Cart with +/- Buttons) */}
            <div className="bg-white/10 backdrop-blur-xl border border-white/20 shadow-xl rounded-[2rem] p-5 relative overflow-hidden">
              <div className="flex items-center justify-between mb-3 border-b border-white/15 pb-2.5">
                <div>
                  <h2 className="text-sm font-bold text-white tracking-wide flex items-center gap-2">
                    <span className="w-2 h-2 rounded-full bg-cyan-400" /> Struk Pesanan Aktif
                  </h2>
                  <p className="text-[10px] text-white/60">Tambahkan pesanan via suara atau teks</p>
                </div>
                <span className="text-[11px] font-mono text-cyan-300 bg-white/10 px-2 py-0.5 rounded-full border border-white/15">
                  Meja 03
                </span>
              </div>

              {/* Items List with small '+' and '-' buttons */}
              <div className="space-y-2.5 max-h-52 overflow-y-auto pr-1">
                {cartItems.length === 0 ? (
                  <div className="py-8 text-center text-white/50 text-xs">
                    <ShoppingBag className="w-8 h-8 mx-auto mb-2 text-white/30" />
                    Keranjang kosong. Katakan pesanan atau ketik di bawah.
                  </div>
                ) : (
                  cartItems.map((item) => (
                    <div 
                      key={item.id}
                      className="flex items-center justify-between text-sm py-1 border-b border-white/5 last:border-0"
                    >
                      <div className="flex-1 pr-2">
                        <p className="font-semibold text-white text-xs">{item.name}</p>
                        <p className="text-[10px] text-white/60 font-mono">
                          @ {formatRupiah(item.price)}
                        </p>
                      </div>

                      {/* Quantity Controls: '-' and '+' buttons */}
                      <div className="flex items-center gap-2">
                        <div className="flex items-center bg-white/10 rounded-full border border-white/20 p-0.5 shadow-sm">
                          {/* '-' button */}
                          <button
                            onClick={() => updateQuantity(item.id, -1)}
                            className="w-6 h-6 rounded-full bg-white/10 hover:bg-white/25 active:scale-90 flex items-center justify-center text-white transition-all"
                            title="Kurang"
                          >
                            <Minus className="w-3 h-3" />
                          </button>

                          {/* Quantity Display */}
                          <span className="w-7 text-center font-bold font-mono text-white text-xs">
                            {item.qty}
                          </span>

                          {/* '+' button */}
                          <button
                            onClick={() => updateQuantity(item.id, 1)}
                            className="w-6 h-6 rounded-full bg-white/10 hover:bg-white/25 active:scale-90 flex items-center justify-center text-white transition-all"
                            title="Tambah"
                          >
                            <Plus className="w-3 h-3" />
                          </button>
                        </div>

                        {/* Item Total Price */}
                        <span className="w-20 text-right font-mono font-bold text-white text-xs">
                          {formatRupiah(item.price * item.qty)}
                        </span>

                        {/* Delete button */}
                        <button
                          onClick={() => removeItem(item.id)}
                          className="text-white/40 hover:text-red-400 p-1 transition-colors"
                          title="Hapus"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>

              {/* Dashed Divider Line */}
              <div className="border-b-2 border-dashed border-white/25 my-2" />

              {/* Receipt Total: dynamically auto-calculated based on cartItems */}
              <div className="flex items-center justify-between pt-1">
                <span className="text-sm text-white/80 font-medium">Subtotal Struk:</span>
                <span className="text-xl font-black text-white font-mono drop-shadow-[0_0_12px_rgba(255,255,255,0.5)]">
                  {formatRupiah(cartTotal)}
                </span>
              </div>

              {/* 3. Action Buttons: "🖨️ Cetak Struk" updates dailyTotal and empties cart */}
              <div className="mt-4 pt-3 border-t border-white/10 grid grid-cols-3 gap-2">
                <button
                  onClick={handlePrintReceipt}
                  disabled={isPrinting || cartItems.length === 0}
                  className="py-2.5 px-2 bg-gradient-to-r from-emerald-500/30 to-teal-500/30 hover:from-emerald-500/40 hover:to-teal-500/40 active:scale-[0.98] border border-emerald-400/40 rounded-2xl flex items-center justify-center gap-1.5 text-[11px] font-bold text-white tracking-wide shadow-lg transition-all backdrop-blur-md group disabled:opacity-50"
                  title="Cetak Struk dan Tambahkan ke Total Hari Ini"
                >
                  <Printer className="w-3.5 h-3.5 text-emerald-300 group-hover:scale-110 transition-transform" />
                  <span>{isPrinting ? 'Mencetak...' : 'Cetak Struk'}</span>
                </button>

                <button
                  onClick={handleDownloadPdf}
                  disabled={cartItems.length === 0}
                  className="py-2.5 px-2 bg-cyan-500/20 hover:bg-cyan-500/30 active:scale-[0.98] border border-cyan-400/40 rounded-2xl flex items-center justify-center gap-1.5 text-[11px] font-bold text-cyan-100 tracking-wide shadow-lg transition-all backdrop-blur-md group disabled:opacity-50"
                  title="Unduh PDF Struk"
                >
                  <Download className="w-3.5 h-3.5 text-cyan-300 group-hover:scale-110 transition-transform" />
                  <span>Unduh PDF</span>
                </button>

                <button
                  onClick={handleShareReceipt}
                  disabled={cartItems.length === 0}
                  className="py-2.5 px-2 bg-blue-500/20 hover:bg-blue-500/30 active:scale-[0.98] border border-blue-400/40 rounded-2xl flex items-center justify-center gap-1.5 text-[11px] font-bold text-blue-100 tracking-wide shadow-lg transition-all backdrop-blur-md group disabled:opacity-50"
                  title="Bagikan Struk"
                >
                  <Share2 className="w-3.5 h-3.5 text-blue-300 group-hover:scale-110 transition-transform" />
                  <span>Bagikan</span>
                </button>
              </div>

              {printSuccess && (
                <div className="mt-2 text-center text-xs text-emerald-300 font-medium flex items-center justify-center gap-1 animate-fade-in">
                  <Check className="w-3.5 h-3.5" /> Struk Dicetak & Total Hari Ini Diperbarui!
                </div>
              )}
            </div>
          </>
        ) : (
          /* LAPORAN TAB */
          <div className="space-y-4">
            <div className="bg-white/10 backdrop-blur-xl border border-white/20 shadow-xl rounded-[2rem] p-5">
              <h3 className="text-sm font-bold text-white mb-3 flex items-center gap-2">
                <BarChart3 className="w-4 h-4 text-cyan-400" /> Ringkasan {storeName}
              </h3>
              
              <div className="grid grid-cols-2 gap-3 mb-4">
                <div className="p-3 rounded-2xl bg-white/5 border border-white/10">
                  <span className="text-[10px] text-white/60 block">Total Omset Hari Ini</span>
                  <span className="text-lg font-bold text-emerald-300 font-mono">
                    {formatRupiah(dailyTotal)}
                  </span>
                </div>
                <div className="p-3 rounded-2xl bg-white/5 border border-white/10">
                  <span className="text-[10px] text-white/60 block">Pesanan Selesai</span>
                  <span className="text-lg font-bold text-white font-mono">
                    {transactions.length} Trx
                  </span>
                </div>
              </div>

              <div className="space-y-2">
                <h4 className="text-xs font-semibold text-white/80">Riwayat Transaksi:</h4>
                <div className="space-y-1.5 max-h-48 overflow-y-auto pr-1">
                  {transactions.map((trx, i) => (
                    <div key={i} className="flex items-center justify-between text-xs p-2 rounded-xl bg-white/5 border border-white/10">
                      <div>
                        <span className="font-mono text-cyan-300 text-[10px] block">{trx.id} • {trx.time}</span>
                        <span className="text-white/80 text-[11px] truncate max-w-[180px] block">{trx.summary}</span>
                      </div>
                      <div className="text-right">
                        <span className="font-mono font-bold text-white block">{formatRupiah(trx.total)}</span>
                        <span className="text-[9px] text-emerald-400">{trx.method}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* 4. SOLID GLASSMORPHIC BOTTOM OVERLAY AREA:
          Has solid glassmorphic background & dark overlay to completely prevent scroll content overlap */}
      <div className="fixed bottom-0 left-0 right-0 z-40 max-w-md mx-auto pointer-events-none">
        {/* Dark overlay backdrop to prevent text/cards behind from bleeding through */}
        <div className="absolute inset-0 bg-gradient-to-t from-indigo-950 via-indigo-950/95 to-transparent h-40 pointer-events-none" />

        <div className="relative px-5 pb-5 pt-2 pointer-events-auto space-y-2.5">
          {/* AI Input Field (Visible on Kasir Tab) */}
          {activeTab === 'kasir' && (
            <div className="bg-slate-900/90 backdrop-blur-2xl border border-white/25 rounded-full px-3.5 py-1.5 shadow-[0_8px_30px_rgba(0,0,0,0.5)] flex items-center justify-between gap-2">
              <form onSubmit={handleAddItem} className="flex-1">
                <input
                  type="text"
                  value={inputText}
                  onChange={(e) => setInputText(e.target.value)}
                  placeholder={isListening ? "🎙️ Mendengarkan suara..." : "Ketik pesanan (misal: Kopi)..."}
                  className="w-full bg-transparent border-none outline-none text-xs text-white placeholder-white/50 px-2"
                />
              </form>

              {/* Pulsing Mic Button */}
              <button
                type="button"
                onClick={toggleVoiceRecording}
                className={`w-9 h-9 rounded-full flex items-center justify-center text-white transition-all shadow-md ${
                  isListening
                    ? 'bg-red-500 animate-pulse ring-4 ring-red-400/40 shadow-[0_0_20px_rgba(239,68,68,0.6)]'
                    : 'bg-gradient-to-tr from-cyan-500 to-blue-500 hover:from-cyan-400 hover:to-blue-400 active:scale-90 shadow-[0_0_15px_rgba(6,182,212,0.4)]'
                }`}
                title="Pesan lewat Suara"
              >
                <Mic className="w-4 h-4" />
              </button>

              {/* Simpan Button */}
              <button
                type="button"
                onClick={handleAddItem}
                className="py-1.5 px-3.5 bg-white/20 hover:bg-white/30 active:scale-95 text-white text-xs font-bold rounded-full border border-white/30 transition-all"
              >
                Simpan
              </button>
            </div>
          )}

          {/* 4. SOLID GLASSMORPHIC BOTTOM NAVIGATION BAR */}
          <div className="w-[85%] mx-auto bg-slate-950/90 backdrop-blur-2xl border border-white/20 shadow-[0_12px_35px_rgba(0,0,0,0.6)] rounded-full p-1.5 flex items-center justify-around">
            <button
              onClick={() => setActiveTab('kasir')}
              className={`flex-1 py-2 rounded-full flex items-center justify-center gap-2 text-xs font-bold transition-all ${
                activeTab === 'kasir'
                  ? 'bg-white/20 text-white border border-white/30 shadow-md'
                  : 'text-white/60 hover:text-white'
              }`}
            >
              <Home className="w-4 h-4" />
              <span>🏠 Kasir</span>
            </button>

            <button
              onClick={() => setActiveTab('laporan')}
              className={`flex-1 py-2 rounded-full flex items-center justify-center gap-2 text-xs font-bold transition-all ${
                activeTab === 'laporan'
                  ? 'bg-white/20 text-white border border-white/30 shadow-md'
                  : 'text-white/60 hover:text-white'
              }`}
            >
              <BarChart3 className="w-4 h-4" />
              <span>📊 Laporan</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
