package kr.or.arex.smartwork.canvas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nexacro.NexacroActivity;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.UUID;

import kr.or.arex.smartwork.NexacroActivityExt;
import kr.or.arex.smartwork.plugin.StandardObject;

public class CanvasActivity extends Activity {

    String LOG_TAG = "NexacroPlugin";

    public StandardObject standardObject  = null;
    NexacroActivityExt nexaobj =  (NexacroActivityExt)NexacroActivity.getInstance();

    private static final String TAG = "StandardObject";
    LinearLayout ll;// 여러 뷰를 합칠 컨테이너
    String base64;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_canvas);// 2022.02.03 이거 안됨 지금은 바쁘니 나중에 바꿀것
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ll = new LinearLayout(this); // 여러 뷰를 합칠 컨테이너
//      LinearLayout ll = findViewById(R.id.linearLayout);// 2022.02.03 이거 안됨 지금은 바쁘니 나중에 바꿀것
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.BOTTOM);

        final Button button1 = new Button(this);
        button1.setText("서명 저장");
        button1.setTextSize(25);
        button1.setPaintFlags(button1.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);//글씨 볼드체
        button1.setBackgroundColor(Color.YELLOW);
        button1.setHeight(100);
        ll.addView(button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll.removeView(button1);

                final String imagename = UUID.randomUUID().toString() + ".png";
                //MediaStore.Images.Media.insertImage(getContentResolver(), ll .getDrawingCache(), imagename, "drawing");

                ll.setDrawingCacheEnabled(true);
                ll.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(ll.getDrawingCache());

                bitmap = resizeBitmapImg(bitmap, 300);//2021.11.25 maxResolution 조정

                v.buildDrawingCache();
                try{
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,80,bos);
                    //byte[] bitmapdata = bos.toByteArray();
                    //bs = new ByteArrayInputStream(bitmapdata);

                    String b64String = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                    base64 = b64String;

//                    Log.d(TAG, "b64String :: " + b64String);

                    String b64Encoder = URLEncoder.encode(b64String, "utf-8");

                    Intent intent1 = new Intent(CanvasActivity.this, NexacroActivityExt.class);
                    //intent1.putExtra("data", b64String);
                    intent1.putExtra("img", bitmap);
                    intent1.putExtra("b64Encoder", b64Encoder);

//                    Bundle bundle = new Bundle();
//                    bundle.putString("data", b64String);
//                    intent1.putExtras(bundle);

//                    Intent intent = getIntent();
//                    intent.putExtra("data", b64String);

                    //setResult(99, intent1);
                    nexaobj.onActivityResult(50001, 99, intent1);
                    //IntentResult result = IntentIntegrator.parseActivityResult(0, 0, intent);
                    if( bos != null){
                        bos.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                finish();
            }
        });

        MyView m = new MyView(CanvasActivity.this);

        ll.addView(m); // 리니어레이아웃에 포함시킴
        setContentView(ll);
    }

    class MyView extends View {
        Paint paint = new Paint();
        Path path  = new Path();    // 자취를 저장할 객체
        public MyView(Context context) {
            super(context);
            paint.setStyle(Paint.Style.STROKE); // 선이 그려지도록
            paint.setStrokeCap(Paint.Cap.ROUND);// 선이 둥글게 끝나도록
            paint.setStrokeWidth(30f); // 선의 굵기 지정
        }
        @Override
        protected void onDraw(Canvas canvas) { // 화면을 그려주는 메서드
            canvas.drawPath(path, paint); // 저장된 path 를 그려라
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN :
                    path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
                    break;
                case MotionEvent.ACTION_MOVE :
                    path.lineTo(x, y); // 자취에 선을 그려라
                    break;
                case MotionEvent.ACTION_UP :
                    break;
            }

           invalidate(); // 화면을 다시그려라

            return true;
        }
    }

    /*
     * 비트맵 이미지의 가로, 세로 이미지 사이즈를 리사이징
     * @param source 원본 Bitmap 객체
     * @param maxResolution 제한 해상도
     * @return 리사이즈된 이미지 Bitmap 객체
     */
    public Bitmap resizeBitmapImg(Bitmap source, int maxResolution){
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height){
            if(maxResolution < width){
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < height){
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent)
//    {		//super.onActivityResult(requestCode, resultCode, data);
//
//        Log.d("", "onActivity!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        Log.d("", "onActivity!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        Log.d("", "111111111111111");
//        Log.d("", "22222222222222222");
//        Log.d("", "333333333333333333");
//        Log.d("", "44444444444444444444");
//        Log.d("", "55555555555555555555555555");
//        Log.d("", "requestCode :: " + requestCode + "!!!!!!!!!!!!!");
//
//        switch(requestCode) {
//            case 2:
////                if (resultCode == -1 || resultCode == 0) {
////                    this.cameraSend.onActivityResult(requestCode, resultCode, intent);
////                }
//                super.onActivityResult(requestCode, resultCode, intent);
//                break;
//            case 3:
////                if (resultCode == -1 || resultCode == 0) {
////                    this.cameraSend.onActivityResult(requestCode, resultCode, intent);
////                }
//                super.onActivityResult(requestCode, resultCode, intent);
//                break;
//            case 1004:
////                if (resultCode == -1) {
////                    FileUpload.getInstance().onActivityResult(this, intent, true);
////                }
//                super.onActivityResult(requestCode, resultCode, intent);
//                break;
//            case 1005:
////                if (resultCode == -1) {
////                    FileUpload.getInstance().onActivityResult(this, intent, false);
////                }
//                super.onActivityResult(requestCode, resultCode, intent);
//                break;
//            default:
//
//                Log.d("", "default");
//                Log.d("", "resultCode :: " + resultCode);
//
//                if (resultCode == Activity.RESULT_OK) {
//
//                    //IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//                   // String re = scanResult.getContents();
//                    String message = base64;
//                    Log.d("onActivityResult", "onActivityResult: ." + message);
//                    //Toast.makeText(this, re, Toast.LENGTH_LONG).show();
//
//                    standardObject.send(0, message);
//                }
//        }
//    }

}

