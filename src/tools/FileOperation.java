package tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import basicaldefine.Account;
import basicaldefine.Subject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class FileOperation {
	private Context context;

	// 接收来自主活动的context
	public FileOperation(Context context) {
		super();
		this.context = context;
	}

	// 读取字符串文件
	public FileInputStream readStringFile(String fileName) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = context.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return fileInputStream;
	}

	// 写入字符串文件
	public void writeStringFile(String content, FileOutputStream fileOutputStream) {
		byte[] buffer = content.getBytes();
		try {
			fileOutputStream.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 读取课表列表对象
	@SuppressWarnings("unchecked")
	public HashMap<Integer, ArrayList<Subject>> readSubjectsFile() {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();
		try {
			fileInputStream = context
					.openFileInput(constant.Constants.SYLLABUS_FILE_OBJ);
			objectInputStream = new ObjectInputStream(fileInputStream);
			Object object = objectInputStream.readObject();
			subjects = (HashMap<Integer, ArrayList<Subject>>) object;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
				objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return subjects;
	}

	// 写入课表列表对象
	public void writeSubjectsFile(HashMap<Integer, ArrayList<Subject>> subjects) {
		ObjectOutputStream objectOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = context.openFileOutput(
					constant.Constants.SYLLABUS_FILE_OBJ, Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(subjects);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 读取个人信息
	public Account readAcountInfoAccountFile() {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Account person = null;
		try {
			fileInputStream = context
					.openFileInput(constant.Constants.PERSONAL_INFORMATION_FILE_OBJ);
			objectInputStream = new ObjectInputStream(fileInputStream);
			person = (Account) objectInputStream.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
				objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return person;
	}

	// 写入个人信息
	public void writeAccountInfoFile(Account person) {
		ObjectOutputStream objectOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = context.openFileOutput(
					constant.Constants.PERSONAL_INFORMATION_FILE_OBJ,
					Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(person);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 写入对象
	public void writeObj(Object obj, ObjectOutputStream objectOutputStream) {
		try {
			objectOutputStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
