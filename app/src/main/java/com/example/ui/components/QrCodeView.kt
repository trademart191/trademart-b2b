package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun QrCodeView(
  data: String,
  modifier: Modifier = Modifier,
  size: Dp = 180.dp,
  primaryColor: Color = Color(0xFF1E293B),
  backgroundColor: Color = Color.White
) {
  // Generate a deterministic 21x21 pseudo QR matrix based on hash of `data`
  val matrix = remember(data) {
    val gridSize = 21
    val grid = Array(gridSize) { BooleanArray(gridSize) }

    fun drawFinder(startX: Int, startY: Int) {
      for (x in 0 until 7) {
        for (y in 0 until 7) {
          val isOuter = x == 0 || x == 6 || y == 0 || y == 6
          val isInner = x in 2..4 && y in 2..4
          grid[startX + x][startY + y] = isOuter || isInner
        }
      }
    }

    // Three Finder patterns
    drawFinder(0, 0)
    drawFinder(gridSize - 7, 0)
    drawFinder(0, gridSize - 7)

    // Timing lines
    for (i in 7 until gridSize - 7) {
      grid[6][i] = (i % 2 == 0)
      grid[i][6] = (i % 2 == 0)
    }

    // Fill data cells deterministically using hash
    val hash = abs(data.hashCode())
    for (x in 0 until gridSize) {
      for (y in 0 until gridSize) {
        // Skip finder pattern zones
        val inTopLeft = x < 8 && y < 8
        val inTopRight = x >= gridSize - 8 && y < 8
        val inBottomLeft = x < 8 && y >= gridSize - 8
        val inCenterLogo = x in 9..11 && y in 9..11
        if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterLogo) {
          val cellSeed = (x * 37 + y * 73 + hash) xor (x * y * 13)
          grid[x][y] = (cellSeed % 3 == 0 || (cellSeed % 7 == 2))
        }
      }
    }
    grid
  }

  Box(
    modifier = modifier
      .size(size)
      .background(backgroundColor, RoundedCornerShape(12.dp))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
      .padding(12.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val gridSize = 21
      val cellSize = this.size.width / gridSize

      for (x in 0 until gridSize) {
        for (y in 0 until gridSize) {
          if (matrix[x][y]) {
            drawRoundRect(
              color = primaryColor,
              topLeft = Offset(x * cellSize, y * cellSize),
              size = Size(cellSize, cellSize),
              cornerRadius = CornerRadius(cellSize * 0.2f, cellSize * 0.2f)
            )
          }
        }
      }
    }

    // Small center logo badge
    Box(
      modifier = Modifier
        .size(size * 0.22f)
        .background(Color.White, RoundedCornerShape(6.dp))
        .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "B2B",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}
