package in.imust.pnd.base;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @date 2014-7-23
 * @author declan.z(declan.zhang@gmail.com)
 *
 * @param <T>
 * @param <K>
 */
public abstract class CustomerAdapter<T extends AdapterData, K extends AdapterView<T>> 
	extends BaseAdapter {

	private ArrayList<T> mListData;
	private Context mContext;
	
	public CustomerAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mListData == null ? 0 : mListData.size();
	}

	@Override
	public T getItem(int position) {
		return mListData == null ? null : mListData.get(position);
	}
   
	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setList(ArrayList<T> list) { 
		mListData = list;
		notifyDataSetInvalidated();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		K adapterView = null;
		if (convertView == null) {
			try {
				adapterView = (K)((Class)((ParameterizedType)this.getClass()
						.getGenericSuperclass()).getActualTypeArguments()[1])
						.getDeclaredMethod("build", Context.class).invoke(null, mContext);
				convertView = adapterView.getView();
				convertView.setTag(adapterView);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			adapterView = (K) convertView.getTag();
		}
		
		T data = getItem(position);
		adapterView.bindData(data);
		
		return convertView;
	}

	
	
}
