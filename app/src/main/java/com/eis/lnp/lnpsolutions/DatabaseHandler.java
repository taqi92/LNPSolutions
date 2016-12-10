package com.eis.lnp.lnpsolutions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.entity.mime.content.FileBody;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "LNPsolutions";

    // LNP table name
    private static final String TABLE_TAGS = "Tags";
    private static final String TABLE_IMAGES = "Images";

    // tags Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";
    private static final String KEY_LAND_TYPE = "land_type";
    private static final String KEY_LAND_SIZE = "land_size";
    private static final String KEY_FARMER_NAME = "farmer_name";
    private static final String KEY_FARMER_PHONE = "farmer_phone";
    private static final String KEY_HARVEST = "harvest";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_TAG_ID = "tag_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TAGS_TABLE = "CREATE TABLE " + TABLE_TAGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LAND_TYPE + " TEXT,"
                + KEY_LAND_SIZE + " TEXT,"
                + KEY_FARMER_NAME + " TEXT,"
                + KEY_FARMER_PHONE + " TEXT,"
                + KEY_HARVEST + " TEXT,"
                + KEY_LAT + " TEXT,"
                + KEY_LON + " TEXT" + ")";
        db.execSQL(CREATE_TAGS_TABLE);

        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PATH + " TEXT,"
                + KEY_TAG_ID + " TEXT" + ")";
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addTag(Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FARMER_NAME, tag.farmer_name); // Tag
        values.put(KEY_FARMER_PHONE, tag.farmer_phone); // Tag
        values.put(KEY_LAND_TYPE, tag.land_type); // Tag
        values.put(KEY_LAND_SIZE, tag.land_size); // Tag
        values.put(KEY_HARVEST, tag.harvest); // Tag
        values.put(KEY_LAT, tag.lat); // Tag
        values.put(KEY_LON, tag.lon); // Tag


        // Inserting Row
        long tagid=db.insert(TABLE_TAGS, null, values);
        int j=0;

        if(tag.images.size()>0){
            for(String im : tag.images){
                ContentValues vals = new ContentValues();

                vals.put(KEY_PATH, im); // Tag
                vals.put(KEY_TAG_ID, tagid); // Tag
                if(!tag.blocked.contains(j)){
                    db.insert(TABLE_IMAGES, null, vals);
                }
                j++;
            }
        }

    }

    void manClosedb(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.close(); // Closing database connection
    }

    // Getting single contact
    /*Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }*/

    // Getting All Tags
    public List<Tag> getAllTags() {
        List<Tag> tList = new ArrayList<Tag>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TAGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ArrayList<String> imags= new ArrayList<String>();
                String selectQuery1 = "SELECT  * FROM " + TABLE_IMAGES + " WHERE "+KEY_TAG_ID+"="+cursor.getString(0);

                Cursor cursor1 = db.rawQuery(selectQuery1, null);

                if (cursor1.moveToFirst()) {
                    do {
                        // Adding images to list
                        imags.add(cursor1.getString(1));
                    } while (cursor1.moveToNext());
                }

                Tag tag = new Tag(imags,cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
                        cursor.getString(6),cursor.getString(7),null);
                tag.setID(Integer.parseInt(cursor.getString(0)));

                // Adding Tag to list
                tList.add(tag);
            } while (cursor.moveToNext());
        }

        // return contact list
        return tList;
    }

    /*
    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }*/

    // Deleting single Tag
    public void deleteTag(Tag t) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TAGS, KEY_ID + " = ?",
                new String[] { String.valueOf(t.id) });
        db.close();
    }


    // Getting contacts Count
    public int getTagsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TAGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }

}