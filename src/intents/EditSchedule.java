/*
 * �༭����ӿγ���Ϣ��ͨ�����ļ�ʵ�֣���ӵĻ���ֱ����ԭ���б������һ����Ŀ��д���ļ���
 * �༭����ֱ���޸ĺ�д���ļ�
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

	// ������ÿ����ѡ����ʾ�����ݣ�����ͼ���ʾ�� 1,2,3,4,5����ʾ�����ݱ������ַ��������ܸ���������
	String[] weekListString = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" };
	// ���Ĭ��״̬ѡ����Щ��ǩ������Ĭ��ǰ15���Ѿ�ѡ����ˣ����Ҫѡ��ĳ����ǩ���뽫��ֵ��Ϊtrue
	boolean[] choosed = { true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true, false, false, false,
			false, false };
	
	private final String[] testTypes= {"δ֪", "����", "����"};
	private final String[] subjectAttrs = {"δ֪", "����", "ѡ��"};

	private String selectTestType = testTypes[0];
	private String selectSubjectAttr = subjectAttrs[0];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_subject);

		// ��ȡ�����ϵ����а�ť���ı��༭��
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

		// ���մ���һ������ݹ���������
		Intent intent = getIntent();
		dayOfWeek = intent.getIntExtra("weekday", 0);
		order = intent.getIntExtra("time", 0);
		action = intent.getStringExtra("action");

		subjects = MainActivity.subjects;// ��ȡ�γ��б�
		for (int i = 0; i < choosed.length; i++) {
			if (choosed[i]) {
				newWeekSet.add(i + 1);
			}
		}

		explicttimEditText.setText("��" + dayOfWeek + "��" + order + "��");// ��ʾ�����޸ĵĿγ��ǵڼ��ܣ��ڼ���
		explicttimEditText.setKeyListener(null);// ��ֹ�޸ĵڼ��ܵڼ��ڵ�����������״̬

		// ���ݴ��ݹ����Ĳ����̱��β������޸Ļ�����ӿγ���Ϣ
		if (action.equals("add")) {
			setTitle("�����" + dayOfWeek + "��" + order + "��");
			addButton.setText("���");
		} else {
			setTitle("�޸���" + dayOfWeek + "��" + order + "��");
			ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10
					+ order);// ��ȡָ���ճ̵Ŀγ�
			index = intent.getIntExtra("item", 0);// ��ȡ���б��еĵڼ���

			Log.e("get subjectArraylist", subjectArrayList.toString());

			newWeekSet = subjectArrayList.get(index).getWeeks();

			// �趨TextViewĬ����ʾ�Ŀγ�����
			Log.e("get index", index + "");
			Log.e("get name", subjectArrayList.get(index).getName());
			classNameEditText.setText(subjectArrayList.get(index).getName());
			// ���ÿγ�����Ĭ����ʾ���Ͽεص�
			classLocationEditText.setText(subjectArrayList.get(index)
					.getLocation());
			
			selectTestType = subjectArrayList.get(index).getTestAttr();
			
			selectSubjectAttr = subjectArrayList.get(index).getSubjectAttr();
			
			// show default teacher name
			teacherEditText.setText(subjectArrayList.get(index).getTeacher());
			for (int i = 0; i < choosed.length; i++) {
				choosed[i] = false;
			}
			// �趨��ѡ�����Ĭ���Ѿ�ѡ�������
			Iterator<Integer> iterator = newWeekSet.iterator();
			while (iterator.hasNext()) {
				choosed[(Integer) iterator.next() - 1] = true;
				;
			}
			addButton.setText("�޸�");
		}

		// �����趨�����İ�ť
		weekPicker.setOnClickListener(new OnClickListener() {
			// ���������趨��������ô�͵���getWeekList��������Ӧ������ȡ����
			public void onClick(View v) {
				getWeeks();
			}
		});

		// ����������ݵİ�ť
		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ������������
				classLocationEditText.setText("");
				classNameEditText.setText("");

				// ��ΪĬ�����
				for (int i = 0; i < choosed.length; i++) {
					if (i > 14) {
						choosed[i] = false;
					} else {
						choosed[i] = true;
					}
				}
			}
		});

		// ������Ӱ�ť��״̬, �����޸Ĳ���
		addButton.setOnClickListener(new OnClickListener() {// ��ӿγ�
					public void onClick(View v) {
						ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10 + order);
						if (subjectArrayList == null) {
							subjectArrayList = new ArrayList<Subject>();
						}
						
						Subject subject = new Subject();//ʹ���µĶ��󱣴���Ϣ
						
						if (classNameEditText.getText().toString().trim().length() == 0) {
							waringDialog(1);// ����1��ʾû���γ���
							return;
						}
						
						subject.setName(classNameEditText.getText().toString());
						if (classLocationEditText.getText().toString().trim().length() == 0) {
							waringDialog(2);// ����2��ʾû���Ͽεص�
							return;
						}
						
						String teacher = teacherEditText.getText().toString();
						subject.setTeacher(teacher.trim().length() > 0 ? teacher : "δ����");
						
						subject.setTestAttr(selectTestType);
						
						subject.setDayOfweekAndOrder(dayOfWeek * 10 + order);
						
						subject.setSubjectAttr(selectSubjectAttr);
						
						subject.setLocation(classLocationEditText.getText().toString());
						if (newWeekSet.isEmpty()) {
							waringDialog(3);// ����3��ʾû�趨�Ͽ�����
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
							subjects.put(dayOfWeek * 10 + order, subjectArrayList);// �����µĿγ���Ϣ
							FileOperation fileOperation = new FileOperation(
									getApplicationContext());
							// д���ļ�
							fileOperation.writeSubjectsFile(subjects);
							tips("�޸ĳɹ�");// �����Ի�����ʾ
						} else {
							Toast.makeText(getApplicationContext(), "����ӵ�\"" + subject.getName() + "\"��\"" + temp.getName() + "��" + conflictWeek.toString() + "��ͻ", Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	private void waringDialog(int waringCode) {
		AlertDialog.Builder waring = new AlertDialog.Builder(this);
		waring.setTitle("����");
		if (waringCode == 1) {
			waring.setMessage("����д�γ���");
		} else if (waringCode == 2) {
			waring.setMessage("����д�Ͽεص�");
		} else if (waringCode == 3) {
			waring.setMessage("������ѡ��һ�����ϵ��Ͽ�����");
		}

		waring.setPositiveButton("��֪����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		waring.create().show();
	}

	// ���÷��ؼ��ļ�����ͨ������ֱ�ӻص���ҳ��
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();// ������ǰ�
			Intent intent = new Intent(getApplicationContext(),
					ModifyContent.class);
			startActivity(intent);// �������
		}
		return super.onKeyDown(keyCode, event);
	}

	// ����ѡ���б�ѡ��������1-20�ܵ��Ͽ�����
	public void getWeeks() {
		// ��������ѡ��Ի���
		Builder pickWeek = new AlertDialog.Builder(this);
		pickWeek.setTitle("�Ͽ�����");

		// ����ѡ�����
		pickWeek.setMultiChoiceItems(weekListString, choosed,
				new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// ����Ӧѡ���˵ı�ǩ�±���ΪisChecked(true)
						choosed[which] = isChecked;
					}
				});

		// �û�ȷ��ѡ������ݺ�
		pickWeek.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ����Boolean���飬��ֵΪTRUE��ÿһ���±걣�浽weekListArrayList��ʾ�Ͽε�������д���ļ�
				newWeekSet.clear();
				for (int i = 1; i <= 20; i++) {
					if (choosed[i - 1] == true) {
						newWeekSet.add(new Integer(i));
					}
				}
				dialog.dismiss();
			}
		});

		// ���ȡ���ǲ�������
		pickWeek.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		pickWeek.show();
	}

	// �����Ի�����ʾ�޸ĳɹ�
	private void tips(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ʾ");
		builder.setMessage(message);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();// ������ǰ�
				MainActivity.subjects = subjects;
				Intent intent = new Intent(getApplicationContext(),
						ModifyContent.class);
				startActivity(intent);// �ص��������ʵ�����ݵ���������
			}
		});
		builder.show();
	}
}
