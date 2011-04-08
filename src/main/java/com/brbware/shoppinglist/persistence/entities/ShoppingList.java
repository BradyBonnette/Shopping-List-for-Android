package com.brbware.shoppinglist.persistence.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "shopping_lists")
public class ShoppingList {


    @DatabaseField(generatedId = true, columnName = "shopping_list_id")
    private int shoppingListId;

    @DatabaseField(columnName = "created_date")
    private long createdDate;

    @ForeignCollectionField(eager = false)
    ForeignCollection<ShoppingListItem> shoppingListItems;

    public ShoppingList() {
        this.createdDate = new Date().getTime();
    }

    public int getId() {
        return this.shoppingListId;
    }

    public Date getCreatedDate() {
        return new Date(this.createdDate);
    }

    public ForeignCollection<ShoppingListItem> getShoppingListItems() {
        return shoppingListItems;
    }
}