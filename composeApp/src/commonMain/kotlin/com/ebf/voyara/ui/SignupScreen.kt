package com.ebf.voyara.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebf.voyara.ViewModels.SignupViewModel
import globetrotter.composeapp.generated.resources.Res
import globetrotter.composeapp.generated.resources.voyaralogo1
import org.jetbrains.compose.resources.painterResource

/**
 * Premium Signup Screen with Dark Theme
 */
@Composable
fun SignupScreen(
    viewModel: SignupViewModel,
    onNavigateToLogin: () -> Unit = {},
    onSignupSuccess: () -> Unit = {}
) {
    // Collect ViewModel states
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Handle UI State Changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.ebf.voyara.sealedClasses.AuthUiState.Success -> {
                // Show success dialog instead of immediately navigating
                showSuccessDialog = true
            }
            is com.ebf.voyara.sealedClasses.AuthUiState.Error -> {
                // Error will be shown in the UI
            }
            else -> {}
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            title = {
                androidx.compose.material3.Text(
                    text = "Success!",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            },
            text = {
                androidx.compose.material3.Text(
                    text = "Your account has been created successfully! Please login to continue.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetState()
                        onSignupSuccess()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B35)
                    )
                ) {
                    androidx.compose.material3.Text("Go to Login")
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )
    }

    // Animations
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Deep slate
                        Color(0xFF1E293B), // Dark slate
                        Color(0xFF334155)  // Slate gray
                    )
                )
            )
    ) {
        // Decorative floating orbs for premium feel
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33F97316), // Orange glow
                        Color.Transparent
                    )
                ),
                radius = 300f,
                center = center.copy(x = size.width * 0.2f, y = size.height * 0.15f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x332563EB), // Blue glow
                        Color.Transparent
                    )
                ),
                radius = 250f,
                center = center.copy(x = size.width * 0.8f, y = size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 40.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section - Logo and Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painterResource(Res.drawable.voyaralogo1),
                    contentDescription = "logo",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Create Account",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Start your journey with Voyara",
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Middle Section - Signup Form Card with Glass Morphism
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xE61E293B), // Semi-transparent dark
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Show error message if exists
                    if (uiState is com.ebf.voyara.sealedClasses.AuthUiState.Error) {
                        Text(
                            text = (uiState as com.ebf.voyara.sealedClasses.AuthUiState.Error).errorMessage,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }

                    // Full Name Field
                    PremiumSignupTextField(
                        value = fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = "Full Name",
                        placeholder = "John Doe",
                        leadingIcon = "👤",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    PremiumSignupTextField(
                        value = email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Email",
                        placeholder = "you@example.com",
                        leadingIcon = "✉️",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    PremiumSignupPasswordField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Password",
                        placeholder = "••••••••",
                        leadingIcon = "🔒",
                        passwordVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Requirements
                    Text(
                        text = "Min 8 characters with letters & numbers",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Signup Button with Gradient
                    Button(
                        onClick = { viewModel.onSignupClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF97316) // Premium orange
                        ),
                        enabled = uiState !is com.ebf.voyara.sealedClasses.AuthUiState.Loading
                    ) {
                        if (uiState is com.ebf.voyara.sealedClasses.AuthUiState.Loading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Creating Account...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = "Create Account",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Loading hint for cold start
                    if (uiState is com.ebf.voyara.sealedClasses.AuthUiState.Loading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "First connection may take up to 60 seconds",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.alpha(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider with "or"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF475569),
                            thickness = 1.dp
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF475569),
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Social Signup Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PremiumGoogleSignupButton(
                            modifier = Modifier.weight(1f),
                            onClick = { /* Handle Google signup */ }
                        )
                        PremiumAppleSignupButton(
                            modifier = Modifier.weight(1f),
                            onClick = { /* Handle Apple signup */ }
                        )
                    }
                }
            }

            // Bottom Section - Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1)
                )
                Text(
                    text = "Sign in",
                    fontSize = 14.sp,
                    color = Color(0xFF60A5FA),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

/**
 * Premium Text Field for Signup with Dark Theme
 */
@Composable
fun PremiumSignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Text(
                    text = leadingIcon,
                    fontSize = 18.sp
                )
            },
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF97316),
                unfocusedBorderColor = Color(0xFF475569),
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFF97316)
            ),
            singleLine = true
        )
    }
}

/**
 * Premium Password Field for Signup with Dark Theme
 */
@Composable
fun PremiumSignupPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: String,
    passwordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Text(
                    text = leadingIcon,
                    fontSize = 18.sp
                )
            },
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Text(
                        text = if (passwordVisible) "👁" else "👁‍🗨",
                        fontSize = 18.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFF97316),
                unfocusedBorderColor = Color(0xFF475569),
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFF97316)
            ),
            singleLine = true
        )
    }
}

/**
 * Premium Google Signup Button
 */
@Composable
fun PremiumGoogleSignupButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
        contentPadding = PaddingValues(20.dp)
    ) {
        // Google Logo - Centered
        Canvas(
            modifier = Modifier.size(10.dp)
        ) {
            val size = this.size.minDimension
            scale(size / 10f) {
                // Google "G" logo with 4 colors
                // Blue section
                drawPath(
                    path = Path().apply {
                        moveTo(12f, 5.5f)
                        cubicTo(13.8f, 5.5f, 15.4f, 6.2f, 16.6f, 7.3f)
                        lineTo(19.7f, 4.2f)
                        cubicTo(17.6f, 2.3f, 14.9f, 1f, 12f, 1f)
                        cubicTo(7.3f, 1f, 3.4f, 4f, 1.7f, 8.2f)
                        lineTo(5.3f, 11f)
                        cubicTo(6.3f, 7.8f, 8.9f, 5.5f, 12f, 5.5f)
                        close()
                    },
                    color = Color(0xFF4285F4) // Google Blue
                )
                // Green section
                drawPath(
                    path = Path().apply {
                        moveTo(23f, 12.3f)
                        cubicTo(23f, 11.5f, 22.9f, 10.7f, 22.8f, 10f)
                        lineTo(12f, 10f)
                        lineTo(12f, 14.5f)
                        lineTo(18.2f, 14.5f)
                        cubicTo(17.9f, 16.1f, 17f, 17.4f, 15.7f, 18.3f)
                        lineTo(19.3f, 21.1f)
                        cubicTo(21.5f, 19.1f, 23f, 16f, 23f, 12.3f)
                        close()
                    },
                    color = Color(0xFF34A853) // Google Green
                )
                // Yellow section
                drawPath(
                    path = Path().apply {
                        moveTo(5.3f, 11f)
                        cubicTo(4.9f, 12f, 4.7f, 13f, 4.7f, 14f)
                        cubicTo(4.7f, 15f, 4.9f, 16f, 5.3f, 17f)
                        lineTo(1.7f, 19.8f)
                        cubicTo(1.2f, 18.5f, 1f, 17.3f, 1f, 16f)
                        cubicTo(1f, 14.7f, 1.2f, 13.5f, 1.7f, 12.2f)
                        close()
                    },
                    color = Color(0xFFFBBC05) // Google Yellow
                )
                // Red section
                drawPath(
                    path = Path().apply {
                        moveTo(12f, 23f)
                        cubicTo(14.9f, 23f, 17.6f, 22f, 19.3f, 20.5f)
                        lineTo(15.7f, 17.7f)
                        cubicTo(14.6f, 18.5f, 13.2f, 19f, 12f, 19f)
                        cubicTo(8.9f, 19f, 6.3f, 16.7f, 5.3f, 13.5f)
                        lineTo(1.7f, 16.3f)
                        cubicTo(3.4f, 20.5f, 7.3f, 23f, 12f, 23f)
                        close()
                    },
                    color = Color(0xFFEA4335) // Google Red
                )
            }
        }
    }
}

/**
 * Premium Apple Signup Button
 */
@Composable
fun PremiumAppleSignupButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
        contentPadding = PaddingValues(20.dp)
    ) {
        // Apple Logo - Centered
        Canvas(
            modifier = Modifier.size(10.dp)
        ) {
            val size = this.size.minDimension
            scale(size / 10f) {
                // Apple logo path
                drawPath(
                    path = Path().apply {
                        // Apple bite and leaf
                        moveTo(17.6f, 13.8f)
                        cubicTo(17.5f, 10.5f, 20.2f, 8.9f, 20.4f, 8.8f)
                        cubicTo(18.8f, 6.5f, 16.3f, 6.2f, 15.4f, 6.2f)
                        cubicTo(13.4f, 6f, 11.5f, 7.4f, 10.5f, 7.4f)
                        cubicTo(9.4f, 7.4f, 7.9f, 6.2f, 6.2f, 6.3f)
                        cubicTo(4f, 6.3f, 1.9f, 7.5f, 0.8f, 9.5f)
                        cubicTo(-1.5f, 13.5f, 0.2f, 19.3f, 2.5f, 22.5f)
                        cubicTo(3.6f, 24.1f, 4.9f, 25.8f, 6.6f, 25.8f)
                        cubicTo(8.2f, 25.7f, 8.8f, 24.7f, 10.8f, 24.7f)
                        cubicTo(12.7f, 24.7f, 13.3f, 25.8f, 15f, 25.8f)
                        cubicTo(16.7f, 25.7f, 17.8f, 24.2f, 18.9f, 22.6f)
                        cubicTo(20.1f, 20.8f, 20.6f, 19.1f, 20.6f, 19f)
                        cubicTo(20.6f, 18.9f, 17.6f, 17.8f, 17.6f, 13.8f)
                        close()

                        // Leaf on top
                        moveTo(14.7f, 4.1f)
                        cubicTo(15.6f, 3f, 16.2f, 1.5f, 16f, 0f)
                        cubicTo(14.7f, 0.1f, 13.1f, 0.9f, 12.2f, 2f)
                        cubicTo(11.4f, 2.9f, 10.7f, 4.4f, 10.9f, 5.9f)
                        cubicTo(12.3f, 6f, 13.8f, 5.2f, 14.7f, 4.1f)
                        close()
                    },
                    color = Color.White
                )
            }
        }
    }
}

