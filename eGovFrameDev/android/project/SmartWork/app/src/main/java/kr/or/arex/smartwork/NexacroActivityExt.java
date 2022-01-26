package kr.or.arex.smartwork;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nexacro.NexacroActivity;

import java.io.ByteArrayOutputStream;

import kr.or.arex.smartwork.plugin.StandardObject;

public class NexacroActivityExt extends NexacroActivity
{
	String LOG_TAG = "NexacroPlugin";

    public StandardObject standardObject  = null;
    public void setStandardObject(StandardObject obj) {
        this.standardObject = obj;
    }


    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.e(LOG_TAG, "onCreate");

		super.onCreate(savedInstanceState);
	}

	@Override
	@SuppressLint("NewApi")
	protected void onResume()
	{
		//this.callScript(script);
		// TODO Auto-generated method stub
		super.onResume();

//		IntentIntegrator integrator = new IntentIntegrator( this );
//		integrator.setCaptureActivity(CustomScannerActivity.class);
//		integrator.initiateScan();

	}
	
	/*
	public void setExtObj(WoosimObject obj)
	{
		this.extobj = obj;
	}
	*/

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{		//super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "onActivity!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.d(LOG_TAG, "requestCode :: " + requestCode + "!!!!!!!!!!!!!");

        switch(requestCode) {
            case 2:
//                if (resultCode == -1 || resultCode == 0) {
//                    this.cameraSend.onActivityResult(requestCode, resultCode, intent);
//                }
                    super.onActivityResult(requestCode, resultCode, intent);
                break;
            case 3:
//                if (resultCode == -1 || resultCode == 0) {
//                    this.cameraSend.onActivityResult(requestCode, resultCode, intent);
//                }
                    super.onActivityResult(requestCode, resultCode, intent);
                break;
            case 1004:
//                if (resultCode == -1) {
//                    FileUpload.getInstance().onActivityResult(this, intent, true);
//                }
                    super.onActivityResult(requestCode, resultCode, intent);
                break;
            case 1005:
//                if (resultCode == -1) {
//                    FileUpload.getInstance().onActivityResult(this, intent, false);
//                }
                    super.onActivityResult(requestCode, resultCode, intent);
                break;
            default:

                Log.d(LOG_TAG, "default");
                Log.d(LOG_TAG, "resultCode :: " + resultCode);

                if (resultCode == Activity.RESULT_OK) {

                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                    String re = scanResult.getContents();
                    String message = re;
                    Log.d(LOG_TAG, "onActivityResult: ." + re);
                    //Toast.makeText(this, re, Toast.LENGTH_LONG).show();

                    this.standardObject.send(0, re);
                 } else if (resultCode == 99) {

                    //String data = intent.getStringExtra("data");
                    Bitmap bitmap = intent.getParcelableExtra("img");
                    String b64Encoder = intent.getStringExtra("b64Encoder");

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,80,bos);

                    String b64String = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);

                    Log.d(LOG_TAG, "data11 :: " + b64String);

                    Log.d(LOG_TAG, "b64Encoder :: " + b64Encoder);

                    Log.d(LOG_TAG, "b64Decoding :: " + new String(Base64.decode(b64String, Base64.DEFAULT)));

                    try{
                        if( bos != null){
                            bos.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


//                    Bundle bundle = intent.getExtras();
//                    String data = bundle.getString("data");

                    Log.d(LOG_TAG, "data22 :: " + b64String);

                    this.standardObject.send(0, b64String);

                }
        }
	}

}
