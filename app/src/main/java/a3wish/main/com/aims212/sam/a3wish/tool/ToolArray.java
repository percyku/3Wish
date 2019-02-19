package a3wish.main.com.aims212.sam.a3wish.tool;

import java.util.List;
import java.util.Map;

/**
 * Created by percyku on 2017/9/17.
 */

public class ToolArray {

    //可刪除
    public  static  String Register="http://140.137.51.169:8080/RegisterMember.ashx";
    //對應註冊頁面
    public  static  String RegisterV2="http://140.137.51.169:8080/RegisterMemberV2.ashx";
    //對應忘記密碼頁面
    public  static  String ForgetPasswords="http://140.137.51.169:8080/ForgetPasswords.ashx";
    //可刪除
    public  static  String Login="http://140.137.51.169:8080/LoginPage.ashx";
    //對應進入的loading頁面與登入頁面
    public  static  String LoginUpdate="http://140.137.51.169:8080/LoginPageUpdate.ashx";
    //以下兩個刪掉可能會出錯，可不要理他，
    public  static  String UpdateCharacter="http://140.137.51.169:8080/UpdateCharacter.ashx";
    public  static  String UpdateMemberData="http://140.137.51.169:8080/UpdateMemberData.ashx";
    //可刪除
    public  static  String UpdateMemberDataV2="http://140.137.51.169:8080/UpdateMemberDataV2.ashx";
    //對應修改會員資料頁面
    public  static  String UpdateMemberDataV3="http://140.137.51.169:8080/UpdateMemberDataV3.ashx";
    //對應上傳大頭貼的頁面（google drive上傳圖片）
    public  static  String UpdateMemberPic="http://140.137.51.169:8080/UpdateMemberPic.ashx";


    //對應修改密碼的頁面
    public  static  String UpdatePasswords="http://140.137.51.169:8080/UpdatePasswords.ashx";
    //可刪除
    public  static  String HopePageInsert="http://140.137.51.169:8080/HopePageInsert.ashx";
    //許願頁面
    public  static  String HopePageInsertV2="http://140.137.51.169:8080/HopePageInsertV2.ashx";
    //可刪除
    public  static  String NeedHelpeList="http://140.137.51.169:8080/NeedHelpeList.ashx";
    //圓夢者進入得到的json資料
    public  static  String NeedHelpeListV2="http://140.137.51.169:8080/NeedHelpeListV2.ashx";

    //圓夢頁面（google drive ＆youtube上傳頁面）
    public  static  String AchieveHopeV2="http://140.137.51.169:8080/AchieveHopeV2.ashx";





    public static boolean refreshState=false;



    public static boolean changeAccountState=false;


    public static boolean changePasswordsState=false;


    public static String changePasswordsString="";


    public static boolean uploadState=false;


    //google drive資料id(大頭貼)
    public static String fold_person_pic="0B-RR6-bb-4NAWkxET09JbXVzLVk";

    //google drive資料id(分享圖片)
    public static String fold_share_pic="1LO6yphaYkZPnorFhTGTCxJjOPdp-DcRm";

    //google drive資料id(當沒有使用者沒有圖片時，根據使用性別給予圖片)
    public static String image_boy="1NxI3GuAwpZog6Xb4gr394lYSB0dH-9Pi";

    public static String image_girl="1VTPWoD5q9JuyeYZ0lO2G9NnSHGKpXexu";

    public static NeedHelpeList needHelpeListArry[];

    public static NeedHelpeListV2 needHelpeListArryV2[];



    public static int allJsonString,imageJsonString,panorameJsonString,videoJsonString;





}
