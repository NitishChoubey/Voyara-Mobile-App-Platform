package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebf.voyara.utils.TokenManager

/**
 * User Profile Screen with Settings
 */
@Composable
fun ProfileScreen(
    tokenManager: TokenManager,
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // Get user info
    val fullName = remember { tokenManager.getUserFullName() ?: "User" }
    val email = remember { tokenManager.getUserEmail() ?: "" }
    val userId = remember { tokenManager.getUserId() ?: "" }

    // Edit states
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(fullName) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Animations
    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Deep slate
                        Color(0xFF1E293B), // Dark slate
                        Color(0xFF1E293B)  // Consistent dark
                    )
                )
            )
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33F97316), // Orange glow
                        Color.Transparent
                    )
                ),
                radius = 350f,
                center = center.copy(x = size.width * 0.85f, y = size.height * 0.15f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x332563EB), // Blue glow
                        Color.Transparent
                    )
                ),
                radius = 300f,
                center = center.copy(x = size.width * 0.15f, y = size.height * 0.8f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alphaAnim)
                .padding(top = 16.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header with back button
            item {
                ProfileHeader(onNavigateBack = onNavigateBack)
            }

            // Profile Picture and Name
            item {
                ProfileInfoSection(
                    fullName = fullName,
                    email = email,
                    isEditingName = isEditingName,
                    editedName = editedName,
                    onEditNameClick = { isEditingName = true },
                    onNameChange = { editedName = it },
                    onSaveName = {
                        // TODO: Save to backend/local storage
                        tokenManager.saveUserInfo(userId, email, editedName)
                        isEditingName = false
                    },
                    onCancelEdit = {
                        editedName = fullName
                        isEditingName = false
                    }
                )
            }

            // Account Settings Section
            item {
                SettingsSection(
                    title = "Account Settings",
                    items = listOf(
                        SettingItem("✉️", "Email", email),
                        SettingItem("👤", "User ID", userId.take(8) + "..."),
                        SettingItem("🌍", "Language", "English")
                    )
                )
            }

            // Privacy & Security
            item {
                SettingsSection(
                    title = "Privacy & Security",
                    items = listOf(
                        SettingItem("🔒", "Change Password", "Update your password"),
                        SettingItem("🔔", "Notifications", "Manage notifications"),
                        SettingItem("🔐", "Privacy Settings", "Control your privacy")
                    ),
                    isClickable = true
                )
            }

            // App Settings
            item {
                SettingsSection(
                    title = "App Settings",
                    items = listOf(
                        SettingItem("🎨", "Theme", "Dark Mode"),
                        SettingItem("💱", "Currency", "INR"),
                        SettingItem("📏", "Units", "Metric")
                    ),
                    isClickable = true
                )
            }

            // Logout Button
            item {
                LogoutButton(onClick = { showLogoutDialog = true })
            }

            // Delete Account
            item {
                DeleteAccountButton(onClick = { showDeleteDialog = true })
            }

            // Bottom spacing (for bottom nav bar)
            item {
                Spacer(modifier = Modifier.height(86.dp))
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            LogoutDialog(
                onConfirm = {
                    tokenManager.clearTokens()
                    tokenManager.clearUserInfo()
                    showLogoutDialog = false
                    onLogout()
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        // Delete Account Confirmation Dialog
        if (showDeleteDialog) {
            DeleteAccountDialog(
                onConfirm = {
                    // TODO: Call delete account API
                    tokenManager.clearTokens()
                    tokenManager.clearUserInfo()
                    showDeleteDialog = false
                    onLogout()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
private fun ProfileHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Placeholder for symmetry
        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun ProfileInfoSection(
    fullName: String,
    email: String,
    isEditingName: Boolean,
    editedName: String,
    onEditNameClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onSaveName: () -> Unit,
    onCancelEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFF97316),
                                Color(0xFFEC4899)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fullName.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Editable Name
            if (isEditingName) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Name", color = Color.White.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF97316),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSaveName,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF97316)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save")
                    }
                    OutlinedButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fullName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable(onClick = onEditNameClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✏️",
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

data class SettingItem(
    val icon: String,
    val title: String,
    val subtitle: String
)

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingItem>,
    isClickable: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.08f)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEachIndexed { index, item ->
                    SettingItemRow(
                        item = item,
                        isClickable = isClickable,
                        onClick = { /* TODO: Handle click */ }
                    )
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    item: SettingItem,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = item.subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
        if (isClickable) {
            Text(
                text = "›",
                fontSize = 24.sp,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF97316)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚪",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Logout",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DeleteAccountButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFEF4444)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🗑️",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Delete Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logout",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout?",
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Account",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444)
            )
        },
        text = {
            Text(
                text = "This action cannot be undone. All your data will be permanently deleted.",
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(20.dp)
    )
}

