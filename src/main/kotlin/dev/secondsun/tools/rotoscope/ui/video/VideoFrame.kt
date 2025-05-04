package dev.secondsun.tools.rotoscope.ui.video

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import dev.secondsun.tools.rotoscope.ui.drawing.RotoscopeAppModel
import dev.secondsun.tools.rotoscope.ui.drawing.drawPoly
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint
import dev.secondsun.tools.rotoscope.ui.vo.Polygon
import java.awt.Cursor


@Composable
fun VideoFrame(modifier: Modifier = Modifier, videoUtil: VideoUtil, model: RotoscopeAppModel) {
    println("VideoFrame Composition")
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var highlightVertices by remember { mutableStateOf(false) }
    var hoveredVertex by remember { mutableStateOf<Pair<PolyPoint, Int>?>(null) }
    val videoFrame = remember { videoUtil.image.value.toComposeImageBitmap() }
    // Track the current vertex and polygon being dragged
    var draggedVertex by remember { mutableStateOf<Triple<PolyPoint, Int, Polygon>?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    val polygons = model.polygonList.value

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(Modifier.fillMaxSize().weight(1f)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coordinates ->
                            boxSize = coordinates.size
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                if (!isDragging) {
                                    scale *= zoom
                                    offset += pan
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            val viewportScaleX = this.size.width / videoUtil.image.value.width.toFloat()
                            val viewportScaleY = this.size.height / videoUtil.image.value.height.toFloat()

                            // Add points when clicking (if not in vertex editing mode)
                            detectTapGestures {
                                if (!highlightVertices) {
                                    model.addPointToCurrentPoly(
                                        PolyPoint(
                                            (it.x / viewportScaleX - offset.x).toInt(),
                                            (it.y / viewportScaleY - offset.y).toInt()
                                        )
                                    )
                                }
                            }
                        }
                        .then(
                            if (highlightVertices) {
                                Modifier
                                    .pointerInput(Unit) {
                                        // Track hover state for vertices
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val position = event.changes.first().position

                                                if (!isDragging) {
                                                    // Check if mouse is near any vertex of the current polygon
                                                    hoveredVertex = null
                                                    val currentPolyIndex = model.polyIndex.value
                                                    if (currentPolyIndex >= 0 && currentPolyIndex < polygons.size) {
                                                        val poly = polygons[currentPolyIndex]
                                                        for (point in poly.points) {
                                                            val adjustedPoint = Offset(
                                                                (point.x + offset.x).toFloat(),
                                                                (point.y + offset.y).toFloat()
                                                            )
                                                            if ((position - adjustedPoint).getDistance() < 10f) {
                                                                hoveredVertex = point to poly.colorIndex
                                                                break
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        // Handle dragging vertices
                                        detectDragGestures(
                                            onDragStart = { dragStartPosition ->
                                                // Find which vertex (if any) is being dragged in the current polygon
                                                val currentPolyIndex = model.polyIndex.value
                                                if (currentPolyIndex >= 0 && currentPolyIndex < polygons.size) {
                                                    val poly = polygons[currentPolyIndex]
                                                    for (point in poly.points) {
                                                        val adjustedPoint = Offset(
                                                            (point.x + offset.x).toFloat(),
                                                            (point.y + offset.y).toFloat()
                                                        )
                                                        if ((dragStartPosition - adjustedPoint).getDistance() < 10f) {
                                                            draggedVertex =
                                                                Triple(point, poly.points.indexOf(point), poly)
                                                            isDragging = true
                                                            break
                                                        }
                                                    }
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                draggedVertex?.let { (point, index, poly) ->
                                                    // Create a new point at the updated position
                                                    val newPoint = PolyPoint(
                                                        (point.x + dragAmount.x).toInt(),
                                                        (point.y + dragAmount.y).toInt()
                                                    )

                                                    // Update the polygon with the new point
                                                    val polyIndex = polygons.indexOf(poly)
                                                    if (polyIndex >= 0) {
                                                        // First remove the old point
                                                        model.polygonList.value[polyIndex].points.removeAt(index)
                                                        // Then add the new point at the same index
                                                        model.polygonList.value[polyIndex].points.add(index, newPoint)
                                                        // Update the draggedVertex to track the new point
                                                        draggedVertex = Triple(newPoint, index, poly)
                                                        // Recalculate bounds for the polygon
                                                        model.polygonList.value[polyIndex].recalculateBounds()
                                                    }
                                                }
                                            },
                                            onDragEnd = {
                                                isDragging = false
                                                draggedVertex = null
                                            }
                                        )
                                    }
                                    .pointerHoverIcon(
                                        if (hoveredVertex != null) PointerIcon(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
                                        else PointerIcon(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)),
                                        true
                                    )
                            } else Modifier
                        )
                ) {
                    // Draw video frame
                    val viewportScaleX = this.size.width / videoUtil.image.value.width.toFloat()
                    val viewportScaleY = this.size.height / videoUtil.image.value.height.toFloat()
                    //Draw the video frame
                    drawImage(
                        image = videoUtil.image.value.toComposeImageBitmap(),
                        srcOffset = IntOffset(-offset.x.toInt(), -offset.y.toInt()),
                        dstSize = IntSize((size.width * scale).toInt(), (size.height * scale).toInt()),
                    )

                    // Current selected polygon index
                    val currentPolyIndex = model.polyIndex.value

                    // Draw polygons
                    polygons.forEachIndexed { index, poly ->
                        // Draw the polygon
                        drawPoly(
                            poly = Polygon(poly.colorIndex, poly.key).apply {
                                points.addAll(poly.points.map {
                                    PolyPoint(
                                        ((it.x + offset.x) * viewportScaleX).toInt(),
                                        ((it.y + offset.y) * viewportScaleY).toInt()
                                    )

                                })
                            },
                            palette = model.palette
                        )

                        // Draw vertex highlights only for the current polygon
                        if (highlightVertices && index == currentPolyIndex) {
                            for (point in poly.points) {
                                val isHovered = hoveredVertex?.first == point
                                val isDragged = draggedVertex?.first == point

                                val highlightColor = when {
                                    isDragged -> Color.Red
                                    isHovered -> Color.Yellow
                                    else -> Color.White
                                }

                                val radius = when {
                                    isDragged -> 10f
                                    isHovered -> 8f
                                    else -> 6f
                                }

                                drawCircle(
                                    color = highlightColor,
                                    radius = radius,
                                    center = Offset(
                                        (point.x + offset.x).toFloat(),
                                        (point.y + offset.y).toFloat()
                                    ),
                                    style = Stroke(width = 2f)
                                )

                                // Fill the circle if it's hovered or dragged
                                if (isHovered || isDragged) {
                                    drawCircle(
                                        color = highlightColor.copy(alpha = 0.3f),
                                        radius = radius,
                                        center = Offset(
                                            (point.x + offset.x).toFloat(),
                                            (point.y + offset.y).toFloat()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().background(MaterialTheme.colors.primary)
            ) {
                VideoControlBar(
                    modifier = Modifier.weight(1f),
                    videoUtil = videoUtil
                )
                // Add vertex highlight toggle button
                IconToggleButton(
                    checked = highlightVertices,
                    onCheckedChange = { highlightVertices = it }
                ) {
                    Icon(
                        imageVector = if (highlightVertices) Icons.Filled.Edit else Icons.Outlined.Edit,
                        contentDescription = "Toggle vertex highlights",
                        tint = if (highlightVertices) Color.Yellow else Color.White
                    )
                }
            }
        }
    }
}