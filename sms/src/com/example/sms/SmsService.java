package com.example.sms;

import android.R.integer;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.Observable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SmsService extends Service {
	
	private String uri = "content://sms/inbox"; 
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Context context = this;
		
		Log.d("tsh", "sms server start");
		
		SmsObserver smsObserver = new SmsObserver(new Handler(), context);
		
		getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	class SmsObserver extends ContentObserver{
		
		private Context context;

		public SmsObserver(Handler handler,Context context) {
			super(handler);
			this.context = context;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			ContentResolver resolver = context.getContentResolver();
//			Cursor cursor = resolver.query(Uri.parse(uri), null, null,null, null);
			Cursor cursor = resolver.query(Uri.parse(uri), null, "read = ?", new String[]{"0"}, null);
			String info;
			if (cursor.moveToFirst()) {         
				 int phoneNumberColumn = cursor.getColumnIndex("address");  
			     int smsbodyColumn = cursor.getColumnIndex("body");
			     int id = cursor.getColumnIndex("_id");
				 int thread_id = cursor.getColumnIndex("thread_id");
			     Log.d("tsh", cursor.getString(thread_id)+":"+cursor.getString(id)+"--"+ cursor.getString(phoneNumberColumn)+":"+cursor.getString(smsbodyColumn));
			     if("abc".equals(cursor.getString(smsbodyColumn))){
			    	 remove(cursor.getString(thread_id),cursor.getString(id));
			     }
			     //			    do{     
//			    for(int j = 0; j < cursor.getColumnCount(); j++){     
//			            info = "name:" + cursor.getColumnName(j) + "=" + cursor.getString(j); 
//			            Log.d("tsh", info); 
//			        } 
//			    }while(cursor.moveToNext());      
			}
		}
		
		private void remove(String threadId,String id){
			Log.d("tsh", "remove id:"+id);
			getContentResolver().delete(Uri.parse("content://sms/conversations/"+threadId), "_id=?", new String[]{id});
		}
		
	}

}
