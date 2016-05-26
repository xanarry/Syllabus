package intents;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import network.NetworkTasks;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import tools.FileChecker;
import tools.FileOperation;
import tools.TimeCalculator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import basicaldefine.Account;
import basicaldefine.Subject;

import com.example.syllabus.R;
import com.syllabus.MainActivity;

@SuppressWarnings("deprecation")
public class Update extends Activity {

	// ���岼���еĲ���
	private EditText accountText;
	private EditText passwordText;
	private EditText verifyText;
	private Button nextVerifyCodeButton;
	private Button updateButton;
	private ImageView verifyCodeImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ���������߳���ʹ���������
		String strVer = android.os.Build.VERSION.RELEASE;
		strVer = strVer.substring(0, 3).trim();
		float fv = Float.valueOf(strVer);
		if (fv > 2.3) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // ��������滻ΪdetectAll()
																			// �Ͱ����˴��̶�д������I/O
					.penaltyLog() // ��ӡlogcat����ȻҲ���Զ�λ��dropbox��ͨ���ļ�������Ӧ��log
					.build());
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_content);
		setTitle("��������");

		// ��ȡ���ֿؼ�
		accountText = (EditText) findViewById(R.id.account);
		passwordText = (EditText) findViewById(R.id.password);
		verifyText = (EditText) findViewById(R.id.vevify_code);
		nextVerifyCodeButton = (Button) findViewById(R.id.next_verify_code);
		updateButton = (Button) findViewById(R.id.update);
		verifyCodeImage = (ImageView) findViewById(R.id.verify_code_image);

		// ���Զ�ȡ���������Ϣ���ļ��������Ϊ�յĻ������˻��������Ĭ����ʾ�˺�����
		FileOperation fileOperation = new FileOperation(getApplicationContext());
		try {
			Account tperson = fileOperation.readAcountInfoAccountFile();
			if (tperson != null && tperson.getStuNum() != null
					&& tperson.getPassword() != null) {
				if (tperson.getStuNum().length() > 0
						&& tperson.getPassword().length() > 0) {
					accountText.setText(tperson.getStuNum().toString());// ��ʾѧ��
					passwordText.setText(tperson.getPassword().toString());// �趨����
				}
			}
		} catch (Exception e) {

		}

		// ����client����
		final HttpClient httpClient = new DefaultHttpClient();
		// �趨��ʱ
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// ����ʱ
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				6000);// ��ȡ��ʱ

		// ���������������
		final NetworkTasks networkTasks = new NetworkTasks(
				getApplicationContext());
		// �򿪽���ҳ��
		if (!networkTasks.openLoginPage(httpClient)) {
			tips("�޷����ʽ��� ");
			finish();
			return;
		}

		// ��ȡ��֤��
		Bitmap bm = BitmapFactory.decodeStream(networkTasks
				.generateVerifyCodeFile(httpClient));
		if (bm == null) {// ����Ƿ�������ȡ��֤��
			tips("��ȡ��֤��ʧ��");
			return;
		}
		verifyCodeImage.setImageBitmap(bm);// ��ʾ��֤�뵽����

		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String stuNum = accountText.getText().toString();// ��ȡѧ��
				String password = passwordText.getText().toString();// ��ȡ����
				String verifyCode = verifyText.getText().toString();// ��ȡ��֤��

				// �������Ϸ���
				if (stuNum.length() == 0 || password.length() == 0) {
					Toast.makeText(getApplicationContext(), "�����пգ�����",
							Toast.LENGTH_LONG).show();
					return;
				}
				Log.e("update about 132", stuNum + " " + password);
				
				updateButton.setText("����Ŭ�������У����ĵȴ�");
				// ��½���񴦣�������httpclient�� ѧ�ţ����룬��֤��
				Pair<Boolean, String> rs = networkTasks.login(httpClient,
						stuNum, password, verifyCode);
				if (!rs.first) {// ����Ƿ��пɷ��ʵ�����///////////////////////////////////
					Toast.makeText(getApplicationContext(), rs.second,
							Toast.LENGTH_LONG).show();
					updateButton.setText("������Ϣ");
					return;
				}

				// ��ȡ��½��������ҳ��������Ϣ����ɶ���
				////////////////////////////////////////????????????????????????????/
				Log.e("update", "start call networkTasks.getSyllabus");
				
				HashMap<Integer, ArrayList<Subject>> subjects = networkTasks.getSyllabus(httpClient);
				
				
				Account account = new Account();
				account.setStuNum(stuNum);
				account.setPassword(password);

				MainActivity.subjects = subjects;// ���ø����
		
				// ʵ�����ļ���������
				FileOperation fileOperation = new FileOperation(
						getApplicationContext());
				// д������ļ����ñ���
				fileOperation.writeAccountInfoFile(account);
				fileOperation.writeSubjectsFile(subjects);

				// д������ļ�������
				FileChecker fileChecker = new FileChecker(
						getApplicationContext());
				if (!fileChecker.checkStartTimeFile()) {
					String startTime = TimeCalculator.getStartTime(3);
					MainActivity.startTime = startTime;
					FileOutputStream fileOutputStream = null;
					try {
						fileOutputStream = getApplicationContext().openFileOutput(constant.Constants.START_TIME,
										Context.MODE_PRIVATE);
						fileOperation.writeStringFile(startTime,
								fileOutputStream);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							fileOutputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				// ����ļ�������������ʾ
				if (fileChecker.checkFilesExistence()) {
					dialog();
					// Toast.makeText(getApplicationContext(), "���³ɹ�",
					// Toast.LENGTH_LONG).show();
				}
			}
		});

		// ������һ����֤��İ�ť
		nextVerifyCodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				networkTasks.openLoginPage(httpClient);
				// ��ȡ��֤��
				Bitmap bm = BitmapFactory.decodeStream(networkTasks
						.generateVerifyCodeFile(httpClient));
				ImageView imageView = (ImageView) findViewById(R.id.verify_code_image);
				if (bm == null) {
					tips("��ȡ��֤��ʧ��");
					return;
				}
				// �趨��֤����ʾ
				imageView.setImageBitmap(bm);
			}
		});
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);// ���嵯����
		builder.setMessage("�������,ȷ�Ϸ���������");// ������Ϣ����
		builder.setTitle("��ʾ");// ���ñ���
		builder.setCancelable(false);
		builder.setPositiveButton("ȷ��",// ����ȷ������ʾ�����ݼ������Ĳ���
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// �˳��Ի���
						// �ر��������
						finish();
						// �ر��������
						android.os.Process.killProcess(android.os.Process
								.myPid()); // ��ȡPID
						System.exit(0);
					}
				});
		builder.create().show();
	}

	public void tips(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("��ʾ");
		dialog.setMessage(message + "  ��������ͨ����");
		dialog.setPositiveButton("֪����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.create().show();
	}
}
