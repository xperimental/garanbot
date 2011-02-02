package net.sourcewalker.garanbot;

import android.app.Activity;
import android.os.Bundle;

/**
 * This activity should display one item from the database in a read-only
 * manner. It should have a button or menu item to bring the user to the
 * {@link EditItemActivity} where he can edit the item.
 * 
 * @author Xperimental
 */
public class ViewItemActivity extends Activity {
	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);

    }

}
