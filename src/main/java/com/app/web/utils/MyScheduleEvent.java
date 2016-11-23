package com.app.web.utils;

import org.primefaces.model.ScheduleEvent;

import com.app.business.bo.Tache;

public interface MyScheduleEvent extends ScheduleEvent {

	public boolean isCompleted();

	public void setCompleted(boolean completed);

	public Tache getTask();

	public void setTask(Tache task);

	public String getRAF();

	public void setRAF(String rAF);

	public String getCauseAbsence();

	public void setCauseAbsence(String causeAbsence);

}
