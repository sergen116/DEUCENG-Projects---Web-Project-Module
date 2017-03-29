package com.converter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.jpa.entities.Project;
import com.jpa.session.beans.interfaces.ProjectBean;

@RequestScoped
@ManagedBean
public class ProjectConverter implements Converter {
	
	@EJB
	private ProjectBean projectBean;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value.isEmpty() || value == null)
			return null;

			try {
				Project project = projectBean.getProject(value);
				
				return project;
			} catch (ConverterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}
}
