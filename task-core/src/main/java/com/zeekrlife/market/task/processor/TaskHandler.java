package com.zeekrlife.market.task.processor;


public interface TaskHandler {

    /**
     * 处理链条上的请求
     */
    void handle(Chain chain);

    /**
     * 请求链
     */
    interface Chain {
        /**
         * 获取请求实体
         */
        TaskRequest getRequest();

        /**
         * 开始或继续执行请求。
         */
        void proceed(TaskRequest context);

        /**
         * 中断任务
         */
        void abort();
    }
}
