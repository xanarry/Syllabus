package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.syllabus.MainActivity;

import android.util.Log;
import basicaldefine.Subject;

public class ScheduleOfDay {
	// ������л�ȡ���пγ̵�����
	private static HashMap<Integer, ArrayList<Subject>> subjects = MainActivity.subjects;

	// ����ĳ�����ڼ��Ŀγ�
	public static ArrayList<Subject> getScheduleOf(int week, int dayOfWeek) {
		ArrayList<Subject> schedule = new ArrayList<Subject>();
		// ������һ������пΣ�ͨ����ֱֵ�ӻ�ȡ�����list��
		for (int order = 1; order <= 5; order++) {//�ֱ��ȡ��һ�ڵ������
			//��ȡ�ڸýڿ�ʱ���пε����пγ�
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

	// ����ĳ��ĳ��ĳ�յ���Ϣ
	public static ArrayList<Subject> getScheduleOf(int year, int month, int day) {
		// ��ʱ��תΪ�ܺ����ڼ�
		HashMap<String, Integer> weekAndDayOfWeek = TimeCalculator
				.getWeekAndDayOfweek(year, month, day);
		int week = weekAndDayOfWeek.get("WEEK");
		int dayOfWeek = weekAndDayOfWeek.get("DAY_OF_WEEK");
		// ��������ĺ���
		return getScheduleOf(week, dayOfWeek);
	}

	// ���ص���Ŀγ���Ϣ
	public static ArrayList<Subject> getScheduleOfCurrentDay() {
		HashMap<String, Integer> currentDay = TimeCalculator
				.getCurrentDayInfo();
		return getScheduleOf(currentDay.get("DAY"), currentDay.get("MONTH"),
				currentDay.get("DAY"));
	}
}
