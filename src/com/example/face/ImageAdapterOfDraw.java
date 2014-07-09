package com.example.face;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageAdapterOfDraw extends BaseAdapter {
	private Context mContext;
	private Integer width;
	private Integer height;
	private Integer[] mImageIds;
	private File file,file1;

	public ImageAdapterOfDraw(Context c) {
		mContext = c;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);

		// 設定圖片來源

		if (position<mImageIds.length)
		imageView.setImageResource(mImageIds[position]);

		else
		{
			file1 = new File(file.listFiles()[position-mImageIds.length].toString());
			InputStream in;
				try {
					in = mContext.getContentResolver().openInputStream(Uri.fromFile(file1));
					BitmapFactory.Options opts = new BitmapFactory.Options();
			        opts.inJustDecodeBounds = true;
			        BitmapFactory.decodeStream(in, null, opts);
			        in.close();
			        BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = computeSampleSize(opts, -1, 100*100);
					Bitmap bmp = BitmapFactory.decodeFile(file1.getAbsolutePath(),options);
					imageView.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}
		// 設定圖片的寬、高
		imageView.setLayoutParams(new Gallery.LayoutParams(width, height));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageView;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer[] getmImageIds() {
		return mImageIds;
	}

	public void setmImageIds(Integer[] mImageIds) {
		this.mImageIds = mImageIds;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public int getCount() {
		return mImageIds.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public void setFile(File f) {
		file=f;
		// TODO Auto-generated method stub

	}
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;

		if (initialSize <= 8) {

			roundedSize = 1;

			while (roundedSize < initialSize) {

				roundedSize <<= 1;

			}

		} else {

			roundedSize = (initialSize + 7) / 8 * 8;

		}
        return roundedSize;

	}
}
