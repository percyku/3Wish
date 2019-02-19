package a3wish.main.com.aims212.sam.a3wish.tool;

/**
 * Created by percyku on 2017/9/29.
 */

public class NeedHelpeList {


    private String Hope_Sn, Hope_Email, Hope_Name, Hope_Sex, Hope_Image, Hope_lat, Hope_long, Hope_Content, Hope_Note, Hope_Type, Hope_Date;


    public NeedHelpeList(String Hope_Sn, String Hope_Email, String Hope_Name, String Hope_Sex, String Hope_Image, String Hope_lat, String Hope_long, String Hope_Content, String Hope_Note, String Hope_Type, String Hope_Date) {

        this.Hope_Sn = Hope_Sn;
        this.Hope_Email = Hope_Email;
        this.Hope_Name = Hope_Name;
        this.Hope_Sex = Hope_Sex;
        this.Hope_Image = Hope_Image;
        this.Hope_lat = Hope_lat;
        this.Hope_long = Hope_long;
        this.Hope_Content = Hope_Content;
        this.Hope_Note = Hope_Note;
        this.Hope_Type = Hope_Type;
        this.Hope_Date = Hope_Date;
    }

    public String getHope_Sn() {
        return Hope_Sn;
    }

    public String getHope_Email() {
        return Hope_Email;
    }

    public String getHope_Name() {
        return Hope_Name;
    }

    public String getHope_Sex() {
        return  Hope_Sex;
    }

    public String getHope_Image() {
        return Hope_Image;
    }

    public String getHope_lat() {
        return Hope_lat;
    }

    public String getHope_long() {
        return Hope_long;
    }

    public String getHope_Content() {
        return Hope_Content;
    }

    public String getHope_Note() {
        return Hope_Note;
    }

    public String getHope_Type() {
        return Hope_Type;
    }

    private String getHope_Date() {
        return Hope_Date;
    }

}
