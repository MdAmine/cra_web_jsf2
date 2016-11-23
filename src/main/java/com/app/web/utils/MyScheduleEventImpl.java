package com.app.web.utils;

import java.util.Date;

import org.primefaces.model.DefaultScheduleEvent;

import com.app.business.bo.Tache;

public class MyScheduleEventImpl extends DefaultScheduleEvent implements MyScheduleEvent {

	private String causeAbsence;
	private boolean completed = false;
	private Tache task;
	private String RAF;

	public MyScheduleEventImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyScheduleEventImpl(String title, Date start, Date end, boolean allDay) {
		super(title, start, end, allDay);
		// TODO Auto-generated constructor stub
	}

	public MyScheduleEventImpl(String title, Date start, Date end, Object data) {
		super(title, start, end, data);
		// TODO Auto-generated constructor stub
	}

	public MyScheduleEventImpl(String title, Date start, Date end, String styleClass) {
		super(title, start, end, styleClass);
		// TODO Auto-generated constructor stub
	}

	public MyScheduleEventImpl(String title, Date start, Date end) {
		super(title, start, end);
		// TODO Auto-generated constructor stub
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Tache getTask() {
		return task;
	}

	public void setTask(Tache task) {
		this.task = task;
	}

	public String getRAF() {
		return RAF;
	}

	public void setRAF(String rAF) {
		RAF = rAF;
	}

	public String getCauseAbsence() {
		return causeAbsence;
	}

	public void setCauseAbsence(String causeAbsence) {
		this.causeAbsence = causeAbsence;
	}

}
