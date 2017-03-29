package com.domain;

public class FilteredStudent {

	private int student_ID;
	private String student_FullName;
	private String student_interests;
	private String workingAreas;
	private double GPA;

	public FilteredStudent(int studentID, String fullName, String interests,
			String workingAreas, double GPA) {
		this.student_ID = studentID;
		this.student_FullName = fullName;
		this.student_interests = interests;
		this.workingAreas = workingAreas;
		this.GPA = GPA;
	}
	public FilteredStudent(){
		
	}
	
	public int getStudent_ID() {
		return student_ID;
	}

	public void setStudent_ID(int student_ID) {
		this.student_ID = student_ID;
	}
	public String getStudent_FullName() {
		return student_FullName;
	}

	public void setStudent_FullName(String student_FullName) {
		this.student_FullName = student_FullName;
	}

	public String getStudent_interests() {
		return student_interests;
	}

	public void setStudent_interests(String student_interests) {
		this.student_interests = student_interests;
	}

	public double getGPA() {
		return GPA;
	}

	public void setGPA(double gPA) {
		GPA = gPA;
	}

	public String getWorkingAreas() {
		return workingAreas;
	}

	public void setWorkingAreas(String workingAreas) {
		this.workingAreas = workingAreas;
	}

}
