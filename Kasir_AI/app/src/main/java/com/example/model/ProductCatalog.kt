package com.example.model

data class ProductDefinition(
    val name: String,
    val defaultPrice: Int,
    val category: String,
    val keywords: List<String>
)

object PredefinedProducts {
    val CATALOG: List<ProductDefinition> = listOf(
        ProductDefinition(
            name = "Nasi Ayam Krispi",
            defaultPrice = 15000,
            category = "Makanan",
            keywords = listOf("nasi ayam krispi", "nasi ayam", "ayam krispi", "ayam goreng", "ayam crispy", "ayam")
        ),
        ProductDefinition(
            name = "Nasi Telor Dadar",
            defaultPrice = 10000,
            category = "Makanan",
            keywords = listOf("nasi telor dadar", "nasi telor", "nasi telur", "telor dadar", "telur dadar", "telur", "telor")
        ),
        ProductDefinition(
            name = "Nasi Campur Spesial",
            defaultPrice = 14000,
            category = "Makanan",
            keywords = listOf("nasi campur", "campur", "nasi rames")
        ),
        ProductDefinition(
            name = "Bakso Urat Malang",
            defaultPrice = 13000,
            category = "Makanan",
            keywords = listOf("bakso urat malang", "bakso urat", "bakso malang", "bakso")
        ),
        ProductDefinition(
            name = "Soto Ayam Lamongan",
            defaultPrice = 14000,
            category = "Makanan",
            keywords = listOf("soto ayam lamongan", "soto ayam", "soto lamongan", "soto")
        ),
        ProductDefinition(
            name = "Mie Goreng Telur",
            defaultPrice = 9000,
            category = "Makanan",
            keywords = listOf("mie goreng telur", "mie goreng", "indomie", "mie telur", "mie")
        ),
        ProductDefinition(
            name = "Es Teh Manis",
            defaultPrice = 3000,
            category = "Minuman",
            keywords = listOf("es teh manis", "es teh", "esteh", "teh manis", "teh dingin", "teh")
        ),
        ProductDefinition(
            name = "Es Jeruk Segar",
            defaultPrice = 5000,
            category = "Minuman",
            keywords = listOf("es jeruk segar", "es jeruk", "jeruk peras", "jeruk dingin", "jeruk")
        ),
        ProductDefinition(
            name = "Kopi Susu Gula Aren",
            defaultPrice = 6000,
            category = "Minuman",
            keywords = listOf("kopi susu gula aren", "kopi susu", "kopsus", "kopi aren")
        ),
        ProductDefinition(
            name = "Kopi Tubruk Panas",
            defaultPrice = 4000,
            category = "Minuman",
            keywords = listOf("kopi tubruk panas", "kopi tubruk", "kopi panas")
        ),
        ProductDefinition(
            name = "Kopi",
            defaultPrice = 5000,
            category = "Minuman",
            keywords = listOf("kopi hitam", "kopi")
        ),
        ProductDefinition(
            name = "Gorengan Gurih",
            defaultPrice = 1000,
            category = "Snack",
            keywords = listOf("gorengan", "bakwan", "tahu isi", "tempe mendoan", "bala bala", "gehu", "tempe", "tahu")
        ),
        ProductDefinition(
            name = "Pisang Goreng Keju",
            defaultPrice = 2500,
            category = "Snack",
            keywords = listOf("pisang goreng keju", "pisang goreng", "pisang keju", "pisang")
        )
    )

    fun matchProducts(spokenText: String): List<Pair<ProductDefinition, Int>> {
        val trimmed = spokenText.trim()
        if (trimmed.isEmpty()) return emptyList()

        // Split spoken text if conjunctions like "dan", "sama", "serta", ",", "lalu", "tambah" exist
        val segments = trimmed.split(Regex("""(?i)\s*(?:,|\bdan\b|\bsama\b|\bserta\b|\blalu\b|\bterus\b|\btambah\b|\+)\s*"""))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (segments.size > 1) {
            val list = mutableListOf<Pair<ProductDefinition, Int>>()
            for (seg in segments) {
                list.add(matchSingleProduct(seg))
            }
            return list
        }

        return listOf(matchSingleProduct(trimmed))
    }

    fun matchProduct(spokenText: String): Pair<ProductDefinition, Int> {
        return matchSingleProduct(spokenText)
    }

    private fun matchSingleProduct(spokenText: String): Pair<ProductDefinition, Int> {
        val lower = spokenText.lowercase().trim()

        // Extract quantity from speech
        var qty = 1
        val numMatch = Regex("""\b(\d+)\b""").find(lower)
        if (numMatch != null) {
            qty = numMatch.value.toIntOrNull() ?: 1
        } else if (lower.contains("sepasang") || lower.contains("dua")) {
            qty = 2
        } else if (lower.contains("tiga")) {
            qty = 3
        } else if (lower.contains("empat")) {
            qty = 4
        } else if (lower.contains("lima")) {
            qty = 5
        } else if (lower.contains("enam")) {
            qty = 6
        } else if (lower.contains("tujuh")) {
            qty = 7
        } else if (lower.contains("delapan")) {
            qty = 8
        } else if (lower.contains("sembilan")) {
            qty = 9
        } else if (lower.contains("sepuluh")) {
            qty = 10
        } else if (lower.contains("satu") || lower.contains("sebungkus") || lower.contains("seporsi") || lower.contains("segelas") || lower.contains("semangkok") || lower.contains("sebiji") || lower.contains("sebuah")) {
            qty = 1
        }

        // Match against catalog with priority on longest keywords first to prevent partial matches
        var bestMatch: ProductDefinition? = null
        var bestKwLength = -1

        for (prod in CATALOG) {
            for (kw in prod.keywords) {
                if (lower.contains(kw) && kw.length > bestKwLength) {
                    bestMatch = prod
                    bestKwLength = kw.length
                }
            }
        }

        if (bestMatch != null) {
            return Pair(bestMatch, qty)
        }

        // Fallback for custom spoken items not strictly in keyword aliases
        val cleanName = spokenText
            .replace(Regex("""(?i)\b(pesan|tambah|beli|tolong|ambilkan|minta|porsi|bungkus|gelas|biji|buah|satu|dua|tiga|empat|lima|\d+)\b"""), "")
            .trim()
            .ifEmpty { "Menu Tambahan" }
            .replaceFirstChar { it.uppercase() }

        val fallbackProduct = ProductDefinition(
            name = cleanName,
            defaultPrice = 5000,
            category = "Lainnya",
            keywords = emptyList()
        )
        return Pair(fallbackProduct, qty)
    }
}
