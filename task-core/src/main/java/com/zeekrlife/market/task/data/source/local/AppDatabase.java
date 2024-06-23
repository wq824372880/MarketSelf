package com.zeekrlife.market.task.data.source.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zeekr.basic.Common;
import com.zeekrlife.market.task.data.source.TaskEntity;

@Database(entities = {TaskEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "task_db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance() {
        if (instance == null) {
            instance = create(Common.app);
        }
        return instance;
    }

    /**
     * 创建数据库实例。
     * 该方法使用Room库的databaseBuilder来构建AppDatabase的实例。
     * 它配置了数据库的初始化，包括允许在主线程上执行查询，以及在数据库创建、打开和破坏性迁移时的回调。
     *
     * @param context 应用的上下文环境，用于访问应用的资源和其他组件。
     * @return 返回一个初始化好的AppDatabase实例。
     */
    private static AppDatabase create(final Context context) {
        // 使用Room库的databaseBuilder来构建数据库实例
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .allowMainThreadQueries() // 允许在主线程上执行查询
                .addCallback(new Callback() { // 添加数据库操作的回调
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db); // 在数据库创建时调用
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db); // 在数据库打开时调用，启用外键约束
                        db.execSQL("PRAGMA foreign_keys = ON;"); // 启用外键约束
                    }

                    @Override
                    public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                        super.onDestructiveMigration(db); // 在破坏性迁移时调用
                    }
                })
                .fallbackToDestructiveMigration() // 如果迁移失败，则进行破坏性迁移，即重新创建数据库
                .build(); // 构建并返回数据库实例
    }

    public abstract TasksDao taskDao();
}
