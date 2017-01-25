/* ***********************
** TaskQueue.java
** ***********************
** ������
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
	
	// ����������б��β���һ������
	synchronized public void putTask(String[] task)
	{
		if (task != null)
			task_v.addElement(task);
		this.notify();
	}
	
	// ����������б���ײ���һ������
	synchronized public void insertTask(String[] task)
	{
		if (task != null)
			task_v.add(0, task);
		this.notify();			
	}
	
	// ����������б���ײ����������
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
	
	// �ӹ���������б��ȡһ������
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
	
	// �жϹ���������б��Ƿ�Ϊ�ա�
	synchronized public boolean isEmpty()
	{
		return task_v.size() == 0 ? true : false;
	}

	synchronized public void clear()
	{
		task_v.clear();
	}
}
