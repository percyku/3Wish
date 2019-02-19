package a3wish.main.com.aims212.sam.a3wish.tool;

/**
 * Created by percyku on 2017/9/29.
 */

public class NeedHelpeListV2 {


    private String Wish_Sn, Wish_Email, Wish_Name, Wish_Sex, Wish_Image,Wish_lat, Wish_long,
            Wish_Content, Wish_Note, Wish_Type, Wish_Date,Member_Sn;




    public NeedHelpeListV2(String Wish_Sn, String Wish_lat, String Wish_long, String Wish_Content,
                           String Wish_Note, String Wish_Type, String Wish_Date,String Member_Sn,
                           String Wish_Name,String Wish_Image,String Wish_Email, String Wish_Sex){
        this.Wish_Sn=Wish_Sn;
        this.Wish_lat=Wish_lat;
        this.Wish_long=Wish_long;
        this.Wish_Content=Wish_Content;
        this.Wish_Note=Wish_Note;
        this.Wish_Type=Wish_Type;
        this.Wish_Date=Wish_Date;
        this.Member_Sn=Member_Sn;
        this.Wish_Name=Wish_Name;
        this.Wish_Image=Wish_Image;
        this.Wish_Email=Wish_Email;
        this.Wish_Sex=Wish_Sex;

    }


    public String getMember_Sn(){return Member_Sn;}

    public String getWish_Sn() {
        return Wish_Sn;
    }

    public String getWish_Email() {
        return Wish_Email;
    }

    public String getWish_Name() {
        return Wish_Name;
    }

    public String getWish_Sex() {
        return  Wish_Sex;
    }

    public String getWish_Image() {
        return Wish_Image;
    }


    public String getWish_lat() {
        return Wish_lat;
    }

    public String getWish_long() {
        return Wish_long;
    }

    public String getWish_Content() {
        return Wish_Content;
    }

    public String getWish_Note() {
        return Wish_Note;
    }

    public String getWish_Type() {
        return Wish_Type;
    }

    private String getWish_Date() {
        return Wish_Date;
    }

}
