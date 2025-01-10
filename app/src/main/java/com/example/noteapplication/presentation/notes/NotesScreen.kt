package com.example.noteapplication.presentation.notes

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapplication.R
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.presentation.NoteViewmodel
import com.example.noteapplication.utils.Constants
import com.example.noteapplication.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(noteViewmodel: NoteViewmodel, onClick: (String) -> Unit, onLogOut:() -> Unit){
    val context = LocalContext.current
    // collect the list of notes and search text from the ViewModel state
    val notes by noteViewmodel.notesFlow.collectAsState(emptyList())  // Collect Flow
    val searchText by noteViewmodel.searchText.collectAsState("")
    val user = FirebaseAuth.getInstance().currentUser
    val userName = if (user != null) user.displayName?:"" else ""
    val isLoading by noteViewmodel.isLoading.collectAsState()


    // side effect to fetch notes when the screen is first composed
    LaunchedEffect(true) {
        noteViewmodel.fetchNotes()
    }

    // launch a side-effect to collect one-time UI events
    LaunchedEffect(Unit) {
        noteViewmodel.notesUIEvent.collectLatest { event ->
            when (event) {
                // handle the navigate to note screen event
                is NoteScreenUIEvent.FinishActivity -> onLogOut()
            }
        }
    }

    Utils.ShowCircularProgress(isLoading = isLoading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.hello, userName),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 25.sp,
                        fontFamily = FontFamily(Font(R.font.courgette))
                    )
                },
                navigationIcon = {
                    // back button to finish the activity (navigate up)
                    IconButton(onClick = {  (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    // displays a dropdown menu with options
                    DropdownMenuWithDetails(noteViewmodel) },
            )
        },
        floatingActionButton = {
            // fab to add a new note
            FloatingActionButton(
                onClick = {
                    // pass a blank noteId and navigate to the detail screen
                    onClick(" ") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        // main content of the screen: display notes and provide search functionality
        ScreenContent(innerPadding = innerPadding, searchText = searchText, notes = notes,
            viewmodel = noteViewmodel, onClick = onClick)
    }
}

@Composable
fun DropdownMenuWithDetails(noteViewmodel: NoteViewmodel) {
    var expanded by remember { mutableStateOf(false) }

    // button to trigger the dropdown menu when clicked
    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Default.MoreVert, contentDescription = "More options")
    }
    // dropdownMenu which is shown when 'expanded' is true
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.log_out)) },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_log_out),
                    contentDescription = "null",
                    modifier = Modifier.size(28.dp)
                )
            },
            onClick = { noteViewmodel.noteScreenEvent(NotesScreenEvent.LogOut) }
        )
    }
}

@Composable
fun ScreenContent(
    innerPadding: PaddingValues,
    searchText: String,
    notes: List<Note>,
    viewmodel: NoteViewmodel,
    onClick: (String) -> Unit,
){
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        // search text field
        Utils.TextFields(
            value = searchText,
            onValueChanged = {
                viewmodel.noteScreenEvent(NotesScreenEvent.SearchTextChange(it))
                viewmodel.noteScreenEvent(NotesScreenEvent.OnNoteFiltersChange)
            },
            textLabel = stringResource(R.string.search_your_notes),
            leadingIcon = Icons.Default.Search,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(20.dp),
            imeAction = ImeAction.Done // Show "Done" action
            ,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() } // Close the keyboard
            ),
        )

        NoteCategories(noteViewmodel = viewmodel)

        Spacer(modifier = Modifier.height(16.dp))


        NoteList(
            notes = notes,
            onClick = { noteId ->  onClick(noteId)},
            noteViewmodel = viewmodel
        )
    }
}

@Composable
fun NoteCategories(noteViewmodel: NoteViewmodel){
    var selectedChipIndex by rememberSaveable {
        mutableIntStateOf(0) // Initial value is the first chip (index 0)
    }

    val noteCategories = listOf("All", "Work", "Reading", "Important")

    // LazyRow to display the chips horizontally and optimize performance
    LazyRow {
        // Creates a composable item for each chip in the list
        items(noteCategories.size) { index ->

            // Box to contain each chip and align the content at the center
            Box(
                contentAlignment = Alignment.Center, // Center-aligns the text inside the chip
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 15.dp,
                    ) // Adds padding around each chip
                    .clickable {
                        // Updates the selected chip index when a chip is clicked
                        selectedChipIndex = index
                        noteViewmodel.noteScreenEvent(NotesScreenEvent.CategoryChange(index))
                        noteViewmodel.noteScreenEvent(NotesScreenEvent.SearchTextChange(""))
                        noteViewmodel.noteScreenEvent(NotesScreenEvent.OnNoteFiltersChange)
                    }
                    .height(50.dp)
                    .width(95.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(16.dp)
                    )// Rounds the corners of the chip
                    .background(
                        // Changes background color based on whether the chip is selected
                        if (selectedChipIndex == index) MaterialTheme.colorScheme.secondary // Selected chip color
                        else MaterialTheme.colorScheme.surface // Unselected chip color
                    )
                    .padding(15.dp) // Adds padding inside the chip
            ) {
                // Displays the chip's text with a fixed text color
                Text(
                    text = noteCategories[index], // The text of the current chip
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * A composable to display a list of notes in a grid layout.
 *
 * @param notes The list of `Note` objects to display.
 * @param onClick Callback triggered on a short click on a note.
 */
@Composable
fun NoteList(
    notes: List<Note>,
    onClick: (String) -> Unit, // Modified to pass the clicked note
    noteViewmodel: NoteViewmodel,
) {
    val shouldShowDialog = remember { mutableStateOf(false) } // 1
    // Column wrapping the LazyVerticalGrid to provide padding and styling
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Defines a grid with 2 columns
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                bottom = 100.dp // Leaves space for a floating action button, if any
            ),
            modifier = Modifier.fillMaxHeight()
        ) {
            // Iterates over the list of notes
            items(notes.size) { index ->
                val note = notes[index] // Gets the note at the current index

                if (shouldShowDialog.value){
                    DeleteDialog(noteId = note.noteId, noteViewmodel = noteViewmodel, shouldShowDialog = shouldShowDialog)

                }

                // Card representing an individual note
                Card(
                    shape = RoundedCornerShape(10.dp), // Rounded corners for the card
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp), // Subtle shadow
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary // Background color for the card
                    ),
                    modifier = Modifier
                        .padding(8.dp) // Adds padding around each card
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onClick(note.noteId) }, // Trigger short click callback
                                onLongPress = {
                                    shouldShowDialog.value = true
                                } // Trigger long click callback
                            )
                        }
                ) {
                    // Column to layout the content of the note card
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Displays the note title
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Displays the note description with ellipsis for overflow
                        // Conditional description or placeholder
                        Text(
                            text = note.description,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Row to position the date at the end of the card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.weight(1f)) // Pushes the date to the right
                            // Convert the note's creation date from the default format to the UI date format
                            val dateCreated = Utils.convertDateString(
                                note.dateCreated,
                                Constants.DateFormats.DEFAULT,
                                Constants.DateFormats.UI_DATE_FORMAT
                            )
                            // Display the formatted date or "Today" if the date is today's date
                            Text(
                                // Set the text to "Today" if the note's creation date is today's date, else display the formatted date
                                text = if (Utils.isTodaysDate(dateCreated)) stringResource(R.string.today) else dateCreated,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(noteId:String, noteViewmodel: NoteViewmodel, shouldShowDialog: MutableState<Boolean>){
    when {
        // check if the dialog should be shown (based on the 'shouldShowDialog' state)
        shouldShowDialog.value -> {
            // display the custom AlertDialog with the provided parameters
            Utils.AlertDialog(
                dialogTitle = stringResource(R.string.delete_note),
                dialogText = stringResource(R.string.delete_note_desc),
                confirmText = stringResource(R.string.yes),
                dismissText = stringResource(R.string.no),
                onDismissRequest = { shouldShowDialog.value = false },
                onConfirmation = {
                    shouldShowDialog.value = false
                    // call the ViewModel to delete the note with the given ID
                    noteViewmodel.noteScreenEvent(NotesScreenEvent.OnDeleteClicked(noteId))
                }
            )
        }
    }
}