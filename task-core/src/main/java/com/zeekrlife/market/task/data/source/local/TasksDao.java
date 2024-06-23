package com.zeekrlife.market.task.data.source.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zeekrlife.market.task.data.source.TaskEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertTask(TaskEntity taskEntity);

    @Delete
    Completable deleteTask(TaskEntity taskEntity);

    @Update
    Completable updateTask(TaskEntity taskEntity);

    @Query("SELECT * FROM task")
    Single<List<TaskEntity>> getTasks();

    @Query("SELECT * FROM task WHERE id = :taskId")
    Single<TaskEntity> getTask(String taskId);

    @Query("SELECT * FROM task")
    List<TaskEntity> getTasksSync();

    @Query("SELECT * FROM task WHERE id = :taskId")
    TaskEntity getTaskSync(String taskId);
}
