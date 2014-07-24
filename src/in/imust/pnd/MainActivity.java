package in.imust.pnd;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import in.imust.pnd.R;
import demo.p2p.hl.base.CustomerAdapter;
import demo.p2p.hl.data.TestData;
import demo.p2p.hl.view.TestView;

import android.app.Activity;
import android.widget.ListView;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    
	@ViewById
	ListView mList;
	
	@AfterViews
	void test () {
		CustomerAdapter<TestData, TestView> adapter = new CustomerAdapter<TestData, TestView>(this) {};
		
	}
	
	
}
