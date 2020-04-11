package Common;

import android.content.Context;
import android.content.Intent;

import com.codepth.maps.Welcomepage;

import Buyer.BuyeProfileCreation;
import Buyer.BuyerChatActivity;
import Buyer.MainActivity;
import Buyer.SellerListActivity;

public class DrawerController {

    private static String IdentifyActivity="null";

    public static void setIdentity(String string){
        IdentifyActivity=string;
    }

    private static String getIdentity(){
        return IdentifyActivity;
    }

    public static boolean sendUsertologinactivity(Context context) {
        if(DrawerController.IdentifyActivity.equals("LoginActivity")){
            return false;
        }
        Intent loginintent=new Intent(context, Welcomepage.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginintent);
        return  true;
    }

    public static boolean sendUserToSettingActivity(Context context){
        if(DrawerController.IdentifyActivity.equals("SettingsActivity")){
            return false;
        }
        Intent intent=new Intent(context, BuyeProfileCreation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        return true;
    }

    public static boolean toChatList(Context context){
        if(DrawerController.IdentifyActivity.equals("BuyerChatActivity")){
            return false;
        }
        context.startActivity(new Intent(context, BuyerChatActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        return true;
    }

    public static boolean toShopList(Context context){
             if(DrawerController.IdentifyActivity.equals("SellerListActivity")){
                 return false;
        }
        else {
                 context.startActivity(new Intent(context, SellerListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                 return true;
             }
     }
    public static  boolean toMainActivity(Context context){
        if(DrawerController.IdentifyActivity.equals("MainActivity")){
            return false;
        }
        else {
            Intent intent = new Intent(context,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            return true;
        }
    }
}
