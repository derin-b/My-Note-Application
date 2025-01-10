package com.example.noteapplication.presentation.landing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapplication.R
import com.example.noteapplication.ui.theme.ArmyGreen
import com.example.noteapplication.ui.theme.Cream
import com.example.noteapplication.ui.theme.Green
import com.example.noteapplication.ui.theme.Lemon

@Composable
fun LandingScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Lemon, Cream, Color.Transparent),
                    startY = Float.POSITIVE_INFINITY,
                    endY = 0f
                )
            )
            .padding(26.dp)
    ) {
        LandingText(text = stringResource(id = R.string.landing_text))

       /* Text(
            text = "the\nbest\napp\nfor your\nnotes",
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle(R.font.mont),
            fontSize = 80.sp,
            letterSpacing = 0.5.sp,
            lineHeight = 100.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp)
        )*/

        Spacer(modifier = Modifier.height(50.dp))

        ButtonTexts(onLoginClick = onLoginClick, onRegisterClick = onRegisterClick)

    }
}

/**
 * A composable that animates the display of text, simulating a typewriting effect.
 *
 * @param text The string of text that will be animated character by character.
 * @param modifier A [Modifier] to be applied to the Box container.
 * @param isVisible A flag to determine whether the animation should start or reset.
 * @param spec The animation specification used for controlling the timing of the animation.
 * @param preoccupySpace If true, the space for the text is reserved even before the animation finishes.
 */
@Composable
fun LandingText(
    text: String, // The text to animate
    modifier: Modifier = Modifier, // Modifier to apply to the outer Box
    isVisible: Boolean = true, // Whether the text is visible (triggers the animation)
    spec: AnimationSpec<Int> = tween(durationMillis = text.length * 100, easing = LinearEasing), // The animation spec controlling the speed of the animation
    preoccupySpace: Boolean = true // Whether to occupy space for the whole text before animation ends
) {
    // Holds the text that will be displayed during the animation.
    var textToAnimate by remember { mutableStateOf("") }

    // Animates the index of the text that is being displayed.
    val index = remember {
        Animatable(initialValue = 0, typeConverter = Int.VectorConverter)
    }

    // LaunchedEffect triggered by changes in isVisible.
    LaunchedEffect(isVisible) {
        // When the text is visible, animate the text display.
        if (isVisible) {
            textToAnimate = text // Set the full text to animate
            index.animateTo(text.length, spec) // Animate the index from 0 to the full length of the text
        } else {
            index.snapTo(0) // Reset the animation index if not visible
        }
    }

    // Box composable to wrap the text
    Box(modifier = modifier) {
        // If preoccupySpace is true and the animation is still running, display hidden text to reserve space
        if (preoccupySpace && index.isRunning) {
            Text(
                text = text,
                modifier = Modifier.alpha(0f), // Hide this text, just to reserve space
            )
        }

        // Display the animated portion of the text based on the current index value
        Text(
            text = textToAnimate.substring(0, index.value), // Only show the text up to the current index
            color = Color.DarkGray, // Text color
            fontWeight = FontWeight.SemiBold, // Text weight
            fontFamily = FontFamily(Font(R.font.courgette)), // Font style (can be customized)
            fontSize = 80.sp, // Font size for the text
            letterSpacing = 0.5.sp, // Letter spacing to simulate typing
            lineHeight = 100.sp, // Line height to make the text more readable
            modifier = Modifier
                .padding(top = 16.dp) // Padding for the text
        )
    }
}

@Composable
fun ButtonTexts(modifier: Modifier = Modifier, onLoginClick:()-> Unit, onRegisterClick:()-> Unit){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Green, shape = RoundedCornerShape(8.dp))
    ) {
        Button(
            onClick = onRegisterClick,
            modifier = modifier
                .weight(1f) // Each button takes equal weight
                .fillMaxWidth() // Ensure it fills its allocated space
                .height(60.dp),
            //.shadow(8.dp, shape = RoundedCornerShape(8.dp))
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ArmyGreen, // Background color
                contentColor = Color.White   // Text color
            ),

            ) {
            Text(text = stringResource(R.string.register), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Button(
            onClick = onLoginClick,
            modifier = modifier
                .weight(1f) // Each button takes equal weight
                .fillMaxWidth()// Ensure it fills its allocated space
                .height(60.dp),
            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green, // Background color
                contentColor = Color.White   // Text color
            )

        ) {
            Text(text = stringResource(R.string.login_in), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
