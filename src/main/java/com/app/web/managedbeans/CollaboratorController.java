package com.app.web.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.app.business.bo.Cra;
import com.app.business.bo.Tache;
import com.app.business.bo.Utilisateur;
import com.app.business.service.CollaboraterService;
import com.app.business.service.UserManager;
import com.app.web.utils.MyScheduleEvent;
import com.app.web.utils.MyScheduleEventImpl;
import com.mdamine.dao.exceptions.EntityNotFoundException;

public class CollaboratorController extends BaseManagedBean implements Serializable {

	private ScheduleModel eventModel;

	private ScheduleModel lazyEventModel;

	private String task;
	private Boolean checkAbsence;
	private List<String> causeAbsenceList = new ArrayList<String>();
	private String causeAbsence;

	private Tache tache = new Tache();

	private MyScheduleEvent event = new MyScheduleEventImpl();

	private CollaboraterService collServ;
	private List<Cra> craList = new ArrayList<Cra>();
	private List<Tache> taskList = new ArrayList<Tache>();
	private List<Cra> tacheCras = new ArrayList<Cra>();

	private String currentUserName;
	private Utilisateur utilisateur = new Utilisateur();

	private UserManager userManagerService;

	private DashboardModel model;

	public CollaboratorController() {
		System.out.println(">>instance of CollaboratorController");
	}

	@PostConstruct
	public void populate() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		currentUserName = auth.getName();
		utilisateur = userManagerService.getUserByLogin(currentUserName);

		craList.addAll(utilisateur.getCras());
		taskList.addAll(utilisateur.getTaches());

		causeAbsenceList.add("en congé");
		causeAbsenceList.add("malade");
		causeAbsenceList.add("autre");

		eventModel = new DefaultScheduleModel();

		for (Cra cra : craList) {
			if (cra.getCauseAbsence() == null) {
				eventModel
						.addEvent(new MyScheduleEventImpl(cra.getTitle(), cra.getDateDebut(), cra.getDateFin(), "ev2"));
			} else {
				eventModel
						.addEvent(new MyScheduleEventImpl(cra.getTitle(), cra.getDateDebut(), cra.getDateFin(), "ev1"));
			}
		}

		event = (MyScheduleEvent) getSession().getAttribute("event");

		lazyEventModel = new LazyScheduleModel() {
			public void loadEvents(Date start, Date end) {
				Date random = getRandomDate(start);
				addEvent(new MyScheduleEventImpl("Lazy Event 1", random, random));

				random = getRandomDate(start);
				addEvent(new MyScheduleEventImpl("Lazy Event 2", random, random));
			}
		};

		model = new DefaultDashboardModel();
		DashboardColumn column1 = new DefaultDashboardColumn();
		DashboardColumn column2 = new DefaultDashboardColumn();

		column1.addWidget("tache");
		column1.addWidget("infos");

		model.addColumn(column1);
		model.addColumn(column2);
	}

	public String showTask() {

		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		try {
			tache = collServ.getTaskById(idParsed);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		tacheCras.addAll(tache.getCras());

		return collaboratorPage("showTask");
	}

	public Date getRandomDate(Date base) {
		Calendar date = Calendar.getInstance();
		date.setTime(base);
		date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1); // set random

		return date.getTime();
	}

	public Date getInitialDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar.getTime();
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	public MyScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(MyScheduleEvent event) {
		this.event = event;
	}

	public String addEvent(ActionEvent actionEvent) {

		if ("abs".equals(this.getParameter("form"))) {
			collServ.addCra(
					new Cra(" " + causeAbsence, event.getStartDate(), event.getEndDate(), utilisateur, causeAbsence));

			eventModel.addEvent(
					new MyScheduleEventImpl(event.getTitle(), event.getStartDate(), event.getEndDate(), "ev1"));

		} else if ("tache".equals(this.getParameter("form"))) {

			Tache t = collServ.getTaskByTitle(task);
			if (event.isCompleted()) {
				t.setCompleted(true);
				collServ.updateTask(t);
			}
			collServ.addCra(
					new Cra(" " + task, event.getStartDate(), event.getEndDate(), utilisateur, t, event.getRAF()));

			t.setDureeActuelle(collServ.getActualDuration(t));
			collServ.updateTask(t);

			eventModel.addEvent(
					new MyScheduleEventImpl(event.getTitle(), event.getStartDate(), event.getEndDate(), "ev2"));
		}

		event = new MyScheduleEventImpl();

		return collaboratorPage("listCras");
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (MyScheduleEvent) selectEvent.getObject();
	}

	public void onDateSelect(SelectEvent selectEvent) {
		event = new MyScheduleEventImpl("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
		getSession().setAttribute("event", event);
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

		addMessage(message);
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public CollaboraterService getCollServ() {
		return collServ;
	}

	public void setCollServ(CollaboraterService collServ) {
		this.collServ = collServ;
	}

	public List<Cra> getCraList() {
		return craList;
	}

	public void setCraList(List<Cra> craList) {
		this.craList = craList;
	}

	public List<Tache> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Tache> taskList) {
		this.taskList = taskList;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public UserManager getUserManagerService() {
		return userManagerService;
	}

	public void setUserManagerService(UserManager userManagerService) {
		this.userManagerService = userManagerService;
	}

	public Tache getTache() {
		return tache;
	}

	public void setTache(Tache tache) {
		this.tache = tache;
	}

	public List<Cra> getTacheCras() {
		return tacheCras;
	}

	public void setTacheCras(List<Cra> tacheCras) {
		this.tacheCras = tacheCras;
	}

	public Boolean getCheckAbsence() {
		return checkAbsence;
	}

	public void setCheckAbsence(Boolean checkAbsence) {
		this.checkAbsence = checkAbsence;
	}

	public List<String> getCauseAbsenceList() {
		return causeAbsenceList;
	}

	public void setCauseAbsenceList(List<String> causeAbsenceList) {
		this.causeAbsenceList = causeAbsenceList;
	}

	public String getCauseAbsence() {
		return causeAbsence;
	}

	public void setCauseAbsence(String causeAbsence) {
		this.causeAbsence = causeAbsence;
	}

	public void onRowEdit(RowEditEvent event) {
		collServ.updateCra(((Cra) event.getObject()));
	}

	public void onRowCancel(RowEditEvent event) {
		System.out.println(((Cra) event.getObject()).getId());
	}

	public String doDeleteCra() {

		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		Cra c = null;
		Tache t = null;
		Boolean abs = false;

		try {
			c = collServ.getCraById(idParsed);
			if (c.getTache() != null) {
				t = c.getTache();
			} else {
				abs = true;
			}
			collServ.deleteCra(idParsed);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (abs == false) {
			t.setDureeActuelle(collServ.getActualDuration(t));
			collServ.updateTask(t);
		}

		return collaboratorPage("listUsers");
	}

	public DashboardModel getModel() {
		return model;
	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex()
				+ ", Sender index: " + event.getSenderColumnIndex());

		addMessage(message);
	}

	public void handleClose(CloseEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Panel Closed",
				"Closed panel id:'" + event.getComponent().getId() + "'");

		addMessage(message);
	}

	public void handleToggle(ToggleEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, event.getComponent().getId() + " toggled",
				"Status:" + event.getVisibility().name());

		addMessage(message);
	}

}
