package a3wish.main.com.aims212.sam.a3wish.tool;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by percyku on 2017/1/19.
 */
//裡面有撰寫get &post的方法
public class Networks {


    private static String responseString;
     static StringBuilder sb;


//get
    public static  String getHttp(String urlString){
        //String urlString = "http://140.137.51.169/AgriGoodJson.aspx";
        HttpURLConnection connection = null;
        try {
            // 初始化 URL
            URL url = new URL(urlString);
            // 取得連線物件
            connection = (HttpURLConnection) url.openConnection();
            // 設定 request timeout
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            // 模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
            // 設定開啟自動轉址
            connection.setInstanceFollowRedirects(true);

            // 若要求回傳 200 OK 表示成功取得網頁內容
            if( connection.getResponseCode() == HttpsURLConnection.HTTP_OK ){
                // 讀取網頁內容
                InputStream inputStream     = connection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader( new InputStreamReader(inputStream) );

                String tempStr;
                StringBuffer stringBuffer = new StringBuffer();

                while( ( tempStr = bufferedReader.readLine() ) != null ) {
                    stringBuffer.append( tempStr );
                }

                bufferedReader.close();
                inputStream.close();

                // 取得網頁內容類型
                String  mime = connection.getContentType();
                boolean isMediaStream = false;

                // 判斷是否為串流檔案
                if( mime.indexOf("audio") == 0 ||  mime.indexOf("video") == 0 ){
                    isMediaStream = true;
                }

                // 網頁內容字串
                responseString= stringBuffer.toString();

                Log.e("your",responseString);
//                Toast.makeText(MainActivity.this,responseString,Toast.LENGTH_SHORT).show();


            }
        } catch (IOException e) {
            e.printStackTrace();
            responseString=null;
        }
        finally {
            // 中斷連線
            if( connection != null ) {
                connection.disconnect();
            }
        }
        return responseString;

    }





    //post 第一參數是網址，第二參數是String陣列是給的數值
    public static String post(String urlString,String[] itemName) throws UnsupportedEncodingException {

        String[] items=new String[itemName.length];

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "==================================";

        try {

            //String urlString = "http://140.137.51.169/NewProduct.ashx";
            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");

            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);


            DataOutputStream ds = new DataOutputStream(con.getOutputStream());


            for(int i=0;i<itemName.length;i++){
                int a=i+1;
                items[i]="Content-Disposition: form-data; name="+"\""+"item"+a+"\"";
                Log.e("item",items[i]+":"+itemName[i]);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes(items[i] + end);
                ds.writeBytes(end);
                ds.write(itemName[i].getBytes());
                ds.writeBytes(end);

            }



            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();
            ds.close();


            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

            }
            reader.close();
            Log.d("YOU", "上傳成功：" + sb.toString().trim());

        }catch (Exception e){

            Log.d("YOU", e.toString());

        }

        if(sb!=null)
            return sb.toString().trim();
        else
            return "upload fail";


    }

    //post 第一參數是網址，第二參數是String陣列是給的數值 第三參數是String陣列是圖片的uri

    public static String post(String urlString,String[] items,String[] pic) throws UnsupportedEncodingException {

        String name = "國北特餐";
        String category = "飯類";
        String business = "品客";
        String price = "75元";
        String count = "40";
        String description = "好吃又大碗";
        String special = "牛肉";

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "==================================";

        try {

            //String urlString = "http://140.137.51.169/NewProduct.ashx";
            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");

            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);


            DataOutputStream ds = new DataOutputStream(con.getOutputStream());

//1
            String itemName = "Content-Disposition: form-data; name=\"item1\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemName + end);
            ds.writeBytes(end);
            ds.write(name.getBytes());
            ds.writeBytes(end);
//2
            String itemCategory = "Content-Disposition: form-data; name=\"item2\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemCategory + end);
            ds.writeBytes(end);
            ds.write(category.getBytes());
            ds.writeBytes(end);
//3
            String itemBusiness = "Content-Disposition: form-data; name=\"item3\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemBusiness + end);
            ds.writeBytes(end);
            ds.write(business.getBytes());
            ds.writeBytes(end);
//4
            String itemPrice = "Content-Disposition: form-data; name=\"item4\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemPrice + end);
            ds.writeBytes(end);
            ds.write(price.getBytes());
            ds.writeBytes(end);
//5
            String itemCount = "Content-Disposition: form-data; name=\"item5\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemCount + end);
            ds.writeBytes(end);
            ds.write(count.getBytes());
            ds.writeBytes(end);
//6
            String itemDescription = "Content-Disposition: form-data; name=\"item6\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemDescription + end);
            ds.writeBytes(end);
            ds.write(description.getBytes());
            ds.writeBytes(end);
//7
            String itemSpecial = "Content-Disposition: form-data; name=\"item7\"";
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes(itemSpecial + end);
            ds.writeBytes(end);
            ds.write(special.getBytes());
            ds.writeBytes(end);

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            ds.flush();
            ds.close();


            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

            }
            reader.close();
            Log.d("YOU", "上傳成功：" + sb.toString().trim());

        }catch (Exception e){

            Log.d("YOU", e.toString());

        }


        return sb.toString().trim();

    }




}
