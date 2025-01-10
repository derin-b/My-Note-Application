package com.example.noteapplication.presentation.noteDetail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapplication.R
import com.example.noteapplication.domain.models.Media
import com.example.noteapplication.presentation.NoteViewmodel
import com.example.noteapplication.utils.Constants
import com.example.noteapplication.utils.Utils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetail(noteId: String, noteViewmodel: NoteViewmodel, onBackClick: ()-> Unit) {
    val context = LocalContext.current
    val selectedImageUri by noteViewmodel.selectedImageUri.collectAsState()
    val selectedVideoUri by noteViewmodel.selectedVideoUri.collectAsState()
    println("New note id: $noteId")
    // launch a side-effect to update note id whenever the value changes
    LaunchedEffect(noteId) {
        noteViewmodel.getNoteDetail(noteId)
        noteViewmodel.updateNoteId(noteId)
    }

    // launch a side-effect to collect one-time UI events
    LaunchedEffect(Unit) {
        noteViewmodel.uiEvent.collectLatest { event ->
            when (event) {
                // handle the SaveNote event
                is NoteDetailUiEvent.SaveNote -> { onBackClick() }
            }
        }
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                noteViewmodel.noteDetailEvent(
                    NoteDetailEvent.OnMediaSelected(Media(uri = it.toString(), type = Constants.MediaTypes.IMAGES))
                )
            }
        }
    )

    val singleVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                noteViewmodel.noteDetailEvent(
                    NoteDetailEvent.OnMediaSelected(Media(uri = it.toString(), type = Constants.MediaTypes.VIDEOS))
                )
            }
        }
    )

    Scaffold(   topBar = {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            actions = { Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Leading Icon",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(25.dp)
                    .clickable {
                        noteViewmodel.noteDetailEvent(NoteDetailEvent.SaveNote)
                    }
            ) },
        )
    }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier) {
                NoteCategoryDropdown(noteViewmodel)

                Spacer(modifier = Modifier.height(8.dp))

                TitleTextField(noteViewmodel)

                DescriptionField(
                    selectedImageUri = selectedImageUri,
                    selectedVideoUri = selectedVideoUri,
                    noteViewmodel = noteViewmodel
                )
            }

            AddMedia(
                modifier = Modifier.align(Alignment.BottomStart),
                onImageClick = {  Utils.checkStoragePermission(context = context, onPermissionGranted = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly) )},
                    onPermissionDenied = {Utils.longToastShow("Permission denied. Cannot access images.", context)})
                },
                onVideoClicked = {   Utils.checkStoragePermission(context = context, onPermissionGranted = {
                    singleVideoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly) )},
                    onPermissionDenied = {Utils.longToastShow("Permission denied. Cannot access videos.", context)})
                }
            )

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class) // Opting into experimental Material3 APIs
@Composable
fun NoteCategoryDropdown(noteViewmodel: NoteViewmodel) {
    // Observing the selected category state from the ViewModel
    val noteDetail by noteViewmodel.noteDetailUIState.collectAsState() // Collect Flow
    // Fetching the list of categories from the ViewModel
    val categories = noteViewmodel.categories

    // To manage the dropdown menu's expanded state (showing or hiding)
    var expanded by remember { mutableStateOf(false) }

    // Layout container for the dropdown, using Row for horizontal arrangement
    Row(
        modifier = Modifier
            .fillMaxWidth() // Make the dropdown take full width
            .padding(start = 16.dp), // Padding on the left side
        verticalAlignment = Alignment.CenterVertically // Align items vertically in the center
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded, // Binding the expanded state to show or hide the menu
            onExpandedChange = { expanded = it } // Callback for handling state change of dropdown
        ) {
            // Row acting as the dropdown trigger; it's clickable to open/close the dropdown
            Row(
                modifier = Modifier
                    .menuAnchor() // Attach the dropdown to the Row
                    .clickable { expanded = true }, // Open the menu when clicked
                verticalAlignment = Alignment.CenterVertically // Vertically center the content
            ) {
                // Display an icon (bookmark icon here) for the dropdown button
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.bookmark), // Icon resource
                    contentDescription = "Category Icon", // Content description for accessibility
                    modifier = Modifier.size(20.dp) // Icon size
                )
                Spacer(modifier = Modifier.width(8.dp)) // Spacer between the icon and text
                // Display the selected category text in bold
                Text(
                    text = noteDetail.selectedCategory,
                    fontWeight = FontWeight.Bold,
                )
                // Display dropdown arrow icon (up/down based on expanded state)
                Icon(
                    imageVector = if(expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow", // Accessibility description for the icon
                    modifier = Modifier
                        .size(20.dp) // Icon size
                        .align(Alignment.CenterVertically) // Vertically align the icon
                )
            }

            // Dropdown menu showing available categories
            ExposedDropdownMenu(
                expanded = expanded, // Controls visibility of the dropdown menu
                onDismissRequest = { expanded = false } // Close the dropdown when clicked outside
            ) {
                // Iterate over each category in the list and display it as a menu item
                categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) }, // Display the category name
                        onClick = {
                            // Handle category selection and update the ViewModel
                            noteViewmodel.noteDetailEvent(NoteDetailEvent.Category(option))
                            expanded = false // Close the dropdown after selecting a category
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TitleTextField(noteViewmodel: NoteViewmodel) {
    val noteDetail by noteViewmodel.noteDetailUIState.collectAsState() // Collect Flow
    // TextField for viewing and editing the note title
    Utils.CustomTextField(
        value = noteDetail.title,
        onValueChange = { noteViewmodel.noteDetailEvent(NoteDetailEvent.EnteredTitle(it)) },
        placeholder = "Title..",
        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DescriptionField(selectedImageUri: Pair<Uri?, String?>?, selectedVideoUri: Pair<Uri?, String?>?,noteViewmodel: NoteViewmodel) {
    val playWhenReady by remember { mutableStateOf(false) } // start with play disabled
    val noteDetail by noteViewmodel.noteDetailUIState.collectAsState() // Collect Flow
    // box container for the description TextField with scrollability
    Column(
        modifier = Modifier
            .fillMaxWidth() // ensure the Box takes full width
            .fillMaxHeight() // ensure the Box takes full height
            .verticalScroll(rememberScrollState()) // enable vertical scrolling for large content
    ) {
        // If the image is selected, display it first
        if (selectedImageUri?.first != null || selectedImageUri?.second.isNullOrBlank().not()) {
            Utils.ImagePicker(
                uri = selectedImageUri?.first,
                downloadUrlString = selectedImageUri?.second,
                onClose = {
                    // remove the image and trigger the OnMediaRemoved event
                    noteViewmodel.noteDetailEvent(
                        NoteDetailEvent.OnMediaRemoved(mediaType = Constants.MediaTypes.IMAGES)
                    )
                }
            )
        }
        // textField for editing the note description
        Utils.CustomTextField(
            value = noteDetail.description,
            onValueChange = { noteViewmodel.noteDetailEvent(NoteDetailEvent.EnteredDescription(it)) },
            placeholder = "Enter text..",
            modifier = Modifier.fillMaxSize()
        )

        if (selectedVideoUri?.first != null || selectedVideoUri?.second != null) {
            // only show video player if one of them is not null
            Utils.VideoPlayerScreen(
                uri = selectedVideoUri.first,
                downloadUrlString = selectedVideoUri.second,
                playWhenReady = playWhenReady
            ) { // remove the image and trigger the OnMediaRemoved event
                noteViewmodel.noteDetailEvent(
                    NoteDetailEvent.OnMediaRemoved(mediaType = Constants.MediaTypes.VIDEOS)
                )
            }
        }

    }
}

@Composable
fun AddMedia(
    modifier: Modifier,
    onImageClick:()-> Unit,
    onVideoClicked:()-> Unit
) {
    val context = LocalContext.current
    // Function to handle image click with permission check

    Row(
        modifier = modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onImageClick) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_image_library),
                contentDescription = "Add Image",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(onClick = onVideoClicked) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_video_library),
                contentDescription = "Add Video",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}




