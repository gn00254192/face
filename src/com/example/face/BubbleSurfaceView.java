package com.example.face;

/*
 * pen 0 is pen .
 * pen 1 is eraser
 * pen 2 is move.
 * pen 3 is intelligent .
 * down1 move1 is for pen 3.
 */
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class p {
	Path path;
	String pathlog = "";
	short pen = 0;
	short a = 0, r = 0, g = 0, b = 0, w = 0;
}

class BubbleSurfaceView extends View implements
		ColorPickerDialog.OnColorChangedListener {
	float[][] Cost;
	float[] cmap0 = new float[256];
	float[] gmap = new float[1024];
	short rw, rh;
	double rs, rx, ry;
	float x, y, cX, cY;
	float previousX, previousY;
	float gmax;
	int border;
	int color;
	p np1;
	int s = 0;
	p np;
	Context context = null;
	ImageView im;
	int i, j, k;
	float scale = 1;
	public float[][] gray;
	private final String LOG_TAG = this.getClass().getSimpleName();
	private float mSignatureWidth = 2f;
	private int mSignatureColor = Color.RED;
	private boolean mCapturing = true;
	private Bitmap mSignature = null, beginbmp = null;
	private Canvas mCanvas;
	private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
	private static final boolean DITHER_FLAG = true;

	static Paint mPaint = new Paint();
	static int ih = 900, iw = 1280;
	private Paint mPainte = new Paint();
	private Paint mPaintw = new Paint();
	private Paint mBitmapPaint;
	private Path mPath = new Path();
	List<p> paths = new ArrayList<p>();
	private final Rect mInvalidRect = new Rect();
	private float mX;
	private float mY;

	private float mCurveEndX;
	private float mCurveEndY;

	private int mInvalidateExtraBorder = 10;

	public BubbleSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BubbleSurfaceView(Context context) {
		super(context);
		init();
	}

	public BubbleSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		setWillNotDraw(false);

		mPainte.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
		mPainte.setColor(Color.WHITE);
		mPainte.setStyle(Paint.Style.STROKE);
		mPainte.setStrokeJoin(Paint.Join.ROUND);
		mPainte.setStrokeCap(Paint.Cap.ROUND);
		mPainte.setStrokeWidth(setting.penweith);
		mPainte.setDither(DITHER_FLAG);
		mPainte.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		// mPainte.setAntiAlias(true);
		mPaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
		mPaint.setColor(mSignatureColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(setting.penweith);
		mPaint.setDither(DITHER_FLAG);

		mPaintw.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
		mPaintw.setColor(Color.WHITE);
		mPaintw.setStyle(Paint.Style.STROKE);
		mPaintw.setStrokeJoin(Paint.Join.ROUND);
		mPaintw.setStrokeCap(Paint.Cap.ROUND);
		mPaintw.setStrokeWidth(setting.penweith);
		mPaintw.setDither(DITHER_FLAG);
		// mPath.reset();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(Color.WHITE);
		canvas.drawColor(Color.argb(0, 255, 255, 0));

		// Log.v("path", st);
		if (mSignature != null) {
			canvas.drawBitmap(mSignature, 0, 0, mBitmapPaint);
		}
		if (setting.pen == 0 || setting.pen == 3) {
			mPaint.setColor(mSignatureColor);
			mPaint.setStrokeWidth(setting.penweith);

			canvas.drawPath(mPath, mPaint);

		} else {
			mPaintw.setStrokeWidth(np.w);

			canvas.drawPath(mPath, mPaintw);
		}

		// s = paths.size() - 1;

		// for (int i = pb; i < (s); i++) {
		// np = paths.get(i);
		// if (np.pen == 0) {
		//
		// canvas.drawPath(np.path, mPaint);
		//
		// } else {
		// canvas.drawPath(np.path, mPainte);
		//
		// }
		// }
		// canvas.drawPath(mPath, mPaint);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (mCapturing) {
			processEvent(event);
			// Log.d(VIEW_LOG_TAG, "dispatchTouchEvent");
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("NewApi")
	private boolean processEvent(MotionEvent event) {
		if (setting.pen < 2) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown(event);
				invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:

				Rect rect = touchMove(event);
				if (rect != null) {
					invalidate();
				}
				return true;

			case MotionEvent.ACTION_UP:

				touchUp(event, false);
				invalidate();
				return true;

			case MotionEvent.ACTION_CANCEL:

				touchUp(event, true);
				invalidate();
				return true;

			}
		} else if (setting.pen == 2) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				mX = event.getX();
				mY = event.getY();
				return true;
			case MotionEvent.ACTION_MOVE:
				im = (ImageView) ((Activity) context)
						.findViewById(R.id.ImageView1);
				im.setX(im.getX() + (event.getX() - mX));
				im.setY(im.getY() + (event.getY() - mY));
				this.setX(this.getX() + (event.getX() - mX));
				this.setY(this.getY() + (event.getY() - mY));
				return true;
			}
		} else if (setting.pen == 3) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown1(event);
				invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:

				Rect rect = touchMove1(event);
				if (rect != null) {
					invalidate();
				}
				return true;

			case MotionEvent.ACTION_UP:

				touchUp(event, false);
				invalidate();
				return true;

			case MotionEvent.ACTION_CANCEL:

				touchUp(event, true);
				invalidate();
				return true;

			}
		}
		return false;

	}

	private void touchUp(MotionEvent event, boolean b) {
		// TODO Auto-generated method stub

		mPath.lineTo(mX, mY);
		np1.path = mPath;
		np1.pen = setting.pen;
		color = new Integer(mSignatureColor);
		np1.a = (short) ((color >> 24) & 0xFF);
		np1.r = (short) ((color >> 16) & 0xFF);
		np1.g = (short) ((color >> 8) & 0xFF);
		np1.b = (short) ((color >> 0) & 0xFF);
		np1.w = (short) setting.penweith;
		np1.pathlog = np1.pathlog + "l," + mX + "," + mY + ";";

		mPath = new Path();
		if (s == 0)
			paths.clear();
		if (s > 0)
			paths = paths.subList(0, s);
		paths.add(np1);
		s++;
		// Log.v("s", s+",");
		for (i = 0; i < (s); i++) {
			np = paths.get(i);
			if (np.pen == 0 || np.pen == 3) {
				mPaint.setColor(Color.argb(np.a, np.r, np.g, np.b));
				mPaint.setStrokeWidth(np.w);
				mCanvas.drawPath(np.path, mPaint);

			} else {
				mPainte.setStrokeWidth(np.w);
				mCanvas.drawPath(np.path, mPainte);

			}
		}
		// paths = new ArrayList<p>();

	}

	private Rect touchMove(MotionEvent event) {
		Rect areaToRefresh = null;

		x = event.getX();
		y = event.getY();
		// Log.v("path", x + "," + y);
		previousX = mX;
		previousY = mY;

		areaToRefresh = mInvalidRect;

		// start with the curve end
		border = mInvalidateExtraBorder;
		areaToRefresh.set((int) mCurveEndX - border, (int) mCurveEndY - border,
				(int) mCurveEndX + border, (int) mCurveEndY + border);

		cX = mCurveEndX = (x + previousX) / 2;
		cY = mCurveEndY = (y + previousY) / 2;

		mPath.quadTo(previousX, previousY, cX, cY);
		np1.pathlog = np1.pathlog + "q," + previousX + "," + previousY + ","
				+ cX + "," + cY + ";";

		// p np = new p();
		// np.path = mPath;
		// np.pen = setting.pen;
		// paths.add(np);
		//
		//
		// mPath = new Path();
		mPath.moveTo(cX, cY);
		np1.pathlog = np1.pathlog + "m," + cX + "," + cY + ";";

		// union with the control point of the new curve
		areaToRefresh.union((int) previousX - border, (int) previousY - border,
				(int) previousX + border, (int) previousY + border);

		// union with the end point of the new curve
		areaToRefresh.union((int) cX - border, (int) cY - border, (int) cX
				+ border, (int) cY + border);

		mX = x;
		mY = y;

		return areaToRefresh;

	}

	private void touchDown(MotionEvent event) {
		np1 = new p();
		mX = event.getX();
		mY = event.getY();

		mPath.moveTo(mX, mY);
		np1.pathlog = np1.pathlog + "m," + mX + "," + mY + ";";

		border = mInvalidateExtraBorder;
		mInvalidRect.set((int) mX - border, (int) mY - border, (int) mX
				+ border, (int) mY + border);

		mCurveEndX = mX;
		mCurveEndY = mY;
	}

	private Rect touchMove1(MotionEvent event) {
		Rect areaToRefresh = null;

		x = event.getX();
		y = event.getY();
		// Log.v("path", x + "," + y);
		previousX = mX;
		previousY = mY;

		cX = mCurveEndX = (x + previousX) / 2;
		cY = mCurveEndY = (y + previousY) / 2;

		np1.pathlog = np1.pathlog + "q0," + previousX + "," + previousY + ","
				+ cX + "," + cY + ";";
		if (x > rx && x < rx + rw && y > ry && y < ry + rh) {

			cX = (float) Math.floor((x - rx) * rs);
			cY = (float) Math.floor((y - ry) * rs);
			j = 1000000;
			// train((int) cX, (int) cY, 1, (x - mX), (y - mX));
			IS((int) cX, (int) cY, 1, (x - mX), (y - mX));

			LOG.OUT(x, y);

		}
		areaToRefresh = mInvalidRect;

		// start with the curve end
		border = mInvalidateExtraBorder;
		areaToRefresh.set((int) mCurveEndX - border, (int) mCurveEndY - border,
				(int) mCurveEndX + border, (int) mCurveEndY + border);

		cX = mCurveEndX = (x + previousX) / 2;
		cY = mCurveEndY = (y + previousY) / 2;

		np1.pathlog = np1.pathlog + "q," + previousX + "," + previousY + ","
				+ cX + "," + cY + ";";
		mPath.quadTo(previousX, previousY, cX, cY);

		// p np = new p();
		// np.path = mPath;
		// np.pen = setting.pen;
		// paths.add(np);
		//
		//
		// mPath = new Path();
		mPath.moveTo(cX, cY);
		np1.pathlog = np1.pathlog + "m," + cX + "," + cY + ";";

		// union with the control point of the new curve
		areaToRefresh.union((int) previousX - border, (int) previousY - border,
				(int) previousX + border, (int) previousY + border);

		// union with the end point of the new curve
		areaToRefresh.union((int) cX - border, (int) cY - border, (int) cX
				+ border, (int) cY + border);

		mX = x;
		mY = y;

		return areaToRefresh;

	}

	private void touchDown1(MotionEvent event) {

		np1 = new p();
		mX = event.getX();
		mY = event.getY();
		np1.pathlog = np1.pathlog + "m0," + mX + "," + mY + ";";
		if (mX > rx && mX < rx + rw && mY > ry && mY < ry + rh) {
			x = (float) Math.floor((mX - rx) * rs);
			y = (float) Math.floor((mY - ry) * rs);
			IS((int) x, (int) y, 2, 0, 0);
			LOG.OUT(mX, mY);
		}

		mPath.moveTo(mX, mY);
		np1.pathlog = np1.pathlog + "m," + mX + "," + mY + ";";

		border = mInvalidateExtraBorder;
		mInvalidRect.set((int) mX - border, (int) mY - border, (int) mX
				+ border, (int) mY + border);

		mCurveEndX = mX;
		mCurveEndY = mY;

	}

	/**
	 * Erases the signature.
	 */
	public void clear() {
		mSignature = null;
		mPath.rewind();
		// Repaints the entire view.
		invalidate();
	}

	public boolean isCapturing() {
		return mCapturing;
	}

	public void setIsCapturing(boolean mCapturing) {
		this.mCapturing = mCapturing;
	}

	public void setSignatureBitmap(Context c, int widthPixels, int heightPixels) {

		context = c;

		mSignature = Bitmap.createBitmap(widthPixels, heightPixels,
				Bitmap.Config.ARGB_8888);
		for (i = 0; i < widthPixels; i++) {
			for (j = 0; j < heightPixels; j++) {
				mSignature.setPixel(i, j, Color.argb(0, 0, 0, 255));
			}
		}

		beginbmp = mSignature.copy(Bitmap.Config.ARGB_8888, false);
		mCanvas = new Canvas(mSignature);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		invalidate();
	}

	public Bitmap getSignatureBitmap() {
		if (mSignature != null) {
			Log.e("LOG_mSignature", "1");
			return mSignature;
		} else if (mPath.isEmpty()) {
			Log.e("LOG_mSignature", "2");
			return null;
		} else {
			Log.e("LOG_mSignature", "3");
			Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bmp);
			c.drawPath(mPath, mPaint);
			return bmp;
		}
	}

	public void setSignatureWidth(float width) {
		mSignatureWidth = width;
		mPaint.setStrokeWidth(mSignatureWidth);
		invalidate();
	}

	public float getSignatureWidth() {
		return mPaint.getStrokeWidth();
	}

	public void setSignatureColor(int color) {
		mSignatureColor = color;
	}

	/**
	 * @return the byte array representing the signature as a PNG file format
	 */
	public byte[] getSignaturePNG() {
		return getSignatureBytes(CompressFormat.PNG, 0);
	}

	/**
	 * @param quality
	 *            Hint to the compressor, 0-100. 0 meaning compress for small
	 *            size, 100 meaning compress for max quality.
	 * @return the byte array representing the signature as a JPEG file format
	 */
	public byte[] getSignatureJPEG(int quality) {
		return getSignatureBytes(CompressFormat.JPEG, quality);
	}

	private byte[] getSignatureBytes(CompressFormat format, int quality) {
		// Log.d(LOG_TAG, "getSignatureBytes() path is empty: " +
		// mPath.isEmpty());
		Bitmap bmp = getSignatureBitmap();
		if (bmp == null) {
			return null;
		} else {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			getSignatureBitmap().compress(format, quality, stream);

			return stream.toByteArray();
		}
	}

	/** 畫布放大 */
	@SuppressLint("NewApi")
	public void zoomout() {
		scale = (float) (scale * 1.3);
		im = (ImageView) ((Activity) context).findViewById(R.id.ImageView1);
		// LOG.OUT((float) rx, (float) ry, (float) rh, (float) rw, (float) rs,
		// map[0].length, map[0][0].length);

		im.setScaleX(scale);
		im.setScaleY(scale);
		this.setScaleX(scale);
		this.setScaleY(scale);
	}

	/** 畫布縮小 */
	@SuppressLint("NewApi")
	public void zoomin() {
		scale = (float) (scale * 0.7);

		im = (ImageView) ((Activity) context).findViewById(R.id.ImageView1);
		im.setScaleX(scale);
		im.setScaleY(scale);
		this.setScaleX(scale);
		this.setScaleY(scale);
	}

	@SuppressLint("NewApi")
	public void zoom1() {
		scale = 1;
		im = (ImageView) ((Activity) context).findViewById(R.id.ImageView1);
		im.setX(0);
		im.setY(0);
		this.setX(0);
		this.setY(0);
		im.setScaleX(scale);
		im.setScaleY(scale);
		this.setScaleX(scale);
		this.setScaleY(scale);
	}

	@Override
	public void colorChanged(int color) {
		// TODO Auto-generated method stub
		mSignatureColor = color;

	}

	public void undo() {
		// TODO Auto-generated method stub
		// Log.v("s", s+","+(s-1));
		if ((s - 1) > 0) {
			mSignature = beginbmp.copy(Bitmap.Config.ARGB_8888, true);
			mCanvas = new Canvas(mSignature);
			s = s - 1;

			for (i = 0; i < (s); i++) {
				np = paths.get(i);
				if (np.pen == 0 || np.pen == 3) {
					mPaint.setColor(Color.argb(np.a, np.r, np.g, np.b));
					mPaint.setStrokeWidth(np.w);
					mCanvas.drawPath(np.path, mPaint);

				} else {
					mPainte.setStrokeWidth(np.w);
					mCanvas.drawPath(np.path, mPainte);

				}
			}
			invalidate();

		} else if ((s - 1) == 0) {
			mSignature = beginbmp.copy(Bitmap.Config.ARGB_8888, true);
			mCanvas = new Canvas(mSignature);
			s = s - 1;
			invalidate();
		}

	}

	public void redo() {
		// Log.v("s", s+","+(s+1));

		if ((s + 1) <= paths.size()) {
			// TODO Auto-generated method stub
			mSignature = beginbmp.copy(Bitmap.Config.ARGB_8888, true);
			mCanvas = new Canvas(mSignature);
			s = s + 1;
			for (i = 0; ((i < (s)) && (i <= (paths.size()))); i++) {
				np = paths.get(i);
				if (np.pen == 0 || np.pen == 3) {
					mPaint.setColor(Color.argb(np.a, np.r, np.g, np.b));
					mPaint.setStrokeWidth(np.w);
					mCanvas.drawPath(np.path, mPaint);

				} else {
					mPainte.setStrokeWidth(np.w);
					mCanvas.drawPath(np.path, mPainte);

				}
			}
			invalidate();
		}
	}

	// private void train(int xx, int yy, int stat, float dx, float dy) {
	//
	// }

	private void IS(int xx, int yy, int stat, float dx, float dy) {
		int bx, by, ex, ey, tempx, tempy, px = xx, py = yy;// begin,end
		bx = (int) (((xx - setting.weight) >= 0) ? (-setting.weight) : (-xx));
		by = (int) (((yy - setting.weight) >= 0) ? (-setting.weight) : (-yy));
		ex = (int) (((xx + setting.weight) < Cost.length) ? (setting.weight)
				: (Cost.length - 1));
		ey = (int) (((yy + setting.weight) < Cost[1].length) ? (setting.weight)
				: (Cost[1].length - 1));
		float min, cost;
		min = setting.WG + setting.WL + setting.WZ;
		dx = dx / (float) Math.sqrt(dx * dx + dy * dy);
		dy = dy / (float) Math.sqrt(dx * dx + dy * dy);
		if (stat == 1) {
			for (int i = bx; i < 0; i++) {
				tempx = xx + i;
				for (j = by; j < 0; j++) {
					tempy = yy + j;
					cost = (bx * dx + by * dy)
							/ (float) Math.sqrt(bx * bx + by * by);
					if (cost > setting.TH) {
						cost = 1 - cost;
						cost = cost
								* (Cost[tempx][tempy] + setting.WL * (-bx - by)
										/ (2 * setting.weight));
						if (cost < min) {
							min = cost;
							px = tempx;
							py = tempy;
						}
					}
				}
				for (j = 0; j < ey; j++) {
					tempy = yy + j;
					cost = (bx * dx + by * dy)
							/ (float) Math.sqrt(bx * bx + by * by);
					if (cost > setting.TH) {
						cost = 1 - cost;
						cost = cost
								* (Cost[tempx][tempy] + setting.WL * (-bx - by)
										/ (2 * setting.weight));
						if (cost < min) {
							min = cost;
							px = tempx;
							py = tempy;
						}
					}
				}
			}
			tempx = xx;

			for (j = by; j < 0; j++) {
				tempy = yy + j;
				cost = (bx * dx + by * dy)
						/ (float) Math.sqrt(bx * bx + by * by);
				if (cost > setting.TH) {
					cost = 1 - cost;
					cost = cost
							* (Cost[tempx][tempy] + setting.WL * (-bx - by)
									/ (2 * setting.weight));
					if (cost < min) {
						min = cost;
						px = tempx;
						py = tempy;
					}
				}
			}
			for (j = 1; j < ey; j++) {
				tempy = yy + j;
				cost = (bx * dx + by * dy)
						/ (float) Math.sqrt(bx * bx + by * by);
				if (cost > setting.TH) {
					cost = 1 - cost;
					cost = cost
							* (Cost[tempx][tempy] + setting.WL * (-bx - by)
									/ (2 * setting.weight));
					if (cost < min) {
						min = cost;
						px = tempx;
						py = tempy;
					}
				}
			}
			for (int i = 1; i < ex; i++) {
				tempx = xx + i;
				for (j = by; j < 0; j++) {
					tempy = yy + j;
					cost = (bx * dx + by * dy)
							/ (float) Math.sqrt(bx * bx + by * by);
					if (cost > setting.TH) {
						cost = 1 - cost;
						cost = cost
								* (Cost[tempx][tempy] + setting.WL * (-bx - by)
										/ (2 * setting.weight));
						if (cost < min) {
							min = cost;
							px = tempx;
							py = tempy;
						}
					}
				}
				for (j = 0; j < ey; j++) {
					tempy = yy + j;
					cost = (bx * dx + by * dy)
							/ (float) Math.sqrt(bx * bx + by * by);
					if (cost > setting.TH) {
						cost = 1 - cost;
						cost = cost
								* (Cost[tempx][tempy] + setting.WL * (-bx - by)
										/ (2 * setting.weight));
						if (cost < min) {
							min = cost;
							px = tempx;
							py = tempy;
						}
					}
				}
			}
		} else {
			for (int i = bx; i < 0; i++) {
				tempx = xx + i;
				for (j = by; j < 0; j++) {
					tempy = yy + j;

					cost = (Cost[tempx][tempy] + setting.WL * (-bx - by)
							/ (2 * setting.weight));
					if (cost < min) {
						min = cost;
						px = tempx;
						py = tempy;
					}

				}
				for (j = 0; j < ey; j++) {
					tempy = yy + j;
					cost = (Cost[tempx][tempy] + setting.WL * (-bx - by)
							/ (2 * setting.weight));
					if (cost < min) {
						min = cost;
						px = tempx;
						py = tempy;
					}
				}
			}
			tempx = xx;

			for (j = by; j < 0; j++) {
				tempy = yy + j;
				cost = (Cost[tempx][tempy] + setting.WL * (-bx - by)
						/ (2 * setting.weight));
				if (cost < min) {
					min = cost;
					px = tempx;
					py = tempy;
				}
			}
			for (j = 1; j < ey; j++) {
				tempy = yy + j;
				cost = (Cost[tempx][tempy] + setting.WL * (-bx - by)
						/ (2 * setting.weight));
				if (cost < min) {
					min = cost;
					px = tempx;
					py = tempy;
				}
			}
			for (int i = 1; i < ex; i++) {
				tempx = xx + i;
				for (j = by; j < 0; j++) {
					tempy = yy + j;
					cost = (Cost[tempx][tempy] + setting.WL * (-bx - by)
							/ (2 * setting.weight));
					if (cost < min) {
						min = cost;
						px = tempx;
						py = tempy;
					}
				}
				for (j = 0; j < ey; j++) {
					tempy = yy + j;
					cost = (Cost[tempx][tempy] + setting.WL * (-bx - by)
							/ (2 * setting.weight));
					if (cost < min) {
						min = cost;
						px = tempx;
						py = tempy;
					}
				}
			}
		}
		if (stat == 1) {
			x = (float) (px / rs + rx);
			y = (float) (py / rs + ry);
		} else if (stat == 2) {
			mX = (float) (px / rs + rx);
			mY = (float) (py / rs + ry);
			// x = (float) (map[k][(int) cX][(int) cY][0] / rs + rx);
			// y = (float) (map[k][(int) cX][(int) cY][1] / rs + ry);
		}
	}

	public void updatepaht() {
		np1 = new p();
			np1.pathlog = np1.pathlog + "pin;";
			paths.add(np1);
			s++;
	}
}