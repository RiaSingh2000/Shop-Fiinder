package Chats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.codepth.maps.R;
import com.github.chrisbanes.photoview.PhotoView;

public class Zoom extends AppCompatActivity {

    PhotoView i1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        i1=findViewById(R.id.zoom);

        Bundle bundle=getIntent().getExtras();

        if(bundle!=null)
        {
            int resid=bundle.getInt("resId");
            i1.setImageResource(resid);
        }

    }
}
