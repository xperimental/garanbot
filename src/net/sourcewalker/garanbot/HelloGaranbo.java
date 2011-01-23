package net.sourcewalker.garanbot;

import net.sourcewalker.garanbot.api.ClientException;
import net.sourcewalker.garanbot.api.GaranboClient;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HelloGaranbo extends Activity {

    private static final String USER = "";
    private static final String PASS = "";
    private static final int MENU_ITEMS = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView view = (TextView) findViewById(R.id.text);

        GaranboClient client = new GaranboClient(USER, PASS);

        try {
            view.setText(client.item().get(410).toString());
        } catch (ClientException e) {
            Log.e("HelloGaranbo", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ITEMS, 0, R.string.menuItems).setShortcut('1', 'f');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEMS:
            Intent iItems = new Intent(this, GaranbotItems.class);
            startActivity(iItems);
            return true;
        }
        return false;
    }
}
