package demo.p2p.hl.view;

import android.content.Context;
import android.widget.LinearLayout;
import demo.p2p.hl.data.AdapterData;

public abstract class AdapterView<T extends AdapterData> extends LinearLayout {
	public AdapterView(Context context) {
		super(context);
	}
	public abstract void initView(Context context);
	public abstract void bindData(T data);
}
