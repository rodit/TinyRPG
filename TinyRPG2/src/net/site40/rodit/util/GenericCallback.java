package net.site40.rodit.util;

public interface GenericCallback {
	
	public void callback();
	
	public static interface ObjectCallback<T>{
		
		public void callback(T object);
	}
}
