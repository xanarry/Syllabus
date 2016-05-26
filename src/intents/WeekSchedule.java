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
	private float x1, x2, y1, y2;// ������꣬�ж���ָ�Ļ�������
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();
	private HashMap<String, Integer> today = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentWeekAndDayOfWeek = new HashMap<String, Integer>();
	private Integer changedWeek = 1;
	private String title = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		subjects = MainActivity.subjects;// ������л�ȡ�γ�����
		today = TimeCalculator.getCurrentDayInfo();// �����������������ڼ�
		currentWeekAndDayOfWeek = TimeCalculator.getWeekAndDayOfweek(
				today.get("YEAR"), today.get("MONTH"),
				today.get("DAY_OF_MONTH"));

		changedWeek = currentWeekAndDayOfWeek.get("WEEK");

		title = String.format("��%02d��  [��ǰ��]", changedWeek);
		setTitle(title);// ���ñ���
		setContentView(R.layout.week_schedule);// ���ò���

		addWegit(0);// ��ӿγ̱�����ʾ�������ֱ���-1�� 0�� 1����ʾǰһ�ܣ���ǰ�ܣ�������һ��
	}

	// ������Ļ���ƻ����ļ���
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// �̳���Activity��onTouchEvent������ֱ�Ӽ�������¼�
		int temp = changedWeek;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// ����ָ���µ�ʱ��
			x1 = event.getX();
			y1 = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// ����ָ�뿪��ʱ��
			x2 = event.getX();
			y2 = event.getY();

			if (x1 - x2 > 40 && (temp + 1) <= 20) {
				// ���󻬶��ǣ����ú���addWegit�����ݲ���1���г���һ�ܵĿγ̱�
				addWegit(1);
				// �����ϵ�����ͬʱ����Ӧ����
				if (temp + 1 == currentWeekAndDayOfWeek.get("WEEK")) {
					title = String.format("��%02d��  [��ǰ��]",
							currentWeekAndDayOfWeek.get("WEEK"));
				} else {
					title = String.format("��%02d��", temp + 1);
				}
				setTitle(title);// ���ñ���
			} else if (x2 - x1 > 40 && (temp - 1) > 0) {
				// ���󻬶��ǣ����ú���addWegit�����ݲ���1���г���һ�ܵĿγ̱�
				addWegit(-1);// ///////////////////////////////////////////////////////////////////////
				if (temp - 1 == currentWeekAndDayOfWeek.get("WEEK")) {
					title = String.format("��%02d��  [��ǰ��]",
							currentWeekAndDayOfWeek.get("WEEK"));
				} else {
					title = String.format("��%02d��", temp - 1);
				}
				setTitle(title);// ���ñ���
			} else if (Math.abs(y2 - y1) > 50) {
				addWegit(0);
				title = String.format("��%02d��  [��ǰ��]",
						currentWeekAndDayOfWeek.get("WEEK"));
				setTitle(title);// ���ñ���
			}
		}
		return super.onTouchEvent(event);
	}

	// ��ʾ�γ̱�(��̬���ɵ����в���)
	@SuppressWarnings("deprecation")
	public void addWegit(int one) {
		// �����γ��б�Ĳ���,���ɵĲ����Ƿŵ�������ֿ�������
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);

		if (one == 0) {
			changedWeek = currentWeekAndDayOfWeek.get("WEEK");
		} else {
			changedWeek += one;// ��ǰ���ܼ��ϴ��ݹ��������ݣ��õ��µ�����
		}

		// �Ƴ��ϴ���ʾ�����ݣ���Ȼ�µĿγ̸��ӻ������ʾ���·�����Ҫ����ˢ�£���������
		tableLayout.removeAllViews();

		// ȫ�����Զ����հ״�
		tableLayout.setStretchAllColumns(true);

		// ���ݾ���
		// tableLayout.setGravity(Gravity.CENTER_VERTICAL);

		// Resources����resΪ��ȡÿ�����ӵĲ�������׼��
		Drawable shape = null;
		Resources res = getResources();
		tableLayout.setBackgroundResource(MainActivity.backgroundId);// //////////////////////////////////////////������ɫ
		for (int order = 1; order <= 5; order++) { // һ����ʾ5�У���ʾһ���5�ڿ�
			TableRow tableRow = new TableRow(this);// ���ɱ���һ��
			tableRow.setGravity(Gravity.CENTER);
			for (int dayOfWeek = 0; dayOfWeek <= 7; dayOfWeek++) { // ��һ�������Ŀγ̣����0��ʾ��������һ�еĿγ̽���
				TextView textView = new TextView(this);// ���ɱ��һ���е�ÿһ������

				if (dayOfWeek == 0) {// ÿ�еĵ�һ����ʾ�ڼ��ڿ�
					shape = res.getDrawable(R.drawable.class_order); // ʹ����ʾ��TextView����ʽ
					textView.setBackground(res.getDrawable(R.drawable.blank));
					textView.setText(String.valueOf(order));// ������ʾ������
					textView.setGravity(Gravity.CENTER);
					// ���ÿ��
					textView.setMaxWidth(5);
					textView.setMinLines(5);
					// ������ɫ
					textView.setTextSize(15);
					textView.setTextColor(Color.parseColor("#FF0033"));

					tableRow.addView(textView); // ��ӵ�����*/
				} else {// ��������α�
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
								"��" + dayOfWeek + "��" + order + "�Ŀδ��ڳ�ͻ, ���޸ĸ�",
								Toast.LENGTH_LONG).show();
					}

					if (subject != null) {
						shape = res.getDrawable(R.drawable.subjectbackground);
						textView.setBackground(shape);

						// ��������
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

						// ��ӵ�����
						tableRow.addView(textView);
					} else {
						textView.setText("                        ");// ���ü����ո��ֹӦΪ������û�����ݶ���С��ѹ����Ӱ�����
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
			tableLayout.addView(tableRow);// �½���TableRow���У���ӵ�TableLayout����
		}
	}
}
