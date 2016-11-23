package com.app.web.managedbeans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.app.business.bo.Projet;
import com.app.business.bo.Utilisateur;
import com.app.business.service.AdminService;
import com.app.business.service.ProjectManagerService;
import com.app.business.service.UserManager;
import com.mdamine.dao.exceptions.EntityNotFoundException;

public class AdminController extends BaseManagedBean {

	private UserManager userManagerService;
	private ProjectManagerService prjManServ;
	private AdminService adminServ;

	private String currentUserName;
	private Utilisateur utilisateur = new Utilisateur();

	private List<Utilisateur> usersList = new ArrayList<Utilisateur>();

	private DashboardModel model;

	private int countProjectCreatedToday;
	private int countCollConnectedToday;
	private int countChefConnectedToday;
	private int countOfChef;
	private int countOfColl;

	public AdminController() {
		System.out.println(">>instance of AdminControllerController");
	}

	@PostConstruct
	public void populate() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		currentUserName = auth.getName();
		utilisateur = userManagerService.getUserByLogin(currentUserName);

		usersList = prjManServ.getAllUsers();

		countProjectCreatedToday = adminServ.getCountOfProjectCreatedToday();
		countCollConnectedToday = adminServ.getCountOfCollConnectedToday();
		countChefConnectedToday = adminServ.getCountOfChefConnectedToday();

		countOfChef = prjManServ.getChefMan().size();
		countOfColl = prjManServ.getCollaborators().size();
		
		model = new DefaultDashboardModel();
		DashboardColumn column1 = new DefaultDashboardColumn();
		DashboardColumn column2 = new DefaultDashboardColumn();

		column1.addWidget("projet");
		column1.addWidget("collaborateur");

		column2.addWidget("chef");
		column2.addWidget("infos");

		model.addColumn(column1);
		model.addColumn(column2);

	}

	public void onRowEdit(RowEditEvent event) {
		System.out.println("edit");
		if (event.getObject() instanceof Projet)
			prjManServ.updateProject(((Projet) event.getObject()));
		else if (event.getObject() instanceof Utilisateur) {
			Utilisateur u = (Utilisateur) event.getObject();
			userManagerService.updateUtilisateur(u);
		}
	}

	public void onRowCancel(RowEditEvent event) {

		if (event.getObject() instanceof Projet)
			System.out.println(((Projet) event.getObject()).getId());
		else if (event.getObject() instanceof Utilisateur) {
			System.out.println(((Utilisateur) event.getObject()).getId());
		}

	}

	public String doDeleteUser() {

		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		try {
			userManagerService.deleteUser(idParsed);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		usersList = prjManServ.getAllUsers();

		return adminPage("listUsers");
	}

	public UserManager getUserManagerService() {
		return userManagerService;
	}

	public void setUserManagerService(UserManager userManagerService) {
		this.userManagerService = userManagerService;
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

	public ProjectManagerService getPrjManServ() {
		return prjManServ;
	}

	public void setPrjManServ(ProjectManagerService prjManServ) {
		this.prjManServ = prjManServ;
	}

	public List<Utilisateur> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<Utilisateur> usersList) {
		this.usersList = usersList;
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

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public DashboardModel getModel() {
		return model;
	}

	public int getCountProjectCreatedToday() {
		return countProjectCreatedToday;
	}

	public void setCountProjectCreatedToday(int countProjectCreatedToday) {
		this.countProjectCreatedToday = countProjectCreatedToday;
	}

	public AdminService getAdminServ() {
		return adminServ;
	}

	public void setAdminServ(AdminService adminServ) {
		this.adminServ = adminServ;
	}

	public int getCountCollConnectedToday() {
		return countCollConnectedToday;
	}

	public void setCountCollConnectedToday(int countCollConnectedToday) {
		this.countCollConnectedToday = countCollConnectedToday;
	}

	public int getCountChefConnectedToday() {
		return countChefConnectedToday;
	}

	public void setCountChefConnectedToday(int countChefConnectedToday) {
		this.countChefConnectedToday = countChefConnectedToday;
	}

	public int getCountOfChef() {
		return countOfChef;
	}

	public void setCountOfChef(int countOfChef) {
		this.countOfChef = countOfChef;
	}

	public int getCountOfColl() {
		return countOfColl;
	}

	public void setCountOfColl(int countOfColl) {
		this.countOfColl = countOfColl;
	}

}
