package com.example.matchify.ui.auth.chooserole

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R

@Composable
fun ChooseRoleScreen(
    onTalentSelected: () -> Unit,
    onRecruiterSelected: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Choose Your Role",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Select the account type that suits you best.",
            color = Color.Gray,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Talent Card
        RoleCard(
            title = "Talent",
            subtitle = "For creators, artists, influencers, freelancers.",
            image = R.drawable.talent,
            isSelected = selectedRole == UserRole.Talent,
            onClick = { viewModel.selectRole(UserRole.Talent) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Recruiter Card
        RoleCard(
            title = "Recruiter",
            subtitle = "For companies or individuals hiring talent.",
            image = R.drawable.recruiter,
            isSelected = selectedRole == UserRole.Recruiter,
            onClick = { viewModel.selectRole(UserRole.Recruiter) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // CONTINUE BUTTON
        Button(
            onClick = { viewModel.continueNext() },
            enabled = selectedRole != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF),    //  iOS blue
                disabledContainerColor = Color(0xFFBAD7FF)  // iOS disabled blue
            )

        ) {
            Text(
                text = "Continue",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE9F0FF) else Color(0xFFF5F5F7)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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