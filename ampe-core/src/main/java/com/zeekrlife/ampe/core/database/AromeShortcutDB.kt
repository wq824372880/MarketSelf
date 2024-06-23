package com.zeekrlife.ampe.core.database

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AromeShortcutBean::class],
    version = 1,
    exportSchema = false
)
abstract class AromeShortcutDB : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AromeShortcutDB? = null
        fun getInstance(context: Context): AromeShortcutDB {
            return instance ?: synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AromeShortcutDB::class.java,
                    "shortcut.db"
                ).allowMainThreadQueries()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                        }

                    }).build()
            }
        }

        fun fromContentValues(values: ContentValues?): AromeShortcutBean {
            val aromeShortcutBean = AromeShortcutBean()
            if (values != null && values.containsKey("id")) {
                aromeShortcutBean.id = values.getAsLong("id")
            }
            if (values != null && values.containsKey("name")) {
                aromeShortcutBean.name = values.getAsString("name")
            }
            if (values != null && values.containsKey("slogan")) {
                aromeShortcutBean.slogan = values.getAsString("slogan")
            }
            if (values != null && values.containsKey("appletUrl")) {
                aromeShortcutBean.appletUrl = values.getAsString("appletUrl")
            }
            if (values != null && values.containsKey("appletByteArray")) {
                aromeShortcutBean.appletByteArray = values.getAsByteArray("appletByteArray")
            }
            return aromeShortcutBean
        }
    }

    abstract fun getAromeShortcutDao(): AromeShortcutDao
}