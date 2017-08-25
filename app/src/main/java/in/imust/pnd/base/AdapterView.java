package in.imust.pnd.base;

import android.view.View;

public interface AdapterView<T extends AdapterData> {
	public abstract View getView();
	public abstract void bindData(T data);
}
