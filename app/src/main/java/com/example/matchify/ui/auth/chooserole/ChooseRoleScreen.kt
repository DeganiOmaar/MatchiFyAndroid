package com.example.matchify.ui.auth.chooserole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R

@Composable
fun ChooseRoleScreen(
    onTalentSelected: () -> Unit,
    onRecruiterSelected: () -> Unit,
    onBack: () -> Unit = {}
) {
    val viewModel: ChooseRoleViewModel = viewModel()

    val selectedRole by viewModel.selectedRole.collectAsState()
    val goNext by viewModel.goNext.collectAsState()

    // Navigate when Next is triggered
    LaunchedEffect(goNext) {
        if (goNext) {
            when (selectedRole) {
                UserRole.Talent -> onTalentSelected()
                UserRole.Recruiter -> onRecruiterSelected()
                else -> {}
            }
        }
    }

    // Design colors from reference
    val darkBackground = Color(0xFF0F172A)
    val fieldBackground = Color(0xFF1E293B)
    val textColor = Color(0xFFCBD5E1)
    val blueColor = Color(0xFF3B82F6)
    val whiteColor = Color(0xFFFFFFFF)
    val selectedBackground = blueColor.copy(alpha = 0.2f)
    val unselectedBackground = fieldBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Choose Your Role",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Select the account type that suits you best.",
                color = textColor.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Talent Card
            RoleCard(
                title = "Talent",
                subtitle = "For creators, artists, influencers, freelancers.",
                image = R.drawable.talent,
                isSelected = selectedRole == UserRole.Talent,
                onClick = { viewModel.selectRole(UserRole.Talent) },
                textColor = textColor,
                selectedBackground = selectedBackground,
                unselectedBackground = unselectedBackground,
                blueColor = blueColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recruiter Card
            RoleCard(
                title = "Recruiter",
                subtitle = "For companies or individuals hiring talent.",
                image = R.drawable.recruiter,
                isSelected = selectedRole == UserRole.Recruiter,
                onClick = { viewModel.selectRole(UserRole.Recruiter) },
                textColor = textColor,
                selectedBackground = selectedBackground,
                unselectedBackground = unselectedBackground,
                blueColor = blueColor
            )

            Spacer(modifier = Modifier.height(40.dp))

            // CONTINUE BUTTON
            Button(
                onClick = { viewModel.continueNext() },
                enabled = selectedRole != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueColor,
                    disabledContainerColor = blueColor.copy(alpha = 0.5f),
                    contentColor = whiteColor,
                    disabledContentColor = whiteColor.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "Continue",
                    color = whiteColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    textColor: Color,
    selectedBackground: Color,
    unselectedBackground: Color,
    blueColor: Color
) {
    val backgroundColor = if (isSelected) selectedBackground else unselectedBackground
    val borderColor = if (isSelected) blueColor else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = textColor.copy(alpha = 0.7f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseRolePreview() {
    ChooseRoleScreen(
        onTalentSelected = {},
        onRecruiterSelected = {}
    )
}