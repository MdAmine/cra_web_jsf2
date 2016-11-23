package com.app.web.managedbeans.customizedauthentication;

import java.io.IOException;
import java.util.Collection;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import com.app.web.managedbeans.BaseManagedBean;
import com.app.web.utils.UAgentInfo;

/**
 * Permet de personaliser l'authentification par d�faut de Spring Security
 * 
 * @author BOUDAA
 *
 */
public class LoginBean extends BaseManagedBean {

	private String username;
	private String password;

	public String doLogin() throws IOException, ServletException {

		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
				.getRequestDispatcher("/j_spring_security_check?j_username=" + username + "&j_password=" + password);

		dispatcher.forward((ServletRequest) context.getRequest(), (ServletResponse) context.getResponse());

		FacesContext.getCurrentInstance().responseComplete();
		// It's OK to return null here because Faces is just going to exit.

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// System.out.println(auth.toString());

		return null;
	}

	/**
	 * Permet de rediriger vers l'espace home selon le syst�me utilis� et le
	 * type de l'utilisateur
	 * 
	 * @throws IOException
	 */
	public void redirect() throws IOException {

		System.out.println("aaa");
		String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

		HttpServletRequest request = getRequest();
		String userAgent = request.getHeader("User-Agent");
		String httpAccept = request.getHeader("Accept");
		UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);

		// On acc�de au contexte de s�curit� pour r�cup�rer le role de
		// l'utilisateur
		Collection ath = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

		if (ath.contains(new GrantedAuthorityImpl("ROLE_COLLABORATOR"))) {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(contextPath + "/pages/collaborator/home.xhtml");
		} else if (ath.contains(new GrantedAuthorityImpl("ROLE_PROJECTMAN"))) {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(contextPath + "/pages/projectMan/home.xhtml");
		}

		else if (ath.contains(new GrantedAuthorityImpl("ROLE_ADMIN"))) {

			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(contextPath + "/pages/admin/home.xhtml");

		}

	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}