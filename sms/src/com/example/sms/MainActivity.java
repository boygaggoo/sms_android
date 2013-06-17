package com.example.sms;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button button = null;  
      
    EditText mNumber = null;  
      
    /**编辑信息**/  
    EditText mMessage = null;  
	
	/**发送与接收的广播**/  
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";  
   
      
      
    Context mContext = null;  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);  
	    setContentView(R.layout.activity_main);  
	  
	    button = (Button) findViewById(R.id.button);  
	    mNumber = (EditText) findViewById(R.id.number);  
	    mMessage = (EditText) findViewById(R.id.message);  
	  
	    mContext = this;  
	    button.setOnClickListener(new OnClickListener() {  
	  
	        @Override  
	        public void onClick(View view) {  
	  
	        /** 拿到输入的手机号码 **/  
	        String number = mNumber.getText().toString();  
	        /** 拿到输入的短信内容 **/  
	        String text = mMessage.getText().toString();  
	  
	        /** 手机号码 与输入内容 必需不为空 **/  
	//        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(text)) {  
	            sendSMS(number, text);  
	//        }  
	        }  
	    });  
  
      
	    // 注册广播 发送消息  
	    registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));  
	    registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));  
	    
	    
//	    addSmsObserver();
	    startSmsService();
  
    }  
    
    
    private void startSmsService(){
    	Intent intent = new Intent(this,SmsService.class);
    	startService(intent);
    }
      
    private BroadcastReceiver sendMessage = new BroadcastReceiver() {  
  
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	        //判断短信是否发送成功  
	        switch (getResultCode()) {  
	        case Activity.RESULT_OK:  
	        Log.d("tsh", "短信发送成功");
	        Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();  
	        break;  
	        default:  
	        	 Log.d("tsh", "短信发送失败");
	        Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();  
	        break;  
	        }  
	    }  
    };  
      
     
    private BroadcastReceiver receiver = new BroadcastReceiver() {  
  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        //表示对方成功收到短信  
    	Log.d("tsh", "对方接收成功");
        Toast.makeText(mContext, "对方接收成功",Toast.LENGTH_LONG).show();  
    }  
    };  
      
    /** 
     * 参数说明 
     * destinationAddress:收信人的手机号码 
     * scAddress:发信人的手机号码  
     * text:发送信息的内容  
     * sentIntent:发送是否成功的回执，用于监听短信是否发送成功。 
     * DeliveryIntent:接收是否成功的回执，用于监听短信对方是否接收成功。 
     */  
    private void sendSMS(String phoneNumber, String message) {  
    // ---sends an SMS message to another device---  
    SmsManager sms = SmsManager.getDefault();  
  
    // create the sentIntent parameter  
    Intent sentIntent = new Intent(SENT_SMS_ACTION);  
    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,  
        0);  
  
    // create the deilverIntent parameter  
    Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
    PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,  
        deliverIntent, 0);  
  
	    //如果短信内容超过70个字符 将这条短信拆成多条短信发送出去  
	    if (message.length() > 70) {  
	        ArrayList<String> msgs = sms.divideMessage(message);  
	        for (String msg : msgs) {  
	        sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);  
	        }  
	    } else {  
	        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);  
	    } 
	    
	    addSmsToDb(phoneNumber, message);
	    
    }  
    
    private void addSmsToDb(String number, String message){

    	/**将发送的短信插入数据库**/  
   	        ContentValues values = new ContentValues();  
   	        //发送时间  
   	        values.put("date", System.currentTimeMillis());  
   	        //阅读状态  
   	        values.put("read", 0);  
   	        //1为收 2为发  
   	        values.put("type", 2);  
   	        //送达号码  
   	         values.put("address", number);  
   	         //送达内容  
   	         values.put("body", message);  
   	         //插入短信库  
   	         getContentResolver().insert(Uri.parse("content://sms"),values); 

    }
    
//    /**
//     * 短信数据库监听
//     */
//    private void addSmsObserver(){
//    	SmsObServer smsObServer = new SmsObServer(new Handler(), this);
//    	getContentResolver().registerContentObserver( Uri.parse("content://sms/"), true, smsObServer);
//    }
    
//    /**
//     * 短信数据库监听
//     * @author tsh
//     *
//     */
//    class SmsObServer extends ContentObserver{
//    	
//    	private Context ctx;
//
//    	private Handler _handler = null;
//    	
//		public SmsObServer(Handler handler,Context context) {
//			super(handler);
//			// TODO Auto-generated constructor stub
//			_handler = handler;
//			ctx = context;
//		}
//		
//		@Override
//		public void onChange(boolean selfChange) {
//			// TODO Auto-generated method stub
//			super.onChange(selfChange);
//			Log.d("tsh", "observer change");
//			try {
//				Uri uri = Uri.parse("content://sms/inbox");
//				Cursor cursor = ctx.getContentResolver().query(uri, null, "read=" + 0, null, null);
//				
//				if (cursor != null){  
//					 while (cursor.moveToNext()){
//						 int phoneNumberColumn = cursor.getColumnIndex("address");  
//					      int smsbodyColumn = cursor.getColumnIndex("body");
//					      Log.d("tsh", cursor.getString(phoneNumberColumn)+":"+cursor.getString(smsbodyColumn));
//					 }
//	           
//	            }  
//				
//			
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//    	
//    }

}
