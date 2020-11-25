package com.xdevelopers.iosseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ArrayRes
import androidx.core.graphics.ColorUtils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class IOSColorSeekBar(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), GestureDetector.OnGestureListener {
    // SeekBar Colors to display on the Rectangle Canvas
    private var seekBarColors = intArrayOf(Color.parseColor("#FF5252"), Color.parseColor("#FFEB3B"), Color.parseColor("#00C853"), Color.parseColor("#00B0FF"), Color.parseColor("#D500F9"), Color.parseColor("#8D6E63"))
    private var canvasHeight: Int = 60
    private var barHeight: Int = 20
    private var rectf: RectF = RectF()
    private var rectPaint: Paint = Paint()
    private var thumbBorderPaint: Paint = Paint()
    private var thumbPaint: Paint = Paint()
    private lateinit var colorGradient: LinearGradient
    private var thumbX: Float = 24f
    private var thumbY: Float = (canvasHeight / 2).toFloat()
    private var thumbBorder: Float = 0f
    private var thumbRadius: Float = 16f
    private var thumbBorderRadius: Float = thumbRadius + thumbBorder
    private var thumbBorderColor = Color.WHITE
    private var paddingStart = 30f
    private var paddingEnd = 30f
    private var barCornerRadius: Float = 8f
    private var initialPosition = 26f
    private var maxRadius = 15f
    private var minRadius = 5f
    private var hue = 0f
    private var min = 0.2f
    private var max = 0.8f
    // default shadow maximum radius
    private var thumbShadowRadius = maxRadius
    // default shadow color
    private var thumbShadowColor = Color.parseColor("#e0e0e0")
    // lightness of color
    private var lightness = max
    // brightness of color
    private var saturation = 0.75f
    private var isLoaded = true
    private var oldThumbRadius = thumbRadius
    private var oldThumbBorderRadius = thumbBorderRadius
    private var colorChangeListener: OnColorChangeListener? = null
    private var mGestureDetector = GestureDetector(context, this)

    init {
        attributeSet.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.IOSColorSeekBar)
            val colorsId = typedArray.getResourceId(R.styleable.IOSColorSeekBar_seekBarColors, 0)
            thumbShadowRadius = typedArray.getFloat(R.styleable.IOSColorSeekBar_shadowLayerRadius, 5f)
            if ( typedArray.getString(R.styleable.IOSColorSeekBar_shadowLayerColor) != null){
                thumbShadowColor = Color.parseColor(typedArray.getString(R.styleable.IOSColorSeekBar_shadowLayerColor))
            }

            if (typedArray.getBoolean(R.styleable.IOSColorSeekBar_shadowLayer, false)) {
                thumbShadowRadius = when {
                    thumbShadowRadius > maxRadius -> {
                        // set to maximum radius
                        maxRadius
                    }
                    thumbShadowRadius < minRadius -> {
                        // set to minimum radius
                        minRadius
                    }
                    else -> {
                        thumbShadowRadius
                    }
                }
                thumbBorderPaint.setShadowLayer(thumbShadowRadius, 0f, 10f, thumbShadowColor)
            }
            if (colorsId != 0) seekBarColors = getColorsById(colorsId)
            barCornerRadius = typedArray.getDimension(R.styleable.IOSColorSeekBar_cornerRadius, 2f)
            barHeight = typedArray.getDimension(R.styleable.IOSColorSeekBar_barHeight, 6f).toInt()
            typedArray.recycle()
        }
        rectPaint.isAntiAlias = true

        thumbBorderPaint.isAntiAlias = true
        thumbBorderPaint.color = thumbBorderColor

        thumbPaint.isAntiAlias = true

        thumbRadius = 40f
        thumbBorderRadius = thumbRadius + thumbBorder
        canvasHeight = (thumbBorderRadius * 3).toInt()
        thumbY = (canvasHeight / 2).toFloat()

        oldThumbRadius = thumbRadius
        oldThumbBorderRadius = thumbBorderRadius
    }

    private fun getColorsById(@ArrayRes id: Int): IntArray {
        if (isInEditMode) {
            val s = context.resources.getStringArray(id)
            val colors = IntArray(s.size)
            for (j in s.indices) {
                colors[j] = Color.parseColor(s[j])
            }
            return colors
        } else {
            val typedArray = context.resources.obtainTypedArray(id)
            val colors = IntArray(typedArray.length())
            for (j in 0 until typedArray.length()) {
                colors[j] = typedArray.getColor(j, Color.BLACK)
            }
            typedArray.recycle()
            return colors
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //color bar position
        val barLeft: Float = paddingStart + initialPosition
        val barRight: Float = (width.toFloat() - paddingEnd) - initialPosition
        val barTop: Float = ((canvasHeight / 2) - (barHeight / 2)).toFloat()
        val barBottom: Float = ((canvasHeight / 2) + (barHeight / 2)).toFloat()
        //draw color bar
        rectf.set(barLeft, barTop, barRight, barBottom)
        canvas?.drawRoundRect(rectf, barCornerRadius, barCornerRadius, rectPaint)
        if (isLoaded) {
            thumbX = (hue * (width - (paddingStart + paddingEnd)) + paddingStart)
        }
        when {
            thumbX < barLeft -> {
                thumbX = barLeft
            }
            thumbX > barRight -> {
                thumbX = barRight
            }
        }
        val color = pickColor(thumbX, width)
        thumbPaint.color = color
        // draw color bar thumb
        colorChangeListener?.onColorChangeListener(getCurrentColor())
        canvas?.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbBorderPaint)
        canvas?.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint)
        // draw white thumb
        canvas?.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbBorderPaint)
        thumbBorderPaint.color = Color.WHITE
        canvas?.drawCircle(thumbX, thumbY, thumbRadius, thumbBorderPaint)
    }

    /**
     * Return a color of Int Type
     *
     * @param position is the thumb position on x-axis
     * @param canvasWidth is the canvas width
     *
     * @return an Int value of Color
     */
    private fun pickColor(position: Float, canvasWidth: Int): Int {
        val value = (position - paddingStart) / (canvasWidth - (paddingStart + paddingEnd))
        return when {
            value <= 0.0 -> seekBarColors[0]
            value >= 1 -> seekBarColors[seekBarColors.size - 1]
            else -> {
                lightness = when{
                    lightness > max ->{
                        max
                    }
                    lightness<min ->{
                        min
                    }
                    else->{
                        lightness
                    }
                }
                hsbToColor(value, saturation, lightness)
            }
        }
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        colorGradient = LinearGradient(0f, 0f, w.toFloat(), 0f, seekBarColors, null, Shader.TileMode.CLAMP)
        rectPaint.shader = colorGradient
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, canvasHeight)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isLoaded = false
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                event.x.let {
                    thumbX = it
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                invalidate()
            }
        }
        mGestureDetector.onTouchEvent(event)
        return true
    }

    @SuppressLint("Recycle")
            /**
             * set the position of thumb on color
             */
    fun seekTo(color: Int) {
        isLoaded = true
        hue = getHue(color)
        invalidate()
    }

    /**
     * Returns the thumbPaint color in Int
     */
    fun getCurrentColor() = thumbPaint.color
    fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener) {
        this.colorChangeListener = onColorChangeListener
    }

    // color seekBar callbacks
    interface OnColorChangeListener {
        // on color change call back
        fun onColorChangeListener(color: Int)

        // on thumb single tap up callback
        fun onThumbClickedListener(color: Int)
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return true
    }


    override fun onShowPress(p0: MotionEvent?) {

    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        colorChangeListener?.onThumbClickedListener(thumbPaint.color)
        return true
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return true
    }

    override fun onLongPress(p0: MotionEvent?) {

    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return true
    }

    /**
     * Return dark or lighter color
     * @param color which has to be darker or lighter
     * @param alpha lightness which has to be added to the color (0.0 to 1.0)
     *
     * @return color Int (darker color or lighter color)
     */
    fun darkOrLightColor(color: Int, alpha: Float): Int {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)
        hsl[2] = alpha
        return ColorUtils.HSLToColor(hsl)
    }
    /**
     * @param brightness value between (0.2 to 0.8)
     * because lesser value leads to black color and greater value leads to lighter color
     * set the color brightness
     */
    fun setColorBrightness(brightness:Float){
        lightness = brightness
    }
    // Utility Function
    /**
     * Returns the hue component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    private fun getHue(color: Int): Float {
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color and 0xFF
        val v = max(b, max(r, g))
        val temp = min(b, min(r, g))
        var hue: Float
        if (v == temp) {
            hue = 0f
        } else {
            val vTemp = (v - temp).toFloat()
            val cr = (v - r) / vTemp
            val cg = (v - g) / vTemp
            val cb = (v - b) / vTemp
            hue = when {
                r == v -> {
                    cb - cg
                }
                g == v -> {
                    2 + cr - cb
                }
                else -> {
                    4 + cg - cr
                }
            }
            hue /= 6f
            if (hue < 0) {
                hue++
            }
        }
        return hue
    }
    // Utility Function
    /**
     * Convert HSB components to an ARGB color. Alpha set to 0xFF.
     *     hsv[0] is Hue [0 .. 1)
     *     hsv[1] is Saturation [0...1]
     *     hsv[2] is Value [0...1]
     * If hsv values are out of range, they are pinned.
     * @param h Hue component
     * @param s Saturation component
     * @param b Brightness component
     * @return the resulting argb color
     *
     * @hide Pending API council
     */
    private fun hsbToColor(h: Float, s: Float, b: Float): Int {
        var red = 0.0f
        var green = 0.0f
        var blue = 0.0f
        val hf = (h - h.toInt()) * 6.0f
        val ihf = hf.toInt()
        val f = hf - ihf
        val pv = b * (1.0f - s)
        val qv = b * (1.0f - s * f)
        val tv = b * (1.0f - s * (1.0f - f))
        when (ihf) {
            0 -> {
                red = b
                green = tv
                blue = pv
            }
            1 -> {
                red = qv
                green = b
                blue = pv
            }
            2 -> {
                red = pv
                green = b
                blue = tv
            }
            3 -> {
                red = pv
                green = qv
                blue = b
            }
            4 -> {
                red = tv
                green = pv
                blue = b
            }
            5 -> {
                red = b
                green = pv
                blue = qv
            }
        }
        return -0x1000000 or ((red * 255.0f).toInt() shl 16) or
                ((green * 255.0f).toInt() shl 8) or (blue * 255.0f).toInt()
    }
}