package basicaldefine;

import java.io.Serializable;

public class SubjectScore implements Serializable {
	/*
	 * 单一科目成绩类
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String credit;
	private String subjectAttr;
	private String score;

	public SubjectScore(String name, String credit, String subjectAttr,
			String score) {
		super();
		this.name = name;
		this.credit = credit;
		this.subjectAttr = subjectAttr;
		this.score = score;
	}

	public SubjectScore() {
		super();
		this.name = "";
		this.credit = "";
		this.subjectAttr = "";
		this.score = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getSubjectAttr() {
		return subjectAttr;
	}

	public void setSubjectAttr(String subjectAttr) {
		this.subjectAttr = subjectAttr;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "SubjectScore [name=" + name + ", credit=" + credit
				+ ", subjectAttr=" + subjectAttr + ", score=" + score + "]";
	}

}
