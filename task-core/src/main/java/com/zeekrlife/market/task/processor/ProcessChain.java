package com.zeekrlife.market.task.processor;

import java.util.List;


public class ProcessChain implements TaskHandler.Chain {
    private List<TaskHandler> mProcessors;
    private int mIndex;
    private TaskRequest mChainContext;

    public ProcessChain(List<TaskHandler> processors, TaskRequest chainContext) {
        this(processors, 0, chainContext);
    }

    public ProcessChain(List<TaskHandler> processors, int index, TaskRequest chainContext) {
        mProcessors = processors;
        mIndex = index;
        mChainContext = chainContext;
    }

    /**
     * 获取当前链上下文的请求信息。
     *
     * 该方法重写了getRequest方法，用于返回mChainContext对象，该对象代表了当前链的请求信息。
     *
     * @return TaskRequest 返回当前链上下文的请求信息对象。
     */
    @Override
    public TaskRequest getRequest() {
        return mChainContext;
    }

    /**
     * 该方法用于终止当前正在进行的所有处理器。
     * 该方法不接受任何参数，也不返回任何结果。
     */
    @Override
    public void abort() {
        // 清除所有处理器，以终止正在进行的处理任务
        mProcessors.clear();
    }

    /**
     * 继续处理任务请求。
     * 该方法会检查处理器列表中是否还有待处理的处理器，如果有，则调用下一个处理器的处理方法。
     * 处理器会依次调用，直到处理器列表结束或任务处理被中断。
     *
     * @param processContext 任务请求的上下文，包含任务的相关信息和状态。
     */
    @Override
    public void proceed(TaskRequest processContext) {
        if (mProcessors.size() > mIndex) {
            // 检查是否还有处理器需要处理
            TaskHandler processor = mProcessors.get(mIndex); // 获取当前待处理的处理器

            // 准备下一个处理链，更新索引和上下文
            ProcessChain nextChain = new ProcessChain(mProcessors, mIndex + 1, processContext);

            // 调用当前处理器的处理方法，并传入下一个处理链作为参数
            processor.handle(nextChain);
        }
    }

}
