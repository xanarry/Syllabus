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
	// ��¼������Ļ����������ǵ���Ļ���꣬�ж��ֵĻ�������
	private float x1 = 0;
	@SuppressWarnings("unused")
	private float y1 = 0;
	private float x2 = 0;
	@SuppressWarnings("unused")
	private float y2 = 0;

	// �ɱ�ı���
	private int changingYear;// ��ʾ��
	private int changingMonth;// ��ʾ��
	private int changingDay;// ��ʾ��
	private int changingWeek;// ��ʾ��
	private int changingDayOfWeek;// ��ʾ���ڼ�

	// ��������������Ϣ�����������գ�key: "YEAR", "MONTH", "DAY_OF_WEEK"
	private HashMap<String, Integer> today = new HashMap<String, Integer>();

	// ��������ǵڼ������ڼ���key: "WEEK", "DAY_OF_WEEK"
	private HashMap<String, Integer> todayWDOW = new HashMap<String, Integer>();

	// ���浱����Ŀγ���Ϣ������ÿһ�ڿ��������ڼ�*10+�ڼ��ڵ���ֵ��Ϊmap��key�����ﱣ���ڿ�
	private ArrayList<Map<String, Object>> scheduleList = new ArrayList<Map<String, Object>>();

	// �������Ŀγ���Ϣ�����������в����
	private ArrayList<Subject> todaySchedule = new ArrayList<Subject>();
	private ArrayList<Subject> changingSchedule = new ArrayList<Subject>();

	private String title;// ��������
	private ListView listView;// ��ȡlistview�������趨����

	// subjects �������пγ���Ϣ
	public static HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();

	public static String startTime = "";// ���濪ѧʱ��20130812����ʽ�����ڼ���������Ĭ���趨Ϊ��

	private NumberPicker numberPicker;// ��������ѡ����
	private FileOperation fileOperation = null;// �����ļ���������

	private int dayCounter = 0;// ������������һ����Ĵ������������ƻ�������

	public static int backgroundId = R.drawable.bg_17;// ��Ǳ���ͼƬ��λ�ã�Ĭ��Ϊ�̶��ĵ�17��ͼ

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fileOperation = new FileOperation(getApplicationContext());// ʵ�����ļ�������

		// ����ļ������ԣ��Ƿ��ʼ����ɣ�
		FileChecker checkFile = new FileChecker(getApplicationContext());
		Log.e("debug", "check file existense");
		if (!checkFile.checkFilesExistence()) {
			// ����ļ�������������update����ӽ��������ļ�
			Log.e("debug", "file not exist update");
			Intent UpdateInformation = new Intent(getApplicationContext(),
					intents.Update.class);
			startActivity(UpdateInformation);
		} else {
			Log.e("debug", "file is existed loading");
			// �ļ�����������ļ�
			subjects = fileOperation.readSubjectsFile();// ��ȡ�γ̱�����
			Log.e("debug", "get subjects: size = " + subjects.size());

			// ��ȡ��ѧʱ��
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

		// ��������������Ϣ�����ᱻ�޸�
		today = TimeCalculator.getCurrentDayInfo();
		// ��ȡ�����������Ϣ
		todayWDOW = TimeCalculator.getWeekAndDayOfweek(today.get("YEAR"),
				today.get("MONTH"), today.get("DAY_OF_MONTH"));

		pickedNewWeek = todayWDOW.get("WEEK");
		todaySchedule = ScheduleOfDay.getScheduleOf(pickedNewWeek,
				todayWDOW.get("DAY_OF_WEEK"));

		// ��ʼ���ɱ���Ϣ
		changingYear = today.get("YEAR");
		changingMonth = today.get("MONTH");
		changingDay = today.get("DAY_OF_MONTH");
		changingWeek = todayWDOW.get("WEEK");
		changingDayOfWeek = todayWDOW.get("DAY_OF_WEEK");

		// ���ó�ʼ��Ϊ����ı���
		title = String.format("[����] %02d��%02d�� ��%02d����%d", today.get("MONTH"),
				today.get("DAY_OF_MONTH"), todayWDOW.get("WEEK"),
				todayWDOW.get("DAY_OF_WEEK"));

		backgroundId = BackgroundSelector.getBackground(today
				.get("DAY_OF_MONTH"));// ���ݽ������������趨����ͼƬ

		// ��ȡlistview�����������������һ�����ָ�ļ����¼������ݴ���
		listView = getListView();

		listView.setBackgroundResource(backgroundId);// ���ñ���

		listView.setDividerHeight(5);// ���ø�itemֱ�ӵĿ��

		// ����ÿ����item��ı���
		listView.setDivider(getApplicationContext().getResources().getDrawable(
				R.drawable.diver));

		// showSubjectList����������ʾ���������ڵ�������Ϣ
		showSubjectList(title, todaySchedule);

		// ##����Ҫ��MLISTVIEW�����ü���������ֱ����Mainactivity�����ü�����������listview�Ḳ�ǵ�����ʹ���õļ�����Ч
		listView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// ����ָ���µ�ʱ���ȡ�����
					x1 = event.getX();
					y1 = event.getY();
				}

				if (event.getAction() == MotionEvent.ACTION_UP) {
					// ����ָ�뿪��ʱ���ȡ�յ�����
					x2 = event.getX();
					y2 = event.getY();
					if (x1 - x2 > 70 && dayCounter < 7) { // ����߻�������ʾ��һ��Ŀα���������ʾ7��
						dayCounter++;
						if (dayCounter < 0) {// �����������֮ǰ��һ��
							// ���ð�ɫ����
							listView.setBackgroundResource(R.drawable.passed);
						} else {
							// ��ԭԭ�б���
							listView.setBackgroundResource(backgroundId);
						}

						// TimeCalculator.getNextDay����ĳ�����ڵ���һ�������
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

						// �����趨��Ҫ��ʾ����Ϣ
						if (changingMonth == today.get("MONTH")
								&& changingDay == today.get("DAY_OF_MONTH")) {
							title = String.format("[����] %02d��%02d�� ��%02d����%d",
									today.get("MONTH"),
									today.get("DAY_OF_MONTH"),
									todayWDOW.get("WEEK"),
									todayWDOW.get("DAY_OF_WEEK"));
						} else {
							title = String.format("%02d��%02d�� ��%02d����%d",
									changingMonth, changingDay, changingWeek,
									changingDayOfWeek);
						}

						// ��ȡ��һ��Ŀγ�
						ArrayList<Subject> tSchedule = ScheduleOfDay
								.getScheduleOf(changingWeek, changingDayOfWeek);

						// ���ú���������Ļ����
						showSubjectList(title, tSchedule);///////////////////////

					} else if (x2 - x1 > 70 && dayCounter > -1) { // ���ұ߻�������ʾǰ��Ŀγ�
						dayCounter--;
						if (dayCounter < 0) {
							// �����������֮ǰ��һ�죬���ð�ɫ����
							listView.setBackgroundResource(R.drawable.passed);
						} else {
							// ��ԭ����
							listView.setBackgroundResource(backgroundId);
						}
						// TimeCalculator.getPreviousDay����ĳ�����ڵ�ǰһ�������
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

						// ���»�ȡ����
						if (changingMonth == today.get("MONTH")
								&& changingDay == today.get("DAY_OF_MONTH")) {
							title = String.format("[����] %02d��%02d�� ��%02d����%d",
									today.get("MONTH"),
									today.get("DAY_OF_MONTH"),
									todayWDOW.get("WEEK"),
									todayWDOW.get("DAY_OF_WEEK"));
						} else {
							title = String.format("%02d��%02d�� ��%02d����%d",
									changingMonth, changingDay, changingWeek,
									changingDayOfWeek);
						}

						ArrayList<Subject> tSchedule = ScheduleOfDay
								.getScheduleOf(changingWeek, changingDayOfWeek);

						// ˢ����ʾ����
						showSubjectList(title, tSchedule);

					} else if (Math.abs(x2 - x1) > 70) {// ����������Ƶ�����
						tipsDialog();// ������ʾ
					}
				}
				return false;
			}
		});
	}

	// �����ֱ��Ǳ��⣬ÿ���γ���Ŀ�����ֺͿγ̵���ϸ��Ϣ
	private void showSubjectList(String title,
			final ArrayList<Subject> currentDaySchedule) {
		changingSchedule = currentDaySchedule;
		scheduleList.clear();// ����ϴ���ʾ�����ݣ���Ȼ�µ����ݺ�ֱ����ӵ�ԭ������֮��
		title = title.charAt(title.length() - 1) == '7' ? title.substring(0, title.length() - 1) + "��" : title;
		setTitle(title);// ���ñ�����ʾʱ��

		// ��������Ŀγ̹��������Ϣ
		Iterator<Subject> iterator = currentDaySchedule.iterator();
		while (iterator.hasNext()) {
			Subject subject = (Subject) iterator.next();
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("order",
					String.valueOf(subject.getDayOfweekAndOrder() % 10));
			item.put("name", subject.getName());
			item.put("location", subject.getLocation() != "" ?  "�ص�:  " + subject.getLocation() : "");
			item.put(
					"time",
					Constants.CLASS_TIME[subject.getDayOfweekAndOrder() % 10 - 1]);////////////////////////////
			scheduleList.add(item);
		}

		// ���õ��item�ļ��������������ʾ�ÿγ̵���ϸ��Ϣ
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ���ݸÿγ̵���Ϣ���ú��������Ի�����ʾ
				if (currentDaySchedule.get(position).getName() != "") {//����û�пε���Ŀ�����ü���
					showDetail(position, changingWeek, changingDayOfWeek);
				}
			}
		});
		listView.setCacheColorHint(0);

		// ʹ��adapter�������벼��ƴ��һ��
		SubjectList adapter = new SubjectList(this, scheduleList, R.layout.list_item, 
			new String[] { "order", "name", "location", "time" }, 
			new    int[] { R.id.list_order, R.id.list_name,
						   R.id.list_location, R.id.list_time });
		
		setListAdapter(adapter);// ������ʾ
	}

	// �����Զ����adapter����listview����
	public class SubjectList extends SimpleAdapter {
		private String[] basicColor = { "#CCFFCC", "#CCFFFF" };// ����������ɫ��item��ʾ�н���任
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
			int colorLength = basicColor.length;// ��ȡ��ɫ����

			// ���ݵ�ǰitem��index��ȡ������ɫ
			int selected = Color.parseColor(arrayOfInt[position % colorLength]);

			View localView = super.getView(position, convertView, parent);
			// ������ɫ
			
			Log.e("color index", position + " : " + changingSchedule.get(position).getName());
			Subject curSubject = changingSchedule.get(position);/////////////////////////////
				
				
			localView.setBackgroundColor(selected);
			localView.getBackground().setAlpha(200);// ����͸����
			if (curSubject.getName() == "") {
				localView.setBackgroundColor(Color.parseColor(gray));
				localView.getBackground().setAlpha(400);// ����͸����
			}
			return localView;
		}
	}

	@Override
	// ��ʾ���ò˵����������
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 4, 3, "�����ܴ�");
		menu.add(Menu.NONE, 3, 4, "�༭�γ�");
		menu.add(Menu.NONE, 2, 6, "�����γ�");
		menu.add(Menu.NONE, 1, 7, "�ܿγ̱�");
		menu.add(Menu.NONE, 6, 2, "������Ϣ");
		menu.add(Menu.NONE, 7, 1, "���ڴ����");
		return true;
	}

	// �����˵�Ŀ¼�µ�item����¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case 1:// ������ܿα�
			Intent WeekSchedule = new Intent(getApplicationContext(),
					WeekSchedule.class);
			startActivity(WeekSchedule);
			return true;

		case 2:// ��������
			Intent searchSyllabusIntent = new Intent(getApplicationContext(),
					intents.SearchSchedule.class);
			startActivity(searchSyllabusIntent);
			return true;

		case 3:// �༭
				// finish();//�رո�������ֹ���ӻ�з��غ�Ҫ�����������˳������˳�
			Intent ModifyContent = new Intent(getApplicationContext(),
					intents.ModifyContent.class);
			startActivity(ModifyContent);
			return true;

		case 4:// �趨�µ�����
				// finish();//�رո�������ֹ���ӻ�з��غ�Ҫ�����������˳������˳�
			setNewWeekDialog();
			return true;

		case 6:// ������Ϣ
			Intent UpdateInformation = new Intent(getApplicationContext(),
					intents.Update.class);
			startActivity(UpdateInformation);
			return true;

		case 7:// ����
			Intent Copyright = new Intent(getApplicationContext(),
					intents.Copyright.class);
			startActivity(Copyright);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// //////////�˳��ĵ����˵����뿪ʼ��////////////////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// �����ֻ��ķ��ذ���
			dialog();// ���������������������
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// �����Ի�����ʾ��ϸ��Ϣ �ڼ��ڿ� �� ���ڼ�
	protected void showDetail(int position, int week, int dayOfWeek) {
		AlertDialog.Builder detail = new AlertDialog.Builder(this);// ���嵯����
		// ��ȡ����γ���Ϣ
		ArrayList<Subject> currendDaySchedul = ScheduleOfDay.getScheduleOf(
				week, dayOfWeek);
		// ��ȡ����ĳ�ڿγ�
		Subject subject = currendDaySchedul.get(position);
		// ���ÿγ���Ϊ����
		detail.setTitle(subject.getName());

		// ����ʣ������
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
			restWeek = "�Ѿ����";
		}

		// �趨��Ϣ
		detail.setMessage("�Ͽεص�:    " + subject.getLocation() + "\nʣ������:    "
				+ restWeek + "\n�γ�����:    " + subject.getSubjectAttr()
				+ "\n��������:    " + subject.getTestAttr() + "��\n��        ʦ:    "
				+ subject.getTeacher());
		// ����ȷ����ť
		detail.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// ֱ�ӹرնԻ���
					}
				});
		detail.create().show();// ��ʾ�Ի���
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);// ���嵯����
		builder.setMessage("ddup��ȷ���˳�");// ������Ϣ����
		builder.setTitle("��ʾ");// ���ñ���
		builder.setPositiveButton("ȷ��",// ����ȷ������ʾ�����ݼ������Ĳ���
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						dialog.dismiss();// �˳��Ի���
						// �ر��������
						android.os.Process.killProcess(android.os.Process
								.myPid()); // ��ȡPID
						System.exit(0);
					}
				});
		builder.setNegativeButton("ȡ��",// ����ȡ������Ϣ
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// ֱ�ӹرնԻ���
					}
				});
		builder.create().show();
	}

	// //////////�˳��ĵ����˵������β��////////////////////

	protected void tipsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);// ���嵯����
		builder.setMessage("ֻ����鿴ǰһ������7��Ŀγ�");// ������Ϣ����
		builder.setTitle("��ʾ");// ���ñ���

		builder.setNegativeButton("��֪����",// ����ȡ������Ϣ
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// ֱ�ӹرնԻ���
					}
				});
		builder.create().show();
	}

	public void setNewWeekDialog() {
		Dialog dialog = new Dialog(MainActivity.this);
		View npView = getLayoutInflater().inflate(R.layout.new_week, null);

		// �趨����ѡ����
		numberPicker = (NumberPicker) npView.findViewById(R.id.picked_week);
		// �趨��Χ
		numberPicker.setMaxValue(20);
		numberPicker.setMinValue(1);
		int currentWeek = TimeCalculator.getWeekAndDayOfweek(today.get("YEAR"),
				today.get("MONTH"), today.get("DAY_OF_MONTH")).get("WEEK");
		pickedNewWeek = currentWeek;

		numberPicker.setValue(currentWeek);// �趨Ĭ��ֵ

		numberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				pickedNewWeek = newVal;
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ѡ���µ�����,���ý���������Ч");
		builder.setView(npView);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
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
						"�ѽ���ǰ����Ϊ��" + pickedNewWeek + "�ܣ� �´�������Ч",
						Toast.LENGTH_LONG).show();
			}
		});

		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}
}