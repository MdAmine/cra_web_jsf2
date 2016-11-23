package com.app.web.managedbeans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.app.business.bo.Projet;
import com.app.business.bo.Tache;
import com.app.business.bo.Utilisateur;
import com.app.business.service.ProjectManagerService;
import com.app.business.service.UserManager;
import com.mdamine.dao.exceptions.EntityNotFoundException;

public class ProjectManagerController extends BaseManagedBean {

	private Projet projet = new Projet();
	private Tache tache = new Tache();
	private List<Utilisateur> usersToAffect = new ArrayList<Utilisateur>();
	private String affectedUser;
	private List<Projet> projetList = new ArrayList<Projet>();
	private List<Tache> projetTaches = new ArrayList<Tache>();
	private boolean edit = false;
	private String currentUserName;
	private Utilisateur utilisateur = new Utilisateur();

	private ProjectManagerService prjManServ;
	private UserManager userManagerService;

	private DashboardModel model;

	private int actualDurationOfProject;
	private int reelDurationOfProject;

	public ProjectManagerController() {
		System.out.println(">> instance");
	}

	@PostConstruct
	public void populate() {

		usersToAffect = prjManServ.getCollaborators();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		currentUserName = auth.getName();
		utilisateur = userManagerService.getUserByLogin(currentUserName);
		projetList = prjManServ.getUserProjects(utilisateur.getId());

		if ((Projet) getSession().getAttribute("projet") != null) {
			projet = (Projet) getSession().getAttribute("projet");
			projetTaches = prjManServ.getTasksOfProject(projet.getId());
		}
		
		

	}

	public String doNew() {
		edit = false;
		projet = null;
		return projectManPage("newProjet");
	}

	public String doCreateProjet() {

		edit = Boolean.parseBoolean(this.getParameter("edit"));

		if (edit == false) {
			projet.setDateCreation(new Date());
			projet.setUtilisateur(utilisateur);
			prjManServ.addProject(projet);
		} else {

			projet = (Projet) getSession().getAttribute("projet");

			prjManServ.updateProject(projet);
			System.out.println("ko");

		}

		projetList = prjManServ.getUserProjects(utilisateur.getId());
		return projectManPage("listProjets");
	}

	public String addTask() {

		Long idProjet = Long.parseLong(this.getParameter("id"));

		try {
			projet = prjManServ.getProjectById(idProjet);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		if (!affectedUser.isEmpty()) {
			Utilisateur u = prjManServ.getUserByName(affectedUser);
			tache.setUtilisateur(u);
		}

		tache.setProjet(projet);
		tache.setDureeActuelle(0);
		prjManServ.addTask(tache);

		return showProject();
	}

	public String showProject() {

		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		try {
			projet = prjManServ.getProjectById(idParsed);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		projetTaches = prjManServ.getTasksOfProject(idParsed);
		getSession().setAttribute("projet", projet);

		usersToAffect = prjManServ.getCollaborators();

		actualDurationOfProject = prjManServ.getActualDuration(projet);
		reelDurationOfProject = prjManServ.getReelDuration(projet);

		createBarModel();
		
		return projectManPage("showProject");
	}

	public String doDeleteProjet() {

		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		try {
			prjManServ.deleteProject(idParsed);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		projetList = prjManServ.getUserProjects(utilisateur.getId());

		return projectManPage("listProjets");
	}

	public String doDeleteTache() {

		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		try {
			prjManServ.deleteTask(idParsed);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		projet = (Projet) getSession().getAttribute("projet");
		projetTaches = prjManServ.getTasksOfProject(projet.getId());

		return projectManPage("showProject");
	}

	public String doUpdate() {

		edit = true;
		String id = this.getParameter("id");
		Long idParsed = Long.parseLong(id);

		try {
			projet = prjManServ.getProjectById(idParsed);
			getSession().setAttribute("projet", projet);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		return projectManPage("newProjet");
	}

	// public String doUpdateProjet() {
	//
	// System.out.println("=======>" + projet.getId());
	// //prjManServ.updateProject(projet);
	//
	// return projectManPage("listProjets");
	// }

	public Projet getProjet() {
		return projet;
	}

	public void setProjet(Projet projet) {
		this.projet = projet;
	}

	public List<Projet> getProjetList() {
		return projetList;
	}

	public void setProjetList(List<Projet> projetList) {
		this.projetList = projetList;
	}

	public ProjectManagerService getPrjManServ() {
		return prjManServ;
	}

	public void setPrjManServ(ProjectManagerService prjManServ) {
		this.prjManServ = prjManServ;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public List<Tache> getProjetTaches() {
		return projetTaches;
	}

	public void setProjetTaches(List<Tache> projetTaches) {
		this.projetTaches = projetTaches;
	}

	public List<Utilisateur> getUsersToAffect() {
		return usersToAffect;
	}

	public void setUsersToAffect(List<Utilisateur> usersToAffect) {
		this.usersToAffect = usersToAffect;
	}

	public String getAffectedUser() {
		return affectedUser;
	}

	public void setAffectedUser(String affectedUser) {
		this.affectedUser = affectedUser;
	}

	public Tache getTache() {
		return tache;
	}

	public void setTache(Tache tache) {
		this.tache = tache;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUser) {
		this.currentUserName = currentUser;
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

	public void onRowEdit(RowEditEvent event) {
		System.out.println("edit");
		if (event.getObject() instanceof Projet)
			prjManServ.updateProject(((Projet) event.getObject()));
		else if (event.getObject() instanceof Tache) {
			Tache t = (Tache) event.getObject();
			if (!"".equals(affectedUser)) {
				Utilisateur u = prjManServ.getUserByName(affectedUser);
				t.setUtilisateur(u);
			} else {
				t.setUtilisateur(null);
			}
			prjManServ.updateTask(t);
		}
	}

	public void onRowCancel(RowEditEvent event) {

		if (event.getObject() instanceof Projet)
			System.out.println(((Projet) event.getObject()).getId());
		else if (event.getObject() instanceof Tache) {
			System.out.println(((Tache) event.getObject()).getId());
		}

	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();

		if (newValue != null && !newValue.equals(oldValue)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed",
					"Old: " + oldValue + ", New:" + newValue);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
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

	public int getActualDurationOfProject() {
		return actualDurationOfProject;
	}

	public void setActualDurationOfProject(int actualDurationOfProject) {
		this.actualDurationOfProject = actualDurationOfProject;
	}

	public int getReelDurationOfProject() {
		return reelDurationOfProject;
	}

	public void setReelDurationOfProject(int reelDurationOfProject) {
		this.reelDurationOfProject = reelDurationOfProject;
	}
	
	private BarChartModel barModel;
 
    public BarChartModel getBarModel() {
        return barModel;
    }
 
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries dureePlanifié = new ChartSeries();
        dureePlanifié.setLabel("durée planifié");
        ChartSeries dureeActuel = new ChartSeries();
        dureeActuel.setLabel("durée actuel");
        
        System.out.println("__"+projetTaches.size());
        
        for (int i = 0; i < projetTaches.size(); i++) {
        	System.out.println("for");
        	dureePlanifié.set(projetTaches.get(i).getTitreTache(), projetTaches.get(i).getDuree());
        	dureeActuel.set(projetTaches.get(i).getTitreTache(), projetTaches.get(i).getDureeActuelle());
		}
 
        model.addSeries(dureePlanifié);
        model.addSeries(dureeActuel);
         
        return model;
    }
     
     
    private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("Taches");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Taches");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Duree");
        yAxis.setMin(0);
        yAxis.setMax(150);
    }
	
	

}
