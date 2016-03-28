package in.imust.pnd;

import in.imust.pnd.base.CustomerAdapter;
import in.imust.pnd.data.SimpleBackup;
import in.imust.pnd.util.ShellUtils;
import in.imust.pnd.view.SimpleBackupView_;

import java.io.File;
import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    
	public static final String PACKAGE_JP = "jp.gungho.pad";
	public static final String PACKAGE_HT = "jp.gungho.padHT";
	public static final String PATH_BAKCUP = "/PadBack/";
	
	public static final int TYPE_JP = 0;
	public static final int TYPE_HT = 1;
	
	private File mBackupDir = null;
	private int mType = TYPE_HT;
	private String mPackage = PACKAGE_HT;
	private int mMaxIndex = 0;
	private int mSelectIndex = -1;
	
	@ViewById
	ListView mList;
	@ViewById(R.id.restore)
	Button mRestore;
	@ViewById
	TextView mAbout;
	
	CustomerAdapter<SimpleBackup, SimpleBackupView_> mAdapter;
	
	@AfterViews
	void init () {
		mAdapter = new CustomerAdapter<SimpleBackup, SimpleBackupView_>(this) {};
		mList.setAdapter(mAdapter);
		
		String path = Environment.getExternalStorageDirectory() + PATH_BAKCUP;
		mBackupDir = new File(path);
		if (!mBackupDir.exists() || !mBackupDir.isDirectory()) {
			mBackupDir.mkdir();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	@UiThread
	void refresh() {
		setTitle(mType == TYPE_HT ? "PND.HT港服" : "PND.JP日服");
		refreshListData();
		mSelectIndex = -1;
        mRestore.setText("Load");
	}
	
	@Background
	void refreshListData() {
		ArrayList<SimpleBackup> list = new ArrayList<SimpleBackup>();
		mMaxIndex = 0;
		
		for (File file : mBackupDir.listFiles()) {
			if (file.isDirectory()) {
				File data048 = new File(file, "data048.bin");
				if (data048.exists()) {
					try {
						int index = Integer.parseInt(file.getName());
						mMaxIndex = Math.max(index, mMaxIndex);
						
						SimpleBackup sb = new SimpleBackup();
						sb.name = file.getName();
						sb.setDate(data048.lastModified());
						list.add(sb);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		refreshListView(list);
	}
	
	@UiThread
	void refreshListView(ArrayList<SimpleBackup> list) {
		mAdapter.setList(list);
		mAdapter.notifyDataSetChanged();
        if (mMaxIndex == 0) {
            mAbout.setVisibility(View.VISIBLE);
        } else {
            mAbout.setVisibility(View.GONE);
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
			String command = String.format("rm -rf %s/*", mBackupDir.getAbsolutePath());
			ShellUtils.execCommand(command, false);
			break;
		case R.id.exchange:
			mType = mType == TYPE_HT ? TYPE_JP : TYPE_HT;
			mPackage = mType == TYPE_HT ? PACKAGE_HT : PACKAGE_JP;
			break;
		}
		refresh(); 
		return super.onOptionsItemSelected(item);
	}
	
	
	@ItemClick(R.id.mList)
	void listItemClick(int position) {
	    mSelectIndex = position +1;
	    mRestore.setText(String.format("Load(%1$s)", mSelectIndex));
	}
	
	@Click
	void backup() {
		File nextDir = new File(mBackupDir, mMaxIndex + 1 + "");
		nextDir.mkdir();
		String command = String.format("cp /data/data/%1$s/files/*.bin %2$s/"
				, mPackage, nextDir.getAbsolutePath());
		ShellUtils.execCommand(command, true);
		refresh();
	}
	
	@Click
	void restore() {
		File restoreDir = new File(mBackupDir, mSelectIndex == -1 ? mMaxIndex + "" : mSelectIndex + "");
		if (restoreDir.exists()) {
			// 先备份一下避免按错
			File temp = new File(mBackupDir, "temp");
			if (!temp.exists()) {
				temp.mkdir();
			}
			String command = String.format("cp -f /data/data/%1$s/files/*.bin %2$s/"
					, mPackage, temp.getAbsolutePath());
			ShellUtils.execCommand(command, true);
			
			// 然后再真的还原
			command = String.format("cp -f %1$s/*.bin /data/data/%2$s/files/"
					, restoreDir.getAbsolutePath(), mPackage);
			ShellUtils.execCommand(command, true);
			refresh();
		}
	}
}
