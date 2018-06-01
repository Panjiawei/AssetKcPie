package com.example.andriod_pan.assetkcpie.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.example.andriod_pan.assetkcpie.model.AssetKcData;
import com.example.andriod_pan.assetkcpie.utils.DensityUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/27.
 */

public class AssetKcPie extends View {//继承View类
    private Context mContext;
    private Paint textPaint;
    private Paint arcPaint;
    private Paint linePaint;

    private WeakReference<Bitmap>  bitmapBuffer;
    private Canvas bitmapCanvas;

    private float distance;
    private float radius;
    private int barWidth,barHeight;
    private List<AssetKcData> datas;
    private List<AngleSE> angleSEs;
    private List<RectF> lengedRectes;
    private OnSelectedListener mListener;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float actionX = event.getX(); //点击点的坐标
            float actionY = event.getY();
            double distance = Math.sqrt(Math.pow(Math.abs(actionX-barWidth/2),2)+
                    Math.pow(Math.abs(actionY-barHeight/2),2));
            double angle = Math.atan((actionY-barHeight/2) /(actionX-barWidth/2)) /3.14 * 180 - 90;
            float X = barWidth/2,Y=(barHeight-lengedHeight)/2;
            if(actionX > X && actionY<Y){
                angle = 90-angle;
            } else if (actionX > X && actionY>Y) {
                angle = 90+angle;
            }else if (actionX < X && actionY>Y) {
                angle = 270-angle;
            }else if (actionX < X && actionY<Y) {
                angle = 270+angle;
            }

            if(angleSEs == null || angleSEs.size() == 0 || mListener == null) return false;

            for(int i=0;i<angleSEs.size();i++){
                if(distance <= radius){
                    if(angle > angleSEs.get(i).getStartAngle() && angle<angleSEs.get(i).getSweepAngle()){
                        mListener.onSelected(i); //当点击点在圆内且在扇形上时，触发监听事件
                    }
                }else if(lengedRectes.get(i).contains(actionX,actionY)){
                    mListener.onSelected(i); //当点击点在描述文字上时，触发监听事件(此处是当饼图部分太小，无法点击时的补充)
                }
            }
            return false;
        }

        return super.onTouchEvent(event);
    }

    private float totalNum;

    private int lengedHeight = 0;
    private boolean isLengedVisible = false;

    public AssetKcPie(Context context) {
        super(context);
    }

    public AssetKcPie(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(DensityUtil.dip2px(context,11));

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setTextSize(radius);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.GRAY);
        linePaint .setTextSize(3);

        distance = DensityUtil.dip2px(context,16);

        setRadius(DensityUtil.dip2px(context,70));

    }

    public AssetKcPie(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRadius(float rs){
        this.radius = rs;
    }

    public void setData(List<AssetKcData> dataS,float total){
        this.datas = dataS;
        this.totalNum = total;
        invalidate();
    }

    public void setLenged(){ //设置是否绘制控件下方的描述
        isLengedVisible = true;
        lengedHeight = (int) DensityUtil.dip2px(mContext,32);
        setMeasuredDimension(onWidthMeasure(getMeasuredWidth()),onHeightMeasure(getMeasuredHeight()));
    }

    private void drawLenged(){ //绘制控件下面的描述
        lengedRectes = new ArrayList<>();
        Rect rect = new Rect();
        float lengedX = distance;
        float lengedY = barHeight - lengedHeight + distance;
        float totalWidth;
        for(int i = 0;i<datas.size();i++){
            RectF rectF = new RectF();
            textPaint.setTextSize(DensityUtil.dip2px(mContext,13));
            textPaint.setFakeBoldText(true);
            textPaint.setShadowLayer(5,4,4,Color.GRAY);
            textPaint.getTextBounds(datas.get(i).getDesc(),0,datas.get(i).getDesc().length(),rect);
            arcPaint.setTextSize(distance/2);
            arcPaint.setColor(datas.get(i).getColor());

            if(!TextUtils.isEmpty(datas.get(i).getDesc())){ //当描述大于一行时，在下一行绘制

                totalWidth = lengedX + distance/2 + 4 + rect.width() +distance;
                if(totalWidth > barWidth){//判断描述的长度是否大于一行
                    lengedY = lengedY +distance;
                    lengedX = distance;
                }

                bitmapCanvas.drawRect(lengedX,lengedY-distance/2,lengedX+distance/2,lengedY ,arcPaint);//绘制描述的颜色方块
                rectF.left = lengedX;
                rectF.top = lengedY-distance;
                lengedX = lengedX + distance/2 + 4;
                bitmapCanvas.drawText(datas.get(i).getDesc(),lengedX,lengedY,textPaint);//绘制描述的颜色文字
                lengedX = lengedX + rect.width() + distance;
                rectF.right = lengedX;
                rectF.bottom = lengedY + distance;
                lengedRectes.add(rectF);//将描述说在的Recf存起来备用(注意放大点击的热区)
            }
        }

        bitmapCanvas.save();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int Width = onWidthMeasure(widthMeasureSpec); //计算控件的宽度
        int height = onHeightMeasure(heightMeasureSpec);//计算控件的高度
        setMeasuredDimension(Width,height);
    }

    //当控件的宽度，高度发生变化是调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getMeasuredWidth();
        int hight = getMeasuredHeight();
        if (bitmapBuffer == null
                || (bitmapBuffer.get().getWidth() != width)
                || (bitmapBuffer.get().getHeight() != hight)) {

            if (width > 0 && hight > 0) {

                bitmapBuffer = new WeakReference<Bitmap>(Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_4444));
                bitmapCanvas = new Canvas(bitmapBuffer.get());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        bitmapBuffer.get().eraseColor(Color.TRANSPARENT);//绘制时先擦除画板上的内容(用于更新界面)

        if(datas != null && datas.size() > 0 ){
            if(isLengedVisible){
                drawLenged();
            }

            angleSEs = new ArrayList<>();

            RectF arcRect = new RectF(barWidth/2-radius,(barHeight-lengedHeight)/2 - radius,
                    barWidth/2+radius,(barHeight-lengedHeight)/2 + radius);//圆形所在的RectF，圆心与控件中心重叠
            float startAngle = -90,sweepAngle = 0;
            float perAngle = totalNum/360;//每一度的数量
            String desc;
            float lineAngle;

            Rect rect = new Rect();
            for(int i=0;i<datas.size();i++){
                sweepAngle = datas.get(i).getNum()/perAngle; //当前值的度数
                arcPaint.setColor(datas.get(i).getColor());
                bitmapCanvas.drawArc(arcRect,startAngle,sweepAngle,true,arcPaint);//绘制扇形
                angleSEs.add(new AngleSE(startAngle,sweepAngle+startAngle));

                lineAngle = startAngle+ sweepAngle/2;//绘制描述文字的指示线，从扇形中间开始
                desc = datas.get(i).getDesc()+","+datas.get(i).getSNum();
                drwaLineAndText(sweepAngle,lineAngle,desc,rect,i);

                startAngle += sweepAngle; //开始角度变为扇形结束的角度，下次绘制时从前一个扇形的结束区绘制*
            }
            bitmapCanvas.save();
            bitmapCanvas.restore();
        }

        Rect displayRect = new Rect(0,0,barWidth,barHeight);
        Rect det = new Rect(0,0,getWidth(),getHeight());

        canvas.drawBitmap(bitmapBuffer.get(),displayRect,det,null);
        canvas.restore();
    }

    private void drwaLineAndText(float sweepAngle,float lineAngle,String desc,Rect rect,int i){
        float lineStartX,lineStartY ,lineEndX,lineEndY ;

        lineStartX   =   barWidth/2   +   (radius- distance)   *  (float) Math.cos(lineAngle *   3.14   /180 );
        lineStartY   =   (barHeight-lengedHeight)/2   +   (radius- distance)  *   (float) Math.sin(lineAngle   *   3.14/180);
        if(Math.abs(sweepAngle) <= 30){ //当偏转角度小于30°时，增加指示线的长度，避免描述文字重叠
            float num = (datas.size() - i)%3;
            lineEndX   =   barWidth/2   +   (radius+ distance*num)   *  (float) Math.cos(lineAngle *   3.14   /180 );
            lineEndY   =   (barHeight-lengedHeight)/2   +   (radius+ distance*num*1f)  *   (float) Math.sin(lineAngle   *   3.14   /180);
        }else {
            lineEndX   =   barWidth/2   +   (radius+ distance)   *  (float) Math.cos(lineAngle *   3.14   /180 );
            lineEndY   =   (barHeight-lengedHeight)/2   +   (radius+ distance)  *   (float) Math.sin(lineAngle   *   3.14   /180);
        }
        bitmapCanvas.drawLine(lineStartX,lineStartY,lineEndX,lineEndY,linePaint);

        textPaint.getTextBounds(desc,0,desc.length(),rect);
        textPaint.setTextSize(DensityUtil.dip2px(mContext,11));
        textPaint.setFakeBoldText(false);
        textPaint.setShadowLayer(0,0,0,Color.TRANSPARENT);
        if (lineStartX>barWidth/2) { //当指示线位于饼图右侧时，在右侧绘制第二条指示线及文字
            bitmapCanvas.drawLine(lineEndX,lineEndY,lineEndX+distance/2,lineEndY,linePaint);
            bitmapCanvas.drawText(desc,lineEndX+distance/2+4,lineEndY+rect.height()/2,textPaint);
        }else {//当指示线位于饼图左侧时，在左侧绘制第二条指示线及文字

            bitmapCanvas.drawLine(lineEndX,lineEndY,lineEndX-distance/2,lineEndY,linePaint);
            bitmapCanvas.drawText(desc,lineEndX-distance/2-4-rect.width(),lineEndY+rect.height()/2,textPaint);
        }
    }

    private int onWidthMeasure(int width){
        int mode = MeasureSpec.getMode(width);
        int size = MeasureSpec.getSize(width);

        if(mode == MeasureSpec.EXACTLY){
            barWidth = size;
        }else if(mode == MeasureSpec.AT_MOST){
            barWidth = width - getPaddingLeft() - getPaddingRight();
        }
        return barWidth;
    }


    private int onHeightMeasure(int height){
        int mode1 = MeasureSpec.getMode(height);
        int size1 = MeasureSpec.getSize(height);
        int minSize = (int) DensityUtil.dip2px(mContext,120);
        if(mode1 == MeasureSpec.EXACTLY){
            barHeight = size1;
        }else { //当控件的高度为wrapContet时计算控件的高度
            if(datas != null && datas.size()>0){
                barHeight = (int) radius*2 + lengedHeight + (int) distance*2 + (int) distance*datas.size() ;
            }else {
                barHeight = minSize - getPaddingTop() - getPaddingBottom();
            }
        }

        return barHeight;
    }

    public void setOnSelectedListener(OnSelectedListener l){
        mListener = l;
    }

    public class AngleSE{
        private float startAngle;
        private float sweepAngle;

        public AngleSE(float startAngle, float sweepAngle) {
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
        }

        public float getStartAngle() {
            return startAngle;
        }

        public float getSweepAngle() {
            return sweepAngle;
        }
    }

    public interface OnSelectedListener{ //点击监听接口
        void onSelected(int position);
    }
}
