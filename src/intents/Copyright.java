package intents;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.syllabus.R;

public class Copyright extends Activity {
	TextView helpText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.copyright);
		setTitle("��Ϣ");
		helpText = (TextView) findViewById(R.id.help);
		helpText.setMovementMethod(ScrollingMovementMethod.getInstance());//���ù�����ı��Զ�����
	}
}
