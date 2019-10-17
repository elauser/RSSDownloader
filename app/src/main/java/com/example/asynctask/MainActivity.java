package com.example.asynctask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView test;
    private String testString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (TextView) findViewById(R.id.test);

        Log.d(TAG, "onCreate: starting Asynctask");
        DownloadData downloadData = new DownloadData();
        downloadData.execute("https://rss.itunes.apple.com/api/v1/us/ios-apps/top-free/all/10/explicit.rss");
        Log.d(TAG, "onCreate: done");



        test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                test.setText(testString);
            }
        });

    }

    private class DownloadData extends AsyncTask<String,Void, String>{
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: Error Downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();

            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was" + response);

                /*InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);*/ //Long Version

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); // That's how you do buffered Reader

                int charsRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0){
                        break;
                    }
                    if(charsRead > 0){
                        xmlResult.append(String.copyValueOf(inputBuffer, 0 , charsRead));
                    }
                }
                reader.close();
                testString = xmlResult.toString();

                return xmlResult.toString();

            } catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL" + e.getMessage());
            } catch (IOException e){
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage() );
            }
            return null;
        }
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
