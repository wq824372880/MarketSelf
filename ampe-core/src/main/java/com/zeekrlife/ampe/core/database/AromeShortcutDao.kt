package com.zeekrlife.ampe.core.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AromeShortcutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAromeShortcut(aromeShortcut: AromeShortcutBean): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAromeShortcutList(list: List<AromeShortcutBean>): LongArray?

    @Query("SELECT * FROM arome_shortcut WHERE id = :id")
    fun queryAromeShortcut(id: Long): Cursor?

    @Query("SELECT * FROM arome_shortcut")
    fun queryAromeShortcutList(): Cursor?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAromeShortcut(aremeShort: AromeShortcutBean): Int

    @Delete
    fun deleteAromeShortcut(aremeShort: AromeShortcutBean)

    @Query("DELETE FROM arome_shortcut WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("DELETE FROM arome_shortcut")
    fun deleteAll()
}