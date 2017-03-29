package com.managedBeans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;


@SessionScoped
@ManagedBean
public class AdminMB {
	
	private String username;
	private String password;
	
	public AdminMB(){
		
	}

	public String isLoginInformationTrue(){
		if(this.username.equalsIgnoreCase("admin") 
				&& this.password.equalsIgnoreCase("admin")){
			successMessage();
			return "welcomePageAdmin.xhtml?faces-redirect=true";
		}
		else{
			addMessage("Please try again !", "Wrong admin login");
			return "loginAdmin.xhtml";
		}
	}
	
	//Redirect to addInternship.xhtml page
	public String navigateToAddInternship(){
		return "addInternship.xhtml?faces-redirect=true";
	}
	//Redirect to editInternship.xhtml page
	public String navigateToEditInternship(){
		return "listInternship.xhtml?faces-redirect=true";
	}
	//Redirect to editInternship.xhtml page
	public String navigateToListInternshipPage(){
		return "listInternship.xhtml?faces-redirect=true";
	}
		
	/**
	 * Set context message normally
	 */	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	 
	/**
	 * Set success message
	 */	
	public void successMessage() {
        addSuccessMessage("Success !", "Successful admin login");
    }
	public void addSuccessMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().getExternalContext()
        .getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
