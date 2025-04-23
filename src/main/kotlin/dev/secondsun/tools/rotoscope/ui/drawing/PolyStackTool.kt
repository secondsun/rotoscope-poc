package dev.secondsun.tools.rotoscope.ui.drawing

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.secondsun.tools.rotoscope.ui.vo.PolyPoint
import dev.secondsun.tools.rotoscope.ui.vo.Polygon
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PolyStackTool(modifier:Modifier = Modifier,  model: RotoscopeAppModel) {

    val list by model.polygonList

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->

        // Update the list
        list.apply {
            model.swapPolys(to.index,from.index)
        }
    }

    Column(modifier
        .fillMaxHeight()
        .wrapContentWidth()) {

        PolystackToolbar(
            model = model,
            addRemoveAction = object: AddRemoveAction {
                override fun add() {
                    model.addPolygon()
                }
        
                override fun remove() {
                    model.removePolygon()
                }
            }
        )

        LazyColumn(modifier = Modifier.wrapContentSize(),
            state = lazyListState,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),) {
            this.itemsIndexed(
                items = list.toList(),
                key = { _:Int, item: Polygon -> item.key }) { index, poly: Polygon ->

                ReorderableItem(
                    reorderableLazyListState,
                    key = poly.key,
                    modifier.padding(12.dp)
                            .border(width = 2.dp, color = MaterialTheme.colors.onSurface)
                            .height(64.dp).width(100.dp)) { isDragging ->
                    // Item content
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(Modifier.fillMaxSize().draggableHandle(interactionSource = interactionSource)) {
                        Canvas(Modifier.background(MaterialTheme.colors.surface).fillMaxSize()) {
                            val canvasWidth = this.size.width
                            val canvasHeight = this.size.height

                            //The big nasty thing scales the polygons so they draw inside of the canvas tile

                            drawPoly(
                                Polygon(poly.colorIndex, poly.key).apply { 
                                    points.addAll(poly.normalizePoints.map { PolyPoint(
                                        ((it.x.toFloat()/poly.bounds.width.toFloat())*canvasWidth).toInt(),
                                        (canvasHeight*((it.y.toFloat()/poly.bounds.height.toFloat()))).toInt()
                                    ) }) 
                                },
                                palette = model.palette
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun PolystackToolbar(
    modifier: Modifier = Modifier, 
    model: RotoscopeAppModel,
    addRemoveAction: AddRemoveAction = AddRemoveAction.TODO
) {
    val selectedColorIndex = model.currentPaletteIndex.value
    val palette = model.palette
    
    Row(modifier.wrapContentHeight()) {
        IconButton(onClick = { addRemoveAction.add() }) {
            Icon(Icons.Default.Add, tint = MaterialTheme.colors.onPrimary, contentDescription = "Add Polygon")
        }
        IconButton(onClick = { addRemoveAction.remove() }) {
            Icon(Icons.Default.Delete, tint = MaterialTheme.colors.onPrimary, contentDescription = "Remove Polygon")
        }
        
        // Color selection button
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
                .background(Color(palette[selectedColorIndex]))
                .border(1.dp, Color.White)
                .clickable { model.setTool(Tools.ColorPickerTool) }
        )
    }
}

interface AddRemoveAction {

    object TODO : AddRemoveAction {
        override fun add() {
            TODO("Not yet implemented")
        }

        override fun remove() {
            TODO("Not yet implemented")
        }
    }

    fun add()
    fun remove()
}

@Preview
@Composable
fun PolyStackPreview() {
    MaterialTheme {
        PolyStackTool(model=RotoscopeAppModel())
    }
}

