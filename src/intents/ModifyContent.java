/*
 * 本类的主要作用是能够监听格子中的长按事件，并弹出响应的菜单让用户选择是编辑还是删除，
 * 其中编辑启动了另外一个活动，删除就在本活动中完成，这些操作都是通过重启主活动来实现
 * 立即更新数据的
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

	private String[] items;// 复选框的显示列表
	private boolean[] checkedItems;// 复选框选择状态列表
	private ArrayList<Subject> subjectArrayListForEdit;
	private int selectIndex = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_schedule);
		setTitle("所有课程");
		subjects = MainActivity.subjects;// 获取课程信息

		// 调用函数显示课程列表
		editSyllabus();

	}

	@SuppressWarnings("deprecation")
	public void editSyllabus() {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);// 整个课程列表的布局
		// 全部列自动填充空白处
		tableLayout.setStretchAllColumns(true);

		Drawable shape = null;
		Resources res = getResources();

		// 设定背景颜色课主活动背景统一
		tableLayout.setBackgroundResource(MainActivity.backgroundId);

		// 使用循序动态生成列表
		for (int order = 1; order <= 5; order++) {
			TableRow tableRow = new TableRow(this);// 表格的一行
			tableRow.setGravity(Gravity.CENTER);// 设定对齐方式

			for (int dayOfWeek = 0; dayOfWeek <= 7; dayOfWeek++) {
				TextView textView = new TextView(this);// 表格一行中的每一个单项

				if (dayOfWeek == 0) {// 每行的第一列显示第几节课
					textView.setBackground(res.getDrawable(R.drawable.blank));
					textView.setText(String.valueOf(order));// 设置显示的数据
					textView.setWidth(5);
					textView.setGravity(Gravity.CENTER);
					// 设置宽度
					textView.setMaxWidth(5);
					textView.setMinLines(5);
					// 设置颜色
					textView.setTextColor(Color.parseColor("#FF0033"));
					tableRow.addView(textView);
				} else {// 正常输出课表
					ArrayList<Subject> subjectArrayList = subjects
							.get(dayOfWeek * 10 + order);
					if (subjectArrayList != null && subjectArrayList.size() > 0) {
						// 设定背景
						String text = "";
						final int totalLen = 8;
						if (subjectArrayList.size() > 1) {// 如果同一个世界有多节课
							int subLen = totalLen / subjectArrayList.size();
							textView.setBackground(res.getDrawable(R.drawable.duplicate_bg));
							for (int i = 0; i < subjectArrayList.size() - 1; i++) {
								text += subjectArrayList.get(i).getName()
										.substring(0, subLen)
										+ "&";
							}
							text += subjectArrayList.get(
									subjectArrayList.size() - 1).getName().substring(0, subLen) + "...";
						} else {// 同一时间只有一节课
							// 获取上课周数的范围
							textView.setBackground(res.getDrawable(R.drawable.subjectbackground));
							TreeSet<Integer> weeks = subjectArrayList.get(0)
									.getWeeks();
							int startWeek = Collections.min(weeks);
							int endWeek = Collections.max(weeks);
							// 设置数据
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

						// 添加到行中
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
			// 新建的TableRow添加到TableLayout
			tableLayout.addView(tableRow);
		}

		// 为课程列表中的每个表格设置长按监听器，使用户长按相关的表格就能弹出菜单
		for (int order = 1; order <= 5; order++) {
			// 获取tableLayout中的每个字项目，即每一行
			TableRow tableRow = (TableRow) tableLayout.getChildAt(order - 1);
			for (int dayOfWeek = 1; dayOfWeek <= 8; dayOfWeek++) {
				// 获取每一行中的值项目，即每一个显示的格子
				TextView textView = (TextView) tableRow
						.getChildAt(dayOfWeek - 1);
				// 设置监听器 传递参数使用自定义的响应
				textView.setOnLongClickListener(new myLongListener(dayOfWeek,
						order, textView));
				textView.setOnClickListener(new myOnClickListener(dayOfWeek,
						order));
			}
		}
	}

	// 长按的事件处理
	private class myLongListener implements OnLongClickListener {

		int weekday = 0, time = 0;
		TextView textView;

		// 获取到点击的格子的基本信息，周，节，和TextView
		public myLongListener(int weekday, int time, TextView textView) {
			super();
			this.weekday = weekday - 1;
			this.time = time;
			this.textView = textView;
		}

		@Override
		public boolean onLongClick(View v) {
			// 最终执行的函数
			operationSelectDialog(weekday, time, textView);
			return false;
		}
	}

	private class myOnClickListener implements OnClickListener {
		int weekday = 0, time = 0;

		// 获取到点击的格子的基本信息，周，节，和TextView
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

		// 如果选择添加课程
		if ((textView.getText().toString().trim().equals("+"))) {
			finish();//终止当前活动
			newActivity(weekday, order, 0, "add", textView.getText().toString());
			// 如果长按的是一个已经有内容的格子，弹出删除和编辑两个按钮，让用户选择操作
		} else {
			// 获取给弹出框设定的布局
			View deleteOrEdit = layoutInflater.inflate(
					R.layout.edit_option_dialog, null);// //////////////////
			// deleteOrEdit.setBackground(getResources().getDrawable(R.drawable.blank));
			// 弹出选择框
			AlertDialog.Builder buttonDialog = new AlertDialog.Builder(this);
			buttonDialog.setIcon(android.R.drawable.ic_dialog_info);

			buttonDialog.setView(deleteOrEdit);
			buttonDialog.setTitle("选择操作");
			buttonDialog.setPositiveButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

			// 获取弹出框的两个按钮
			Button delete = (Button) deleteOrEdit.findViewById(R.id.delete);
			Button edit = (Button) deleteOrEdit.findViewById(R.id.edit);
			Button add = (Button) deleteOrEdit.findViewById(R.id.addButton);
			

			// 为删除按钮设置监听
			delete.setOnClickListener(new OnClickListener() {// 删除课程内容
				@Override
				public void onClick(View v) {
					deleteDialog(weekday, order);// 传递上课的时间调用函数删除这个时间的课成
				}
			});

			// 为编辑按钮设置监听
			edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//finish();// 结束当前活动
					if (subjects.get(weekday * 10 + order).size() == 1) {
						finish();// 结束当前活动
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
			// ####这里相当于获取当前对话空框的状态，应为没有设置确定，取消按钮，就要使用自定义的按钮使改对话框退出
			buttonDialog.create().show();/////////////////////
		}
		// 测试长按按钮时是否捕获到正确的数据
		// Toast.makeText(getApplicationContext(), "周" + weekday + "第" + order +
		// "节：", Toast.LENGTH_SHORT).show();
	}

	private void showDetail(int dayOfWeek, int time) {
		ArrayList<Subject> subjectArrayList = subjects.get(dayOfWeek * 10
				+ time);
		if (subjectArrayList == null || subjectArrayList.size() == 0) {
			return;
		}

		AlertDialog.Builder detail = new AlertDialog.Builder(this);// 定义弹出框

		detail.setTitle("详细信息");
		String text = "";
		int lineCount = 1;
		for (Subject x : subjectArrayList) {
			text += ( "课程名字:    " + x.getName() + "\n"
					+ "教        师:    " + x.getTeacher() + "\n"
					+ "考试类型:    " + x.getTestAttr() + "\n"
					+ "课程属性:    " + x.getSubjectAttr() + "\n"
					+ "上课地点:    " + x.getLocation() + "\n"
					+ "上课周数:    " + x.getWeeks().toString());
			if (lineCount < subjectArrayList.size()) {
				text += "\n\n";
			}
			lineCount++;
		}
		detail.setMessage(text);
		detail.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 直接关闭对话框
					}
				});
		detail.create().show();
	}

	public void selectToEditDialog(final int dayOfWeek, final int order) {
		// 构造对话框中的列表类容
		subjectArrayListForEdit = subjects.get(dayOfWeek * 10 + order);
		items = new String[subjectArrayListForEdit.size()];
		for (int i = 0; i < subjectArrayListForEdit.size(); i++) {
			items[i] = subjectArrayListForEdit.get(i).getName();
		}
		checkedItems = new boolean[subjectArrayListForEdit.size()];
		
		// 启动一个弹出框
		new AlertDialog.Builder(this)
				.setTitle("选择编辑项")
				// 在对话框中使用列表
				.setSingleChoiceItems(items, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								selectIndex = which;
								Log.e("select", selectIndex + " ");
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 如果用户确定删除
					String text = "";// 保存删除的科目名字使用toast输出
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e("index", "index===============" + selectIndex);////-1
						text += subjectArrayListForEdit.get(selectIndex).getName();// 获取删除科目的名字

						dialog.dismiss();// 关闭对话款////////////////////////////////
						finish();// 结束当前活动

						Log.e("debug", "create new intent");
						newActivity(dayOfWeek, order, selectIndex, "edit", text);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 点取消不执行操作
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

	// 删除响应时间的课程
	public void deleteDialog(final int dayOfWeek, final int order) {
		// 构造对话框中的列表类容
		subjectArrayListForEdit = subjects.get(dayOfWeek * 10 + order);
		items = new String[subjectArrayListForEdit.size()];
		for (int i = 0; i < subjectArrayListForEdit.size(); i++) {
			items[i] = subjectArrayListForEdit.get(i).getName();
		}
		checkedItems = new boolean[subjectArrayListForEdit.size()];

		// 启动一个弹出框
		new AlertDialog.Builder(this)
				.setTitle("选择删除项")
				// 在对话框中使用列表
				.setMultiChoiceItems(items, checkedItems,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// TODO Auto-generated method stub

							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 如果用户确定删除
					String text = "";// 保存删除的科目名字使用toast输出

					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < checkedItems.length; i++) {
							if (checkedItems[i]) {
								text += subjectArrayListForEdit.get(i)
										.getName();// 获取删除科目的名字
								subjectArrayListForEdit.remove(i);// 删除指定的课程
								checkedItems[i] = Boolean.FALSE;
							}
						}
						subjects.put(dayOfWeek * 10 + order,
								subjectArrayListForEdit);
						MainActivity.subjects = subjects;// 重置组活动中的课程数据

						// 将新的不包含删除数据列表写入文件
						FileOperation fileOperation = new FileOperation(
								getApplicationContext());
						fileOperation.writeSubjectsFile(subjects);
						dialog.dismiss();// 关闭对话款
						finish();// 结束当前活动

						// 启动主活动
						Intent intent = new Intent(getApplicationContext(),
								ModifyContent.class);
						startActivity(intent);

						Toast.makeText(getApplicationContext(), text + " 删除成功",
								Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();// 点取消不执行操作
					}
				}).show();
	}
}
