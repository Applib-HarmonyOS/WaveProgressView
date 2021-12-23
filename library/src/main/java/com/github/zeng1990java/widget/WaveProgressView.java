package com.github.zeng1990java.widget;

import com.github.zeng1990java.widget.util.AttrUtils;
import ohos.agp.colors.RgbPalette;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class WaveProgressView extends Component implements Component.BindStateChangedListener, Component.DrawTask {

    /*類型常數*/
    public enum Shape {
        CIRCLE(1), SQUARE(2), HEART(3), STAR(4);
        int value;

        Shape(int value) {
            this.value = value;
        }

        static Shape fromValue(int value) {
            for (Shape shape : values()) {
                if (shape.value == value) return shape;
            }
            return CIRCLE;
        }
    }


    /*位移Animator*/
    private float shiftX1 = 0;
    private float waveVector = -0.25f;
    private int waveOffset = 25;
    private int speed = 25;
    private EventRunner thread = EventRunner.create("WaveProgressView_" + hashCode());
    private EventHandler animHandler, uiHandler;

    /*畫筆*/
    private Paint mBorderPaint = new Paint(); //邊線的Paint
    private Paint mViewPaint = new Paint(); //水位的Paint
    private Paint mWavePaint1 = new Paint(); //後波著色
    private Paint mWavePaint2 = new Paint(); //前波著色

    private Path mPathContent;
    private Path mPathBorder;

    /*初始常數*/
    private static final int DEFAULT_PROGRESS = 405;
    private static final int DEFAULT_MAX = 1000;
    private static final int DEFAULT_STRONG = 50;
    public static final int DEFAULT_BEHIND_WAVE_COLOR = RgbPalette.parse("#443030d5");
    public static final int DEFAULT_FRONT_WAVE_COLOR = RgbPalette.parse("#FF3030d5");
    public static final int DEFAULT_BORDER_COLOR = RgbPalette.parse("#000000");
    private static final float DEFAULT_BORDER_WIDTH = 8f;
    public static final int DEFAULT_TEXT_COLOR = RgbPalette.parse("#000000");
    private static final boolean DEFAULT_ENABLE_ANIMATION = false;
    private static final boolean DEFAULT_HIDE_TEXT = false;
    private static final int DEFAULT_SPIKE_COUNT = 5;
    private static final float DEFAULT_PADDING = 0f;

    /*參數值*/
    private float mShapePadding = DEFAULT_PADDING; //內縮
    private int mProgress = DEFAULT_PROGRESS; //水位
    private int mMax = DEFAULT_MAX; //水位最大值
    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR; //前面水波顏色
    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR; //後面水波顏色
    private int mBorderColor = DEFAULT_BORDER_COLOR; //邊線顏色
    private float mBorderWidth = DEFAULT_BORDER_WIDTH; //邊線寬度
    private int mTextColor = DEFAULT_TEXT_COLOR; //字體顏色
    private boolean isAnimation = DEFAULT_ENABLE_ANIMATION;
    private boolean isHideText = DEFAULT_HIDE_TEXT;
    private int mStrong = DEFAULT_STRONG; //波峰
    private int mSpikes = DEFAULT_SPIKE_COUNT;
    private Shape mShape = Shape.CIRCLE;
    private OnWaveStuffListener mListener;
    private Point screenSize = new Point(0, 0);

    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, AttrSet attrSet) {
        super(context, attrSet);

        /*設定xml參數*/
        mFrontWaveColor = AttrUtils.getColorFromAttr(attrSet, "frontColor", DEFAULT_FRONT_WAVE_COLOR);
        mBehindWaveColor = AttrUtils.getColorFromAttr(attrSet, "behideColor", DEFAULT_BEHIND_WAVE_COLOR);
        mBorderColor = AttrUtils.getColorFromAttr(attrSet, "borderColor", DEFAULT_BORDER_COLOR);
        mTextColor = AttrUtils.getColorFromAttr(attrSet, "textColor", DEFAULT_TEXT_COLOR);
        mProgress = AttrUtils.getIntFromAttr(attrSet, "progress", DEFAULT_PROGRESS);
        mMax = AttrUtils.getIntFromAttr(attrSet, "max", DEFAULT_MAX);
        //mBorderWidth =AttrUtils.getFloatFromAttr(attrSet, "borderWidthSize", DEFAULT_BORDER_WIDTH);
        mStrong = AttrUtils.getIntFromAttr(attrSet, "strong", DEFAULT_STRONG);
        mShape = Shape.fromValue(2);
        mShapePadding = AttrUtils.getFloatFromAttr(attrSet, "shapePadding", DEFAULT_PADDING);
        isAnimation = AttrUtils.getBooleanFromAttr(attrSet, "animatorEnable", DEFAULT_ENABLE_ANIMATION);
        isHideText = AttrUtils.getBooleanFromAttr(attrSet, "textHidden", DEFAULT_HIDE_TEXT);

        /*設定抗鋸齒 & 設定為"線"*/
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE_STYLE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(new Color(mBorderColor));

        /*建立著色器*/
        mWavePaint1 = new Paint();
        mWavePaint1.setStrokeWidth(2f);
        mWavePaint1.setAntiAlias(true);
        mWavePaint1.setColor(new Color(mBehindWaveColor));
        mWavePaint2 = new Paint();
        mWavePaint2.setStrokeWidth(2f);
        mWavePaint2.setAntiAlias(true);
        mWavePaint2.setColor(new Color(mFrontWaveColor));


        /*開啟動畫執行緒*/
        animHandler = new EventHandler(thread);
        uiHandler = new UIHandler(new WeakReference<Component>(this));

        screenSize = new Point(getWidth(), getHeight());
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);

        setBindStateChangedListener(this);
        addDrawTask(this);
    }

    /**
     * 設定水位
     * 0-MAX
     */
    public void setProgress(int progress) {
        if (progress <= mMax) {
            if (mListener != null) {
                mListener.onStuffing(progress, mMax);
            }
            mProgress = progress;
            createShader();
            InnerEvent message = InnerEvent.get();
            uiHandler.sendEvent(message);
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void startAnimation() {
        isAnimation = true;
        if (getWidth() > 0 && getHeight() > 0) {
            animHandler.removeAllEvent();
            animHandler.postTask(new Runnable() {
                @Override
                public void run() {
                    shiftX1 += waveVector; //位移量
                    createShader();
                    InnerEvent message = InnerEvent.get();
                    uiHandler.sendEvent(message);
                    if (isAnimation) {
                        animHandler.postTask(this, speed);
                    }
                }
            });
        }
    }

    public void stopAnimation() {
        isAnimation = false;
    }

    public OnWaveStuffListener getListener() {
        return mListener;
    }

    public void setListener(OnWaveStuffListener mListener) {
        this.mListener = mListener;
    }

    /*
     * 設定最大值
     */
    public void setMax(int max) {
        if (mMax != max) {
            if (max >= mProgress) {
                mMax = max;
                createShader();
                InnerEvent message = InnerEvent.get();
                uiHandler.sendEvent(message);
            }
        }
    }

    public int getMax() {
        return mMax;
    }

    /**
     * 設定邊線顏色
     */
    public void setBorderColor(int color) {
        mBorderColor = color;
        mBorderPaint.setColor(new Color(mBorderColor));
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定前波顏色
     */
    public void setFrontWaveColor(int color) {
        mFrontWaveColor = color;
        mWavePaint2.setColor(new Color(mFrontWaveColor));
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定後波顏色
     */
    public void setBehindWaveColor(int color) {
        mBehindWaveColor = color;
        mWavePaint1.setColor(new Color(mBehindWaveColor));
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定文字顏色
     */
    public void setTextColor(int color) {
        mTextColor = color;
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定邊線寬度
     */
    public void setBorderWidth(float width) {
        mBorderWidth = width;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        resetShapes();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定內縮
     */
    public void setShapePadding(float padding) {
        this.mShapePadding = padding;
        resetShapes();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定動畫速度
     * Fast -> Slow
     * 0...∞
     */
    public void setAnimationSpeed(int speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("The speed must be greater than 0.");
        }
        this.speed = speed;
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定前後水波每次刷新偏移多少
     * 0-100
     */
    public void setWaveVector(float offset) {
        if (offset < 0 || offset > 100) {
            throw new IllegalArgumentException("The vector of wave must be between 0 and 100.");
        }
        this.waveVector = (offset - 50f) / 50f;
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定字體是否隱藏
     *
     * @param hidden 隱藏
     */
    public void setHideText(boolean hidden) {
        this.isHideText = hidden;
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定星星的角數
     * 3...∞
     *
     * @param count 角數
     */
    public void setStarSpikes(int count) {
        if (count < 3) {
            throw new IllegalArgumentException("The number of spikes must be greater than 3.");
        }
        this.mSpikes = count;
        if (Math.min(screenSize.getPointXToInt(), screenSize.getPointYToInt()) != 0) {
            /*===星星路徑===*/
            resetShapes();
        }
    }


    /**
     * 設定前後水波相差位移
     * 1-100
     */
    public void setWaveOffset(int offset) {
        this.waveOffset = offset;
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    /**
     * 設定波峰
     * 0-100
     */
    public void setWaveStrong(int strong) {
        this.mStrong = strong;
        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    public void setShape(Shape shape) {
        mShape = shape;
        resetShapes();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    protected void onSizeChanged(int w, int h) {
        screenSize = new Point(w, h);
        resetShapes();
        if (isAnimation) {
            startAnimation();
        }
    }

    private void resetShapes() {
        int radius = Math.min(screenSize.getPointXToInt(), screenSize.getPointYToInt());
        int cx = (screenSize.getPointXToInt() - radius) / 2;
        int cy = (screenSize.getPointYToInt() - radius) / 2;
        switch (mShape) {
            case STAR:
                /*===星星路徑===*/
                mPathBorder = drawStart(radius / 2 + cx, radius / 2 + cy + (int) mBorderWidth, mSpikes, radius / 2 - (int) mBorderWidth, radius / 4);
                mPathContent = drawStart(radius / 2 + cx, radius / 2 + cy + (int) mBorderWidth, mSpikes, radius / 2 - (int) mBorderWidth - (int) mShapePadding, radius / 4 - (int) mShapePadding);
                break;
            case HEART:
                /*===愛心路徑===*/
                mPathBorder = drawHeart(cx, cy, radius);
                mPathContent = drawHeart(cx + ((int) mShapePadding / 2), cy + ((int) mShapePadding / 2), radius - (int) mShapePadding);
                break;
            case CIRCLE:
                /*===圓形路徑===*/
                mPathBorder = drawCircle(cx, cy, radius);
                mPathContent = drawCircle(cx + ((int) mShapePadding / 2), cy + ((int) mShapePadding / 2), radius - (int) mShapePadding);
                break;
            case SQUARE:
                /*===方形路徑===*/
                mPathBorder = drawSquare(cx, cy, radius);
                mPathContent = drawSquare(cx + ((int) mShapePadding / 2), cy + ((int) mShapePadding / 2), radius - (int) mShapePadding);
                break;
        }


        createShader();
        InnerEvent message = InnerEvent.get();
        uiHandler.sendEvent(message);
    }

    private Path drawSquare(int cx, int cy, int radius) {
        Path path = new Path();
        path.moveTo(cx, cy + (mBorderWidth / 2));
        path.lineTo(cx, radius + cy - mBorderWidth);
        path.lineTo(radius + cx, radius + cy - mBorderWidth);
        path.lineTo(radius + cx, cy + mBorderWidth);
        path.lineTo(cx, cy + mBorderWidth);
        path.close();
        return path;
    }

    private Path drawCircle(int cx, int cy, int radius) {
        Path path = new Path();
        path.addCircle((radius / 2) + cx, (radius / 2) + cy, (radius / 2) - mBorderWidth, Path.Direction.COUNTER_CLOCK_WISE);
        path.close();
        return path;
    }

    private Path drawHeart(int cx, int cy, int radius) {
        Path path = new Path();
        /*起此點*/
        path.moveTo(radius / 2 + cx, radius / 5 + cy);
        /*左上升線*/
        path.cubicTo(5 * radius / 14 + cx, cy, cx, radius / 15 + cy, radius / 28 + cx, 2 * radius / 5 + cy);
        /*左下降線*/
        path.cubicTo(radius / 14 + cx, 2 * radius / 3 + cy, 3 * radius / 7 + cx, 5 * radius / 6 + cy, radius / 2 + cx, 9 * radius / 10 + cy);
        /*右下降線*/
        path.cubicTo(4 * radius / 7 + cx, 5 * radius / 6 + cy, 13 * radius / 14 + cx, 2 * radius / 3 + cy, 27 * radius / 28 + cx, 2 * radius / 5 + cy);
        /*右上升線*/
        path.cubicTo(radius + cx, radius / 15 + cy, 9 * radius / 14 + cx, cy, radius / 2 + cx, radius / 5 + cy);
        path.close();
        return path;
    }

    /**
     * 畫星星
     *
     * @param cx          X
     * @param cy          Y
     * @param spikes      星星的角數
     * @param outerRadius 外圈半徑
     * @param innerRadius 內圈半徑
     * @return 路徑
     */
    private Path drawStart(int cx, int cy, int spikes, int outerRadius, int innerRadius) {
        Path path = new Path();
        double rot = Math.PI / 2d * 3d;
        double step = Math.PI / spikes;

        path.moveTo(cx, cy - outerRadius);
        for (int i = 0; i < spikes; i++) {
            path.lineTo(cx + (float) Math.cos(rot) * outerRadius, cy + (float) Math.sin(rot) * outerRadius);
            rot += step;

            path.lineTo(cx + (float) Math.cos(rot) * innerRadius, cy + (float) Math.sin(rot) * innerRadius);
            rot += step;
        }
        path.lineTo(cx, cy - outerRadius);
        path.close();
        return path;
    }


    /**
     * 建立填充著色器
     * y = Asin(ωx+φ)+h 波型公式 (正弦型函数) y = waveLevel * Math.sin(w * x1 + shiftX) + level
     * φ (初相位x)：波型X軸偏移量     $shiftX
     * ω (角頻率)：最小正周期 T=2π/|ω|       $w
     * A (波幅)：駝峰的大小     $waveLevel
     * h (初相位y)：波型Y軸偏移量     $level
     * <p>
     * 二階貝塞爾曲線(Bézier curve)
     * B(t) = X(1-t)^2 + 2t(1-t)Y + Zt^2 , 0 <= t <= n
     */
    private void createShader() {
        if (screenSize.getPointXToInt() <= 0 || screenSize.getPointYToInt() <= 0) {
            return;
        }
        int viewSize = Math.min(screenSize.getPointXToInt(), screenSize.getPointYToInt());
        double w = (2.0f * Math.PI) / viewSize;

        /*建立畫布*/
        PixelMap.InitializationOptions options = new PixelMap.InitializationOptions();
        options.size = new Size(viewSize, viewSize);
        options.pixelFormat = PixelFormat.ARGB_8888;

        PixelMap bitmap = PixelMap.create(options);
        Canvas canvas = new Canvas(new Texture(bitmap));

        float level = ((((float) (mMax - mProgress)) / (float) mMax) * viewSize) + ((screenSize.getPointY() / 2) - (viewSize / 2)); //水位的高度
        int x2 = viewSize + 1;//寬度
        int y2 = viewSize + 1;//高度
        float zzz = (((float) viewSize * ((waveOffset - 50) / 100f)) / ((float) viewSize / 6.25f));
        float shiftX2 = shiftX1 + zzz; //前後波相差
        int waveLevel = mStrong * (viewSize / 20) / 100;  // viewSize / 20

        for (int x1 = 0; x1 < x2; x1++) {
            /*建立後波 (先後再前覆蓋)*/
            float y1 = (float) (waveLevel * Math.sin(w * x1 + shiftX1) + level);
            canvas.drawLine((float) x1, y1, (float) x1, y2, mWavePaint1);
            /*建立前波*/
            y1 = (float) (waveLevel * Math.sin(w * x1 + shiftX2) + level);
            canvas.drawLine((float) x1, y1, (float) x1, y2, mWavePaint2);
        }

        mViewPaint.setShader(new PixelMapShader(new PixelMapHolder(bitmap),
                Shader.TileMode.REPEAT_TILEMODE, Shader.TileMode.CLAMP_TILEMODE), Paint.ShaderType.LINEAR_SHADER);
    }


    @Override
    public void onComponentBoundToWindow(Component component) {

    }

    @Override
    public void onComponentUnboundFromWindow(Component component) {
        if (animHandler != null) {
            animHandler.removeAllEvent();
        }
        if (thread != null) {
            thread.stop();
        }
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        onSizeChanged(getWidth(), getHeight());
        canvas.drawPath(mPathContent, mViewPaint);
        /*畫邊線*/
        if (mBorderWidth > 0) {
            canvas.drawPath(mPathBorder, mBorderPaint);
        }


        if (!isHideText) {
            /*建立百分比文字*/
            float percent = (mProgress * 100) / (float) mMax;
            String text = String.format(Locale.TAIWAN, "%.1f", percent) + "%";
            Paint textPaint = new Paint();
            textPaint.setColor(new Color(mTextColor));
            if (mShape == Shape.STAR) {
                textPaint.setTextSize((int) (Math.min(screenSize.getPointXToInt(), screenSize.getPointYToInt()) / 2f) / 3);
            } else {
                textPaint.setTextSize((int) (Math.min(screenSize.getPointXToInt(), screenSize.getPointYToInt()) / 2f) / 2);
            }

            textPaint.setAntiAlias(true);
            float textHeight = textPaint.descent() + textPaint.ascent();
            canvas.drawText(textPaint, text, (screenSize.getPointXToInt() - textPaint.measureText(text)) / 2.0f,
                    (screenSize.getPointYToInt() - textHeight) / 2.0f);

        }
    }


    private static class UIHandler extends EventHandler {
        private final Component mView;

        UIHandler(WeakReference<Component> view) {
            super(EventRunner.getMainEventRunner());
            mView = view.get();
        }

        @Override
        protected void processEvent(InnerEvent event) {
            super.processEvent(event);
            if (mView != null) {
                mView.invalidate();
            }
        }
    }

}
