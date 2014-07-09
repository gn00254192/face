package com.example.face;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements SurfaceHolder.Callback {
	BubbleSurfaceView bv;
	ProgressDialog mDialog;
	File file;
	// �ŧi�S���u�H���g���H

	private SurfaceHolder surfaceHolder;

	private Camera myCamera;

	private SurfaceView surfaceView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw);

		DisplayMetrics metrics = new DisplayMetrics(); // �^���ù��j�p
		getWindowManager().getDefaultDisplay().getMetrics(metrics);// �^���ù��j�p
		setting.screenweight = metrics.widthPixels;
		setting.screenheit = metrics.heightPixels;
		setting.draw = this;
		bv = (BubbleSurfaceView) findViewById(R.id.surfaceView);
		bv.setSignatureBitmap(this, metrics.widthPixels, metrics.heightPixels);
		GalleryviewofDraw gv = new GalleryviewofDraw(this, bv,
				metrics.heightPixels, metrics.widthPixels);
		findControl();

	}

	private void findControl()

	{

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);

		surfaceHolder = surfaceView.getHolder();
		//
		surfaceHolder.addCallback(this);
		//
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//
	}

	public void cd()
	{
		myCamera.stopPreview();

		myCamera.release();

		myCamera = null;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)

	{


		
		
		Camera.Parameters parameters = myCamera.getParameters();
        List<Size> sizes = parameters.getSupportedPreviewSizes();
    	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
    			sizes.get(sizes.size()-2).height, sizes.get(sizes.size()-2).width);
		surfaceView.setLayoutParams(layoutParams);

        
        
        Size optimalSize = getOptimalPreviewSize(sizes, sizes.get(sizes.size()-2).width,sizes.get(sizes.size()-2).height);

        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        myCamera.setParameters(parameters);
//		parameters.setFocusMode("auto");


		myCamera.startPreview();

	}
	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	@Override
	public void surfaceCreated(SurfaceHolder holder)

	{

		try

		{

			myCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			
			myCamera.setPreviewDisplay(surfaceHolder);

			// ���Y����V�M����ۮt90�סA�ҥH�n��V

			myCamera.setDisplayOrientation(90);

		}

		catch (IOException e)

		{

			myCamera.release();

			myCamera = null;

		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)

	{
		myCamera.stopPreview();

		myCamera.release();

		myCamera = null;
		

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Show home screen when pressing "back" button,
			// so that this app won't be closed accidentally

			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("����");
			dialog.setMessage("�T�w�n�h�X��");
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			// dialog.setCancelable(false);
			dialog.setPositiveButton("�T�w",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (setting.upload != null) {
								setting.upload.recycle();
								setting.upload = null;
							}
							// ���UPositiveButton�n������
							setting.imagenumber = -1;

							finish();

						}
					});
			dialog.setNeutralButton("�T�w�æs��",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								file = new File(Environment
										.getExternalStorageDirectory(),
										"MJCamera");
								// �Y�ؿ����s�b�h�إߥؿ�
								if (!file.mkdirs()) {
									Log.e("LOG_TAG", "�L�k�إߥؿ�");
								}
								long time = System.currentTimeMillis();
								file = new File(file, time / 1000 + ".png");
								FileOutputStream out = new FileOutputStream(
										file);
								// �N Bitmap���Y�����w�榡���Ϥ��üg�J�ɮצ�y
								bv.getSignatureBitmap().compress(
										Bitmap.CompressFormat.PNG, 90, out);
								setting.upload = bv.getSignatureBitmap();
								// ��s�������ɮצ�y
								out.flush();
								out.close();
								// SingleMediaScanner test = new
								// SingleMediaScanner(
								// Draw.this, file);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							finish();
						}
					});
			dialog.setNegativeButton("����",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			dialog.show();

			return true;

		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onResume() {

		super.onResume();
	}

}
