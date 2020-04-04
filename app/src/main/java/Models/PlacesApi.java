package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class PlacesApi {
    public ArrayList<String> autoComplete(String input){
        ArrayList arrayList=new ArrayList();
        HttpURLConnection httpURLConnection=null;
        StringBuilder jsonResult=new StringBuilder();
        StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
        stringBuilder.append("input="+input);
        stringBuilder.append("&key=AIzaSyC4oSY9sO_ta8qGwLO1oVj-0q6D3vZXMhE");
        try {
            URL url=new URL(stringBuilder.toString());
            httpURLConnection=(HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader=new InputStreamReader(httpURLConnection.getInputStream());

            int read;
            char buffer[]=new char[1024];
            while ((read=inputStreamReader.read(buffer))!=-1){
                jsonResult.append(buffer,0,read);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }

        }
        try {
            JSONObject jsonObject=new JSONObject(jsonResult.toString());
            JSONArray predictions=jsonObject.getJSONArray("predictions");
            for(int i=0;i<predictions.length();i++)
                arrayList.add(predictions.getJSONObject(i).getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
