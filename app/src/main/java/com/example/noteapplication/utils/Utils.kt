package com.example.noteapplication.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.noteapplication.data.entity.note.Note
import com.example.noteapplication.domain.models.Media
import com.example.noteapplication.domain.models.NoteDetailUIState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    @Composable
    fun TextFields(
        modifier: Modifier = Modifier,
        value: String,
        onValueChanged: (String) -> Unit,
        textLabel: String,
        leadingIcon: ImageVector? = null,
        iconTint: Color = MaterialTheme.colorScheme.onBackground,
        keyboardType: KeyboardType = KeyboardType.Text,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        trailingIcon: @Composable (() -> Unit)? = null,
        shape: Shape = OutlinedTextFieldDefaults.shape,
        imeAction: ImeAction = ImeAction.Next,
        keyboardActions: KeyboardActions,
        errorStatus:Boolean = false,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            label = { Text(text = textLabel) },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = textLabel,
                        tint = iconTint // Set the desired tint color
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            visualTransformation = visualTransformation,
            modifier = modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
            shape =  shape,
            keyboardActions = keyboardActions,
            isError = errorStatus

        )
    }

    @Composable
    fun CustomTextField(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        textStyle: TextStyle = TextStyle(
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        ),
        modifier: Modifier = Modifier
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = { Text(text = placeholder, style = textStyle) },
            textStyle = textStyle,
            modifier = modifier
        )
    }

    @Composable
    fun TrailingPasswordIcon(
        showPassword: MutableState<Boolean>,
        visibleIcon: Int,
        hiddenIcon: Int,
        iconTint: Color
    ) {
        val (icon, tint) = if (showPassword.value) {
            Pair(visibleIcon, iconTint)
        } else {
            Pair(hiddenIcon, iconTint)
        }

        IconButton(onClick = { showPassword.value = !showPassword.value }) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = "Toggle Password Visibility",
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
    }

    @Composable
    fun HeaderText(
        modifier: Modifier = Modifier,
        text: String,
        color: Color = Color.Unspecified
    ) {
        Text(text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayLarge,
            modifier = modifier,
            color = color
        )
    }

    @Composable
    fun ButtonText(
        modifier: Modifier = Modifier,
        text: String,
        color: Color = Color.Unspecified
    ) {
        Text(text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier,
            color = color
        )
    }

    /**
     * A custom Composable for displaying a Material AlertDialog
     *
     * @param dialogTitle The title to be displayed in the dialog.
     * @param dialogText The main content or message to be shown in the dialog.
     * @param icon The icon to be displayed in the dialog.
     * @param confirmText The text for the confirm button.
     * @param dismissText The text for the dismiss button.
     * @param onDismissRequest A lambda function to handle the action when the dialog is dismissed.
     * @param onConfirmation A lambda function to handle the action when the confirm button is clicked.
     */
    @Composable
    fun AlertDialog(
        dialogTitle: String,
        dialogText: String,
        icon: ImageVector? = null,
        confirmText: String,
        dismissText: String,
        onDismissRequest: () -> Unit,
        onConfirmation: () -> Unit,
    ) {
        AlertDialog(
            icon = {
                if (icon != null) {
                    Icon(icon, contentDescription = null)
                }
            },
            title = { Text(text = dialogTitle, style = TextStyle(fontWeight = FontWeight.Bold)) },
            text = { Text(text = dialogText, style = TextStyle(fontWeight = FontWeight.Medium)) },
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) { Text(confirmText) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) { Text(dismissText) }
            }
        )
    }

    @Composable
    fun ShowCircularProgress(isLoading: Boolean) {
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)) // Semi-transparent overlay
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Composable
    fun RequestReadExternalStoragePermission(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        val context = LocalContext.current

        // Register the permission request launcher
        val permissionRequestLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    onPermissionGranted()  // Call when permission is granted
                } else {
                    onPermissionDenied()   // Call when permission is denied
                }
            }
        )

        // Check if permission is granted
        val hasReadPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        // If permission isn't granted, request it
        if (!hasReadPermission) {
            LaunchedEffect(Unit) {
                permissionRequestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            onPermissionGranted() // If permission is already granted
        }
    }



    fun checkStoragePermission(
        context: Context,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when {
            // For devices with Android versions lower than Android 10
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> {
                // No need to request permission for Android versions below Android 10
                onPermissionGranted()
            }

            // For Android 11 (API level 30) and above, check if the app has MANAGE_EXTERNAL_STORAGE permission
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (Environment.isExternalStorageManager()) {
                    // App has permission to manage all files
                    onPermissionGranted()
                } else {
                    // Only attempt to open the settings intent if the device is running Android 11 (API 30) or above
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        context.startActivity(intent)
                        onPermissionDenied()
                    } catch (e: ActivityNotFoundException) {
                        // Handle the case where the intent cannot be resolved (likely on devices lower than Android 11)
                        onPermissionDenied()
                    }
                }
            }

            // For Android versions between Android 6 (API level 23) and Android 9 (API level 28), check READ_EXTERNAL_STORAGE permission
            else -> {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    onPermissionGranted()
                } else {
                    // Request the permission for Android versions below Android 10
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1001
                    )
                    // You can handle the permission result in a callback or lifecycle observer.
                }
            }
        }
    }




    /**
     * A Composable function to display a video player using ExoPlayer.
     * @param uri The URI of the video to be played.
     * @param playWhenReady A Boolean flag to determine whether the video should start playing immediately.
     */
    @Composable
    fun VideoPlayerScreen(uri: Uri?, downloadUrlString: String?, playWhenReady: Boolean, onDelete: () -> Unit) {
        val context = LocalContext.current


        // Create ExoPlayer instance
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build()
        }

        // Update ExoPlayer media item dynamically
        LaunchedEffect(uri, downloadUrlString) {
            val mediaUri = when {
                !downloadUrlString.isNullOrEmpty() && isConnected(context) -> Uri.parse(downloadUrlString)
                uri != null -> uri
                else -> null // No valid media source
            } ?: return@LaunchedEffect // Exit if no media is available
            println("Media uri: $uri")

            // Set media item and prepare the player
            exoPlayer.setMediaItem(MediaItem.fromUri(mediaUri))
            exoPlayer.prepare()
        }


        // observe playWhenReady state
        LaunchedEffect(playWhenReady) {
            exoPlayer.playWhenReady = playWhenReady
            if (playWhenReady) {
                exoPlayer.play()
            } else {
                exoPlayer.pause()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            AndroidView(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true // show playback controls
                    }
                }
            )
            IconButton(onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)  ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Delete Video",
                    tint = MaterialTheme.colorScheme.secondary)
            }
        }

        // release player when composable is disposed
        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    }

    /**
     * Composable function to display a selected image with a close button.
     *
     * @param uri The URI of the selected image to display.
     * @param onClose A callback function that is triggered when the close button is clicked.
     */
    @Composable
    fun ImagePicker(
        uri: Uri? = null,
        downloadUrlString: String? = null,
        onClose: () -> Unit
    ) {
        val context = LocalContext.current

        val model = when {
            isConnected(context) && !downloadUrlString.isNullOrBlank() -> downloadUrlString
            else -> uri
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.TopCenter // align content to the top-center
        ) {
            // box to hold the image and the close button
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .width(250.dp)
            ) {
                // display the selected image
                AsyncImage(
                    model = model, //  image source URI
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // crop the image to fit the container
                )

                // close button to remove the image
                IconButton(
                    onClick = onClose, // trigger the callback when clicked
                    modifier = Modifier
                        .align(Alignment.TopEnd) // align the button to the top-right of the image
                        .background(Color.LightGray, shape = CircleShape)
                        .size(25.dp) // set the size of the button
                ) {
                    // icon inside the close button
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.Black
                    )
                }
            }
        }
    }



    /**
     * Checks if the provided date string corresponds to today's date.
     * @param dateString The date string to check, in the specified format.
     * @param dateFormat The format of the date string, default is "MMMM dd, yyyy".
     * @return True if the provided date string corresponds to today's date, otherwise false.
     */
    fun isTodaysDate(dateString: String, dateFormat: String = Constants.DateFormats.UI_DATE_FORMAT.sdfFormat): Boolean {
        // Parse the date string into a Date object
        val format = SimpleDateFormat(dateFormat, Locale.getDefault())
        val date: Date? = format.parse(dateString)

        if (date != null) {
            // Normalize both dates to consider only the date part (year, month, day)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val today = Calendar.getInstance()

            // Compare the year, month, and day
            return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
        }

        // Return false if date parsing failed
        return false
    }


    /**
     * Formats a date into a string according to the specified date format and optional time zone.
     * @param dateFormat The desired date format, specified as an enum constant.
     * @param date The `Date` object to format. Defaults to the current date and time.
     * @param timeZone Optional time zone string to apply to the date formatting.
     * @return A string representing the formatted date.
     */
    fun getDateString(
        dateFormat: Constants.DateFormats,
        date: Date = Date(),
        timeZone: String? = null
    ): String {
        // Create a SimpleDateFormat object using the provided date format and default locale
        val dateFormatter = SimpleDateFormat(dateFormat.sdfFormat, Locale.getDefault())

        // If a time zone is provided, set it to the date formatter
        if (!timeZone.isNullOrEmpty()) {
            dateFormatter.timeZone = TimeZone.getTimeZone(timeZone)
        }

        // Format the date using the specified format and return the formatted date string
        return dateFormatter.format(date)
    }


    /**
     * Converts a date string from one format to another.
     * @param dateString The date string to convert.
     * @param inputDateFormat The format of the input date string.
     * @param outputDateFormat The desired format for the output date string.
     * @return The formatted date string, or an empty string if parsing fails.
     */
    fun convertDateString(
        dateString: String,
        inputDateFormat: Constants.DateFormats,
        outputDateFormat: Constants.DateFormats
    ): String {
        // create a SimpleDateFormat object for parsing the input date string
        val sdf = SimpleDateFormat(inputDateFormat.sdfFormat, Locale.getDefault())

        // parse the input date string into a Date object
        val parsedDate = sdf.parse(dateString)

        // if the date parsing is successful, format the parsed date into the desired format
        return if (parsedDate != null) {
            // call getDateString to return the formatted date string in the output format
            getDateString(
                dateFormat = outputDateFormat,
                date = parsedDate
            )
        } else {
            // return empty string if parsing fails
            ""
        }
    }

    /**
     * Checks if the device is currently connected to the internet.
     * @param context The context required to access system services.
     * @return `true` if the device is connected to the internet via WiFi, Cellular, or VPN; otherwise, `false`.
     */
    fun isConnected(context: Context): Boolean {
        // Retrieve the ConnectivityManager system service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return when {
            // Check connectivity for Android versions Marshmallow (API 23) and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val activeNetwork = connectivityManager.activeNetwork
                    ?: return false // No active network
                val cap = connectivityManager.getNetworkCapabilities(activeNetwork)
                    ?: return false // No network capabilities

                // Check for specific transport types
                when {
                    cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // Connected via WiFi
                    cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // Connected via cellular network
                    else -> false // No supported transport types
                }
            }
            else -> {
                // Use deprecated methods for devices running below Marshmallow (API 23)
                val activeNetwork = connectivityManager.activeNetworkInfo
                    ?: return false // No active network

                // Check for specific network types
                return when (activeNetwork.type) {
                    ConnectivityManager.TYPE_MOBILE -> true // Connected via mobile data
                    ConnectivityManager.TYPE_WIFI -> true // Connected via WiFi
                    ConnectivityManager.TYPE_VPN -> true // Connected via VPN
                    else -> false // No supported network types
                }
            }
        }
    }

    // function to create a new note with default or provided values
    fun createNewNote(noteDetail: NoteDetailUIState, userId: String): Note {
        val mediaListString = Gson().toJson(noteDetail.mediaList)
        return Note(
            title = noteDetail.title.ifBlank { " " },
            description = noteDetail.description.ifBlank { " " },
            dateCreated = getDateString(Constants.DateFormats.DEFAULT),
            userId = userId,
            noteId = noteDetail.noteId,
            noteCategory = noteDetail.selectedCategory,
            mediaId = noteDetail.mediaId,
            mediaList = mediaListString,
            syncFlag = 0
        )
    }

    fun longToastShow(msg:String, context: Context){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
    }


    fun generateNoteId(userId: String):String{
        return "${userId}_${getDateString(Constants.DateFormats.CONCAT_FILE)}"
    }

    fun getListFromString(jsonString: String): List<Media> {
        val typeToken = object : TypeToken<List<Media>>() {}.type
        return Gson().fromJson(jsonString, typeToken)
    }

}



