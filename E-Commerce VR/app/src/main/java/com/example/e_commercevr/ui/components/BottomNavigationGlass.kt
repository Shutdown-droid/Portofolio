package com.example.e_commercevr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercevr.ui.theme.AuraColors
import com.example.e_commercevr.ui.theme.AuraShapes

@Composable
fun BottomNavigationGlass(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphismCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .height(64.dp),
        shape = AuraShapes.Large,
        fillColor = AuraColors.GlassFillHeavy,
        strokeColor = AuraColors.GlassStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem("home", currentRoute, "Home", Icons.Default.Home, onNavigate)
            NavItem("transaksi", currentRoute, "Transaksi", Icons.Default.List, onNavigate)
            NavItem("profile", currentRoute, "Profile", Icons.Default.Person, onNavigate)
        }
    }
}

@Composable
private fun NavItem(
    route: String,
    currentRoute: String,
    label: String,
    icon: ImageVector,
    onNavigate: (String) -> Unit
) {
    val isSelected = currentRoute == route || (route == "transaksi" && currentRoute == "transactions")
    val contentColor = if (isSelected) AuraColors.Primary else AuraColors.Primary.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = AuraColors.GlassFillElevated,
                        shape = AuraShapes.Pill
                    )
                } else Modifier
            )
            .clip(AuraShapes.Pill)
            .clickable { onNavigate(route) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
