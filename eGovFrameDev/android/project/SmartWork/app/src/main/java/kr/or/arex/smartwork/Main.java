package kr.or.arex.smartwork;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nexacro.NexacroUpdatorActivity;

import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends NexacroUpdatorActivity
{

    private Toast toast;
//  private String arexUrl = "http://192.168.43.57:8080/angs/";     // local
//      private String arexUrl = "http://192.168.42.15:8080/angs/";     // local
//    private String arexUrl = "http://192.168.65.11:8037/angs/";   // dev
    private String arexUrl = "http://sw.arex.or.kr/angs/";        // 운영

    public Main()
    {
        super();

        // 개발자 로컬
        setBootstrapURL(arexUrl+"start_android.json");
        setProjectURL(arexUrl);

        // 개발서버
 //       setBootstrapURL("http://192.168.65.11:8037/angs/start_android.json");
  //      setProjectURL("http://192.168.65.11:8037/angs/");

        // 통합테스트
        //setBootstrapURL("http://sw.arex.or.kr/angs/start_android.json");
        //setProjectURL("http://sw.arex.or.kr/angs/");

        this.setStartupClass(NexacroActivityExt.class);

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        if(!isNetworkConnected()){
            toast = Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.\n네트워크 환경을 확인해주세요.", Toast.LENGTH_LONG);
            toast.show();

            super.finish();

        }else{
            boolean online = isOnline(arexUrl);

            if(!online){
                toast = Toast.makeText(this, "VPN에 연결되어 있지 않습니다.\nVPN 로그인 후 다시 앱을 실행해 주세요.", Toast.LENGTH_LONG);
                toast.show();

                super.finish();
            }

        }
        try{
            Thread.sleep(2000);
            toast = Toast.makeText(this, "2021-12-24에 업데이트 된 버전입니다.", Toast.LENGTH_LONG);
            toast.show();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("updateUrl");
            if (url != null) {
                setBootstrapURL(url);
            }

        }

        super.onCreate(savedInstanceState);
    }

    //!!
    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
    }

    public static boolean isOnline(String url) {
        CheckConnect cc = new CheckConnect(url);
        cc.start();
        try{
            cc.join();
            return cc.isSuccess();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private static class CheckConnect extends Thread{
        private boolean success;
        private String host;

        public CheckConnect(String host){
            this.host = host;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)new URL(host).openConnection();
                conn.setRequestProperty("User-Agent","Android");
                conn.setConnectTimeout(5000);
                conn.connect();
                int responseCode = conn.getResponseCode();

                System.out.println("####################################");
                System.out.println("####################################");
                System.out.println("responseCode :: " + responseCode);
                System.out.println("####################################");
                System.out.println("####################################");


                if(responseCode == 204 || responseCode == 200) {
                    success = true;
                }else{
                    success = false;
                }

            }
            catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if(conn != null){
                conn.disconnect();
            }
        }

        public boolean isSuccess(){
            return success;
        }

    }


}
