package Buyer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AboutUsDialog extends AppCompatDialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("About Us")
                .setMessage(
                        "This app aims to bring you closer to the shops near your location.It brings them on a single platform for an easier access.\n" +
                        "It enables you to get in touch with the shopkeeper and pre-book the available products easily.\n" +
                        "User can contact the shopkeeper via phone call or through the messaging platorm provided by us in the application.\n" +
                        "It works on Gps system hence requires the location to be turned on for better experience.\n" +
                        "\nAbout Developers\n" +
                        "We at Codepth aim to deliver products which are need of the hour and is user friendly.\n\n" +
                        "Developed by:\n" +
                        "Roli Verma\n" +
                        "Aditi Agrawal\n" +
                        "Ria Singh\n" +
                        "Anmol Sinha")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
