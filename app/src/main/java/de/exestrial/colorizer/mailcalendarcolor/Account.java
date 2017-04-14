package de.exestrial.colorizer.mailcalendarcolor;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Philipp on 17.04.2016.
 */
public class Account implements Parcelable{

    public static class Color{
        public final int mail_value;
        public final int calendar_value;
        public final int rgb;
        public Color(int id, int rgb){
            this.rgb = rgb;
            this.mail_value = id;
            this.calendar_value = rgb-256*256*256;
        }
        public static Color byRGB(int rgb){
            for (Color c : COLORS){
                if (rgb == c.rgb){
                    return c;
                }
            }
            return null;
        }
        public static Color byMailValue(int id){
            for (Color c : COLORS){
                if (id == c.mail_value){
                    return c;
                }
            }
            return null;
        }
    }

    public static final Color[] COLORS = {
            new Color(  3, 0x4EA770),
            new Color(262, 0x2E9F36),
            new Color(  0, 0x0086CB),
            new Color(  4, 0x0F4EAC),
            new Color(265, 0x118F97),
            new Color(257, 0x3DD0AC),
            new Color(270, 0x88CBFF),
            new Color(  2, 0x8766C8),
            new Color(266, 0xC8919E),
            new Color(268, 0xC14CB5),
            new Color(260, 0xEA89EE),
            new Color(256, 0xFB5E6D),
            new Color(259, 0xF88461),
            new Color(  1, 0xFF5D3D),
            new Color(263, 0xB42A2E),
            new Color(264, 0x9E593B),
            new Color(271, 0xDB9000),
            new Color(261, 0xD3C203),
            new Color(258, 0x978E25),
            new Color(267, 0x8A997C),
            new Color(269, 0xB1C2C7)

    };

    final static int NO_ID = -1;
    String mail = null;
    Color color = null;
    int mailDbId = NO_ID;
    String title = null;

    public Account(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mail);
        dest.writeInt(color == null ? -1 : color.rgb);
        dest.writeInt(mailDbId);
        dest.writeString(title);
    }

    public static final Parcelable.Creator<Account> CREATOR
            = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private Account(Parcel in) {
        mail = in.readString();
        int c = in.readInt();
        if (c >=0 )
            color = Color.byRGB(c);
        mailDbId = in.readInt();
        title = in.readString();
    }

}
