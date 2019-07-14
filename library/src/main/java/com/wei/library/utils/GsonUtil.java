package com.wei.library.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gson解析工具类
 * 
 * @author xiao
 * 
 */
public class GsonUtil {

	private static final String TAG = "GsonUtil";
	
	private static final String LIST_TAG = "list";
	private static final String OBJ_TAG = "obj";
	
	private static Gson gson = new Gson();

	/**
	 * 实体类转化为json
	 * 
	 * @param bean
	 * @return
	 */
	public static String bean2json(Object bean) {
		return gson.toJson(bean);
	}

	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param type
	 *            转化的目标实体类
	 * @return
	 */
	public static <T> T json2bean(String json, Type type)throws JsonSyntaxException {
		return gson.fromJson(json, type);
	}

	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param type
	 *            转化的目标实体类
	 * @return
	 */
	public static <T> T json2Obj(String json, Type type) throws JsonSyntaxException {
		JsonObject jsonObject = new JsonParser().parse(json)
		.getAsJsonObject().getAsJsonObject(OBJ_TAG);
		return gson.fromJson(jsonObject.toString(), type);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param arrayElement 数组元素名称
	 * @param type 转换的目标对象类
	 * @return
	 */
	@Deprecated
	public static <T> List<T> jsonArray2List(String json, String arrayElement,Type type) {
		
		List<T> t = new ArrayList<T>();
		
		JsonArray jsonarray = new JsonParser().parse(json)
				.getAsJsonObject().getAsJsonArray(arrayElement);
		for (int i = 0; i < jsonarray.size(); i++) {
			JsonObject obj = jsonarray.get(i).getAsJsonObject();
			
			t.add((T) json2bean(obj.toString(), type));
		}
		return t;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param type 转换的目标对象类
	 * @return
	 */
	public static <T> List<T> jsonArray2List(String json, Type type) {
		
		List<T> t = new ArrayList<T>();
		
		JsonArray jsonarray = new JsonParser().parse(json)
				.getAsJsonObject().getAsJsonArray(LIST_TAG);
		if (jsonarray==null) {
			return Collections.emptyList();
		}
		for (int i = 0; i < jsonarray.size(); i++) {
			JsonObject obj = jsonarray.get(i).getAsJsonObject();
			
			t.add((T) json2bean(obj.toString(), type));
		}
		return t;
	}
}
