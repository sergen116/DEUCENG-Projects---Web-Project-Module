package com.managedBeans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

import com.domain.FilteredStudent;
import com.jpa.entities.FacultyMember;
import com.jpa.entities.Project;
import com.jpa.entities.ProjectStudent;
import com.jpa.entities.Project_studentPK;
import com.jpa.entities.Student;
import com.jpa.session.beans.interfaces.ProjectBean;
import com.jpa.session.beans.interfaces.ProjectDAOBean;
import com.jpa.session.beans.interfaces.ProjectStudentDAOBean;
import com.jpa.session.beans.interfaces.StudentBean;
import com.jpa.session.beans.interfaces.StudentDAOBean;
import com.service.StudentService;

@SessionScoped
@ManagedBean
public class NewProjectMB extends Project {

	private static final long serialVersionUID = 1L;
	private static int passed_project_id;
	private static int passed_project_id_for_deletion;

	private List<FilteredStudent> students;
	private List<FilteredStudent> filteredStudents;
	private List<FilteredStudent> selectedStudents;

	private List<String> researchAssistants;
	private boolean skip;

	// used in editProject function
	List<FilteredStudent> selections = new ArrayList<FilteredStudent>();

	@EJB
	private ProjectDAOBean projectDAOBean;

	@EJB
	private ProjectBean projectBean;

	@EJB
	private StudentBean studentBean;

	@EJB
	private StudentDAOBean studentDAOBean;

	@EJB
	private ProjectStudentDAOBean projectStudentDAOBean;

	@ManagedProperty("#{studentService}")
	private StudentService service;

	public NewProjectMB() {

	}

	@PostConstruct
	public void init() {
		/**
		 * Set filter table view
		 */
		this.students = service.createFilteredStudents();
		this.researchAssistants = extractResearchAssistants();
	}

	public Project prepare_the_project_informations() {
		Project newProject = new Project();
		newProject.setAssigned_assistant(this.getAssigned_assistant());
		newProject.setDeadline(this.getDeadline());

		HttpSession sessionFacultyMember = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);

		setFaculty_member((FacultyMember) sessionFacultyMember
				.getAttribute("facultyMemberSession"));

		newProject.setFaculty_member(this.getFaculty_member());
		newProject.setFk_team_leader_id(this.getFaculty_member()
				.getId_faculty_member());
		newProject.setMilestones(null);
		newProject.setProject_name(this.getProject_name());

		return newProject;
	}

	public String startProject() {

		Project newProject = prepare_the_project_informations();
		try {
			projectDAOBean.create(newProject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			successfullCreateMessage();
		}

		int project_id = (projectBean.getProject(this.getProject_name()))
				.getId_project();

		for (FilteredStudent student : this.selectedStudents) {

			String firstname = extractFirstname(student.getStudent_FullName());
			String lastname = extractLastname(student.getStudent_FullName());
			Student foundStudent = studentBean.findStudentAccordingToFullName(
					firstname, lastname);

			ProjectStudent newProjectStudent = new ProjectStudent();
			newProjectStudent.setFk_project_id(project_id);
			newProjectStudent.setFk_student_id(foundStudent.getId_student());
			newProjectStudent.setProject_student(projectDAOBean
					.getProject(project_id));
			newProjectStudent.setStudent(foundStudent);

			try {
				projectStudentDAOBean.create(newProjectStudent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			newProject.getProjectAndStudents().add(newProjectStudent);

		}

		/*
		 * Burasý project_student tablosu güncellendikten sonra yapýlacak
		 */
		newProject.setProjectAndStudents(newProject.getProjectAndStudents());

		return "listProjectsFacultyMember.xhtml?faces-redirect=true";
	}

	public List<Student> getStudents_of_The_Project(int project_id) {

		List<Integer> student_ýds = projectStudentDAOBean
				.getStudent_IDs(project_id);
		List<Student> tempStudents = new ArrayList<Student>();
		tempStudents.clear();

		for (Integer student_ID : student_ýds) {
			Student tempStudent = studentDAOBean.getStudent(student_ID
					.intValue());
			tempStudents.add(tempStudent);
		}

		return tempStudents;
	}

	public String navigate_To_Edit_Page() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		passed_project_id = Integer.parseInt(params.get("passed_project_id"));

		// Setting of selected students of the project

		selections.clear();
		Project editableProject = projectDAOBean.getProject(passed_project_id);
		List<Student> selected_students = getStudents_of_The_Project(editableProject
				.getId_project());

		for (Student selectedStudent : selected_students) {
			for (FilteredStudent filteredStudent : this.students) {
				if (filteredStudent.getStudent_ID() == selectedStudent
						.getId_student()) {
					selections.add(filteredStudent);
				}
			}
		}
		// Setting of selected students of the project

		this.setSelectedStudents(selections);
		this.setAssigned_assistant(editableProject.getAssigned_assistant());
		this.setDeadline(editableProject.getDeadline());
		this.setProject_name(editableProject.getProject_name());

		return "editProject.xhtml?faces-redirect=true";
	}

	public List<FilteredStudent> find_old_students(
			List<FilteredStudent> old_students,
			List<FilteredStudent> new_students) {

		List<FilteredStudent> oldStudents = new ArrayList<FilteredStudent>();
		boolean willBeDeleted = true;

		for (FilteredStudent old_student : old_students) {
			willBeDeleted = true;
			for (FilteredStudent new_student : new_students) {
				if (old_student.getStudent_FullName().equalsIgnoreCase(
						new_student.getStudent_FullName())) {
					willBeDeleted = false;
				}

			}
			if (willBeDeleted)
				oldStudents.add(old_student);

		}

		return oldStudents;
	}

	public List<FilteredStudent> find_new_students(
			List<FilteredStudent> old_students,
			List<FilteredStudent> new_students) {

		List<FilteredStudent> newStudents = new ArrayList<FilteredStudent>();
		boolean willBeAdded = true;

		for (FilteredStudent new_student : new_students) {
			willBeAdded = true;
			for (FilteredStudent old_student : old_students) {
				if (old_student.getStudent_FullName().equalsIgnoreCase(
						new_student.getStudent_FullName())) {
					willBeAdded = false;
				}

			}
			if (willBeAdded)
				newStudents.add(new_student);

		}

		return newStudents;
	}

	public void deleteRowsFrom_ProjectStudent_Entity(
			List<FilteredStudent> will_Be_Deleted_Students) {

		if (!will_Be_Deleted_Students.isEmpty()) {
			for (FilteredStudent deletedStudent : will_Be_Deleted_Students) {
				Project_studentPK id = new Project_studentPK(passed_project_id,
						deletedStudent.getStudent_ID());
				// Remove old selected student from project_student entity
				try {
					projectStudentDAOBean.remove(id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void addRowsTo_ProjectStudent_Entity(
			List<FilteredStudent> will_Be_Added_Students) {

		if (!will_Be_Added_Students.isEmpty()) {
			for (FilteredStudent addedStudent : will_Be_Added_Students) {
				ProjectStudent newProjectStudent = new ProjectStudent();
				newProjectStudent.setFk_project_id(passed_project_id);
				newProjectStudent
						.setFk_student_id(addedStudent.getStudent_ID());
				newProjectStudent.setProject_student(projectDAOBean
						.getProject(passed_project_id));
				newProjectStudent.setStudent(studentDAOBean
						.getStudent(addedStudent.getStudent_ID()));

				try {
					projectStudentDAOBean.create(newProjectStudent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public String editProject() {

		Project editableProject = projectDAOBean.getProject(passed_project_id);

		// Find the students will be deleted from ProjectStudent entity
		List<FilteredStudent> will_Be_Deleted_Students = find_old_students(
				selections, this.getSelectedStudents());

		// Find the students will be added to ProjectStudent entity
		List<FilteredStudent> will_Be_Added_Students = find_new_students(
				selections, this.getSelectedStudents());

		// Update operation on project_student table
		deleteRowsFrom_ProjectStudent_Entity(will_Be_Deleted_Students);
		addRowsTo_ProjectStudent_Entity(will_Be_Added_Students);

		// Update operation on project entity
		editableProject.setAssigned_assistant(this.getAssigned_assistant());
		editableProject.setDeadline(this.getDeadline());

		HttpSession sessionFacultyMember = (HttpSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		editableProject.setFaculty_member((FacultyMember) sessionFacultyMember
				.getAttribute("facultyMemberSession"));
		editableProject.setFk_team_leader_id(editableProject
				.getFaculty_member().getId_faculty_member());
		editableProject.setProject_name(this.getProject_name());

		try {
			projectDAOBean.update(editableProject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			successUpdateMessage();
		}

		return "listProjectsFacultyMember.xhtml?faces-redirect=true";
	}

	public String deleteProject() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		passed_project_id_for_deletion = Integer.parseInt(params
				.get("passed_project_id_for_deletion"));

		try {
			projectDAOBean.remove(passed_project_id_for_deletion);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "listProjectsFacultyMember.xhtml?faces-redirect=true";
	}

	private String extractFirstname(String fullName) {
		String[] names = fullName.split(" ");
		String firstName = "";

		for (int i = 0; i < names.length; i++) {
			if (names[i] == names[names.length - 1])
				continue;
			else
				firstName += names[i] + " ";

		}
		firstName = firstName.trim();

		return firstName;
	}

	private String extractLastname(String fullName) {
		String[] names = fullName.split(" ");
		String lastName = "";

		for (int i = 0; i < names.length; i++) {
			if (names[i] == names[names.length - 1])
				lastName = names[i];
		}

		return lastName;
	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			if (this.selectedStudents.size() <= 3
					&& this.selectedStudents.size() >= 1)
				return event.getNewStep();
			else
				return event.getOldStep();

		}
	}

	// Web page parsing with Jsoup
	public List<String> extractResearchAssistants() {

		Document doc;
		List<String> research_Assistants = new ArrayList<String>();

		try {

			doc = Jsoup.connect(
					"http://ceng.deu.edu.tr/?q=tr/araþtýrma-görevlileri.html")
					.get();

			Element table = doc.select("table").get(0); // select the first
														// table.
			Elements rows = table.select("tr");

			for (int i = 1; i < rows.size(); i += 2) { // first row is the col
														// names so skip it.
				Element row = rows.get(i);
				Elements cols = row.select("td");

				System.out.println(cols.get(1).text());

				String column_1 = cols.get(1).text();
				if (column_1 != null)
					research_Assistants.add(column_1);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return research_Assistants;

	}

	public boolean filterByGPA(Object value, Object filter, Locale locale) {
		String filterText = (filter == null) ? null : filter.toString().trim();
		if (filterText == null || filterText.equals("")) {
			return true;
		}

		if (value == null) {
			return false;
		}

		double doubleFilter = Double.parseDouble(filterText.toString());

		return ((Comparable) value).compareTo(doubleFilter) >= 0;
	}

	public void successUpdateMessage() {
		addSuccessMessage("Successfully updated !", "Successfull updating");
	}

	public void successfullCreateMessage() {
		addSuccessMessage("New project successfully initialized !",
				"Successfull creation");
	}

	public void addSuccessMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, detail);
		FacesContext.getCurrentInstance().getExternalContext().getFlash()
				.setKeepMessages(true);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void onDateSelect(SelectEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected",
						format.format(event.getObject())));
	}

	public List<FilteredStudent> getStudents() {
		return students;
	}

	public void setStudents(List<FilteredStudent> students) {
		this.students = students;
	}

	public List<FilteredStudent> getFilteredStudents() {
		return filteredStudents;
	}

	public void setFilteredStudents(List<FilteredStudent> filteredStudents) {
		this.filteredStudents = filteredStudents;
	}

	public StudentService getService() {
		return service;
	}

	public void setService(StudentService service) {
		this.service = service;
	}

	public List<FilteredStudent> getSelectedStudents() {
		return selectedStudents;
	}

	public void setSelectedStudents(List<FilteredStudent> selectedStudents) {
		this.selectedStudents = selectedStudents;
	}

	public List<String> getResearchAssistants() {
		return researchAssistants;
	}

	public void setResearchAssistants(List<String> researchAssistants) {
		this.researchAssistants = researchAssistants;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public static int getPassed_project_id() {
		return passed_project_id;
	}

	public static void setPassed_project_id(int passed_project_id) {
		NewProjectMB.passed_project_id = passed_project_id;
	}

	public static int getPassed_project_id_for_deletion() {
		return passed_project_id_for_deletion;
	}

	public static void setPassed_project_id_for_deletion(
			int passed_project_id_for_deletion) {
		NewProjectMB.passed_project_id_for_deletion = passed_project_id_for_deletion;
	}

}
