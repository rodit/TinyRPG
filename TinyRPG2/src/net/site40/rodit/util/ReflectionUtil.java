package net.site40.rodit.util;

public class ReflectionUtil {

	public static Class<?> getClass(String clsName){
		try{
			return Class.forName(clsName);
		}catch(Exception e){
			return null;
		}
	}
	
	public static <T>T instantiate(Class<T> cls){
		try{
			return cls.getConstructor().newInstance();
		}catch(Exception e){
			return null;
		}
	}
}
