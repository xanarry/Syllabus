/*
 * 编辑和添加课程信息都通过此文件实现，添加的话是直接在原有列表中添加一个项目后写入文件，
 * 编辑就是直接修改后写入文件
 */
package intents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import tools.FileOperation;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import basicaldefine.Subject;

import com.example.syllabus.R;
import com.syllabus.MainActivity;

@SuppressLint({ "UseValueOf", "UseSparseArrays" })
public class EditSchedule extends Activity {

	private int dayOfWeek = 1;
	private int order = 1;
	private int index = 0;
	private String action;
	private Button weekPicker;
	private Button clearButton;
	private Button addButton;
	private EditText classNameEditText;
	private EditText classLocationEditText;
	private EditText explicttimEditText;
	private EditText teacherEditText;
	private Spinner subjectAttrSelector;
	private Spinner testTypeSelector;

	private TreeSet<Integer> newWeekSet = new TreeSet<Integer>();
	private HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();

	// 定义在每个单选框显示的数据，这里就简单显示了 1,2,3,4,5，显示的数据必须是字符串，不能给其他类型
	String[] weekListString = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" };
	// 标记默认状态选中那些标签，这里默认前15周已经选择好了，如果要选中某个标签，请将其值改为true
	boolean[] choosed = { true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true, false, false, false,
			false, false };
	
	private final String[] testTypes= {"未知", "考试", "考查"};
	private final String[] subjectAttrs = {"未知", "必修", "选修"};

	private String selectTestType = testTypes[0];
	private String selectSubjectAttr = subjectAttrs[0];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_subject);

		// 获取布局上的所有按钮和文本编辑框
		weekPicker = (Button) findViewById(R.id.text_weekRange);
		clearButton = (Button) findViewById(R.id.clear);
		addButton = (Button) findViewById(R.id.add);
		classNameEditText = (EditText) findViewById(R.id.text_className);
		classLocationEditText = (EditText) findViewById(R.id.text_classLocation);
		explicttimEditText = (EditText) findViewById(R.id.text_classTime);
		teacherEditText = (EditText) findViewById(R.id.text_teacher);
		subjectAttrSelector = (Spinner) findViewById(R.id.spiner_subjectAttr);
		testTypeSelector = (Spinner) findViewById(R.id.spiner_testType);

		ArrayAdapter<String> testTypeaAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, testTypes);
		testTypeaAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		testTypeSelector.setAdapter(testTypeaAdapter);
		testTypeSelector.setVisibility(View.VISIBLE);
		testTypeSelector
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						selectTestType = parent.getItemAtPosition(position)
								.toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		ArrayAdapter<String> subjectAttrAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, subjectAttrs);
		subjectAttrAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subjectAttrSelector.setAdapter(subjectAttrAdapter);
		subjectAttrSelector.setVisibility(View.VISIBLE);
		subjectAttrSelector
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						selectSubjectAttr = parent.getItemAtPosition(position)
								.toString();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		// 接收从上一个活动传递过来的数据
		Intent intent = getIntent();
		dayOfWeek = intent.getIntExtra("weekday", 0);
		order = intent.getIntExtra("time", 0);
		action = intent.getStringExtra("action");

		subjects = MainActivity.subjects;// 获取课程列表
		for (int i = 0; i < choosed.length; i++) {
			if (choosed[i]) {
				newWeekSet.add(i + 1);
			}
		}

		explicttimEditText.setText("周" + dayOfWeek + "第" + order + "节");// 显示正在修改的课程是第几周，第几节
		explicttimEditText.setKeyListener(null);// 禁止修改第几周第几节的输入框的输入状态

		// 根据传递过来的参数盘本次操作是修改还是添加课程信息
		if (action.equals("add")) {
			setTitle("添加周" + dayOfWeek + "第" + order + "节");
			addButton.setText("添加");
		} else {
			setTitle("修改周" + dayOfWeek + "第" + order + "节");
			ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10
					+ order);// 获取指定日程的课程
			index = intent.getIntExtra("item", 0);// 获取是列表中的第几节

			Log.e("get subjectArraylist", subjectArrayList.toString());

			newWeekSet = subjectArrayList.get(index).getWeeks();

			// 设定TextView默认显示的课程名字
			Log.e("get index", index + "");
			Log.e("get name", subjectArrayList.get(index).getName());
			classNameEditText.setText(subjectArrayList.get(index).getName());
			// 设置课程名字默认显示的上课地点
			classLocationEditText.setText(subjectArrayList.get(index)
					.getLocation());
			
			selectTestType = subjectArrayList.get(index).getTestAttr();
			
			selectSubjectAttr = subjectArrayList.get(index).getSubjectAttr();
			
			// show default teacher name
			teacherEditText.setText(subjectArrayList.get(index).getTeacher());
			for (int i = 0; i < choosed.length; i++) {
				choosed[i] = false;
			}
			// 设定周选择框中默认已经选择的内容
			Iterator<Integer> iterator = newWeekSet.iterator();
			while (iterator.hasNext()) {
				choosed[(Integer) iterator.next() - 1] = true;
				;
			}
			addButton.setText("修改");
		}

		// 监听设定周数的按钮
		weekPicker.setOnClickListener(new OnClickListener() {
			// 如果点击了设定周数，那么就调用getWeekList函数做回应，并获取数据
			public void onClick(View v) {
				getWeeks();
			}
		});

		// 监听清空数据的按钮
		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 清空所用输入框
				classLocationEditText.setText("");
				classNameEditText.setText("");

				// 置为默认情况
				for (int i = 0; i < choosed.length; i++) {
					if (i > 14) {
						choosed[i] = false;
					} else {
						choosed[i] = true;
					}
				}
			}
		});

		// 监听添加按钮的状态, 保存修改操作
		addButton.setOnClickListener(new OnClickListener() {// 添加课程
					public void onClick(View v) {
						ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10 + order);
						if (subjectArrayList == null) {
							subjectArrayList = new ArrayList<Subject>();
						}
						
						Subject subject = new Subject();//使用新的对象保存信息
						
						if (classNameEditText.getText().toString().trim().length() == 0) {
							waringDialog(1);// 参数1表示没数课程名
							return;
						}
						
						subject.setName(classNameEditText.getText().toString());
						if (classLocationEditText.getText().toString().trim().length() == 0) {
							waringDialog(2);// 参数2表示没数上课地点
							return;
						}
						
						String teacher = teacherEditText.getText().toString();
						subject.setTeacher(teacher.trim().length() > 0 ? teacher : "未定义");
						
						subject.setTestAttr(selectTestType);
						
						subject.setDayOfweekAndOrder(dayOfWeek * 10 + order);
						
						subject.setSubjectAttr(selectSubjectAttr);
						
						subject.setLocation(classLocationEditText.getText().toString());
						if (newWeekSet.isEmpty()) {
							waringDialog(3);// 参数3表示没设定上课周数
							return;
						}
						
						subject.setWeeks(newWeekSet);
						Subject temp = null;
						ArrayList<Integer> conflictWeek = new ArrayList<Integer>();
						if (action.equals("edit")) {
							subjectArrayList.remove(index);
						} else {
							//check conflict
							for (Subject x : subjectArrayList) {
								for (int y : subject.getWeeks()) {
									if (x.getWeeks().contains(y)) {
										temp = x;
										conflictWeek.add(y);
									}
								}
								if (temp != null)
									break;
							}
						}
						
						if (temp == null) {
							subjectArrayList.add(subject);
							subjects.put(dayOfWeek * 10 + order, subjectArrayList);// 插入新的课程信息
							FileOperation fileOperation = new FileOperation(
									getApplicationContext());
							// 写入文件
							fileOperation.writeSubjectsFile(subjects);
							tips("修改成功");// 给出对话框提示
						} else {
							Toast.makeText(getApplicationContext(), "您添加的\"" + subject.getName() + "\"与\"" + temp.getName() + "在" + conflictWeek.toString() + "冲突", Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	private void waringDialog(int waringCode) {
		AlertDialog.Builder waring = new AlertDialog.Builder(this);
		waring.setTitle("警告");
		if (waringCode == 1) {
			waring.setMessage("请填写课程名");
		} else if (waringCode == 2) {
			waring.setMessage("请填写上课地点");
		} else if (waringCode == 3) {
			waring.setMessage("您必须选择一周以上的上课周数");
		}

		waring.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		waring.create().show();
	}

	// 设置返回键的监听，通过返回直接回到主页面
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();// 结束当前活动
			Intent intent = new Intent(getApplicationContext(),
					ModifyContent.class);
			startActivity(intent);// 启动主活动
		}
		return super.onKeyDown(keyCode, event);
	}

	// 定单选框列表，选择数据是1-20周的上课周数
	public void getWeeks() {
		// 弹出周数选择对画框
		Builder pickWeek = new AlertDialog.Builder(this);
		pickWeek.setTitle("上课周数");

		// 设置选择监听
		pickWeek.setMultiChoiceItems(weekListString, choosed,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// 将相应选中了的标签下标标记为isChecked(true)
						choosed[which] = isChecked;
					}
				});

		// 用户确定选择的数据后
		pickWeek.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 遍历Boolean数组，将值为TRUE的每一个下标保存到weekListArrayList表示上课的周数，写入文件
				newWeekSet.clear();
				for (int i = 1; i <= 20; i++) {
					if (choosed[i - 1] == true) {
						newWeekSet.add(new Integer(i));
					}
				}
				dialog.dismiss();
			}
		});

		// 点击取消是不做操作
		pickWeek.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		pickWeek.show();
	}

	// 弹出对话框提示修改成功
	private void tips(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage(message);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();// 结束当前活动
				MainActivity.subjects = subjects;
				Intent intent = new Intent(getApplicationContext(),
						ModifyContent.class);
				startActivity(intent);// 回到主活动，以实现数据的立即更新
			}
		});
		builder.show();
	}
}
