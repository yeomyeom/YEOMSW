package kr.or.arex.smartwork;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// 참고 : https://haruvely.tistory.com/9

public class PermissionChk{
    private Context context;
    private Activity activity;
    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.RESTART_PACKAGES,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.GET_TASKS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_PHONE_NUMBERS
    };
    private List permissionList;

    //권한 요청 시 발생하는 창에 대한 결과값을 받기 위해 지정해주는 임의의 값
    private final int MULTIPLE_PERMISSIONS = 7685;

    PermissionChk(Activity a, Context c){
        this.activity = a;
        this.context = c;
    }

    //배열로 선언한 권한 중 허용되지 않은 권한이 있는지 점검
    public boolean chkPerm(){
        int result;
        permissionList = new ArrayList<String>();

        for(String p : permissions){

            result = ContextCompat.checkSelfPermission(context, p);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(p);
            }
        }
        Log.d("debug Permission List", permissionList.toString());
        return !permissionList.isEmpty();
    }

    //배열로 선언한 권한에 대해 사용자에게 허용 요청
    public void reqPerm(){
        ActivityCompat.requestPermissions(activity, (String[]) permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
    }

    //요청한 권한에 대한 결과값 판단 및 처리
    public boolean resPerm(int reqCode, @NonNull String[] perm, @NonNull int[] grantReslt){
        //reqCode가 아까 위에 숫자와 맞는지 겨과값의 길이가 0보다 큰지 먼저 체크
        if(reqCode == MULTIPLE_PERMISSIONS && grantReslt.length > 0){
            for(int result : grantReslt){
                if(result == -1){return false;}//0 허용 -1 거부
            }
        }
        return true;
    }
}

/*abstract
public class MainActivity extends AppCompatActivity {

    // 클래스 선언
    private PermissionChk permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chkPerm();

    }


    // 권한 체크
    private void chkPerm() {

        // PermissionChk.java 클래스 객체 생성
        permission = new PermissionChk(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.chkPerm()){
            //권한 요청
            permission.reqPerm();
        }
    }

    // Request Permission에 대한 결과 값 받아오기
    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] perm, @NonNull int[] grantReslt) {
        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
        if (!permission.resPerm(reqCode, perm, grantReslt)) {
            // 다시 permission 요청
            permission.reqPerm();
        }
        super.onRequestPermissionsResult(reqCode, perm, grantReslt);
    }


}
*/