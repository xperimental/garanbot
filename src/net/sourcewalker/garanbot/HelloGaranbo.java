package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.api.GaranboClient;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelloGaranbo extends Activity {

    private static final String USER = "";
    private static final String PASS = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView view = (TextView) findViewById(R.id.text);

        GaranboClient client = new GaranboClient(USER, PASS);

        view.setText(client.user().get());
    }
}
