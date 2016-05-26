package tools;

import java.io.File;

import android.content.Context;

public class FileChecker {
	@SuppressWarnings("unused")
	private Context context;
	private String basicPath;
	private String syllabusObjFilePath;
	private String personalInformationObjFilePath;
	private String startTimeFilePath;
	private String lastSemesterScore;

	public FileChecker(Context context) {
		this.context = context;
		// ��ȡӦ�ø�Ŀ¼
		basicPath = context.getFilesDir().getAbsolutePath();
		// �γ̱��ļ�·��
		syllabusObjFilePath = basicPath + "/"
				+ constant.Constants.SYLLABUS_FILE_OBJ;
		// ������Ϣ�ļ�·��
		personalInformationObjFilePath = basicPath + "/"
				+ constant.Constants.PERSONAL_INFORMATION_FILE_OBJ;
		// ��ѧʱ��·��
		startTimeFilePath = basicPath + "/" + constant.Constants.START_TIME;
	}

	public boolean checkFilesExistence() {// һ�μ�������ļ�
		return checkPersonalInformationFile() && checkStartTimeFile() && checkSyllabusSetFile();
	}

	public boolean checkLatestSemesterScore() {// ���ɼ��ļ�
		return checkSinglFile(lastSemesterScore);
	}

	public boolean checkSyllabusSetFile() {// ���γ̱��ļ�
		return checkSinglFile(syllabusObjFilePath);
	}

	public boolean checkPersonalInformationFile() {// ��������Ϣ�ļ�
		return checkSinglFile(personalInformationObjFilePath);
	}

	public boolean checkStartTimeFile() {// ��鿪ѧʱ���ļ�
		return checkSinglFile(startTimeFilePath);
	}

	// ����ʺ������������ļ�·��
	public boolean checkSinglFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
