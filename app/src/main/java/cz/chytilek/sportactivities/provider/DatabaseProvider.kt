package cz.chytilek.sportactivities.provider

import android.content.Context
import android.content.UriMatcher
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseProvider(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "SportActivity.db"
        private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private val AUTHORITY = "cz.chytilek.sportactivities.provider.DatabaseProvider"

        const val TABLE_SPORT = "sportActivity"
        private const val CREATE_SPORT = "create table if not exists sportActivity (_id integer primary key autoincrement, name text not null unique, location text, lengthOfActivity integer, DBType integer)"
        private const val SPORT = 1
    }

    init {
        sUriMatcher.addURI(AUTHORITY, TABLE_SPORT, SPORT)
    }

    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(CREATE_SPORT)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.execSQL("DROP TABLE IF EXISTS $TABLE_SPORT")
        onCreate(p0)
    }
}