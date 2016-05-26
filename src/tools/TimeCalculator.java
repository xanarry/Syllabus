package tools;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.syllabus.MainActivity;

@SuppressLint("SimpleDateFormat")
public class TimeCalculator {
	// ������л�ȥ��ѧʱ��
	private static String startTime = MainActivity.startTime;

	public static HashMap<String, Integer> todayWeekAndDayOfWeek = new HashMap<String, Integer>();

	// �����˶���ʱ��Ҫ�õ�ǰ��ʱ���ܹ�������ʹ��
	public TimeCalculator() {
		HashMap<String, Integer> todayInfo = getCurrentDayInfo();
		// ����ѧʱ�����Ϊ�յ����
		if (startTime == null || startTime.length() == 0) {
			// ��Ϊ���쿪ѧ
			startTime = String.valueOf(todayInfo.get("YEAR"))
					+ String.valueOf(todayInfo.get("MONTH"))
					+ String.valueOf(todayInfo.get("DAY_OF_MONTH"));
		}
		todayWeekAndDayOfWeek = getWeekAndDayOfweek(todayInfo.get("YEAR"),
				todayInfo.get("MONTH"), todayInfo.get("DAY_OF_MONTH"));
	}

	// ͨ��calendar��ȡ�����ʱ��
	public static HashMap<String, Integer> getCurrentDayInfo() {
		Calendar now = Calendar.getInstance();
		HashMap<String, Integer> todayInfo = new HashMap<String, Integer>();

		int tyear = now.get(Calendar.YEAR);
		int tmonth = now.get(Calendar.MONTH) + 1;
		int tday = now.get(Calendar.DAY_OF_MONTH);
		int tdayOfweek = now.get(Calendar.DAY_OF_WEEK);

		if (tdayOfweek == 1) {
			tdayOfweek = 7;
		} else {
			tdayOfweek = tdayOfweek - 1;
		}

		todayInfo.put("YEAR", tyear);
		todayInfo.put("MONTH", tmonth);
		todayInfo.put("DAY_OF_MONTH", tday);
		todayInfo.put("DAY_OF_WEEK", tdayOfweek);
		return todayInfo;
	}

	// ͨ���趨�������͵�ǰ��ϵͳʱ����㴦��ѧ��ʱ�䣬Ϊ�˷���ʹ�ã����������Ŀ�ѧ��ʱ����Զ����������
	public static String getStartTime(int week) {
		// ��ȡ��ǰϵͳʱ��
		HashMap<String, Integer> todayInfo = getCurrentDayInfo();// ////////////////////////////
		int year = todayInfo.get("YEAR");
		int month = todayInfo.get("MONTH");
		int day = todayInfo.get("DAY_OF_MONTH");
		int dayOfWeek = todayInfo.get("DAY_OF_WEEK");

		// ���ϵ��ü���ǰһ��ĺ���������Ҫ�Ĵ������õ���ѧ������
		for (int i = 0; i < dayOfWeek + (week - 1) * 7; i++) {
			todayInfo = getPreviousDay(year, month, day);
			year = todayInfo.get("YEAR");
			month = todayInfo.get("MONTH");
			day = todayInfo.get("DAY_OF_MONTH");
		}

		// ������������Ϊ�ַ���
		StringBuffer now = new StringBuffer();
		now.append(todayInfo.get("YEAR"));
		if (todayInfo.get("MONTH") < 10) {
			now.append(0);
		}
		now.append(todayInfo.get("MONTH"));
		if (todayInfo.get("DAY_OF_MONTH") < 10) {
			now.append(0);
		}
		now.append(todayInfo.get("DAY_OF_MONTH"));

		startTime = now.toString();
		return now.toString();// �����ַ�����ʽ������
	}

	// ���ַ���ʱ���ȡ��һ�����Ϣ
	public HashMap<String, Integer> getWeekAndDayOfweek(String destTime) {
		// �ֽ��ַ���ʱ��Ϊ������ʽ��ʱ��
		int year = Integer.parseInt(destTime.substring(0, 4));
		int month = Integer.parseInt(destTime.substring(4, 6));
		int day = Integer.parseInt(destTime.substring(6, 8));

		// ���ý������ֲ����ĺ���ȥ����
		return getWeekAndDayOfweek(year, month, day);
	}

	// ������ʱ���ȡ��һ�����Ϣ///////////////////////////////////////
	public static HashMap<String, Integer> getWeekAndDayOfweek(int year,
			int month, int day) {// ���ݾ��������ʱ�ڷ����ܺ�����
		HashMap<String, Integer> weekAndDayOfweek = new HashMap<String, Integer>();

		Calendar to = Calendar.getInstance();
		to.set(Calendar.YEAR, year);
		to.set(Calendar.MONTH, month - 1);
		to.set(Calendar.DAY_OF_MONTH, day);

		StringBuffer sb = new StringBuffer();
		sb.append(year);
		if (month < 10) {
			sb.append(0);
		}
		sb.append(month);
		if (day < 10) {
			sb.append(0);
		}
		sb.append(day);

		weekAndDayOfweek.put("WEEK", getWeek(startTime, sb.toString()));
		int dayOfWeek = to.get(Calendar.DAY_OF_WEEK);// (1-7)��Ӧ������-����1-����2----����6;
		if (dayOfWeek == 1) {
			dayOfWeek = 7;
		} else {
			dayOfWeek = dayOfWeek - 1;
		}
		weekAndDayOfweek.put("DAY_OF_WEEK", dayOfWeek);
		return weekAndDayOfweek;
	}

	// ��ȡ�������ڼ��ļ������
	@SuppressLint("SimpleDateFormat")
	public static int getDaysOfaTimeRange(String startTime, String endTime) {
		long to = 0, from = 0;
		// ����yyyymmdd��ʽ��ʱ��
		SimpleDateFormat dayformator = new SimpleDateFormat("yyyyMMdd");
		new SimpleDateFormat();
		try {
			to = dayformator.parse(endTime).getTime();
			from = dayformator.parse(startTime).getTime();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}

		// ͨ������ת��
		long days = (to - from) / (1000 * 60 * 60 * 24);
		return (int) days;
	}

	private static int getWeek(String startTime, String endTime) {
		int week = 1;
		if (startTime.length() == 0)// when from time is null;
			return week;

		int days = getDaysOfaTimeRange(startTime, endTime);// ////////////////////////////////////

		Calendar start = Calendar.getInstance();
		int y = Integer.parseInt(startTime.substring(0, 4));// ������
		int m = Integer.parseInt(startTime.substring(4, 6));// ������
		int d = Integer.parseInt(startTime.substring(6, 8));// ������
		start.set(Calendar.YEAR, y);
		start.set(Calendar.MONTH, m - 1);// MONTH(0-11)
		start.set(Calendar.DAY_OF_MONTH, d);

		week += (days - 1) / 7;
		return week;
	}

	// ����һ�����ڵ�ǰһ������ڣ�����������hashmap
	public static HashMap<String, Integer> getPreviousDay(int currentYear,
			int currentMonth, int currentDay) {
		HashMap<String, Integer> previousDay = new HashMap<String, Integer>();

		if (currentDay > 1) {// ������ڴ���1
			currentDay -= 1;
		} else {// ���� == 1;
			if (currentMonth == 3) {// �����ǰ�µ�ǰһ������2����
				if (isLeapYear(currentYear)) {// ����2��29
					currentDay = 29;
				} else {
					currentDay = 28;
				}
				// �����ǰ�µ�ǰһ������31���-1 3 5 7 8 10 12
			} else if (currentMonth == 2 || currentMonth == 4
					|| currentMonth == 6 || currentMonth == 8
					|| currentMonth == 9 || currentMonth == 11
					|| currentMonth == 1) {
				currentDay = 31;
			} else {
				currentDay = 30;
			}
			currentMonth -= 1;// ��ǰ�·ݼ�һ����Ϊ0����Ϊ����1�£�1 2 3��
			if (currentMonth == 0) {// ˵���ոռ�1������1�£�Ӧ�����µ��·ݵ�12�£���ݼ�һ
				currentMonth = 12;
				currentYear -= 1;
			}
		}

		previousDay.put("YEAR", currentYear);
		previousDay.put("MONTH", currentMonth);
		previousDay.put("DAY_OF_MONTH", currentDay);

		return previousDay;
	}

	// ����һ�����ڵ���һ������ڣ�����������hashmap
	public static HashMap<String, Integer> getNextDay(int currentYear,
			int currentMonth, int currentDay) {
		HashMap<String, Integer> nextDay = new HashMap<String, Integer>();

		int dayLimit = 0;
		switch (currentMonth) {// ��ȡ��������������
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			dayLimit = 31;
			break;

		case 2:
			if (isLeapYear(currentYear)) {
				dayLimit = 29;
			} else {
				dayLimit = 28;
			}
			break;

		default:
			dayLimit = 30;
			break;
		}

		if (currentDay < dayLimit) {// ������첻�ǵ��µ����һ��
			currentDay += 1;// ����ֱ�Ӽ�һ��
		} else {// ����ǵ������һ��
			currentDay = 1;
			if (currentMonth != 12) {// ����·ݲ���12��
				currentMonth += 1;// �·�ֱ�Ӽ�1
			} else {// ����·���12��
				currentMonth = 1;// �·ݱ�Ϊ1
				currentYear += 1;// ��ݼ�1
			}
		}

		nextDay.put("YEAR", currentYear);
		nextDay.put("MONTH", currentMonth);
		nextDay.put("DAY_OF_MONTH", currentDay);

		return nextDay;
	}

	// �ж��Ƿ�Ϊ����
	public static boolean isLeapYear(int year) {
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}
}
