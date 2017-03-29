package com.managedBeans;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.SelectEvent;

import com.jpa.entities.FacultyMember;
import com.jpa.entities.Milestone;
import com.jpa.entities.Project;
import com.jpa.session.beans.interfaces.MilestoneDAOBean;
import com.jpa.session.beans.interfaces.ProjectBean;
import com.jpa.session.beans.interfaces.ProjectDAOBean;

@ViewScoped
@ManagedBean
public class MilestoneMB extends Milestone {

	private static final long serialVersionUID = 1L;
	private List<Project> possibleProjects;
	private List<Project> projects_of_facultyMember;
	private List<Milestone> milestones_of_facultyMember;
	private List<Milestone> allMilestones;

	@EJB
	private ProjectDAOBean projectDAOBean;

	@EJB
	private ProjectBean projectBean;

	@EJB
	private MilestoneDAOBean milestoneDAOBean;

	public MilestoneMB() {

	}

	@PostConstruct
	public void init() {
		possibleProjects = projectDAOBean.getAllProjects();

		HttpSession sessionFacultyMember = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);

		FacultyMember sessionUser = (FacultyMember) sessionFacultyMember
				.getAttribute("facultyMemberSession");
		projects_of_facultyMember = projectBean
				.getFacultyMemberProjects(sessionUser.getId_faculty_member());
		setAllMilestones(milestoneDAOBean.getAllMilestones());

	}

	public String addMilestone() {
		Milestone newMilestone = new Milestone();
		newMilestone.setDeadline(this.getDeadline());
		newMilestone.setFk_project_id(this.getProject().getId_project());
		newMilestone.setProject(this.getProject());
		newMilestone.setShort_summary(this.getShort_summary());
		
		try {
			milestoneDAOBean.create(newMilestone);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "welcomePageFacultyMember.xhtml?faces-redirect=true";
	}

	public void onDateSelect(SelectEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected",
						format.format(event.getObject())));
	}

	public List<Project> getPossibleProjects() {
		return possibleProjects;
	}

	public void setPossibleProjects(List<Project> possibleProjects) {
		this.possibleProjects = possibleProjects;
	}

	public List<Milestone> getMilestones_of_facultyMember() {
		return milestones_of_facultyMember;
	}

	public void setMilestones_of_facultyMember(
			List<Milestone> milestones_of_facultyMember) {
		this.milestones_of_facultyMember = milestones_of_facultyMember;
	}

	public List<Milestone> getAllMilestones() {
		return allMilestones;
	}

	public void setAllMilestones(List<Milestone> allMilestones) {
		this.allMilestones = allMilestones;
	}

	public List<Project> getProjects_of_facultyMember() {
		return projects_of_facultyMember;
	}

	public void setProjects_of_facultyMember(
			List<Project> projects_of_facultyMember) {
		this.projects_of_facultyMember = projects_of_facultyMember;
	}

}
