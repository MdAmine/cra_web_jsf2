package com.app.web.managedbeans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.app.business.bo.Role;
import com.app.business.bo.Utilisateur;
import com.app.business.service.UserManager;
import com.app.business.service.exception.DuplicateLoginException;

public class UserManagerBean extends BaseManagedBean {

	private Utilisateur utilisateur = new Utilisateur();
	private UserManager userManagerService;

	private String selectedRole = "";

	private List<Utilisateur> listeUsers;

	private static List<String> listRoles = new ArrayList<String>();

	public UserManagerBean() {
		utilisateur.setRole(new Role());
	}

	@PostConstruct
	public void updateData() {
		listeUsers = userManagerService.getAllUser();
		utilisateur = new Utilisateur();
		utilisateur.setRole(new Role());
		if (listRoles.size() == 0) {
			listRoles.add("collaborateur");
			listRoles.add("chef de projet");
		}

	}

	public void initForm() {

	}

	public String newUser() {
		return adminPage("newUser");
	}

	public String insription() {
		// System.out.println(utilisateur.getRole().getRoleName());
		try {
			if ("collaborateur".equals(selectedRole)) {
				utilisateur.setRole(userManagerService.getAllRoles().get(2));
			} else if ("chef de projet".equals(selectedRole)) {
				utilisateur.setRole(userManagerService.getRoleByName("ROLE_PROJECTMAN"));
			}

			userManagerService.addUser(utilisateur);
			updateData();

		} catch (DuplicateLoginException ex) {

			addErrorMessage("messages", "Erreur:", "le nom d'utilisateur " + utilisateur.getLogin() + " existe d�j�.");

			return null;
		} catch (Exception ex) {

			addErrorMessage("messages", "Erreur:", "Erreur d'inscription");
			log.error("Erreur � cause de : " + ex);
			System.out.println(ex);
			ex.printStackTrace();
			return null;
		}
		return adminPage("newUser");
	}

	public String addUser() {

		try {
			utilisateur.setRole(userManagerService.getRoleByName(utilisateur.getRole().getRoleName()));
			userManagerService.addUser(utilisateur);
			updateData();

			addInfoMessage("messages", "Utilisateur ajout� :", "l'utilisateur est ajout� dans le syst�me");
		} catch (Exception ex) {

			addErrorMessage("messages", "Erreur:", "Erreur lors de l'ajout de l'utilisateur");

			return errorPage();
		}

		return adminPage("listUsers");
	}

	public void deleteUser(Utilisateur u) {

		try {
			userManagerService.deleteUser(u.getId());
			updateData();

			addInfoMessage("messages", "Utilisateur supprim� :", "l'utilisateur est supprim� correctement");
		} catch (Exception ex) {

			addErrorMessage("messages", "Erreur:", "Erreur lors de la supression d'un utilisateur");
		}

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

	public List<String> getListRoles() {
		return listRoles;
	}

	public List<Utilisateur> getListeUsers() {
		return listeUsers;
	}

	public void setListeUsers(List<Utilisateur> listeUsers) {
		this.listeUsers = listeUsers;
	}

	public static void setListRoles(List<String> listRoles) {
		UserManagerBean.listRoles = listRoles;
	}

	public String getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(String selectedRole) {
		this.selectedRole = selectedRole;
	}

}
