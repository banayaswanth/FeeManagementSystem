package com.example.feemanagementsystem.activity

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class smile(context : Context?, attrs: AttributeSet)  : View(context,attrs) {

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val facecolor = Color.YELLOW
    val borderSize = 4.0f
    val bordercolor = Color.RED
    var size = 140
    var mouthpath = Path()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawface(canvas)
        draweye(canvas)
        drawmouth(canvas)
    }
    fun drawface(canvas : Canvas?) {
        paint.color = facecolor
        paint.style = Paint.Style.FILL
        val radius = size / 2f
        canvas?.drawCircle(size/2f , size/2f, radius , paint)
        paint.color = bordercolor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        canvas?.drawCircle(size/2f , size/2f, radius-borderSize , paint)
    }

    fun draweye(canvas: Canvas?){
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        val left_eye = RectF(size*0.32f , size*0.30f, size*0.43f, size*0.40f )
        canvas?.drawOval(left_eye , paint)
        val rightt_eye = RectF(size*0.57f , size*0.30f, size*0.68f, size*0.40f )
        canvas?.drawOval(rightt_eye , paint)
    }
    fun drawmouth(canvas: Canvas?) {
        mouthpath.moveTo(size * 0.22f, size * 0.7f)
        mouthpath.quadTo(size * 0.35f, size * 0.78f, size * 0.5f, size * 0.8f)
        mouthpath.quadTo(size * 0.65f, size * 0.78f, size * 0.78f, size * 0.7f)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        canvas?.drawPath(mouthpath, paint)
    }






}