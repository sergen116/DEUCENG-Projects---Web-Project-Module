package com.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.domain.FilteredStudent;
import com.jpa.entities.Internship;
import com.jpa.entities.Student;
import com.jpa.session.beans.interfaces.InternshipDAOBean;
import com.jpa.session.beans.interfaces.StudentDAOBean;

@ApplicationScoped
@ManagedBean(name = "studentService")
public class StudentService {

	@EJB
	private InternshipDAOBean internshipDAOBean;

	@EJB
	private StudentDAOBean studentDAOBean;

	public List<FilteredStudent> createFilteredStudents() {
		String working_areas = "";
		String fullName = "";

		List<FilteredStudent> filtered_students = new ArrayList<FilteredStudent>();

		List<Internship> allInternships = internshipDAOBean.getAllInternships();
		List<Student> allStudents = studentDAOBean.getAllStudents();

		/**
		 * Combine the workings areas of all internships of the student
		 */
		for (Student student : allStudents) {
			working_areas = "";

			for (Internship internship : allInternships) {
				if (internship.getFk_student_id() == student.getId_student())
					working_areas += ", " + internship.getWorking_areas();

			}
			fullName = student.getFirstname() + " " + student.getLastname();
			filtered_students.add(new FilteredStudent(student.getId_student(),
					fullName, student.getInterests(), working_areas, student
							.getGpa()));
		}

		return filtered_students;
	}

}
