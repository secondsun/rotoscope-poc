package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.unit.dp




import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.secondsun.tools.rotoscope.ui.vo.Project

@Composable
fun ColorPicker(modifier: Modifier = Modifier, model: RotoscopeAppModel) {
    val palette = model.palette
    val selectedIndex = model.currentPaletteIndex.value
    
    var showColorEditor by remember { mutableStateOf(false) }
    var editingColorIndex by remember { mutableStateOf(0) }
    
    Column(modifier
        .fillMaxHeight()
        .wrapContentWidth()
        .padding(8.dp)) {
        
        Text("Color Palette", style = MaterialTheme.typography.h6)
        
        // First row of colors
        Row(modifier = Modifier.wrapContentHeight().padding(vertical = 4.dp)) {
            for (i in 0..7) {
                ColorBox(
                    color = Color(palette[i]),
                    isSelected = i == selectedIndex,
                    onClick = { 
                        model.setPaletteIndex(i)
                        model.setCurrentPolygonColor(palette[i])
                    },
                    onLongClick = {
                        editingColorIndex = i
                        showColorEditor = true
                    }
                )
            }
        }
        
        // Second row of colors
        Row(modifier = Modifier.wrapContentHeight().padding(vertical = 4.dp)) {
            for (i in 8..14) {
                ColorBox(
                    color = Color(palette[i]),
                    isSelected = i == selectedIndex,
                    onClick = { 
                        model.setPaletteIndex(i)
                        model.setCurrentPolygonColor(palette[i])
                    },
                    onLongClick = {
                        editingColorIndex = i
                        showColorEditor = true
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Current color info
        val currentColor = Color(palette[selectedIndex])
        Text("Selected Color: ${selectedIndex + 1}")
        
        // Display RGB values
        Text("R: ${currentColor.red.toInt()}, G: ${currentColor.green.toInt()}, B: ${currentColor.blue.toInt()}")
    }
    
    // Show color editor dialog when requested
    if (showColorEditor) {
        ColorEditorDialog(
            initialColor = Color(palette[editingColorIndex]),
            onDismiss = { showColorEditor = false },
            onColorSelected = { newColor ->
                model.updatePaletteColor(editingColorIndex, newColor)
                showColorEditor = false
            }
        )
    }
}

@Composable
fun ColorBox(
    color: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(24.dp)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colors.primary else Color.Black
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            }
    )
}

@Composable
fun ColorEditorDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red * 255) }
    var green by remember { mutableStateOf(initialColor.green * 255) }
    var blue by remember { mutableStateOf(initialColor.blue * 255) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Edit Color", style = MaterialTheme.typography.h6)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Color preview
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(red.toInt(), green.toInt(), blue.toInt()))
                        .border(1.dp, Color.Black)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Red slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("R: ", modifier = Modifier.width(20.dp))
                    Slider(
                        value = red,
                        onValueChange = { red = it },
                        valueRange = 0f..255f,
                        modifier = Modifier.weight(1f)
                    )
                    Text(red.toInt().toString(), modifier = Modifier.width(30.dp))
                }
                
                // Green slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G: ", modifier = Modifier.width(20.dp))
                    Slider(
                        value = green,
                        onValueChange = { green = it },
                        valueRange = 0f..255f,
                        modifier = Modifier.weight(1f)
                    )
                    Text(green.toInt().toString(), modifier = Modifier.width(30.dp))
                }
                
                // Blue slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("B: ", modifier = Modifier.width(20.dp))
                    Slider(
                        value = blue,
                        onValueChange = { blue = it },
                        valueRange = 0f..255f,
                        modifier = Modifier.weight(1f)
                    )
                    Text(blue.toInt().toString(), modifier = Modifier.width(30.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newColor = Color(red.toInt(), green.toInt(), blue.toInt())
                            onColorSelected(newColor)
                        }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorBox(color: Int) {
    Box(modifier = Modifier.size(16.dp).background(Color(color)).border(1.dp, Color.White))
}