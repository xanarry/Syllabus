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

	// 定义布局中的部件
	private EditText accountText;
	private EditText passwordText;
	private EditText verifyText;
	private Button nextVerifyCodeButton;
	private Button updateButton;
	private ImageView verifyCodeImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 设置在主线程中使用网络访问
		String strVer = android.os.Build.VERSION.RELEASE;
		strVer = strVer.substring(0, 3).trim();
		float fv = Float.valueOf(strVer);
		if (fv > 2.3) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // 这里可以替换为detectAll()
																			// 就包括了磁盘读写和网络I/O
					.penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
					.build());
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_content);
		setTitle("更新内容");

		// 获取布局控件
		accountText = (EditText) findViewById(R.id.account);
		passwordText = (EditText) findViewById(R.id.password);
		verifyText = (EditText) findViewById(R.id.vevify_code);
		nextVerifyCodeButton = (Button) findViewById(R.id.next_verify_code);
		updateButton = (Button) findViewById(R.id.update);
		verifyCodeImage = (ImageView) findViewById(R.id.verify_code_image);

		// 尝试读取保存个人信息的文件，如果不为空的话设置账户密码框中默认显示账号密码
		FileOperation fileOperation = new FileOperation(getApplicationContext());
		try {
			Account tperson = fileOperation.readAcountInfoAccountFile();
			if (tperson != null && tperson.getStuNum() != null
					&& tperson.getPassword() != null) {
				if (tperson.getStuNum().length() > 0
						&& tperson.getPassword().length() > 0) {
					accountText.setText(tperson.getStuNum().toString());// 显示学号
					passwordText.setText(tperson.getPassword().toString());// 设定密码
				}
			}
		} catch (Exception e) {

		}

		// 创建client对象
		final HttpClient httpClient = new DefaultHttpClient();
		// 设定超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				6000);// 读取超时

		// 创建网络任务对象
		final NetworkTasks networkTasks = new NetworkTasks(
				getApplicationContext());
		// 打开教务处页面
		if (!networkTasks.openLoginPage(httpClient)) {
			tips("无法访问教务处 ");
			finish();
			return;
		}

		// 获取验证码
		Bitmap bm = BitmapFactory.decodeStream(networkTasks
				.generateVerifyCodeFile(httpClient));
		if (bm == null) {// 检查是否正常获取验证码
			tips("获取验证码失败");
			return;
		}
		verifyCodeImage.setImageBitmap(bm);// 显示验证码到桌面

		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String stuNum = accountText.getText().toString();// 获取学号
				String password = passwordText.getText().toString();// 获取密码
				String verifyCode = verifyText.getText().toString();// 获取验证码

				// 检查输入合法性
				if (stuNum.length() == 0 || password.length() == 0) {
					Toast.makeText(getApplicationContext(), "输入有空，请检查",
							Toast.LENGTH_LONG).show();
					return;
				}
				Log.e("update about 132", stuNum + " " + password);
				
				updateButton.setText("正在努力更新中，耐心等待");
				// 登陆教务处，参数是httpclient， 学号，密码，验证码
				Pair<Boolean, String> rs = networkTasks.login(httpClient,
						stuNum, password, verifyCode);
				if (!rs.first) {// 检查是否有可访问的网络///////////////////////////////////
					Toast.makeText(getApplicationContext(), rs.second,
							Toast.LENGTH_LONG).show();
					updateButton.setText("更新信息");
					return;
				}

				// 获取登陆后下载网页并整理信息构造成对象
				////////////////////////////////////////????????????????????????????/
				Log.e("update", "start call networkTasks.getSyllabus");
				
				HashMap<Integer, ArrayList<Subject>> subjects = networkTasks.getSyllabus(httpClient);
				
				
				Account account = new Account();
				account.setStuNum(stuNum);
				account.setPassword(password);

				MainActivity.subjects = subjects;// 设置给主活动
		
				// 实例化文件操作对象
				FileOperation fileOperation = new FileOperation(
						getApplicationContext());
				// 写入对象文件永久保存
				fileOperation.writeAccountInfoFile(account);
				fileOperation.writeSubjectsFile(subjects);

				// 写入后检查文件完整性
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

				// 如果文件完整，给出提示
				if (fileChecker.checkFilesExistence()) {
					dialog();
					// Toast.makeText(getApplicationContext(), "更新成功",
					// Toast.LENGTH_LONG).show();
				}
			}
		});

		// 监听下一张验证码的按钮
		nextVerifyCodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				networkTasks.openLoginPage(httpClient);
				// 获取验证码
				Bitmap bm = BitmapFactory.decodeStream(networkTasks
						.generateVerifyCodeFile(httpClient));
				ImageView imageView = (ImageView) findViewById(R.id.verify_code_image);
				if (bm == null) {
					tips("获取验证码失败");
					return;
				}
				// 设定验证码显示
				imageView.setImageBitmap(bm);
			}
		});
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);// 定义弹出框
		builder.setMessage("更新完成,确认返回主界面");// 设置信息主体
		builder.setTitle("提示");// 设置标题
		builder.setCancelable(false);
		builder.setPositiveButton("确定",// 设置确定键显示的内容及点击后的操作
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 退出对话框
						// 关闭软件进程
						finish();
						// 关闭软件进程
						android.os.Process.killProcess(android.os.Process
								.myPid()); // 获取PID
						System.exit(0);
					}
				});
		builder.create().show();
	}

	public void tips(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("提示");
		dialog.setMessage(message + "  请检查网络通畅性");
		dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.create().show();
	}
}
