package com.app.web.managedbeans;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.app.web.utils.UAgentInfo;

public abstract class BaseManagedBean {

	public static final String PAGES_FOLDER = "/pages/";
	public static final String PAGES_ADMIN_FOLDER = "/pages/admin/";
	public static final String PAGES_PROJECTMAN_FOLDER = "/pages/projectMan/";
	public static final String PAGES_COLLABORATER_FOLDER = "/pages/collaborator/";
	public static final String PAGES_TYPE = ".xhtml";
	protected final Log log = LogFactory.getLog(getClass());

	public boolean isThisRequestCommingFromAMobileDevice(HttpServletRequest request) {

		String userAgent = request.getHeader("User-Agent");
		String httpAccept = request.getHeader("Accept");
		System.out.println(userAgent);

		UAgentInfo detector = new UAgentInfo(userAgent, httpAccept);

		if (detector.detectMobileQuick()) {
			return true;
		}

		return false;
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected Application getApplication() {
		return getFacesContext().getApplication();
	}

	protected HttpSession getSession() {
		return getRequest().getSession();
	}

	protected HttpServletRequest getRequest() {
		HttpServletRequest request = (HttpServletRequest) getFacesContext().getExternalContext().getRequest();
		return request;
	}

	protected String getParameter(String name) {
		HttpServletRequest request = getRequest();
		return request.getParameter(name);
	}

	protected String adminPage(String page) {

		return PAGES_ADMIN_FOLDER + page + PAGES_TYPE;
	}

	protected String collaboratorPage(String page) {

		return PAGES_COLLABORATER_FOLDER + page + PAGES_TYPE;
	}

	protected String projectManPage(String page) {

		return PAGES_PROJECTMAN_FOLDER + page + PAGES_TYPE;
	}

	protected String errorPage() {

		return PAGES_FOLDER + "error" + PAGES_TYPE;
	}

	protected void addInfoMessage(String idComponent, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(idComponent,
				new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));

	}

	protected void addErrorMessage(String idComponent, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(idComponent,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));

	}

}
