package tools;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.syllabus.MainActivity;

@SuppressLint("SimpleDateFormat")
public class TimeCalculator {
	// 从主活动中或去开学时间
	private static String startTime = MainActivity.startTime;

	public static HashMap<String, Integer> todayWeekAndDayOfWeek = new HashMap<String, Integer>();

	// 创建此对象时就要让当前的时间能够被外面使用
	public TimeCalculator() {
		HashMap<String, Integer> todayInfo = getCurrentDayInfo();
		// 处理开学时间可能为空的情况
		if (startTime == null || startTime.length() == 0) {
			// 设为今天开学
			startTime = String.valueOf(todayInfo.get("YEAR"))
					+ String.valueOf(todayInfo.get("MONTH"))
					+ String.valueOf(todayInfo.get("DAY_OF_MONTH"));
		}
		todayWeekAndDayOfWeek = getWeekAndDayOfweek(todayInfo.get("YEAR"),
				todayInfo.get("MONTH"), todayInfo.get("DAY_OF_MONTH"));
	}

	// 通过calendar获取当天的时间
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

	// 通过设定的周数和当前的系统时间计算处理开学的时间，为了方便使用，这里计算出的开学的时间永远都是星期天
	public static String getStartTime(int week) {
		// 获取当前系统时间
		HashMap<String, Integer> todayInfo = getCurrentDayInfo();// ////////////////////////////
		int year = todayInfo.get("YEAR");
		int month = todayInfo.get("MONTH");
		int day = todayInfo.get("DAY_OF_MONTH");
		int dayOfWeek = todayInfo.get("DAY_OF_WEEK");

		// 不断调用计算前一天的函数迭代需要的次数，得到开学的日期
		for (int i = 0; i < dayOfWeek + (week - 1) * 7; i++) {
			todayInfo = getPreviousDay(year, month, day);
			year = todayInfo.get("YEAR");
			month = todayInfo.get("MONTH");
			day = todayInfo.get("DAY_OF_MONTH");
		}

		// 处理数字日期为字符串
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
		return now.toString();// 返回字符串形式的日期
	}

	// 按字符串时间获取这一天的信息
	public HashMap<String, Integer> getWeekAndDayOfweek(String destTime) {
		// 分解字符串时间为数字形式的时间
		int year = Integer.parseInt(destTime.substring(0, 4));
		int month = Integer.parseInt(destTime.substring(4, 6));
		int day = Integer.parseInt(destTime.substring(6, 8));

		// 调用接收数字参数的函数去处理
		return getWeekAndDayOfweek(year, month, day);
	}

	// 按数字时间获取这一天的信息///////////////////////////////////////
	public static HashMap<String, Integer> getWeekAndDayOfweek(int year,
			int month, int day) {// 根据具体的数字时期返回周和星期
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
		int dayOfWeek = to.get(Calendar.DAY_OF_WEEK);// (1-7)对应星期天-星期1-星期2----星期6;
		if (dayOfWeek == 1) {
			dayOfWeek = 7;
		} else {
			dayOfWeek = dayOfWeek - 1;
		}
		weekAndDayOfweek.put("DAY_OF_WEEK", dayOfWeek);
		return weekAndDayOfweek;
	}

	// 获取两个日期键的间隔天数
	@SuppressLint("SimpleDateFormat")
	public static int getDaysOfaTimeRange(String startTime, String endTime) {
		long to = 0, from = 0;
		// 接收yyyymmdd格式的时间
		SimpleDateFormat dayformator = new SimpleDateFormat("yyyyMMdd");
		new SimpleDateFormat();
		try {
			to = dayformator.parse(endTime).getTime();
			from = dayformator.parse(startTime).getTime();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}

		// 通过毫秒转换
		long days = (to - from) / (1000 * 60 * 60 * 24);
		return (int) days;
	}

	private static int getWeek(String startTime, String endTime) {
		int week = 1;
		if (startTime.length() == 0)// when from time is null;
			return week;

		int days = getDaysOfaTimeRange(startTime, endTime);// ////////////////////////////////////

		Calendar start = Calendar.getInstance();
		int y = Integer.parseInt(startTime.substring(0, 4));// 分离年
		int m = Integer.parseInt(startTime.substring(4, 6));// 分离月
		int d = Integer.parseInt(startTime.substring(6, 8));// 分离日
		start.set(Calendar.YEAR, y);
		start.set(Calendar.MONTH, m - 1);// MONTH(0-11)
		start.set(Calendar.DAY_OF_MONTH, d);

		week += (days - 1) / 7;
		return week;
	}

	// 返回一个日期的前一天的日期，返回类型是hashmap
	public static HashMap<String, Integer> getPreviousDay(int currentYear,
			int currentMonth, int currentDay) {
		HashMap<String, Integer> previousDay = new HashMap<String, Integer>();

		if (currentDay > 1) {// 如果日期大于1
			currentDay -= 1;
		} else {// 日期 == 1;
			if (currentMonth == 3) {// 如果当前月的前一个月是2二月
				if (isLeapYear(currentYear)) {// 润年2月29
					currentDay = 29;
				} else {
					currentDay = 28;
				}
				// 如果当前月的前一个月是31天的-1 3 5 7 8 10 12
			} else if (currentMonth == 2 || currentMonth == 4
					|| currentMonth == 6 || currentMonth == 8
					|| currentMonth == 9 || currentMonth == 11
					|| currentMonth == 1) {
				currentDay = 31;
			} else {
				currentDay = 30;
			}
			currentMonth -= 1;// 当前月份减一可能为0，因为存在1月（1 2 3）
			if (currentMonth == 0) {// 说明刚刚减1的月是1月，应该让新的月份到12月，年份减一
				currentMonth = 12;
				currentYear -= 1;
			}
		}

		previousDay.put("YEAR", currentYear);
		previousDay.put("MONTH", currentMonth);
		previousDay.put("DAY_OF_MONTH", currentDay);

		return previousDay;
	}

	// 返回一个日期的下一天的日期，返回类型是hashmap
	public static HashMap<String, Integer> getNextDay(int currentYear,
			int currentMonth, int currentDay) {
		HashMap<String, Integer> nextDay = new HashMap<String, Integer>();

		int dayLimit = 0;
		switch (currentMonth) {// 获取当月天数的上限
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

		if (currentDay < dayLimit) {// 如果当天不是当月的最后一天
			currentDay += 1;// 日数直接加一天
		} else {// 如果是当月最后一天
			currentDay = 1;
			if (currentMonth != 12) {// 如果月份不是12月
				currentMonth += 1;// 月份直接加1
			} else {// 如果月份是12月
				currentMonth = 1;// 月份变为1
				currentYear += 1;// 年份加1
			}
		}

		nextDay.put("YEAR", currentYear);
		nextDay.put("MONTH", currentMonth);
		nextDay.put("DAY_OF_MONTH", currentDay);

		return nextDay;
	}

	// 判断是否为闰年
	public static boolean isLeapYear(int year) {
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}
}
