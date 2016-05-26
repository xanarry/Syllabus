package constant;

public class Constants {
	private final static String IPAddress = "http://218.7.49.53:9003/";
	public final static String LOGIN_ADDRESS = IPAddress + "loginAction.do";// ��½��ַ
	public final static String VERIFYCODE_ADDRESS = IPAddress
			+ "validateCodeAction.do";// ��֤���ַ

	public final static String SYLLABUS_ADDRESS = IPAddress
			+ "xkAction.do?actionType=6";// �α��ַ
	public final static String PERSONAL_INFORMATION_ADDRESS = IPAddress
			+ "xjInfoAction.do?oper=xjxx";// ������Ϣ��ַ

	// �����ļ���
	public final static String START_TIME = "startTime.obj";
	public final static String SYLLABUS_FILE_OBJ = "syllabusFile.obj";
	public final static String PERSONAL_INFORMATION_FILE_OBJ = "personalInformation.obj";

	public final static String[] SEMESTER_TIME = { "��һ����", "��һ����", "�������",
			"�������", "��������", "��������", "��������", "��������" };
	public final static String[] CLASS_TIME = { "08:00-09:45", "10:15-12:00",
			"14:10-15:55", "16:15-18:00", "19:00-20:45" };

}
