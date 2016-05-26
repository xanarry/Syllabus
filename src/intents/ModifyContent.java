/*
 * �������Ҫ�������ܹ����������еĳ����¼�����������Ӧ�Ĳ˵����û�ѡ���Ǳ༭����ɾ����
 * ���б༭����������һ�����ɾ�����ڱ������ɣ���Щ��������ͨ�����������ʵ��
 * �����������ݵ�
 */
package intents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

import tools.FileOperation;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import basicaldefine.Subject;

import com.example.syllabus.R;
import com.syllabus.MainActivity;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ModifyContent extends Activity {
	private HashMap<Integer, ArrayList<Subject>> subjects;

	private String[] items;// ��ѡ�����ʾ�б�
	private boolean[] checkedItems;// ��ѡ��ѡ��״̬�б�
	private ArrayList<Subject> subjectArrayListForEdit;
	private int selectIndex = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_schedule);
		setTitle("���пγ�");
		subjects = MainActivity.subjects;// ��ȡ�γ���Ϣ

		// ���ú�����ʾ�γ��б�
		editSyllabus();

	}

	@SuppressWarnings("deprecation")
	public void editSyllabus() {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);// �����γ��б�Ĳ���
		// ȫ�����Զ����հ״�
		tableLayout.setStretchAllColumns(true);

		Drawable shape = null;
		Resources res = getResources();

		// �趨������ɫ���������ͳһ
		tableLayout.setBackgroundResource(MainActivity.backgroundId);

		// ʹ��ѭ��̬�����б�
		for (int order = 1; order <= 5; order++) {
			TableRow tableRow = new TableRow(this);// ����һ��
			tableRow.setGravity(Gravity.CENTER);// �趨���뷽ʽ

			for (int dayOfWeek = 0; dayOfWeek <= 7; dayOfWeek++) {
				TextView textView = new TextView(this);// ���һ���е�ÿһ������

				if (dayOfWeek == 0) {// ÿ�еĵ�һ����ʾ�ڼ��ڿ�
					textView.setBackground(res.getDrawable(R.drawable.blank));
					textView.setText(String.valueOf(order));// ������ʾ������
					textView.setWidth(5);
					textView.setGravity(Gravity.CENTER);
					// ���ÿ��
					textView.setMaxWidth(5);
					textView.setMinLines(5);
					// ������ɫ
					textView.setTextColor(Color.parseColor("#FF0033"));
					tableRow.addView(textView);
				} else {// ��������α�
					ArrayList<Subject> subjectArrayList = subjects
							.get(dayOfWeek * 10 + order);
					if (subjectArrayList != null && subjectArrayList.size() > 0) {
						// �趨����
						String text = "";
						final int totalLen = 8;
						if (subjectArrayList.size() > 1) {// ���ͬһ�������ж�ڿ�
							int subLen = totalLen / subjectArrayList.size();
							textView.setBackground(res.getDrawable(R.drawable.duplicate_bg));
							for (int i = 0; i < subjectArrayList.size() - 1; i++) {
								text += subjectArrayList.get(i).getName()
										.substring(0, subLen)
										+ "&";
							}
							text += subjectArrayList.get(
									subjectArrayList.size() - 1).getName().substring(0, subLen) + "...";
						} else {// ͬһʱ��ֻ��һ�ڿ�
							// ��ȡ�Ͽ������ķ�Χ
							textView.setBackground(res.getDrawable(R.drawable.subjectbackground));
							TreeSet<Integer> weeks = subjectArrayList.get(0)
									.getWeeks();
							int startWeek = Collections.min(weeks);
							int endWeek = Collections.max(weeks);
							// ��������
							text = subjectArrayList.get(0).getName();
							if (text.length() > 7)
								text = text.substring(0, 7) + "...";
							text = text + "\n" + startWeek + "-" + endWeek;

						}
						textView.setText(text);
						textView.setMaxWidth(50);
						textView.setMinLines(5);
						textView.getBackground().setAlpha(230);
						textView.setTextColor(Color.parseColor("#F00078"));

						// ��ӵ�����
						tableRow.addView(textView);
					} else {
						shape = res.getDrawable(R.drawable.empty_background);
						textView.setBackground(shape);
						textView.setText("     +     ");
						textView.getBackground().setAlpha(160);
						textView.setGravity(Gravity.CENTER);
						textView.setMaxWidth(50);
						textView.setMinLines(5);
						tableRow.addView(textView);
					}
				}
			}
			// �½���TableRow��ӵ�TableLayout
			tableLayout.addView(tableRow);
		}

		// Ϊ�γ��б��е�ÿ��������ó�����������ʹ�û�������صı����ܵ����˵�
		for (int order = 1; order <= 5; order++) {
			// ��ȡtableLayout�е�ÿ������Ŀ����ÿһ��
			TableRow tableRow = (TableRow) tableLayout.getChildAt(order - 1);
			for (int dayOfWeek = 1; dayOfWeek <= 8; dayOfWeek++) {
				// ��ȡÿһ���е�ֵ��Ŀ����ÿһ����ʾ�ĸ���
				TextView textView = (TextView) tableRow
						.getChildAt(dayOfWeek - 1);
				// ���ü����� ���ݲ���ʹ���Զ������Ӧ
				textView.setOnLongClickListener(new myLongListener(dayOfWeek,
						order, textView));
				textView.setOnClickListener(new myOnClickListener(dayOfWeek,
						order));
			}
		}
	}

	// �������¼�����
	private class myLongListener implements OnLongClickListener {

		int weekday = 0, time = 0;
		TextView textView;

		// ��ȡ������ĸ��ӵĻ�����Ϣ���ܣ��ڣ���TextView
		public myLongListener(int weekday, int time, TextView textView) {
			super();
			this.weekday = weekday - 1;
			this.time = time;
			this.textView = textView;
		}

		@Override
		public boolean onLongClick(View v) {
			// ����ִ�еĺ���
			operationSelectDialog(weekday, time, textView);
			return false;
		}
	}

	private class myOnClickListener implements OnClickListener {
		int weekday = 0, time = 0;

		// ��ȡ������ĸ��ӵĻ�����Ϣ���ܣ��ڣ���TextView
		public myOnClickListener(int weekday, int time) {
			super();
			this.weekday = weekday - 1;
			this.time = time;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDetail(weekday, time);
		}
	}

	@SuppressLint("InflateParams")
	public void operationSelectDialog(final int weekday, final int order,
			final TextView textView) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);

		// ���ѡ����ӿγ�
		if ((textView.getText().toString().trim().equals("+"))) {
			finish();//��ֹ��ǰ�
			newActivity(weekday, order, 0, "add", textView.getText().toString());
			// �����������һ���Ѿ������ݵĸ��ӣ�����ɾ���ͱ༭������ť�����û�ѡ�����
		} else {
			// ��ȡ���������趨�Ĳ���
			View deleteOrEdit = layoutInflater.inflate(
					R.layout.edit_option_dialog, null);// //////////////////
			// deleteOrEdit.setBackground(getResources().getDrawable(R.drawable.blank));
			// ����ѡ���
			AlertDialog.Builder buttonDialog = new AlertDialog.Builder(this);
			buttonDialog.setIcon(android.R.drawable.ic_dialog_info);

			buttonDialog.setView(deleteOrEdit);
			buttonDialog.setTitle("ѡ�����");
			buttonDialog.setPositiveButton("ȡ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			// ��ȡ�������������ť
			Button delete = (Button) deleteOrEdit.findViewById(R.id.delete);
			Button edit = (Button) deleteOrEdit.findViewById(R.id.edit);
			Button add = (Button) deleteOrEdit.findViewById(R.id.addButton);
			

			// Ϊɾ����ť���ü���
			delete.setOnClickListener(new OnClickListener() {// ɾ���γ�����
				@Override
				public void onClick(View v) {
					deleteDialog(weekday, order);// �����Ͽε�ʱ����ú���ɾ�����ʱ��Ŀγ�
				}
			});

			// Ϊ�༭��ť���ü���
			edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//finish();// ������ǰ�
					if (subjects.get(weekday * 10 + order).size() == 1) {
						finish();// ������ǰ�
						Log.e("debug", "create new intent");
						newActivity(weekday, order, selectIndex, "edit", subjects.get(weekday * 10 + order).get(0).getName());
					} else {
						selectToEditDialog(weekday, order);
					}
				}
			});
			
			add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
					newActivity(weekday, order, selectIndex, "add", subjects.get(weekday * 10 + order).get(0).getName());
				}
			});
			// ####�����൱�ڻ�ȡ��ǰ�Ի��տ��״̬��ӦΪû������ȷ����ȡ����ť����Ҫʹ���Զ���İ�ťʹ�ĶԻ����˳�
			buttonDialog.create().show();/////////////////////
		}
		// ���Գ�����ťʱ�Ƿ񲶻���ȷ������
		// Toast.makeText(getApplicationContext(), "��" + weekday + "��" + order +
		// "�ڣ�", Toast.LENGTH_SHORT).show();
	}

	private void showDetail(int dayOfWeek, int time) {
		ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10
				+ time);
		if (subjectArrayList == null || subjectArrayList.size() == 0) {
			return;
		}

		AlertDialog.Builder detail = new AlertDialog.Builder(this);// ���嵯����

		detail.setTitle("��ϸ��Ϣ");
		String text = "";
		int lineCount = 1;
		for (Subject x : subjectArrayList) {
			text += ( "�γ�����:    " + x.getName() + "\n"
					+ "��        ʦ:    " + x.getTeacher() + "\n"
					+ "��������:    " + x.getTestAttr() + "\n"
					+ "�γ�����:    " + x.getSubjectAttr() + "\n"
					+ "�Ͽεص�:    " + x.getLocation() + "\n"
					+ "�Ͽ�����:    " + x.getWeeks().toString());
			if (lineCount < subjectArrayList.size()) {
				text += "\n\n";
			}
			lineCount++;
		}
		detail.setMessage(text);
		detail.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// ֱ�ӹرնԻ���
					}
				});
		detail.create().show();
	}

	public void selectToEditDialog(final int dayOfWeek, final int order) {
		// ����Ի����е��б�����
		subjectArrayListForEdit = subjects.get(dayOfWeek * 10 + order);
		items = new String[subjectArrayListForEdit.size()];
		for (int i = 0; i < subjectArrayListForEdit.size(); i++) {
			items[i] = subjectArrayListForEdit.get(i).getName();
		}
		checkedItems = new boolean[subjectArrayListForEdit.size()];
		
		// ����һ��������
		new AlertDialog.Builder(this)
				.setTitle("ѡ��༭��")
				// �ڶԻ�����ʹ���б�
				.setSingleChoiceItems(items, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								selectIndex = which;
								Log.e("select", selectIndex + " ");
							}
						})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					// ����û�ȷ��ɾ��
					String text = "";// ����ɾ���Ŀ�Ŀ����ʹ��toast���
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e("index", "index===============" + selectIndex);////-1
						text += subjectArrayListForEdit.get(selectIndex).getName();// ��ȡɾ����Ŀ������

						dialog.dismiss();// �رնԻ���////////////////////////////////
						finish();// ������ǰ�

						Log.e("debug", "create new intent");
						newActivity(dayOfWeek, order, selectIndex, "edit", text);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// ��ȡ����ִ�в���
					}
				}).show();
	}
	
	private void newActivity(int dayOfWeek, int time, int index, String action, String subjectName) {
		Intent editSyllabus = new Intent(getApplicationContext(), EditSchedule.class);
		editSyllabus.putExtra("weekday", dayOfWeek);
		editSyllabus.putExtra("time", time);
		editSyllabus.putExtra("item", index);
		editSyllabus.putExtra("action", action);
		editSyllabus.putExtra("name", subjectName);
		startActivity(editSyllabus);
	}

	// ɾ����Ӧʱ��Ŀγ�
	public void deleteDialog(final int dayOfWeek, final int order) {
		// ����Ի����е��б�����
		subjectArrayListForEdit = subjects.get(dayOfWeek * 10 + order);
		items = new String[subjectArrayListForEdit.size()];
		for (int i = 0; i < subjectArrayListForEdit.size(); i++) {
			items[i] = subjectArrayListForEdit.get(i).getName();
		}
		checkedItems = new boolean[subjectArrayListForEdit.size()];

		// ����һ��������
		new AlertDialog.Builder(this)
				.setTitle("ѡ��ɾ����")
				// �ڶԻ�����ʹ���б�
				.setMultiChoiceItems(items, checkedItems,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// TODO Auto-generated method stub

							}
						})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					// ����û�ȷ��ɾ��
					String text = "";// ����ɾ���Ŀ�Ŀ����ʹ��toast���

					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < checkedItems.length; i++) {
							if (checkedItems[i]) {
								text += subjectArrayListForEdit.get(i)
										.getName();// ��ȡɾ����Ŀ������
								subjectArrayListForEdit.remove(i);// ɾ��ָ���Ŀγ�
								checkedItems[i] = Boolean.FALSE;
							}
						}
						subjects.put(dayOfWeek * 10 + order,
								subjectArrayListForEdit);
						MainActivity.subjects = subjects;// �������еĿγ�����

						// ���µĲ�����ɾ�������б�д���ļ�
						FileOperation fileOperation = new FileOperation(
								getApplicationContext());
						fileOperation.writeSubjectsFile(subjects);
						dialog.dismiss();// �رնԻ���
						finish();// ������ǰ�

						// �������
						Intent intent = new Intent(getApplicationContext(),
								ModifyContent.class);
						startActivity(intent);

						Toast.makeText(getApplicationContext(), text + " ɾ���ɹ�",
								Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// ��ȡ����ִ�в���
					}
				}).show();
	}
}
