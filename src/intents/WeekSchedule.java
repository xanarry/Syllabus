package intents;

import java.util.ArrayList;
import java.util.HashMap;

import tools.TimeCalculator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import basicaldefine.Subject;

import com.example.syllabus.R;
import com.syllabus.MainActivity;

@SuppressLint("NewApi")
public class WeekSchedule extends Activity {
	private float x1, x2, y1, y2;// 标记坐标，判断手指的滑动方向
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();
	private HashMap<String, Integer> today = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentWeekAndDayOfWeek = new HashMap<String, Integer>();
	private Integer changedWeek = 1;
	private String title = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		subjects = MainActivity.subjects;// 从主活动中获取课程数据
		today = TimeCalculator.getCurrentDayInfo();// 仅仅返回年月日星期几
		currentWeekAndDayOfWeek = TimeCalculator.getWeekAndDayOfweek(
				today.get("YEAR"), today.get("MONTH"),
				today.get("DAY_OF_MONTH"));

		changedWeek = currentWeekAndDayOfWeek.get("WEEK");

		title = String.format("第%02d周  [当前周]", changedWeek);
		setTitle(title);// 设置标题
		setContentView(R.layout.week_schedule);// 设置布局

		addWegit(0);// 添加课程表格的显示，参数分别是-1， 0， 1，表示前一周，当前周，还是下一周
	}

	// 设置屏幕手势滑动的监听
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 继承了Activity的onTouchEvent方法，直接监听点击事件
		int temp = changedWeek;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 当手指按下的时候
			x1 = event.getX();
			y1 = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// 当手指离开的时候
			x2 = event.getX();
			y2 = event.getY();

			if (x1 - x2 > 40 && (temp + 1) <= 20) {
				// 向左滑动是，调用函数addWegit并传递参数1，列出下一周的课程表
				addWegit(1);
				// 标题上的数据同时做相应更改
				if (temp + 1 == currentWeekAndDayOfWeek.get("WEEK")) {
					title = String.format("第%02d周  [当前周]",
							currentWeekAndDayOfWeek.get("WEEK"));
				} else {
					title = String.format("第%02d周", temp + 1);
				}
				setTitle(title);// 设置标题
			} else if (x2 - x1 > 40 && (temp - 1) > 0) {
				// 向左滑动是，调用函数addWegit并传递参数1，列出下一周的课程表
				addWegit(-1);// ///////////////////////////////////////////////////////////////////////
				if (temp - 1 == currentWeekAndDayOfWeek.get("WEEK")) {
					title = String.format("第%02d周  [当前周]",
							currentWeekAndDayOfWeek.get("WEEK"));
				} else {
					title = String.format("第%02d周", temp - 1);
				}
				setTitle(title);// 设置标题
			} else if (Math.abs(y2 - y1) > 50) {
				addWegit(0);
				title = String.format("第%02d周  [当前周]",
						currentWeekAndDayOfWeek.get("WEEK"));
				setTitle(title);// 设置标题
			}
		}
		return super.onTouchEvent(event);
	}

	// 显示课程表，(动态生成的所有部件)
	@SuppressWarnings("deprecation")
	public void addWegit(int one) {
		// 整个课程列表的布局,生成的部件是放到这个布局框架里面的
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);

		if (one == 0) {
			changedWeek = currentWeekAndDayOfWeek.get("WEEK");
		} else {
			changedWeek += one;// 当前的周加上传递过来的数据，得到新的周数
		}

		// 移除上次显示的数据，不然新的课程格子会继续显示在下方，需要的是刷新，不是增加
		tableLayout.removeAllViews();

		// 全部列自动填充空白处
		tableLayout.setStretchAllColumns(true);

		// 内容居中
		// tableLayout.setGravity(Gravity.CENTER_VERTICAL);

		// Resources定义res为获取每个格子的布局做好准备
		Drawable shape = null;
		Resources res = getResources();
		tableLayout.setBackgroundResource(MainActivity.backgroundId);// //////////////////////////////////////////背景颜色
		for (int order = 1; order <= 5; order++) { // 一个显示5行，表示一天的5节课
			TableRow tableRow = new TableRow(this);// 生成表格的一行
			tableRow.setGravity(Gravity.CENTER);
			for (int dayOfWeek = 0; dayOfWeek <= 7; dayOfWeek++) { // 周一到周六的课程，编号0显示表格最左边一列的课程节数
				TextView textView = new TextView(this);// 生成表格一行中的每一个单项

				if (dayOfWeek == 0) {// 每行的第一列显示第几节课
					shape = res.getDrawable(R.drawable.class_order); // 使用显示的TextView的样式
					textView.setBackground(res.getDrawable(R.drawable.blank));
					textView.setText(String.valueOf(order));// 设置显示的数据
					textView.setGravity(Gravity.CENTER);
					// 设置宽度
					textView.setMaxWidth(5);
					textView.setMinLines(5);
					// 设置颜色
					textView.setTextSize(15);
					textView.setTextColor(Color.parseColor("#FF0033"));

					tableRow.addView(textView); // 添加到行中*/
				} else {// 正常输出课表
					ArrayList<Subject> subjectArrayList = subjects
							.get(dayOfWeek * 10 + order);
					Subject subject = null;
					boolean duplicate = false;
					if (subjectArrayList != null) {
						for (Subject x : subjectArrayList) {
							if (x.getWeeks().contains(changedWeek)) {
								if (subject == null) {
									subject = x;
								} else {
									duplicate = true;
								}
							}
						}
					}

					if (duplicate) {
						Toast.makeText(getApplicationContext(),
								"周" + dayOfWeek + "第" + order + "的课存在冲突, 请修改该",
								Toast.LENGTH_LONG).show();
					}

					if (subject != null) {
						shape = res.getDrawable(R.drawable.subjectbackground);
						textView.setBackground(shape);

						// 设置数据
						String name = subject.getName();////////////////////////////////
						if (subject.getName().length() > 8)
							name = subject.getName().substring(0, 9) + "...";
						textView.setText(name);
						textView.setGravity(Gravity.CENTER);
						textView.getBackground().setAlpha(230);
						textView.setWidth(50);
						textView.setMaxWidth(50);
						textView.setMinLines(5);
						textView.setTextColor(Color.parseColor("#000000"));

						// 添加到行中
						tableRow.addView(textView);
					} else {
						textView.setText("                        ");// 设置几个空格防止应为方块中没有内容而大小被压缩，影响外观
						shape = res
								.getDrawable(R.drawable.nosubujectbackground);
						textView.setBackground(shape);
						textView.setMaxWidth(50);
						textView.getBackground().setAlpha(80);
						textView.setMinLines(5);
						tableRow.addView(textView);
					}
				}
			}
			tableLayout.addView(tableRow);// 新建的TableRow（行）添加到TableLayout（表）
		}
	}
}
