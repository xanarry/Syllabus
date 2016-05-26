package basicaldefine;

import java.io.Serializable;

public class Account implements Serializable {
	/*
	 * 个人信息类
	 */
	private static final long serialVersionUID = 1L;
	private String stuNum;
	private String password;

	public Account(String stuNum, String password) {
		super();
		this.stuNum = stuNum;
		this.password = password;
	}

	public Account() {
		super();
		this.stuNum = "";
		this.password = "";
	}

	public String getStuNum() {
		return stuNum;
	}

	public void setStuNum(String stuNum) {
		this.stuNum = stuNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Account [stuNum=" + stuNum + ", password=" + password + "]";
	}

}
