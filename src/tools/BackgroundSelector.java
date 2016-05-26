package tools;

import com.example.syllabus.R;

public class BackgroundSelector {

	// 根据时间选择背景图片
	public static int getBackground(int dayOfMonth) {
		int bgCount = 23;
		int bgindex = dayOfMonth % bgCount + 1;
		switch (bgindex) {
		case 1:
			return R.drawable.bg_1;
		case 2:
			return R.drawable.bg_2;
		case 3:
			return R.drawable.bg_3;
		case 4:
			return R.drawable.bg_4;
		case 5:
			return R.drawable.bg_5;
		case 6:
			return R.drawable.bg_6;
		case 7:
			return R.drawable.bg_7;
		case 8:
			return R.drawable.bg_8;
		case 9:
			return R.drawable.bg_9;
		case 10:
			return R.drawable.bg_10;
		case 11:
			return R.drawable.bg_11;
		case 12:
			return R.drawable.bg_12;
		case 13:
			return R.drawable.bg_13;
		case 14:
			return R.drawable.bg_14;
		case 15:
			return R.drawable.bg_15;
		case 16:
			return R.drawable.bg_16;
		case 17:
			return R.drawable.bg_17;
		case 18:
			return R.drawable.bg_18;
		case 19:
			return R.drawable.bg_19;
		case 20:
			return R.drawable.bg_20;
		case 21:
			return R.drawable.bg_21;
		case 22:
			return R.drawable.bg_22;
		case 23:
			return R.drawable.bg_23;
		default:
			return R.drawable.bg_17;
		}
	}
}
