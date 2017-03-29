package com.converter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.jpa.entities.Student;
import com.jpa.session.beans.interfaces.StudentBean;

@RequestScoped
@ManagedBean
public class StudentConverter implements Converter {
	
	@EJB
	private StudentBean studentBean;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value.isEmpty() || value == null)
			return null;

			try {
				String[] names = value.split(" ");
				String firstName = "";
				String lastName = "";
				
				for (int i = 0; i < names.length; i++) {
					if(names[i] == names[names.length-1]){
						lastName = names[i];
					}
					else{
						firstName += names[i] + " ";
					}
				}
				
				firstName = firstName.trim();
				Student student = studentBean.findStudentAccordingToFullName(firstName, lastName);
				
				return student;
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
