package com.managedBeans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class UtilsMB {
	
	private String initialChoice;

	public String navigateForUserChoice(){
		return getInitialChoice() + "?faces-redirect=true";
	}
	
	public String getInitialChoice() {
		return initialChoice;
	}

	public void setInitialChoice(String initialChoice) {
		this.initialChoice = initialChoice;
	}
}
