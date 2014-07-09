package com.example.face;

import android.util.Log;

public class LOG {

	public static void OUT(float... integers) {
		// TODO Auto-generated method stub
		String st="";
		for(int i=0;i<integers.length;i++)
			st=st+integers[i]+",";
		Log.v("log",st);
		
	}

}
