@file:Suppress("unused")

package net.kigawa.kutil.kutil.api.concurrent

import net.kigawa.kutil.kutil.api.manager.RemoveAble
import java.util.concurrent.*

class ExecuteTask<T>(
  private val executor: AsyncExecutor,
  private val futureTask: FutureTask<T>,
): RemoveAble(executor), ResultTask<T>, Runnable {
  
  constructor(executor: AsyncExecutor, callable: Callable<T>): this(executor, FutureTask(callable))
  
  override fun waitTask(timeoutMilli: Long) {
    if (isDone) return
    try {
      get(timeoutMilli)
    } catch (e: ExecutionException) {
      e.cause?.also(executor.errorHandler::catch) ?: executor.errorHandler.catch(e)
    }
  }
  
  override fun waitTask() {
    waitTask(executor.timeOutMilli)
  }
  
  override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
    return futureTask.cancel(mayInterruptIfRunning)
  }
  
  override fun isCancelled(): Boolean {
    return futureTask.isCancelled
  }
  
  override fun isDone(): Boolean {
    return futureTask.isDone
  }
  
  override fun get(timeoutMilli: Long): T {
    return get(timeoutMilli, TimeUnit.MILLISECONDS)
  }
  
  override fun get(): T {
    return get(executor.timeOutMilli)
  }
  
  override fun get(timeout: Long, unit: TimeUnit): T {
    return futureTask.get(timeout, unit)
  }
  
  override fun run() {
    if (isDone) return
    
    futureTask.run()
    remove()
  }
}