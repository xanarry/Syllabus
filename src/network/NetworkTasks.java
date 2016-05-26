package network;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import basicaldefine.Subject;

@SuppressWarnings("deprecation")
public class NetworkTasks {
	Context context;

	public NetworkTasks(Context context) {
		this.context = context;
	}

	// �����򿪵�½ҳ�棬ͬʱ�᷵����֤�룬���Ǵ�ʱû��������֤��
	public boolean openLoginPage(HttpClient httpClient) {
		HttpGet httpGet = new HttpGet(constant.Constants.LOGIN_ADDRESS);
		try {
			httpClient.execute(httpGet);
			return true;
		} catch (ClientProtocolException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		} catch (IOException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
	}

	// ��ȡ��֤��
	public InputStream generateVerifyCodeFile(HttpClient httpClient) {
		HttpGet writeVerifyCode = new HttpGet(
				constant.Constants.VERIFYCODE_ADDRESS);
		InputStream inputStream = null;
		try {
			HttpResponse response = httpClient.execute(writeVerifyCode);
			inputStream = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	// ��½ҳ�棬���صĵ�½�Ĳ���״̬��״̬�ַ���
	public Pair<Boolean, String> login(HttpClient httpClient, String account,
			String password, String verifyCode) {
		Pair<Boolean, String> returnStuff = null;
		try {
			// �趨post����
			ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("zjh1", ""));
			postData.add(new BasicNameValuePair("tips", ""));
			postData.add(new BasicNameValuePair("lx", ""));
			postData.add(new BasicNameValuePair("eflag", ""));
			postData.add(new BasicNameValuePair("fs", ""));
			postData.add(new BasicNameValuePair("dzslh", ""));
			postData.add(new BasicNameValuePair("zjh", account));// ѧ��
			postData.add(new BasicNameValuePair("mm", password));// ����
			postData.add(new BasicNameValuePair("v_yzm", verifyCode));// ��֤��
			
			Log.e("debug", "now login");
			
			HttpPost post = new HttpPost(constant.Constants.LOGIN_ADDRESS);// ����post����
			post.setEntity(new UrlEncodedFormEntity(postData));// �������
			HttpResponse response = httpClient.execute(post);// ִ�е�½��Ϊ
			int returnCode = response.getStatusLine().getStatusCode();
			if (returnCode == 200) {
				String content = EntityUtils.toString(response.getEntity(),
						"utf-8");
				if (content.contains("��֤�����")) {
					returnStuff = new Pair<Boolean, String>(false, "��֤�����");
				} else if (content.contains("���벻��ȷ")) {
					returnStuff = new Pair<Boolean, String>(false, "ѧ�Ż����벻��ȷ");
				} else {
					returnStuff = new Pair<Boolean, String>(true, "��½�ɹ�");
				}
			} else {
				returnStuff = new Pair<Boolean, String>(false, "" + returnCode);
			}
		} catch (ClientProtocolException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			returnStuff = new Pair<Boolean, String>(false, e.getMessage());
		} catch (IOException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			returnStuff = new Pair<Boolean, String>(false, e.getMessage());
		}
		return returnStuff;
	}

	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, ArrayList<Subject>> getSyllabus(
			HttpClient httpClient) {
		HttpGet httpGetSyllabus = new HttpGet(
				constant.Constants.SYLLABUS_ADDRESS);// create get object
		HashMap<Integer, ArrayList<Subject>> subjects = new HashMap<Integer, ArrayList<Subject>>();
		try {
			HttpResponse response = httpClient.execute(httpGetSyllabus);// set get request

			String rawHtml = EntityUtils.toString(response.getEntity(), "utf-8");// ��ȡδ���κδ������ҳԴ��

			String html = rawHtml.replaceAll("&nbsp;", "");
			// �γ������Ͽ�ʱ��Ϊ����
			
			Document document = Jsoup.parse(html);//////////////////////?????????????????????????
			Elements tables = document.getElementsByClass("displayTag");
			
			
			if (tables.size() > 0) {
				Element ta = tables.get(tables.size() - 1);
				Elements elements = ta.getElementsByTag("tr");

				String preName = null;
				String preSubjectAttr = null;
				String preTestAttr = null;
				String preTeacher = null;
				String major = null;
				major = elements.get(1).getElementsByTag("td").get(0).text();//"���������������"
				for (int i = 1; i < elements.size(); i++) {
					Subject subject = new Subject();
					Elements items = elements.get(i).getElementsByTag("td");
					
					if (items.get(0).text().trim().length() == 0 || items.get(0).text().equals(major)) { //�жϱ������Ƿ�Ϊרҵ��ͷ
						preName = items.get(2).text();
						subject.setName(preName);

						preSubjectAttr = items.get(5).text();
						subject.setSubjectAttr(preSubjectAttr);

						preTestAttr = items.get(6).text();
						subject.setTestAttr(preTestAttr);

						preTeacher = items.get(7).text().replaceAll("\\*", " ");
						subject.setTeacher(preTeacher);

						subject.setWeeks(getWeeks(items.get(11).text()));
						subject.setDayOfweekAndOrder(Integer.parseInt(items
								.get(12).text())
								* 10
								+ Integer.parseInt(items.get(13).text()
										.replace("��", "")));
						subject.setLocation(items.get(15).text()
								+ items.get(16).text() + items.get(17).text());
						
					} else {
						subject.setName(preName);
						subject.setSubjectAttr(preSubjectAttr);
						subject.setTestAttr(preTestAttr);
						subject.setTeacher(preTeacher);
						subject.setWeeks(getWeeks(items.get(0).text()));
						subject.setDayOfweekAndOrder(Integer.parseInt(items
								.get(1).text())
								* 10
								+ Integer.parseInt(items.get(2).text()///////////////////
										.replace("��", "")));
						subject.setLocation(items.get(4).text()
								+ items.get(5).text() + items.get(6).text());
					}					
					ArrayList<Subject> value = new ArrayList<Subject>();
					if (subjects.containsKey(subject.getDayOfweekAndOrder())) {
						value = subjects.get(subject.getDayOfweekAndOrder());
					}
					value.add(subject);
					Log.e("new one", "push " + subject.getDayOfweekAndOrder() + " " + value.toString());
					subjects.put(subject.getDayOfweekAndOrder(), value);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subjects;
	}

	private static TreeSet<Integer> getWeeks(String info) {
		TreeSet<Integer> weeks = new TreeSet<Integer>();
		String temp = info.replace("��", "").replace("��", "");
		String[] allparts = temp.split(",");
		for (String x : allparts) {
			System.out.println(x);
			if (x.contains("-")) {
				String[] range = x.split("-");
				for (int i = Integer.parseInt(range[0]); i <= Integer.parseInt(range[1]); i++) {
					weeks.add(i);
				}
			} else if (x.length() > 0){
				weeks.add(Integer.parseInt(x));
			}
		}
		System.out.println(weeks);
		return weeks;
	}
}
