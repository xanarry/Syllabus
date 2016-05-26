package com.syllabus;

import intents.WeekSchedule;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.example.syllabus.R;

import tools.BackgroundSelector;
import tools.FileChecker;
import tools.FileOperation;
import tools.ScheduleOfDay;
import tools.TimeCalculator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import basicaldefine.Subject;
import constant.Constants;

@SuppressLint({ "InflateParams", "UseSparseArrays" })
public class MainActivity extends ListActivity {

	private int pickedNewWeek;
	// 记录点下屏幕坐标和松手是的屏幕坐标，判断手的滑动方向
	private float x1 = 0;
	@SuppressWarnings("unused")
	private float y1 = 0;
	private float x2 = 0;
	@SuppressWarnings("unused")
	private float y2 = 0;

	// 可变的变量
	private int changingYear;// 表示年
	private int changingMonth;// 表示月
	private int changingDay;// 表示日
	private int changingWeek;// 表示周
	private int changingDayOfWeek;// 表示星期几

	// 保存今天的日期信息，包含年月日，key: "YEAR", "MONTH", "DAY_OF_WEEK"
	private HashMap<String, Integer> today = new HashMap<String, Integer>();

	// 保存今天是第几周星期几，key: "WEEK", "DAY_OF_WEEK"
	private HashMap<String, Integer> todayWDOW = new HashMap<String, Integer>();

	// 保存当天天的课程信息，其中每一节课是以星期几*10+第几节的数值作为map的key，这里保存多节课
	private ArrayList<Map<String, Object>> scheduleList = new ArrayList<Map<String, Object>>();

	// 保存今天的课程信息，操作过程中不变的
	private ArrayList<Subject> todaySchedule = new ArrayList<Subject>();
	private ArrayList<Subject> changingSchedule = new ArrayList<Subject>();

	private String title;// 标题文字
	private ListView listView;// 获取listview，用于设定背景

	// subjects 保存所有课程信息
	public static HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();

	public static String startTime = "";// 保存开学时间20130812的形式，用于计算周数，默认设定为空

	private NumberPicker numberPicker;// 滑块数字选择器
	private FileOperation fileOperation = null;// 定义文件操作变量

	private int dayCounter = 0;// 标记主界面左右滑动的次数，用于限制滑动次数

	public static int backgroundId = R.drawable.bg_17;// 标记背景图片的位置，默认为固定的第17幅图

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fileOperation = new FileOperation(getApplicationContext());// 实例化文件操作类

		// 检查文件完整性，是否初始化完成，
		FileChecker checkFile = new FileChecker(getApplicationContext());
		Log.e("debug", "check file existense");
		if (!checkFile.checkFilesExistence()) {
			// 如果文件不完整，启动update活动连接教务处下载文件
			Log.e("debug", "file not exist update");
			Intent UpdateInformation = new Intent(getApplicationContext(),
					intents.Update.class);
			startActivity(UpdateInformation);
		} else {
			Log.e("debug", "file is existed loading");
			// 文件完整则加载文件
			subjects = fileOperation.readSubjectsFile();// 读取课程表内容
			Log.e("debug", "get subjects: size = " + subjects.size());

			// 读取开学时间
			FileInputStream fileInputStream = fileOperation
					.readStringFile(constant.Constants.START_TIME);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			try {
				startTime = bufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fileInputStream.close();
					inputStreamReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.e("debug", "get start time: " + startTime);
		}

		// 保存今天的日期信息，不会被修改
		today = TimeCalculator.getCurrentDayInfo();
		// 获取今天的周数信息
		todayWDOW = TimeCalculator.getWeekAndDayOfweek(today.get("YEAR"),
				today.get("MONTH"), today.get("DAY_OF_MONTH"));

		pickedNewWeek = todayWDOW.get("WEEK");
		todaySchedule = ScheduleOfDay.getScheduleOf(pickedNewWeek,
				todayWDOW.get("DAY_OF_WEEK"));

		// 初始化可变信息
		changingYear = today.get("YEAR");
		changingMonth = today.get("MONTH");
		changingDay = today.get("DAY_OF_MONTH");
		changingWeek = todayWDOW.get("WEEK");
		changingDayOfWeek = todayWDOW.get("DAY_OF_WEEK");

		// 设置初始化为当天的标题
		title = String.format("[今天] %02d月%02d日 第%02d周周%d", today.get("MONTH"),
				today.get("DAY_OF_MONTH"), todayWDOW.get("WEEK"),
				todayWDOW.get("DAY_OF_WEEK"));

		backgroundId = BackgroundSelector.getBackground(today
				.get("DAY_OF_MONTH"));// 根据今天的日期随机设定背景图片

		// 获取listview用于设置主界面左右滑动手指的监听事件和数据处理
		listView = getListView();

		listView.setBackgroundResource(backgroundId);// 设置背景

		listView.setDividerHeight(5);// 设置个item直接的宽度

		// 设置每两个item间的背景
		listView.setDivider(getApplicationContext().getResources().getDrawable(
				R.drawable.diver));

		// showSubjectList函数用来显示整个主窗口的所用信息
		showSubjectList(title, todaySchedule);

		// ##必须要在MLISTVIEW中设置监听，不能直接在Mainactivity中设置监听，可能是listview会覆盖掉它，使设置的监听无效
		listView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 当手指按下的时候获取起点坐
					x1 = event.getX();
					y1 = event.getY();
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					// 当手指离开的时候获取终点坐标
					x2 = event.getX();
					y2 = event.getY();
					if (x1 - x2 > 70 && dayCounter < 7) { // 向左边滑动，显示下一天的课表，最多向后显示7天
						dayCounter++;
						if (dayCounter < 0) {// 如果滑到今天之前的一天
							// 设置暗色背景
							listView.setBackgroundResource(R.drawable.passed);
						} else {
							// 还原原有背景
							listView.setBackgroundResource(backgroundId);
						}

						// TimeCalculator.getNextDay计算某个日期的下一天的日期
						HashMap<String, Integer> nextDay = TimeCalculator
								.getNextDay(changingYear, changingMonth,
										changingDay);
						changingYear = nextDay.get("YEAR");
						changingMonth = nextDay.get("MONTH");
						changingDay = nextDay.get("DAY_OF_MONTH");
						HashMap<String, Integer> wdow = TimeCalculator
								.getWeekAndDayOfweek(changingYear,
										changingMonth, changingDay);
						changingWeek = wdow.get("WEEK");
						changingDayOfWeek = wdow.get("DAY_OF_WEEK");

						// 重新设定需要显示的信息
						if (changingMonth == today.get("MONTH")
								&& changingDay == today.get("DAY_OF_MONTH")) {
							title = String.format("[今天] %02d月%02d日 第%02d周周%d",
									today.get("MONTH"),
									today.get("DAY_OF_MONTH"),
									todayWDOW.get("WEEK"),
									todayWDOW.get("DAY_OF_WEEK"));
						} else {
							title = String.format("%02d月%02d日 第%02d周周%d",
									changingMonth, changingDay, changingWeek,
									changingDayOfWeek);
						}

						// 获取下一天的课程
						ArrayList<Subject> tSchedule = ScheduleOfDay
								.getScheduleOf(changingWeek, changingDayOfWeek);

						// 调用函数更新屏幕数据
						showSubjectList(title, tSchedule);///////////////////////

					} else if (x2 - x1 > 70 && dayCounter > -1) { // 向右边滑动，显示前天的课程
						dayCounter--;
						if (dayCounter < 0) {
							// 如果滑到今天之前的一天，设置暗色背景
							listView.setBackgroundResource(R.drawable.passed);
						} else {
							// 还原背景
							listView.setBackgroundResource(backgroundId);
						}
						// TimeCalculator.getPreviousDay计算某个日期的前一天的日期
						HashMap<String, Integer> previousDay = TimeCalculator
								.getPreviousDay(changingYear, changingMonth,
										changingDay);
						changingYear = previousDay.get("YEAR");
						changingMonth = previousDay.get("MONTH");
						changingDay = previousDay.get("DAY_OF_MONTH");
						HashMap<String, Integer> wdow = TimeCalculator
								.getWeekAndDayOfweek(changingYear,
										changingMonth, changingDay);
						changingWeek = wdow.get("WEEK");
						changingDayOfWeek = wdow.get("DAY_OF_WEEK");

						// 重新获取数据
						if (changingMonth == today.get("MONTH")
								&& changingDay == today.get("DAY_OF_MONTH")) {
							title = String.format("[今天] %02d月%02d日 第%02d周周%d",
									today.get("MONTH"),
									today.get("DAY_OF_MONTH"),
									todayWDOW.get("WEEK"),
									todayWDOW.get("DAY_OF_WEEK"));
						} else {
							title = String.format("%02d月%02d日 第%02d周周%d",
									changingMonth, changingDay, changingWeek,
									changingDayOfWeek);
						}

						ArrayList<Subject> tSchedule = ScheduleOfDay
								.getScheduleOf(changingWeek, changingDayOfWeek);

						// 刷新显示数据
						showSubjectList(title, tSchedule);

					} else if (Math.abs(x2 - x1) > 70) {// 如果超过限制的天数
						tipsDialog();// 弹出提示
					}
				}
				return false;
			}
		});
	}

	// 参数分别是标题，每个课程项目的名字和课程的详细信息
	private void showSubjectList(String title,
			final ArrayList<Subject> currentDaySchedule) {
		changingSchedule = currentDaySchedule;
		scheduleList.clear();// 清空上次显示的数据，不然新的数据后直接添加到原油数据之后
		title = title.charAt(title.length() - 1) == '7' ? title.substring(0, title.length() - 1) + "日" : title;
		setTitle(title);// 设置标题显示时间

		// 迭代今天的课程构建输出信息
		Iterator<Subject> iterator = currentDaySchedule.iterator();
		while (iterator.hasNext()) {
			Subject subject = (Subject) iterator.next();
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("order",
					String.valueOf(subject.getDayOfweekAndOrder() % 10));
			item.put("name", subject.getName());
			item.put("location", subject.getLocation() != "" ?  "地点:  " + subject.getLocation() : "");
			item.put(
					"time",
					Constants.CLASS_TIME[subject.getDayOfweekAndOrder() % 10 - 1]);////////////////////////////
			scheduleList.add(item);
		}

		// 设置点击item的监听器，点击是显示该课程的详细信息
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 传递该课程的信息调用函数弹出对话框显示
				if (currentDaySchedule.get(position).getName() != "") {//对于没有课的条目不设置监听
					showDetail(position, changingWeek, changingDayOfWeek);
				}
			}
		});
		listView.setCacheColorHint(0);

		// 使用adapter将数据与布局拼到一起
		SubjectList adapter = new SubjectList(this, scheduleList, R.layout.list_item, 
			new String[] { "order", "name", "location", "time" }, 
			new    int[] { R.id.list_order, R.id.list_name,
						   R.id.list_location, R.id.list_time });
		
		setListAdapter(adapter);// 设置显示
	}

	// 设置自定义的adapter捆绑listview数据
	public class SubjectList extends SimpleAdapter {
		private String[] basicColor = { "#CCFFCC", "#CCFFFF" };// 设置两种颜色在item显示中交替变换
		private String gray = "#ADADAD";
		public SubjectList(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public Object getItem(int position) {
			return super.getItem(position);
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String[] arrayOfInt = basicColor;
			int colorLength = basicColor.length;// 获取颜色总数

			// 根据当前item的index获取背景颜色
			int selected = Color.parseColor(arrayOfInt[position % colorLength]);

			View localView = super.getView(position, convertView, parent);
			// 设置颜色
			
			Log.e("color index", position + " : " + changingSchedule.get(position).getName());
			Subject curSubject = changingSchedule.get(position);/////////////////////////////
				
				
			localView.setBackgroundColor(selected);
			localView.getBackground().setAlpha(200);// 设置透明度
			if (curSubject.getName() == "") {
				localView.setBackgroundColor(Color.parseColor(gray));
				localView.getBackground().setAlpha(400);// 设置透明度
			}
			return localView;
		}
	}

	@Override
	// 显示设置菜单里面的内容
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 4, 3, "更新周次");
		menu.add(Menu.NONE, 3, 4, "编辑课程");
		menu.add(Menu.NONE, 2, 6, "搜索课程");
		menu.add(Menu.NONE, 1, 7, "周课程表");
		menu.add(Menu.NONE, 6, 2, "更新信息");
		menu.add(Menu.NONE, 7, 1, "关于此软件");
		return true;
	}

	// 监听菜单目录下的item点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case 1:// 点击本周课表
			Intent WeekSchedule = new Intent(getApplicationContext(),
					WeekSchedule.class);
			startActivity(WeekSchedule);
			return true;

		case 2:// 搜索内容
			Intent searchSyllabusIntent = new Intent(getApplicationContext(),
					intents.SearchSchedule.class);
			startActivity(searchSyllabusIntent);
			return true;

		case 3:// 编辑
				// finish();//关闭父类活动，防止从子活动中返回后要反复点两次退出才能退出
			Intent ModifyContent = new Intent(getApplicationContext(),
					intents.ModifyContent.class);
			startActivity(ModifyContent);
			return true;

		case 4:// 设定新的周数
				// finish();//关闭父类活动，防止从子活动中返回后要反复点两次退出才能退出
			setNewWeekDialog();
			return true;

		case 6:// 更新信息
			Intent UpdateInformation = new Intent(getApplicationContext(),
					intents.Update.class);
			startActivity(UpdateInformation);
			return true;

		case 7:// 帮助
			Intent Copyright = new Intent(getApplicationContext(),
					intents.Copyright.class);
			startActivity(Copyright);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// //////////退出的弹出菜单代码开始处////////////////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 监听手机的返回按键
			dialog();// 调用这个函数跳出弹出框
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 弹出对话框显示详细信息 第几节课 周 星期几
	protected void showDetail(int position, int week, int dayOfWeek) {
		AlertDialog.Builder detail = new AlertDialog.Builder(this);// 定义弹出框
		// 获取当天课程信息
		ArrayList<Subject> currendDaySchedul = ScheduleOfDay.getScheduleOf(
				week, dayOfWeek);
		// 获取具体某节课程
		Subject subject = currendDaySchedul.get(position);
		// 设置课程名为标题
		detail.setTitle(subject.getName());

		// 计算剩余周数
		TreeSet<Integer> currentSubjectWeeks = subject.getWeeks();
		Iterator<Integer> iter = currentSubjectWeeks.iterator();
		String restWeek = "";
		while (iter.hasNext()) {
			Integer integer = (Integer) iter.next();
			if (integer > todayWDOW.get("WEEK")) {
				restWeek += integer.toString() + ",";
			}
		}

		if (restWeek.length() > 0) {
			restWeek = "[" + restWeek.substring(0, restWeek.length() - 1) + "]";
		} else {
			restWeek = "已经结课";
		}

		// 设定信息
		detail.setMessage("上课地点:    " + subject.getLocation() + "\n剩余周数:    "
				+ restWeek + "\n课程属性:    " + subject.getSubjectAttr()
				+ "\n考试类型:    " + subject.getTestAttr() + "课\n教        师:    "
				+ subject.getTeacher());
		// 定义确定按钮
		detail.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 直接关闭对话框
					}
				});
		detail.create().show();// 显示对话框
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);// 定义弹出框
		builder.setMessage("ddup，确认退出");// 设置信息主体
		builder.setTitle("提示");// 设置标题
		builder.setPositiveButton("确定",// 设置确定键显示的内容及点击后的操作
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						dialog.dismiss();// 退出对话框
						// 关闭软件进程
						android.os.Process.killProcess(android.os.Process
								.myPid()); // 获取PID
						System.exit(0);
					}
				});
		builder.setNegativeButton("取消",// 设置取消的信息
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 直接关闭对话框
					}
				});
		builder.create().show();
	}

	// //////////退出的弹出菜单代码结尾处////////////////////

	protected void tipsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);// 定义弹出框
		builder.setMessage("只允许查看前一天至后7天的课程");// 设置信息主体
		builder.setTitle("提示");// 设置标题

		builder.setNegativeButton("我知道了",// 设置取消的信息
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 直接关闭对话框
					}
				});
		builder.create().show();
	}

	public void setNewWeekDialog() {
		Dialog dialog = new Dialog(MainActivity.this);
		View npView = getLayoutInflater().inflate(R.layout.new_week, null);

		// 设定数字选择器
		numberPicker = (NumberPicker) npView.findViewById(R.id.picked_week);
		// 设定范围
		numberPicker.setMaxValue(20);
		numberPicker.setMinValue(1);
		int currentWeek = TimeCalculator.getWeekAndDayOfweek(today.get("YEAR"),
				today.get("MONTH"), today.get("DAY_OF_MONTH")).get("WEEK");
		pickedNewWeek = currentWeek;

		numberPicker.setValue(currentWeek);// 设定默认值

		numberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				pickedNewWeek = newVal;
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择新的周数,设置将在重启生效");
		builder.setView(npView);
		builder.setPositiveButton("重设", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				FileOutputStream fileOutputStream;
				try {
					fileOutputStream = getApplicationContext()
							.openFileOutput(constant.Constants.START_TIME,
									Context.MODE_PRIVATE);
					fileOperation.writeStringFile(String.valueOf(TimeCalculator
							.getStartTime(pickedNewWeek)), fileOutputStream);
					changingWeek = pickedNewWeek;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(),
						"已将当前周设为第" + pickedNewWeek + "周， 下次启动生效",
						Toast.LENGTH_LONG).show();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}
}