package basicaldefine;

import java.io.Serializable;
import java.util.TreeSet;

public class Subject implements Comparable<Subject>, Serializable {
	/**
	 * �γ���Ϣ��
	 * @param name �γ�����
	 * @param subjectAttr �γ��Ǳ��޻���ѡ��
	 * @param testAttr �γ��ǿ��黹�ǿ���
	 * @param teacher �ڿν�ʦ
	 * @param weeks �Ͽγ�������
	 * @param dayOfweekAndOrder �Ͽξ���ʱ�����ڼ��ڼ���
	 * @param location �Ͽεص�
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String subjectAttr;
	private String testAttr;
	private String teacher;
	private TreeSet<Integer> weeks;
	private int dayOfweekAndOrder;
	private String location;

	public Subject(String name, String subjectAttr, String testAttr,
			String teacher, TreeSet<Integer> weeks, int dayOfweekAndOrder,
			String location) {
		super();
		this.name = name;
		this.subjectAttr = subjectAttr;
		this.testAttr = testAttr;
		this.teacher = teacher;
		this.weeks = weeks;
		this.dayOfweekAndOrder = dayOfweekAndOrder;
		this.location = location;
	}
	
	public Subject() {
		super();
		TreeSet<Integer> tweeks = new TreeSet<Integer>();
		tweeks.add(1);
		this.name = "δ֪";
		this.subjectAttr = "δ֪";
		this.testAttr = "δ֪";
		this.teacher = "δ֪";
		this.weeks = tweeks;
		this.dayOfweekAndOrder = 0;
		this.location = "δ֪";
	}
	
	@Override
	public int compareTo(Subject o) {
		if (this.dayOfweekAndOrder < o.dayOfweekAndOrder) {
			return 1;
		} else if (this.dayOfweekAndOrder == o.dayOfweekAndOrder) {
			return 0;
		} else {
			return -1;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubjectAttr() {
		return subjectAttr;
	}

	public void setSubjectAttr(String subjectAttr) {
		this.subjectAttr = subjectAttr;
	}

	public String getTestAttr() {
		return testAttr;
	}

	public void setTestAttr(String testAttr) {
		this.testAttr = testAttr;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public TreeSet<Integer> getWeeks() {
		return weeks;
	}

	public void setWeeks(TreeSet<Integer> weeks) {
		this.weeks = weeks;
	}

	public int getDayOfweekAndOrder() {
		return dayOfweekAndOrder;
	}

	public void setDayOfweekAndOrder(int dayOfweekAndOrder) {
		this.dayOfweekAndOrder = dayOfweekAndOrder;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Subject [name=" + name + ", subjectAttr=" + subjectAttr
				+ ", testAttr=" + testAttr + ", teacher=" + teacher
				+ ", weeks=" + weeks + ", dayOfweekAndOrder="
				+ dayOfweekAndOrder + ", location=" + location + "]";
	}
}
