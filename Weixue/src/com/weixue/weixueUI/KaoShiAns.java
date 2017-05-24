package com.weixue.weixueUI;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.weixue.Adapter.AnsAdapter;
import com.weixue.Model.Ans;

public class KaoShiAns extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kaoshi_ans);

		ListView listView=(ListView) findViewById(R.id.listView1);
		List<Ans> li_Ans=new ArrayList<Ans>();
		
		Ans ans1=new Ans();
		ans1.setTitle("xxxxx");
		ans1.setResult("xxxx");
		li_Ans.add(ans1);
		
		Ans ans2=new Ans();
		ans2.setTitle("xxxxx");
		ans2.setResult("xxxx");
		li_Ans.add(ans2);
		
		Ans ans3=new Ans();
		ans3.setTitle("xxxxx");
		ans3.setResult("xxxx");
		li_Ans.add(ans3);
		
		AnsAdapter adapter= new AnsAdapter(getApplicationContext(),li_Ans);
		listView.setAdapter(adapter);
	}
}
