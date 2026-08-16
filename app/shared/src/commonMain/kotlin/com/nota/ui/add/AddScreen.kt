package com.nota.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nota.ui.add.viewmodel.AddViewModel
import com.nota.ui.add.viewmodel.AddUiState
import com.nota.ui.common.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(viewModel: AddViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBackClick()
        }
    }

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Delete Notation",
            message = "Are you sure you want to delete this notation? This action cannot be undone.",
            confirmButtonText = "Delete",
            onConfirm = { viewModel.deleteNote() },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Notation" else "Create Notation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AddContent(
            uiState = uiState,
            onTitleChange = viewModel::onTitleChange,
            onInstrumentChange = viewModel::onInstrumentChange,
            onScaleChange = viewModel::onScaleChange,
            onTempoChange = viewModel::onTempoChange,
            onBreathTimeChange = viewModel::onBreathTimeChange,
            onNotesInputChange = viewModel::onNotesInputChange,
            onAddMeasure = viewModel::addMeasure,
            onSave = viewModel::saveNote,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AddContent(
    uiState: AddUiState,
    onTitleChange: (String) -> Unit,
    onInstrumentChange: (String) -> Unit,
    onScaleChange: (String) -> Unit,
    onTempoChange: (String) -> Unit,
    onBreathTimeChange: (String) -> Unit,
    onNotesInputChange: (String) -> Unit,
    onAddMeasure: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Basic Info Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    label = { Text("Title (e.g. Raag Yaman)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.instrument,
                        onValueChange = onInstrumentChange,
                        label = { Text("Instrument") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = uiState.scale,
                        onValueChange = onScaleChange,
                        label = { Text("Scale") },
                        modifier = Modifier.weight(0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.tempo,
                        onValueChange = onTempoChange,
                        label = { Text("Tempo") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = uiState.breathTime,
                        onValueChange = onBreathTimeChange,
                        label = { Text("Breath Time (sec)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Preview Area
        Column {
            Text(
                "Current Notation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                if (uiState.measures.isEmpty()) {
                    Text("No measures added yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val notationText = uiState.measures.joinToString(" | ") { it.joinToString(" ") }
                    Text(notationText, style = MaterialTheme.typography.bodyLarge, letterSpacing = 2.sp)
                }
            }
        }

        // Note Input Area
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Add Measure",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.currentNotesInput,
                    onValueChange = onNotesInputChange,
                    placeholder = { Text("Sa, Re, Ga, Ma") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Button(
                    onClick = onAddMeasure,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Add")
                }
            }
            Text(
                "Enter notes separated by commas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.errorMessage != null) {
            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (uiState.isEditMode) "Update Notation" else "Save to Library", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}
