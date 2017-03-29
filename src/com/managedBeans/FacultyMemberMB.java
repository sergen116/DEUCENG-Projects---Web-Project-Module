package com.managedBeans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.jpa.entities.FacultyMember;
import com.jpa.entities.Milestone;
import com.jpa.entities.Project;
import com.jpa.entities.Student;
import com.jpa.session.beans.interfaces.FacultyMemberDAOBean;
import com.jpa.session.beans.interfaces.MilestoneDAOBean;
import com.jpa.session.beans.interfaces.ProjectBean;
import com.jpa.session.beans.interfaces.ProjectDAOBean;
import com.jpa.session.beans.interfaces.ProjectStudentDAOBean;
import com.jpa.session.beans.interfaces.StudentDAOBean;

@SessionScoped
@ManagedBean
public class FacultyMemberMB extends FacultyMember {

	private static final long serialVersionUID = 1L;
	private FacultyMember sessionFacultyMember;
	private List<Milestone> all_milestones = new ArrayList<Milestone>();

	@EJB
	private FacultyMemberDAOBean facultyMemberDAOBean;

	@EJB
	private ProjectDAOBean projectDAOBean;

	@EJB
	private ProjectBean projectBean;

	@EJB
	private StudentDAOBean studentDAOBean;

	@EJB
	private ProjectStudentDAOBean projectStudentDAOBean;

	@EJB
	private MilestoneDAOBean milestoneDAOBean;

	public FacultyMemberMB() {

	}

	@PostConstruct
	public void init() {
		HttpSession sessionFacultyMember = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);

		FacultyMember sessionUser = (FacultyMember) sessionFacultyMember
				.getAttribute("facultyMemberSession");

		if (sessionUser != null) {
			this.setProjects(projectBean.getFacultyMemberProjects(sessionUser
					.getId_faculty_member()));

			List<Project> tempProjects = null;
			List<Student> tempStudents = null;

			for (Project project : this.getProjects()) {
				project.setProjectAndStudents(projectStudentDAOBean
						.getStudents_Of_A_Project(project.getId_project()));

				/**
				 * 
				 */

				List<Integer> student_ýds = projectStudentDAOBean
						.getStudent_IDs(project.getId_project());

				for (Integer student_ID : student_ýds) {
					tempStudents.add(studentDAOBean.getStudent(student_ID
							.intValue()));
				}

				// set students of project
				project.setStudentsOfProject(tempStudents);

				/**
				 * 
				 */
				tempProjects.add(project);
			}

			this.setProjects(tempProjects);

		}
	}

	public String navigateToMilestonePage() {
		return "newMilestone.xhtml?faces-redirect=true";
	}
	
	public String navigate_To_Milestone_Edit_Page(){
		return "editMilestone.xhtml?faces-redirect=true";
	}

	public String navigateTo_listMilestonePage() {
		updateUserProjects();
		return "listMilestones.xhtml?faces-redirect=true";
	}

	public String navigateToSearchPage() {
		return "filterStudentView.xhtml?faces-redirect=true";
	}

	public String navigateToProjectsPage() {
		updateUserProjects();
		return "listProjectsFacultyMember.xhtml?faces-redirect=true";
	}

	private void updateUserProjects() {
		HttpSession sessionFacultyMember = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);

		FacultyMember sessionUser = (FacultyMember) sessionFacultyMember
				.getAttribute("facultyMemberSession");

		this.setProjects(projectBean.getFacultyMemberProjects(sessionUser
				.getId_faculty_member()));

		List<Project> tempProjects = new ArrayList<Project>();
		
		all_milestones.clear();

		for (Project project : this.getProjects()) {

			project.setProjectAndStudents(projectStudentDAOBean
					.getStudents_Of_A_Project(project.getId_project()));

			/**
			 * 
			 */

			List<Integer> student_ýds = projectStudentDAOBean
					.getStudent_IDs(project.getId_project());
			List<Student> tempStudents = new ArrayList<Student>();
			tempStudents.clear();

			for (Integer student_ID : student_ýds) {
				Student tempStudent = studentDAOBean.getStudent(student_ID
						.intValue());
				tempStudents.add(tempStudent);
			}

			// set students of project
			project.setStudentsOfProject(tempStudents);
			
			/**
			 * Set milestones of the projects
			 */
			List<Milestone> milestones = milestoneDAOBean
					.getMilestones_of_Project(project.getId_project());
			project.setMilestones(milestones);
			
			for (Milestone milestone : milestones) {
				all_milestones.add(milestone);
			}
			
			
			/**
			 * 
			 */
			tempProjects.add(project);
		}

		this.setProjects(tempProjects);

	}

	public boolean isFacultyMemberExists() {
		List<FacultyMember> facultyMembers = facultyMemberDAOBean
				.getAllFacultyMembers();

		for (FacultyMember facultyMember : facultyMembers) {
			if (this.getEmail().equalsIgnoreCase(facultyMember.getEmail())
					&& this.getPassword().equalsIgnoreCase(
							facultyMember.getPassword())) {

				setSessionFacultyMember(facultyMember);

				/**
				 * Assign the projects that belongs to current Faculty Member
				 */
				this.setProjects(projectBean.getFacultyMemberProjects(this
						.getSessionFacultyMember().getId_faculty_member()));

				this.setAcademic_title(this.getSessionFacultyMember()
						.getAcademic_title());
				this.setEmail(this.getSessionFacultyMember().getEmail());
				this.setFirstname(this.getSessionFacultyMember().getFirstname());
				this.setId_faculty_member(this.getSessionFacultyMember()
						.getId_faculty_member());
				this.setLastname(this.getSessionFacultyMember().getLastname());
				this.setPassword(this.getSessionFacultyMember().getPassword());
				this.setResearch_interests(this.getSessionFacultyMember()
						.getResearch_interests());

				HttpSession sessionFacultyMember = (HttpSession) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSession(false);
				sessionFacultyMember.setAttribute("facultyMemberSession",
						this.getSessionFacultyMember());

				return true;
			}
		}

		return false;
	}

	public String isLoginInformationTrue() {

		if (isFacultyMemberExists()) {
			successMessage();
			return "welcomePageFacultyMember.xhtml?faces-redirect=true";
		} else {
			addMessage("Please try again !", "Wrong login information");
			return "loginFacultyMember.xhtml";
		}

	}

	public String logoutFacultyMember() {
		HttpSession sessionFacultyMember = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);

		sessionFacultyMember.removeAttribute("facultyMemberSession");

		return "homePage.xhtml?faces-redirect=true";
	}

	/**
	 * Set context message normally
	 */
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Set success message
	 */
	public void successMessage() {
		addSuccessMessage("Success !", "Successful faculty member login");
	}
	public void successUpdateMessage() {
		addSuccessMessage("Successfully updated !", "Successful faculty member login");
	}

	public void addSuccessMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().getExternalContext().getFlash()
				.setKeepMessages(true);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public FacultyMember getSessionFacultyMember() {
		return sessionFacultyMember;
	}

	public void setSessionFacultyMember(FacultyMember currentFacultyMember) {
		this.sessionFacultyMember = currentFacultyMember;
	}

	public List<Milestone> getAll_milestones() {
		return all_milestones;
	}

	public void setAll_milestones(List<Milestone> all_milestones) {
		this.all_milestones = all_milestones;
	}

}
