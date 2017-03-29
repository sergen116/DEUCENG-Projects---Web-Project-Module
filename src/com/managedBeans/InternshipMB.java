package com.managedBeans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import com.jpa.entities.Internship;
import com.jpa.entities.Student;
import com.jpa.session.beans.interfaces.InternshipDAOBean;
import com.jpa.session.beans.interfaces.StudentDAOBean;

@SessionScoped
@ManagedBean
public class InternshipMB extends Internship {

	private static final long serialVersionUID = 1L;
	private List<Student> possibleStudents;
	private Date currentDate = new Date();
	private List<Internship> allInternships; 
	private static int passed_internship_id;
	private static int passed_student_id;
	
	@EJB
	private StudentDAOBean studentDAOBean;
	
	@EJB
	private InternshipDAOBean internshipDAOBean;
	
	public InternshipMB() {

	}
	
	@PostConstruct
	public void init() {
		possibleStudents = studentDAOBean.getAllStudents();
		currentDate = getCurrentDate();
		setAllInternships(internshipDAOBean.getAllInternships());
		
	}

	/**
	 * Add internship 
	 */
	public String addInternship() {
		clear();
		Internship newInternship = new Internship();
		newInternship.setCompany_name(this.getCompany_name());
		newInternship.setEnd_date(this.getEnd_date());
		newInternship.setStudent(this.getStudent());
		newInternship.setFk_student_id(newInternship.getStudent().getId_student());
		newInternship.setStart_date(this.getStart_date());
		newInternship.setType(this.getType());
		newInternship.setWorking_areas(this.getWorking_areas());
		
		try {
			internshipDAOBean.create(newInternship);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "listInternship.xhtml?faces-redirect=true";
	}
	public String navigateToEditPage(){
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		passed_internship_id = Integer.parseInt(params.get("passed_internship_id"));
		passed_student_id = Integer.parseInt(params.get("passed_student_id"));
		
		Internship editableInternship = internshipDAOBean.getInternship(passed_internship_id);
		
		setCompany_name(editableInternship.getCompany_name());
		setEnd_date(editableInternship.getEnd_date());
		setStudent(editableInternship.getStudent());
		setFk_student_id(editableInternship.getFk_student_id());
		setStart_date(editableInternship.getStart_date());
		setType(editableInternship.getType());
		setWorking_areas(editableInternship.getWorking_areas());
		
		return "editInternship.xhtml?faces-redirect=true";
	}
	
	public String editInternship(){
		
		Internship editableInternship = internshipDAOBean.getInternship(passed_internship_id);
		
		editableInternship.setCompany_name(this.getCompany_name());
		editableInternship.setEnd_date(this.getEnd_date());
		editableInternship.setStudent(this.getStudent());
		editableInternship.setFk_student_id(editableInternship.getStudent().getId_student());
		editableInternship.setStart_date(this.getStart_date());
		editableInternship.setType(this.getType());
		editableInternship.setWorking_areas(this.getWorking_areas());
		
		setAllInternships(internshipDAOBean.getAllInternships());
		
		try {
			internshipDAOBean.update(editableInternship);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		setAllInternships(internshipDAOBean.getAllInternships());
		this.setCompany_name(editableInternship.getCompany_name());
		this.setEnd_date(editableInternship.getEnd_date());
		this.setStudent(editableInternship.getStudent());
		this.setFk_student_id(editableInternship.getFk_student_id());
		this.setStart_date(editableInternship.getStart_date());
		this.setType(editableInternship.getType());
		this.setWorking_areas(editableInternship.getWorking_areas());
		
		
		return "listInternship.xhtml?faces-redirect=true";
	}
	
	public void clear(){
		setStart_date(null);
		setEnd_date(null);
		setCompany_name(null);
		setWorking_areas(null);
	}
	
	 public void onDateSelect(SelectEvent event) {
	        FacesContext facesContext = FacesContext.getCurrentInstance();
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
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
        addSuccessMessage("Success", "Success");
    }
	public void addSuccessMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().getExternalContext()
        	.getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public static int getPassed_internship_id() {
		return passed_internship_id;
	}

	public int getPassed_student_id() {
		return passed_student_id;
	}

	public List<Internship> getAllInternships() {
		return allInternships;
	}

	public void setAllInternships(List<Internship> allInternships) {
		this.allInternships = allInternships;
	}

	public List<Student> getPossibleStudents() {
		return possibleStudents;
	}

	public void setPossibleStudents(List<Student> possibleStudents) {
		this.possibleStudents = possibleStudents;
	}
	
	public Date getCurrentDate() {
	    return currentDate;
	}
}
