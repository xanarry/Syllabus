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
		// 获取应用跟目录
		basicPath = context.getFilesDir().getAbsolutePath();
		// 课程表文件路径
		syllabusObjFilePath = basicPath + "/"
				+ constant.Constants.SYLLABUS_FILE_OBJ;
		// 个人信息文件路径
		personalInformationObjFilePath = basicPath + "/"
				+ constant.Constants.PERSONAL_INFORMATION_FILE_OBJ;
		// 开学时间路径
		startTimeFilePath = basicPath + "/" + constant.Constants.START_TIME;
	}

	public boolean checkFilesExistence() {// 一次检查所有文件
		return checkPersonalInformationFile() && checkStartTimeFile() && checkSyllabusSetFile();
	}

	public boolean checkLatestSemesterScore() {// 检查成绩文件
		return checkSinglFile(lastSemesterScore);
	}

	public boolean checkSyllabusSetFile() {// 检查课程表文件
		return checkSinglFile(syllabusObjFilePath);
	}

	public boolean checkPersonalInformationFile() {// 检查个人信息文件
		return checkSinglFile(personalInformationObjFilePath);
	}

	public boolean checkStartTimeFile() {// 检查开学时间文件
		return checkSinglFile(startTimeFilePath);
	}

	// 检查问函数，参数是文件路径
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
