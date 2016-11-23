package com.example.afzal.aura;

import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Afzal on 11/21/2016.
 */
public class BackgroundService extends IntentService {

    File file;

    public BackgroundService() {
        super("LogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        createTextFile();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath(),true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("AURA","File is created");

        //Retrieving App Information

        while(true) {

            Log.d("AURA","Into the loop");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());


            String topPackageName = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                // We get usage stats for the last 10 seconds
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 5000, time);
                Log.d("AURA","Total Number of Apps: "+stats.size());
                // Sort the stats by the last time used
                if (stats != null) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {

                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);

                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {

                        topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        Log.d("AURA","Top Application"+topPackageName);
                        try {
                            if (!topPackageName.isEmpty()) {
                                Log.d("AURA","Writing to File");
                                bufferedWriter.write(currentDateTime + " " + topPackageName);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                Log.d("AURA","Sleep Start");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("AURA","Wake Up!");
        }

    }

    void createTextFile(){

        File root = new File(Environment.getExternalStorageDirectory(), "Logs");
        // if external memory exists and folder with name Notes
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }
        file = new File(root, "log.txt");  // file path to save

    }
}
