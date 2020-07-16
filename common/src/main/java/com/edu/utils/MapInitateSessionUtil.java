package com.edu.utils;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用map实现多线程间数据共享
 * @author WangSong
 *
 */
public class MapInitateSessionUtil {

	//定义一个存放数据的全局map对象
	private static Map<String, Object> map = null;
	
	//构造器实现map创建空间(只创建一次实例)
	public MapInitateSessionUtil() {
//		map = new HashMap<String, Object>();
		map = new ConcurrentHashMap<String, Object>();
	}
	
	//存值方法（因为用到了ConcurrentHashMap，key & value不能为空，需要判断）
	public static void set(String key,Object value) {
		boolean objIsNull = true;//定义obj标识(默认为空)
		//object对象判断空值
		String str = ObjectUtils.toString(value, "");//将object对象转换成string对象
		if(StringUtils.isNotBlank(str)){
			objIsNull = false;
		}
		//判断是否gc + 对象是否不为空，并存值
		if(null != map && !objIsNull) {
			map.put(key, value);
		}else {
			//如果map被gc，则重新开辟空间
			synchronized (MapInitateSessionUtil.class){
				if(null == map && !objIsNull){
//					map = new HashMap<String, Object>();
					map = new ConcurrentHashMap<String, Object>();
				}
			}
			if(!objIsNull){
				map.put(key, value);
			}
		}
	}
	
	//取值方法
	public static Object get(String key) {
		if(null != map) {
			return map.get(key);
		}
		return null;
	}
	
	//gc方法
	public static void gc() {
		map = null;
	}
}
