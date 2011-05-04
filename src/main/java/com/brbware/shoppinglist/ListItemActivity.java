package com.brbware.shoppinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.brbware.shoppinglist.persistence.entities.ShoppingListItem;

public class ListItemActivity extends Activity {

    private static String TAG = "shoppinglist";

    private ListView mainListView;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        setContentView(R.layout.main);

        // Create and attach the ShoppingListAdapter to the main listview.
        //TODO: Add a shopping list reference into the adapter, or pass in a shopping list id
        //TODO: for the adapter to query.
        mainListView = (ListView) findViewById(R.id.mainListView);
        mainListView.setAdapter(new ShoppingListAdapter(this, R.layout.shopping_list_item));

        initAddButton(this);

        if (mainListView.getAdapter().getCount() <= 0 )
            setHintTextToVisible(true);
        else
            setHintTextToVisible(false);
    }

    public void setHintTextToVisible(Boolean setVisible) {

        TextView backgroundView = (TextView) findViewById(R.id.mainListViewBackgroundText);

        if (setVisible)
            backgroundView.setVisibility(ListView.VISIBLE);
        else
            backgroundView.setVisibility(ListView.GONE);

    }

    private void initAddButton(final Context context) {

        Button addButton = (Button) findViewById(R.id.addToListButton);

        final ShoppingListAdapter shoppingListAdapterRef =
                (ShoppingListAdapter) ((ListView) findViewById(R.id.mainListView)).getAdapter();

        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // Create an input dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(context);

                alert.setTitle("Add new item");

                // Set an EditText view to get user input
                final EditText input = new EditText(context);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Editable value = input.getText();

                        if (value.toString().equals("")) return;

                        shoppingListAdapterRef.add(new ShoppingListItem(value.toString()));

                        ((ListItemActivity) context).setHintTextToVisible(false);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                });

                alert.show();
            }
        });
    }
}

