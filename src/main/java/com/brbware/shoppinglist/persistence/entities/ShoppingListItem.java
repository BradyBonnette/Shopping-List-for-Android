package com.brbware.shoppinglist.persistence.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "shopping_list_items")
public class ShoppingListItem {

    @DatabaseField(generatedId = true, columnName = "shopping_list_item_id")
    private int shoppingListItemId;

    @DatabaseField(foreign = true, columnName = "shopping_list_id")
    private ShoppingList shoppingList;

    @DatabaseField(columnName = "created_date")
    private long createdDate;

    @DatabaseField(columnName = "item_text")
    private String itemText;

    @DatabaseField(columnName = "is_checked_off", defaultValue = "0")
    private int isCheckedOff;

    public ShoppingListItem() {
        this.createdDate = new Date().getTime();
    }

    public ShoppingListItem(String text) {
        this.createdDate = new Date().getTime();
        this.shoppingList = null;
        this.itemText = text;
    }
    public ShoppingListItem(ShoppingList list){
        this.createdDate = new Date().getTime();
        this.shoppingList = list;
    }

    public ShoppingListItem(ShoppingList list, String text){
        this.createdDate = new Date().getTime();
        this.shoppingList = list;
        this.itemText = text;
    }

    public String getItemText(){
        return this.itemText;
    }
    
    public void setItemText(String text){
        this.itemText = text;
    }

    public int getId() {
        return this.shoppingListItemId;
    }

    public ShoppingList getShoppingList(){
        return this.shoppingList;
    }

    public void setShoppingList(ShoppingList list) {
        this.shoppingList = list;
    }

    public Date getCreatedDate() {
        return new Date(this.createdDate);
    }

    // Because Bools in SQLite are actually int's.. bleh.
    public void setCheckedOff(Boolean checkedOff){
        if(checkedOff)
            this.isCheckedOff = 1;
        else
            this.isCheckedOff = 0;
    }

    public Boolean isCheckedOff(){
        return this.isCheckedOff != 0;
    }
}
