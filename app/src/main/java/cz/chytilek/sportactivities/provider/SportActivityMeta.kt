package cz.chytilek.sportactivities.provider

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.BaseColumns._ID
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import cz.chytilek.sportactivities.model.SportActivity

object SportActivityMeta : BaseColumns {

    const val CHILD_KEY = "activities"
    const val BUNDLE_KEY = "activity"

    private const val NAME = "name"
    private const val LOCATION = "location"
    private const val LENGTH_OF_ACTIVITY = "lengthOfActivity"
    private const val DB_TYPE = "DBType"

    // SQLite
    fun insert(context: Context, sportActivity: SportActivity){
        val dbHelper = DatabaseProvider(context)
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(NAME, sportActivity.name)
        values.put(LOCATION, sportActivity.location)
        values.put(LENGTH_OF_ACTIVITY, sportActivity.lengthOfActivity)
        values.put(DB_TYPE, sportActivity.DBType)

        if(sportActivity.ID < 0)
            db.insert(DatabaseProvider.TABLE_SPORT, null, values)
        else
            db.update(DatabaseProvider.TABLE_SPORT, values, "$_ID ='${sportActivity.ID}'", null)

        dbHelper.close()
    }

    fun getSportActivityByName(context: Context, name: String): SportActivity? {
        val dbHelper = DatabaseProvider(context)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(_ID, NAME, LOCATION, LENGTH_OF_ACTIVITY, DB_TYPE)
        val cursor = db.query(DatabaseProvider.TABLE_SPORT, projection, "$NAME ='$name'", null, null, null, null)

        var activity: SportActivity? = null
        if(cursor != null){
            if(cursor.moveToFirst()) {
                activity = make(cursor)
            }

            cursor.close()
        }
        dbHelper.close()
        return activity
    }

    fun getSportActivityByID(context: Context, ID: Long): SportActivity? {
        val dbHelper = DatabaseProvider(context)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(_ID, NAME, LOCATION, LENGTH_OF_ACTIVITY, DB_TYPE)
        val cursor = db.query(DatabaseProvider.TABLE_SPORT, projection, "$_ID ='$ID'", null, null, null, null)

        var activity: SportActivity? = null
        if(cursor != null){
            if(cursor.moveToFirst()) {
                activity = make(cursor)
            }

            cursor.close()
        }
        dbHelper.close()
        return activity
    }

    fun getAllSportActivity(context: Context): MutableList<SportActivity> {
        val dbHelper = DatabaseProvider(context)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(_ID, NAME, LOCATION, LENGTH_OF_ACTIVITY, DB_TYPE)
        val cursor = db.query(DatabaseProvider.TABLE_SPORT, projection, null, null, null, null, null)

        val activities = mutableListOf<SportActivity>()
        if(cursor != null){
            while(cursor.moveToNext()) {
                activities.add(make(cursor))
            }
            cursor.close()
        }
        dbHelper.close()
        return activities
    }

    fun make(cursor: Cursor): SportActivity{
        return SportActivity(cursor.getLong(cursor.getColumnIndex(_ID)),
            cursor.getString(cursor.getColumnIndex(NAME)),
            cursor.getString(cursor.getColumnIndex(LOCATION)),
            cursor.getLong(cursor.getColumnIndex(LENGTH_OF_ACTIVITY)),
            cursor.getInt(cursor.getColumnIndex(DB_TYPE)))
    }

    fun delete(context: Context, sportActivity: SportActivity){
        val dbHelper = DatabaseProvider(context)
        val db = dbHelper.writableDatabase

        db.delete(DatabaseProvider.TABLE_SPORT, "$_ID ='${sportActivity.ID}'", null)
        dbHelper.close()
    }

    // Firebase
    fun insertFB(sportActivity: SportActivity){
        val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference(CHILD_KEY)
        firebaseDatabase.child(sportActivity.name).setValue(sportActivity.toMap())
    }

    fun deleteFB(sportActivity: SportActivity){
        val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference(CHILD_KEY)
        firebaseDatabase.child(sportActivity.name).removeValue()
    }

    fun deleteFB(name: String){
        val firebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference(CHILD_KEY)
        firebaseDatabase.child(name).removeValue()
    }
}