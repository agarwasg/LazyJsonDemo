package me.doubledutch.lazyjsondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import me.doubledutch.lazy.LazyArray;
import me.doubledutch.lazy.LazyObject;

public class LazyJsonDemoActivity extends AppCompatActivity {

    public static final String FILE_NAME = "test_data.json";
    public static final String NEW_LINE = "\n";
    public static final int SAMPLING_LOOPS = 500;
    private SimpleTimer simpleTimer;
    private Button startTestButton;
    private TextView parseTextView;
    private TextView timeTakenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lazy_json_demo);
        parseTextView = (TextView) findViewById(R.id.parsedValueEditText);
        timeTakenTextView = (TextView) findViewById(R.id.timeElapsedText);
        startTestButton = (Button) findViewById(R.id.lazyJsonButton);

        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseWithLazyJson();
                startTestButton.setAlpha(0.5f);
                startTestButton.setClickable(false);

            }
        });

    }


    private void parseWithLazyJson() {
        //read file
        parseTextView.setText("");
        new AssetFileReadTask(FILE_NAME, this)
                .readJsonFileAsync()
                .onSuccess(new Continuation<String, Void>() {
                    @Override
                    public Void then(Task<String> task) throws Exception {
                        Log.d("lazy", "parsed file success");
                        String jsonString = task.getResult();
                        double totalTimeLazy = getParseTimeWithLazyJson(jsonString, SAMPLING_LOOPS);
                        double totalTimeGson = parseTimeWithGson(jsonString, SAMPLING_LOOPS);
                        timeTakenTextView.setText("Time Taken for per parse with Lazy Json " + totalTimeLazy / Math.pow(10, 6) + "ms");
                        parseTextView.setText("Time Taken for per parse with Gson Json " + totalTimeGson / Math.pow(10, 6) + "ms");
                        startTestButton.setAlpha(1f);
                        startTestButton.setClickable(true);

                        return null;

                    }
                }, Task.UI_THREAD_EXECUTOR);

    }

    private double getParseTimeWithLazyJson(String jsonString, int samplingLoops) {
        SimpleTimer simpleTimer = new SimpleTimer.Builder().startMessage("Start Parsing LazyJson").logInfo().start();
        for (int k = 0; k < samplingLoops; k++) {
            LazyArray lazyArray = new LazyArray(jsonString);
            int length = lazyArray.length();
            for (int i = 0; i < length; i++) {
                StringBuilder parsedValues = new StringBuilder();
                LazyObject lazyObject = lazyArray.getJSONObject(i);
                parsedValues.append("id:")
                        .append(lazyObject.getString("id"))
                        .append(NEW_LINE)
                        .append("index:")
                        .append(lazyObject.getInt("index"))
                        .append(NEW_LINE)
                        .append("isActive:")
                        .append(lazyObject.getBoolean("isActive"))
                        .append(NEW_LINE)
                        .append("age:")
                        .append(lazyObject.getInt("age"))
                        .append(NEW_LINE);
                LazyArray friendLazyArray = lazyObject.getJSONArray("friends");
                //always read only one object
                LazyObject lazyFriendbject = friendLazyArray.getJSONObject(0);
                parsedValues.append("first friend of user:")
                        .append(lazyFriendbject.getString("name"));
                parsedValues.append(NEW_LINE).append(NEW_LINE);

            }

        }
        return simpleTimer.mark("finished parsing") / samplingLoops;
    }

    private double parseTimeWithGson(String jsonString, int samplingLoops) {

        Gson gson = new Gson();
        SimpleTimer simpleTimer = new SimpleTimer.Builder().startMessage("Start Parsing Gson").logInfo().start();
        for (int k = 0; k < samplingLoops; k++) {
            Type listType = new TypeToken<List<User>>() {
            }.getType();
            List<User> userList = gson.fromJson(jsonString, listType);
            int length = userList.size();
            for (int i = 0; i < length; i++) {
                StringBuilder parsedValues = new StringBuilder();
                parsedValues.append("id:")
                        .append(userList.get(i).getId())
                        .append(NEW_LINE)
                        .append("index:")
                        .append(userList.get(i).getIndex())
                        .append(NEW_LINE)
                        .append("isActive:")
                        .append(userList.get(i).isActive())
                        .append(NEW_LINE)
                        .append("age:")
                        .append(userList.get(i).getAge())
                        .append(NEW_LINE);


                parsedValues.append("first friend of user:")
                        .append(userList.get(i).getFriends().get(0).getName());
                parsedValues.append(NEW_LINE).append(NEW_LINE);
            }

        }

        return simpleTimer.mark("finished parsing") / samplingLoops;

    }
}
