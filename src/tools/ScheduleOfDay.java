package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.syllabus.MainActivity;

import android.util.Log;
import basicaldefine.Subject;

public class ScheduleOfDay {
	// 从主活动中获取所有课程的数据
	private static HashMap<Integer, ArrayList<Subject>> subjects = MainActivity.subjects;

	// 返回某周星期几的课程
	public static ArrayList<Subject> getScheduleOf(int week, int dayOfWeek) {
		ArrayList<Subject> schedule = new ArrayList<Subject>();
		// 遍历这一天的所有课，通过键值直接获取保存的list中
		for (int order = 1; order <= 5; order++) {//分别获取第一节到第五节
			//获取在该节课时间有课的所有课程
			ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10 + order);
			if (subjectArrayList != null && subjectArrayList.size() > 0) {
				boolean mark = false;
				for (Subject x : subjectArrayList) {
					if (x.getWeeks().contains(week) && !schedule.contains(x)) {
						mark = true;
						schedule.add(x);
					}
					if (!mark) {
						schedule.add(new Subject("", "", "", "", new TreeSet<Integer>(), dayOfWeek * 10 + order, ""));
						mark = true;
					}
				}
			} else {
				schedule.add(new Subject("", "", "", "", new TreeSet<Integer>(), dayOfWeek * 10 + order, ""));
			}
		}
		Log.e("debug today schedule", schedule.size() + "");
		return schedule;
	}

	// 返回某年某月某日的信息
	public static ArrayList<Subject> getScheduleOf(int year, int month, int day) {
		// 将时间转为周和星期几
		HashMap<String, Integer> weekAndDayOfWeek = TimeCalculator
				.getWeekAndDayOfweek(year, month, day);
		int week = weekAndDayOfWeek.get("WEEK");
		int dayOfWeek = weekAndDayOfWeek.get("DAY_OF_WEEK");
		// 调用上面的函数
		return getScheduleOf(week, dayOfWeek);
	}

	// 返回当天的课程信息
	public static ArrayList<Subject> getScheduleOfCurrentDay() {
		HashMap<String, Integer> currentDay = TimeCalculator
				.getCurrentDayInfo();
		return getScheduleOf(currentDay.get("DAY"), currentDay.get("MONTH"),
				currentDay.get("DAY"));
	}
}
