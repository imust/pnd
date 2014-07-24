package in.imust.pnd.view;

import in.imust.pnd.R;
import in.imust.pnd.base.AdapterView;
import in.imust.pnd.data.SimpleBackup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@EViewGroup(R.layout.item_backup)
public class SimpleBackupView extends LinearLayout implements AdapterView<SimpleBackup> {

	@ViewById
	TextView mName;
	@ViewById
	TextView mDate;
	
	public SimpleBackupView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleBackupView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleBackupView(Context context) {
		super(context);
	}

	@AfterViews
	public void init() {
		this.setOrientation(VERTICAL);
	}
	
	@Override
	public View getView() {
		return this;
	}

	@Override
	public void bindData(SimpleBackup data) {
		mName.setText(data.name);
		mDate.setText(data.date);
	}

}
