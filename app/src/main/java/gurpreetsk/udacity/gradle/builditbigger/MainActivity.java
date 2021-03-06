package gurpreetsk.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.R;

import java.io.IOException;

import gurpreetsk.me.Joker;
import gurpreetsk.me.jokestar.backend.myApi.MyApi;
import gurpreetsk.me.ohmyjoke_android.JokeActivity;


public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView tv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv = (TextView) findViewById(R.id.instructions_text_view);
        btn = (Button) findViewById(R.id.tell_joke_button);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        tv.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {

        progressBar.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        findViewById(R.id.tell_joke_button).setVisibility(View.GONE);
        String joke = getJoke();
        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, joke));

    }

    public String getJoke() {
        Joker joker = new Joker();
        return joker.getJoke();
    }


    public class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
        private MyApi myApiService = null;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://sunshine-146209.appspot.com/_ah/api/")
                        .setApplicationName(getString(R.string.app_name))
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                myApiService = builder.build();
            }

            String joke = params[0].second;

            try {
                return myApiService.sayHi(joke).execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(MainActivity.this, JokeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra(JokeActivity.JOKE_KEY, result);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);

        }
    }


}
