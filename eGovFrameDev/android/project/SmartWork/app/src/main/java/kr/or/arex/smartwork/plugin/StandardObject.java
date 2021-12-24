package kr.or.arex.smartwork.plugin;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.nexacro.NexacroActivity;
import com.nexacro.plugin.NexacroPlugin;

import org.json.JSONObject;

import kr.or.arex.smartwork.NexacroActivityExt;
import kr.or.arex.smartwork.canvas.CanvasActivity;
import kr.or.arex.smartwork.qrcode.CustomScannerActivity;

public class StandardObject extends NexacroPlugin
{
    // Debugging
    private static final String TAG = "StandardObject";

    private static final String SVCID    = "svcid";
    private static final String REASON   = "reason";
    private static final String RETVAL   = "returnvalue";

    private static final int CODE_SUCCES = 0;
    private static final int CODE_ERROR  = -1;

    private static final String CALL_BACK = "_oncallback";

    private static final String METHOD_CALLMETHOD = "callMethod";

    private String mSerivceId = "";

    NexacroActivityExt nexaobj = null;

   // private SannerActivity sannerActivity;
    private CanvasActivity canvasActivity;

    public StandardObject(String objectId)
    {
        super(objectId);
        nexaobj = (NexacroActivityExt)NexacroActivity.getInstance();
    }

    public boolean send(int reason, Object retval)
    {
        JSONObject obj = new JSONObject();
        try
        {
	        obj.put(SVCID, mSerivceId);
	        obj.put(REASON, reason);
	        obj.put(RETVAL, retval);
	
	        callback(CALL_BACK, obj);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
	        mSerivceId = null;
        }

        return false;
    }

    @Override
    public void execute(String method, JSONObject paramObject)
    {
    	mSerivceId = "";

        if(method.equals(METHOD_CALLMETHOD))
        {
            try
            {
                JSONObject params = paramObject.getJSONObject("params");

                mSerivceId = params.getString("serviceid");
                JSONObject data = params.getJSONObject("param");

                Log.d(TAG, "String data1 : "+data.getString("data1"));

                try
                {
                    String gbn = data.getString("data1");

                    if("qr".equals(gbn)){
                        nexaobj.setStandardObject(this);

                        IntentIntegrator integrator = new IntentIntegrator( nexaobj );
                        integrator.setCaptureActivity(CustomScannerActivity.class);
                        integrator.initiateScan();
                    }else if("base64".equals(gbn)){
                        String bData = data.getString("data2");
                        String b64String = Base64.encodeToString(bData.getBytes(), Base64.DEFAULT);

                        Log.d(TAG, "b64String :: " + b64String);
                        Log.d(TAG, "b64Decoding :: " + new String(Base64.decode(b64String, Base64.DEFAULT)));

                        send(CODE_SUCCES,b64String);

                    }else if("canvas".equals(gbn)){

                        Log.d(TAG, "canvas Start ==================================== ");

                        nexaobj.setStandardObject(this);

                        Intent intent = new Intent(nexaobj, CanvasActivity.class);
                        nexaobj.startActivity(intent);

                        //IntentIntegrator integrator = new IntentIntegrator( nexaobj );
                        //integrator.setCaptureActivity(CanvasActivity.class);
                        //integrator.initiateScan();

                    }

                }
                catch(Exception e)
                {
                	send(CODE_ERROR, METHOD_CALLMETHOD + ":" + e.getMessage());
                }
            }
            catch(Exception e)
            {
            	send(CODE_ERROR, METHOD_CALLMETHOD + ":" + e.getMessage());
            }
        }
    }

    @Override
    public void init(JSONObject paramObject)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void release(JSONObject paramObject)
    {
        // TODO Auto-generated method stub
    }

}
