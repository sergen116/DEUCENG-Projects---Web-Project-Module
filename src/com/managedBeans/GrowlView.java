package com.managedBeans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ViewScoped
@ManagedBean
public class GrowlView {
	
	public void success() {
        addMessage("Success", "Success");
    }
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().getExternalContext()
        .getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, message);
       
    }
}
