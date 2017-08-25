package in.imust.pnd.data;

import in.imust.pnd.base.AdapterData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SimpleBackup implements AdapterData {
	public String name;
	public String date;
	
	public void setDate(long date) {
		this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(date));
	}
}
