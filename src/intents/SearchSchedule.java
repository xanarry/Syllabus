package intents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import tools.ScheduleOfDay;
import tools.TimeCalculator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import basicaldefine.Subject;

import com.example.syllabus.R;

import constant.Constants;

public class SearchSchedule extends Activity {
	private Calendar calendar;
	private int year;
	private int month;
	private int day;
	private int week;
	private int weekday;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_schedule);

		setTitle("课程查询");

		Button searchByDateButton = (Button) findViewById(R.id.bydate);
		Button searchByClassTimeButton = (Button) findViewById(R.id.byclasstime);
		// 按日期查找课程
		searchByDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取指定日期的课程
				ArrayList<Subject> syllabusOfThisDay = ScheduleOfDay
						.getScheduleOf(year, month, day);
				// 调用函数显示，传递列表
				showClassDialog(syllabusOfThisDay);
			}
		});

		// 按上课的周和星期查找
		searchByClassTimeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 获取指定周和星期的课程
				ArrayList<Subject> syllabusOfThisDay = ScheduleOfDay
						.getScheduleOf(week, weekday);
				// 调用函数显示，传递列表
				showClassDialog(syllabusOfThisDay);
			}
		});

		getChangedValue();
	}

	// 函数中弹出对话框显示信息
	public void showClassDialog(ArrayList<Subject> scheduleOfCurrentDay) {
		ArrayList<Subject> schedule = new ArrayList<Subject>();
		for (Subject x : scheduleOfCurrentDay) {
			if (x.getName() != "" && x.getLocation() != "") {
				schedule.add(x);
			}
		}
		String title = null;
		String message = null;
		StringBuffer sb = new StringBuffer();
		if (!schedule.isEmpty()) {
			Iterator<Subject> iterator = schedule.iterator();
			while (iterator.hasNext()) {
				Subject subject = (Subject) iterator.next();
				sb.append("第" + subject.getDayOfweekAndOrder() % 10 + "节:  "
						+ subject.getName() + "\n");
				sb.append("教  师:  " + subject.getTeacher() + "\n");
				sb.append("位  置:  " + subject.getLocation() + "\n");
				sb.append("时  间:  "
						+ Constants.CLASS_TIME[subject.getDayOfweekAndOrder() % 10 - 1]
						+ "\n");
				sb.append("属  性:  " + subject.getSubjectAttr() + " + "
						+ subject.getTestAttr() + "\n\n");
			}
			message = sb.toString();
		} else {
			message = "今天没课      O(∩_∩)O哈哈哈~";
		}

		title = "查询结果";

		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	// 设置三个numberPicker的状态和获取其中选择的数据，具体可百度，在此不再做详细说明
	private void getChangedValue() {
		calendar = Calendar.getInstance();
		NumberPicker yearPicker, monthPicker, dayPicker, weekdayPicker, weekPicker;

		yearPicker = (NumberPicker) findViewById(R.id.year);
		year = calendar.get(Calendar.YEAR);
		yearPicker.setMaxValue(calendar.get(Calendar.YEAR) + 1);
		yearPicker.setMinValue(calendar.get(Calendar.YEAR) - 1);
		yearPicker.setValue(calendar.get(Calendar.YEAR));

		yearPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				year = newVal;
			}
		});

		monthPicker = (NumberPicker) findViewById(R.id.month);
		monthPicker.setMaxValue(12);
		monthPicker.setMinValue(1);
		monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
		month = calendar.get(Calendar.MONTH) + 1;
		monthPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				month = newVal;
			}
		});

		dayPicker = (NumberPicker) findViewById(R.id.day);
		// 此处应当跟据当前所选月限定天数，后面应当优化
		dayPicker.setMaxValue(31);
		dayPicker.setMinValue(1);
		dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH));
		day = calendar.get(Calendar.DAY_OF_MONTH);
		dayPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				// TODO Auto-generated method stub
				day = newVal;
			}
		});

		weekPicker = (NumberPicker) findViewById(R.id.week);
		weekPicker.setMaxValue(20);
		weekPicker.setMinValue(1);
		HashMap<String, Integer> today = TimeCalculator.getCurrentDayInfo();
		HashMap<String, Integer> todayWaDW = TimeCalculator
				.getWeekAndDayOfweek(today.get("YEAR"), today.get("MONTH"),
						today.get("DAY_OF_MONTH"));
		weekPicker.setValue(todayWaDW.get("WEEK"));// 滑块显示的当前周
		week = todayWaDW.get("WEEK");// 设置查找是获取的默认值
		weekPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				// TODO Auto-generated method stub
				week = newVal;
			}
		});

		weekdayPicker = (NumberPicker) findViewById(R.id.weekday);
		weekdayPicker.setMaxValue(6);
		weekdayPicker.setMinValue(1);
		weekdayPicker.setValue(calendar.get(Calendar.DAY_OF_WEEK) - 1);
		weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		weekdayPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				weekday = newVal;
			}
		});
	}

}
