package com.psh.state.task.state;

import com.psh.state.task.enums.ActionType;
import com.psh.state.task.Task;

/**
 * 初始状态
 *
 * @author psh 2023/5/16 22:47
 */
public class TaskInit implements State {

	@Override
	public void update(Task task, ActionType actionType) {

		// 初始化 -> 开始
		if (actionType == ActionType.START) {
			task.setState(new TaskOngoing());
		}
	}

}
