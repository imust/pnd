package demo.p2p.hl.view;

import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.TextView;
import in.imust.pnd.R;
import demo.p2p.hl.data.TestData;

@EViewGroup(R.layout.test_view)
public class TestView extends AdapterView<TestData> {
	
	@ViewById(R.id.name)
	TextView mName;
	@ViewById(R.id.address)
	TextView mAddress;
	
	public TestView(Context context) {
		super(context);
	}

	@Override
	public void initView(Context context) {
		inflate(context, R.layout.test_view, this);
		mName = (TextView) findViewById(R.id.name);
		mAddress = (TextView) findViewById(R.id.address);
	}

	@Override
	public void bindData(TestData data) {
		mName.setText(data.name);
		mAddress.setText(data.address);
	}

	
}
