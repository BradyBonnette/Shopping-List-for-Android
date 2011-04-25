package com.brbware.shoppinglist.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brbware.shoppinglist.persistence.entities.ShoppingList;
import com.brbware.shoppinglist.persistence.entities.ShoppingListItem;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */


//TODO:  Figure out a way to make this class an Application class, or built into an Application class.
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "shopping_list.db";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 2;

    // the DAO object we use to access the SimpleData table
    private Map<Class, Dao> daoList;


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {
            init();
        } catch (SQLException e) {
            //TODO:  Do something with this exception
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void init() throws SQLException {

        daoList = new HashMap<Class, Dao>();

        daoList.put(ShoppingList.class, BaseDaoImpl.createDao(getConnectionSource(), ShoppingList.class));
        daoList.put(ShoppingListItem.class, BaseDaoImpl.createDao(getConnectionSource(), ShoppingListItem.class));

        //--------------TODO:  REMOVE THIS LOOP, TESTING ONLY, REMOVE LATER
        Dao groceryListDao = daoList.get(ShoppingList.class);

        ShoppingList mainList = (ShoppingList) groceryListDao.queryForId(1);

        if (mainList == null) {
            groceryListDao.create(new ShoppingList());
        }
        //--------------------------------------------------------------------
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            for (Map.Entry<Class, Dao> classDaoEntry : daoList.entrySet()) {
                Map.Entry pairs = (Map.Entry) classDaoEntry;
                TableUtils.createTable(connectionSource, (Class) pairs.getKey());
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            for (Map.Entry<Class, Dao> classDaoEntry : daoList.entrySet()) {
                Map.Entry pairs = (Map.Entry) classDaoEntry;
                //TableUtils.createTable(connectionSource, (Class) pairs.getKey());
                TableUtils.dropTable(connectionSource, (Class) pairs.getKey(), true);
            }
            // after we drop the old databases, we create the new ones
            onCreate(db);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao getDaoForClass(Class theClass) {
        return daoList.get(theClass);
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        for (Map.Entry<Class, Dao> classDaoEntry : daoList.entrySet()) {
            Map.Entry pairs = classDaoEntry;
            pairs.setValue(null);
            //TableUtils.createTable(connectionSource, (Class) pairs.getKey());
            //TableUtils.dropTable(connectionSource, (Class) pairs.getKey(), true);
        }
        //simpleDao = null;
    }
}
