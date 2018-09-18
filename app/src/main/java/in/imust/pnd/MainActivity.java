package in.imust.pnd;

import android.app.Activity;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;

import in.imust.pnd.base.CustomerAdapter;
import in.imust.pnd.data.SimpleBackup;
import in.imust.pnd.util.ShellUtils;
import in.imust.pnd.view.SimpleBackupView_;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
    
	public static final String PACKAGE_JP = "jp.gungho.pad";
	public static final String PACKAGE_HT = "jp.gungho.padHT";

	// 总目录
	public static final String PATH_BAKCUP = "/PadBack/";
	// SL目录
    public static final String PATH_SL = "/PadBack/sl";
    // 临时备份目录
    public static final String PATH_TEMP = "/PadBack/temp";
    // 帐号1目录
    public static final String PATH_AC1 = "/PadBack/acc1";
    // 帐号2目录
    public static final String PATH_AC2 = "/PadBack/acc2";



    public static final int TYPE_JP = 0;
	public static final int TYPE_HT = 1;

    private File mSLDir = null;
    private File mTempDir = null;
    private File mAC1Dir = null;
    private File mAC2Dir = null;

    private int mType = TYPE_HT;
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
        initDir();
	}

	void initDir() {
        String all = Environment.getExternalStorageDirectory() + PATH_BAKCUP;
        String sl = Environment.getExternalStorageDirectory() + PATH_SL;
        String temp = Environment.getExternalStorageDirectory() + PATH_TEMP;
        String acc1 = Environment.getExternalStorageDirectory() + PATH_AC1;
        String acc2 = Environment.getExternalStorageDirectory() + PATH_AC2;
        initDir(all);
        initDir(sl);
        initDir(temp);
        initDir(acc1);
        initDir(acc2);

        mSLDir = new File(sl);
        mTempDir = new File(temp);
        mAC1Dir = new File(acc1);
        mAC2Dir = new File(acc2);
    }

    void initDir(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
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

	boolean checkData048(File file) {
	    if (file.isDirectory()) {
            File data048 = new File(file, "data048.bin");
            return data048.exists();
        }
        return false;
    }

    boolean checkData048(String path) {
	    return checkData048(new File(path));
    }

	@Background
	void refreshListData() {
		ArrayList<SimpleBackup> list = new ArrayList<SimpleBackup>();
		mMaxIndex = 0;
		for (File file : mSLDir.listFiles()) {
		    if(checkData048(file)) {
                try {
                    int index = Integer.parseInt(file.getName());
                    mMaxIndex = Math.max(index, mMaxIndex);

                    SimpleBackup sb = new SimpleBackup();
                    sb.name = file.getName();
                    sb.setDate(file.lastModified());
                    list.add(sb);
                } catch (NumberFormatException e) {
                    // 懒得管
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
		    // 清空所有SL存档
            case R.id.delete:
                String command = String.format("rm -rf %s/*", mSLDir.getAbsolutePath());
                ShellUtils.execCommand(command, false);
                break;
            // 切换日港服
            case R.id.exchange:
                mType = mType == TYPE_HT ? TYPE_JP : TYPE_HT;
                //mPackage = mType == TYPE_HT ? PACKAGE_HT : PACKAGE_JP;
                break;
            // 切换至帐号1
            case R.id.ac1 :
                ShellUtils.execCommand(String.format("cp %1$s/*.bin %2$s/", mAC1Dir.getAbsolutePath(), getPackagePath()), true);
                ShellUtils.execCommand(String.format("chmod 666 %1$s/*.bin", getPackagePath()), true);
                ShellUtils.execCommand(String.format("rm -rf %1$s/*.bin", mAC1Dir.getAbsolutePath()), true);
                break;
            //切换至帐号2
            case R.id.ac2 :
                ShellUtils.execCommand(String.format("cp %1$s/*.bin %2$s/", mAC2Dir.getAbsolutePath(), getPackagePath()), true);
                ShellUtils.execCommand(String.format("chmod 666 %1$s/*.bin", getPackagePath()), true);
                ShellUtils.execCommand(String.format("rm -rf %1$s/*.bin", mAC2Dir.getAbsolutePath()), true);
                break;
            // 移动至帐号1
            case R.id.mv1 :
                ShellUtils.execCommand(String.format("cp %1$s/*.bin %2$s/", getPackagePath(), mAC1Dir.getAbsolutePath()), true);
                ShellUtils.execCommand(String.format("rm -rf %1$s/*.bin", getPackagePath()), true);
                break;
            // 移动至帐号2
            case R.id.mv2:
                ShellUtils.execCommand(String.format("cp %1$s/*.bin %2$s/", getPackagePath(), mAC2Dir.getAbsolutePath()), true);
                ShellUtils.execCommand(String.format("rm -rf %1$s/*.bin", getPackagePath()), true);
                break;
            //清空帐号
            case R.id. deleteAcc:
                ShellUtils.execCommand(String.format("rm -rf %1$s/*.bin", getPackagePath()), true);
                break;
		}
		refresh();
		return super.onOptionsItemSelected(item);
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


	    boolean ac1 = checkData048(mAC1Dir);
	    boolean ac2 = checkData048(mAC2Dir);
	    boolean current = checkData048(getPackagePath());

        menu.getItem(1).setEnabled(ac1 && !current);
        menu.getItem(2).setEnabled(ac2 && !current);
        menu.getItem(3).setEnabled(!ac1 && current);
        menu.getItem(4).setEnabled(!ac2 && current);

        return super.onPrepareOptionsMenu(menu);
    }

    @ItemClick(R.id.mList)
	void listItemClick(int position) {
	    mSelectIndex = position +1;
	    mRestore.setText(String.format("Load(%1$s)", mSelectIndex));
	}
	
	@Click
	void backup() {
		File nextDir = new File(mSLDir, mMaxIndex + 1 + "");
        nextDir.mkdir();
        ShellUtils.execCommand(String.format("cp %1$s/*.bin %2$s/", getPackagePath(), nextDir.getAbsolutePath()), true);
        refresh();
	}
	
	@Click
	void restore() {
		File restoreDir = new File(mSLDir, mSelectIndex == -1 ? mMaxIndex + "" : mSelectIndex + "");
		if (restoreDir.exists()) {
			// 先备份一下避免按错
			ShellUtils.execCommand(String.format("cp -f %1$s/*.bin %2$s/", getPackagePath(), mTempDir.getAbsolutePath()), true);
			
			// 然后再真的还原
			ShellUtils.execCommand(String.format("cp -f %1$s/*.bin %2$s/", restoreDir.getAbsolutePath(), getPackagePath()), true);
			refresh();
		}
	}

	String getPackagePath () {
	    return String.format("/data/data/%1$s/files", mType == TYPE_HT ? PACKAGE_HT : PACKAGE_JP);
    }


}
