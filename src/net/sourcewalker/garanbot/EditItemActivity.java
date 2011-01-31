package net.sourcewalker.garanbot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This activity should enable the user to create and edit items in the local
 * database. When the user chooses to save the (new) item he will be brought
 * back to the list activity and a synchronization with the servers should be
 * scheduled.
 * 
 * @author Xperimental
 */
public class EditItemActivity extends Activity {

    private Button applyEditButton;
    private Button cancelEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item);
        applyEditButton = (Button) findViewById(R.id.apply_button);
        applyEditButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

            }
        });

        cancelEditButton = (Button) findViewById(R.id.cancel_button);
        cancelEditButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

}
