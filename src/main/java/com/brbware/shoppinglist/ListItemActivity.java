package com.brbware.shoppinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.brbware.shoppinglist.persistence.DatabaseHelper;
import com.brbware.shoppinglist.persistence.entities.ShoppingList;
import com.brbware.shoppinglist.persistence.entities.ShoppingListItem;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;

public class ListItemActivity extends Activity {

    private static String TAG = "shoppinglist";

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

        ListView mainListView = (ListView) findViewById(R.id.mainListView);

        //TODO: Add a shopping list reference into the adapter, or pass in a shopping list id
        // for the adapter to query.
        mainListView.setAdapter(new ShoppingListAdapter(this, R.layout.shopping_list_item));

        // When a new item is added to the list, the view will scroll to the bottom.
        mainListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        initAddButton(this);

        if (mainListView.getAdapter().getCount() <= 0 )
            setHintTextToVisible(true);
        else
            setHintTextToVisible(false);
    }

    private void setHintTextToVisible(Boolean setVisible) {

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

    public class ShoppingListAdapter extends ArrayAdapter<ShoppingListItem> {

        private ShoppingList shoppingList;
        DatabaseHelper databaseHelper;
        Dao shoppingListDao = null;
        Dao shoppingListItemDao = null;
        Context context;

        private ArrayList<ShoppingListItem> shoppingListItems;

        public ShoppingListAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);
            this.context = context;

            databaseHelper = new DatabaseHelper(context);
            shoppingListDao = databaseHelper.getDaoForClass(ShoppingList.class);
            shoppingListItemDao = databaseHelper.getDaoForClass(ShoppingListItem.class);

            //TODO:  Getting first list. Should be dynamically loaded later
            //Also, doing try/catch in constructor is bad news. This is only temporary for testing.
            shoppingListDao = databaseHelper.getDaoForClass(ShoppingList.class);

            try {
                shoppingList = (ShoppingList) shoppingListDao.queryForId(1);
                shoppingListItems = new ArrayList<ShoppingListItem>(shoppingList.getShoppingListItems());

                if (shoppingList == null) {
                    shoppingListDao.create(new ShoppingList());
                }
            } catch (java.sql.SQLException e) {
                //TODO:  Do something with this exception
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            
            //--------------------------------------------------------------
        }

        @Override
        public void add(ShoppingListItem item) {

            try {

                item.setShoppingList(shoppingList);
                shoppingListItemDao.create(item);
                shoppingListItems.add(item);
                notifyDataSetChanged();

            } catch (java.sql.SQLException e) {
                Log.e("ListItemActivity", "Could not create a new Shopping List Item");
            }

        }

        public int getCount() {
            return shoppingListItems.size();
        }

        public ShoppingListItem getItem(int i) {
            return shoppingListItems.get(i);
        }

        public View getView(final int i, View view, ViewGroup viewGroup) {

            View theView = view;

            if (theView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                theView = vi.inflate(R.layout.shopping_list_item, null);
            }

            TextView textView = (TextView) theView.findViewById(R.id.shopping_list_item_text);

            textView.setText(shoppingListItems.get(i).getItemText());

            if (shoppingListItems.get(i).isCheckedOff()){
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else
                textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);


            ImageView imageView = (ImageView) theView.findViewById(R.id.edit_icon);

            imageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    // Create an input dialog
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                    alert.setTitle("Edit " + shoppingListItems.get(i).getItemText());

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    input.setText(shoppingListItems.get(i).getItemText());
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            Editable value = input.getText();
                            if (value.toString().equals(""))
                                return;

                            ShoppingListItem itemToEdit = shoppingListItems.get(i);
                            itemToEdit.setItemText(value.toString());
                            update(itemToEdit);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {}
                    });

                    alert.show();
                }
            });

            textView.setOnClickListener(new View.OnClickListener(){

                public void onClick(View view) {

                    shoppingListItems.get(i).setCheckedOff(!shoppingListItems.get(i).isCheckedOff());
                    try {
                        shoppingListItemDao.update(shoppingListItems.get(i));
                        notifyDataSetChanged();
                    } catch (java.sql.SQLException e) {
                        //TODO:  Do something with this exception
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });

            ImageView deleteIcon = (ImageView) theView.findViewById(R.id.delete_icon);

            deleteIcon.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {

                    try {
                        shoppingListItemDao.delete(shoppingListItems.get(i));
                        shoppingListItems.remove(i);
                        notifyDataSetChanged();

                        if (shoppingListItems.size() <= 0)
                            ((ListItemActivity) context).setHintTextToVisible(true);

                    } catch (java.sql.SQLException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });

            return theView;
        }

        private void update(ShoppingListItem itemToEdit) {
            try {
                shoppingListItemDao.update(itemToEdit);
                notifyDataSetChanged();
            } catch (java.sql.SQLException e) {
                //TODO:  Do something with this exception
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}

