/* ***********************
** TaskQueue.java
** ***********************
** 队列类
** Build 0712
** **********************/
package ArkFTP.bin.util;

import java.util.Vector;

public class TaskQueue
{
	private Vector<String[]> task_v;
	
	public TaskQueue()
	{
		task_v = new Vector<String[]>();
	}
	
	// 向共享的任务列表表尾添加一条任务，
	synchronized public void putTask(String[] task)
	{
		if (task != null)
			task_v.addElement(task);
		this.notify();
	}
	
	// 向共享的任务列表表首插入一条任务，
	synchronized public void insertTask(String[] task)
	{
		if (task != null)
			task_v.add(0, task);
		this.notify();			
	}
	
	// 向共享的任务列表表首插入多条任务。
	synchronized public void insertTasks(Vector<String[]> task_v)
	{
		if (task_v != null)
			while (task_v.size() != 0)
			{
				String[] task = task_v.remove(task_v.size()-1);
				this.task_v.add(0, task);
			}
		if (task_v.size() != 0)
			this.notify();
	}
	
	// 从共享的任务列表获取一条任务。
	synchronized public String[] getTask()
	{
		String[] task = null;
		if (task_v.size() != 0)
		{
			task = task_v.elementAt(0);
			task_v.removeElementAt(0);
		}
		return task;
	}
	
	// 判断共享的任务列表是否为空。
	synchronized public boolean isEmpty()
	{
		return task_v.size() == 0 ? true : false;
	}

	synchronized public void clear()
	{
		task_v.clear();
	}
}
