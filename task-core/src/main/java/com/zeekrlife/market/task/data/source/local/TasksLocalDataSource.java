package com.zeekrlife.market.task.data.source.local;

import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.market.task.data.source.TaskEntity;
import com.zeekrlife.market.task.data.source.TasksDataSource;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

import java.util.List;

import io.reactivex.schedulers.Schedulers;


public class TasksLocalDataSource implements TasksDataSource {

    /**
     * 获取所有任务实体的列表。
     * <p>
     * 这个方法不需要参数，它会从数据库中同步获取任务列表，并以 {@link List} 的形式返回。
     *
     * @return 返回一个包含所有任务实体的列表。如果数据库中没有任务，则返回空列表。
     */
    @Override
    public List<TaskEntity> getTasks() {
        // 从数据库中同步获取任务列表
        return AppDatabase.getInstance().taskDao().getTasksSync();
    }

    /**
     * 根据任务ID获取任务实体。
     *
     * @param taskId 任务的唯一标识符。
     * @return 返回对应任务ID的TaskEntity对象。
     */
    @Override
    public TaskEntity getTask(String taskId) {
        // 从数据库同步获取指定ID的任务
        return AppDatabase.getInstance().taskDao().getTaskSync(taskId);
    }


    /**
     * 将任务实体添加到数据库。
     * 该方法会异步地将提供的任务实体插入到任务数据库中，并不会立即返回操作结果。
     *
     * @param taskEntity 要添加的任务实体，不应为null。
     */
    @Override
    public void addTask(TaskEntity taskEntity) {
        try {
            // 将任务实体插入数据库的操作会异步执行，订阅发生在IO线程，观察也在IO线程。
            AppDatabase.getInstance().taskDao()
                    .insertTask(taskEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(completableObserver);
        } catch (Exception e) {
            // 捕获异常并记录堆栈跟踪，以便于问题调查和诊断。
            CommExtKt.logStackTrace(e);
        }
    }

    /**
     * 从数据库中移除指定的任务实体。
     * @param taskEntity 要移除的任务实体。
     * 该方法不会返回任何结果，而是通过异步操作在后台线程中执行任务的删除，并在完成时通知观察者。
     */
    @Override
    public void removeTask(TaskEntity taskEntity) {
        try {
            // 从数据库实例中获取任务数据访问对象，并发起删除任务的异步操作
            AppDatabase.getInstance().taskDao()
                    .deleteTask(taskEntity)
                    .subscribeOn(Schedulers.io()) // 在IO线程上执行删除操作
                    .observeOn(Schedulers.io()) // 在IO线程上处理删除操作的结果
                    .subscribe(completableObserver); // 订阅删除操作的结果，无具体操作，可能用于未来的扩展
        } catch (Exception e) {
            // 捕获并记录删除过程中可能发生的异常
            CommExtKt.logStackTrace(e);
        }
    }

    /**
     * 更新任务信息。
     * 此方法会将提供的任务实体更新到数据库中。
     *
     * @param taskEntity 需要更新的任务实体，包含任务的所有信息。
     *                   该实体将被用于替换数据库中相应任务的信息。
     *
     * 注意：此方法使用了RxJava进行异步操作，会在后台线程中更新数据库，
     * 完成后不会返回任何结果，而是通过CompletableObserver进行通知。
     */
    @Override
    public void updateTask(TaskEntity taskEntity) {
        try {
            // 从数据库实例中获取任务数据访问对象，并执行更新任务操作
            // 使用RxJava的subscribeOn(Schedulers.io())方法将操作移到IO线程执行
            // observeOn(Schedulers.io())方法确保后续操作也在IO线程进行
            AppDatabase.getInstance().taskDao()
                    .updateTask(taskEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(completableObserver);
        } catch (Exception e) {
            // 捕获异常，并记录堆栈跟踪，以便于问题调查和诊断
            CommExtKt.logStackTrace(e);
        }
    }

    private final CompletableObserver completableObserver = new CompletableObserver() {
        /**
         * 当订阅发生时被调用。
         * 这个方法是在RxJava中被订阅动作所调用的回调方法，主要用于在订阅时进行一些初始化操作。
         *
         * @param d 被订阅者提供的Disposable对象，它可以被用来在任何时候取消订阅。
         */
        @Override
        public void onSubscribe(Disposable d) {
            // 此处可以添加订阅相关的初始化逻辑
        }

        /**
         * 当某个任务或操作完成时被调用的空实现方法。
         * 这是一个抽象方法的默认实现，提供给继承这个类的开发者一个起点，
         * 他们可以根据自己的需要在这个方法中添加具体的完成后的逻辑处理。
         *
         * @since [类/方法的引入版本号]
         * @version [类/方法的当前版本号]
         */
        @Override
        public void onComplete() {
            // 这里是一个空实现，子类可以根据需要重写这个方法来添加完成后的处理逻辑。
        }

        /**
         * 处理错误的回调方法。
         * 当发生错误时，此方法会被调用，并将错误信息记录下来。
         *
         * @param e 异常对象，包含了错误的详细信息。
         */
        @Override
        public void onError(Throwable e) {
            // 记录异常堆栈信息
            CommExtKt.logStackTrace(e);
        }
    };
}
