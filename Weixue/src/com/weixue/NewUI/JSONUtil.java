package com.weixue.NewUI;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Json��ݲ���������
 * @author ������
 *
 */
public class JSONUtil {

	private static final  String TAG="JSONUtil";

	public static final String EMPTY="";
	/** �յ� {@code JSON} ���"{}"�� */
	public static final String EMPTY_JSON = "{}";
	/** �յ� {@code JSON} ����(����)��� "[]"�� */
	public static final String EMPTY_JSON_ARRAY = "[]";
	/** Ĭ�ϵ� {@code JSON} ����/ʱ���ֶεĸ�ʽ��ģʽ�� */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
	/** {@code Google Gson} ��ע�ⳣ�õİ汾�ų��� - {@code 1.0}�� */
	public static final Double SINCE_VERSION_10 = 1.0d;
	/** {@code Google Gson} �� ע�ⳣ�õİ汾�ų��� - {@code 1.1}�� */
	public static final Double SINCE_VERSION_11 = 1.1d;
	/** {@code Google Gson} ��ע�ⳣ�õİ汾�ų��� - {@code 1.2}�� */
	public static final Double SINCE_VERSION_12 = 1.2d;
	
	/**
	 * �����Ŀ�������ָ������������ת���� {@code JSON} ��ʽ���ַ�
	 * �÷���ת���������ʱ�������׳��κ��쳣�����������ʱ����ͨ���󷵻� "{}"�� ���ϻ�������󷵻�"[]"
	 * 
	 * @param target
	 *            Ŀ�����
	 * @param targetType
	 *            Ŀ���������͡�
	 * @param isSerializeNulls
	 *            �Ƿ����л� {@code null} ֵ�ֶΡ�
	 * @param version
	 *            �ֶεİ汾��ע�⡣
	 * @param datePattern
	 *            �����ֶεĸ�ʽ��ģʽ��
	 * @param excludesFieldsWithoutExpose
	 *            �Ƿ��ų�δ��ע {@literal @Expose} ע����ֶΡ�
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Type targetType, boolean isSerializeNulls, Double version, String datePattern, boolean excludesFieldsWithoutExpose)
	{
		if (target == null)
			return EMPTY_JSON;
		GsonBuilder builder = new GsonBuilder();
		if (isSerializeNulls)
			builder.serializeNulls();
		if (version != null)
			builder.setVersion(version.doubleValue());
		if (TextUtils.isEmpty(datePattern))
			datePattern = DEFAULT_DATE_PATTERN;
		builder.setDateFormat(datePattern);
		if (excludesFieldsWithoutExpose)
			builder.excludeFieldsWithoutExposeAnnotation();
		String result = EMPTY;
		Gson gson = builder.create();
		try
		{
			if (targetType != null)
			{
				result = gson.toJson(target, targetType);
			}
			else
			{
				result = gson.toJson(target);
			}
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Ŀ����� " + target.getClass().getName() + " ת�� JSON �ַ�ʱ�������쳣��", ex);
			if (target instanceof Collection || target instanceof Iterator || target instanceof Enumeration || target.getClass().isArray())
			{
				result = EMPTY_JSON_ARRAY;
			}
			else
				result = EMPTY_JSON;
		}
		return result;
	}
	
	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ֻ����ת����ͨ�� {@code JavaBean}����
	 * �÷���ֻ��ת������ {@literal @Expose} ע����ֶΣ�
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷�����ת������δ��ע���ѱ�ע {@literal @Since} ���ֶ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target)
	{
		return toJson(target, null, true, null, null, false);
	}
	
	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ֻ����ת����ͨ�� {@code JavaBean}����
	 * �÷���ֻ��ת������ {@literal @Expose} ע����ֶΣ�
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷�����ת������δ��ע���ѱ�ע {@literal @Since} ���ֶΣ�
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param datePattern
	 *            �����ֶεĸ�ʽ��ģʽ��
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, String datePattern)
	{
		return toJson(target, null, false, null, datePattern, true);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ֻ����ת����ͨ�� {@code JavaBean}����
	 * �÷���ֻ��ת������ {@literal @Expose} ע����ֶΣ�
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param version
	 *            �ֶεİ汾��ע��({@literal @Since})��
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Double version)
	{
		return toJson(target, null, false, version, null, true);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ֻ����ת����ͨ�� {@code JavaBean}* ����
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷�����ת������δ��ע���ѱ�ע {@literal @Since} ���ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param excludesFieldsWithoutExpose
	 *            �Ƿ��ų�δ��ע {@literal @Expose} ע����ֶΡ�
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, boolean excludesFieldsWithoutExpose)
	{
		return toJson(target, null, false, null, null, excludesFieldsWithoutExpose);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ֻ����ת����ͨ�� {@code JavaBean}����
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param version
	 *            �ֶεİ汾��ע��({@literal @Since})��
	 * @param excludesFieldsWithoutExpose
	 *            �Ƿ��ų�δ��ע {@literal @Expose} ע����ֶΡ�
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Double version, boolean excludesFieldsWithoutExpose)
	{
		return toJson(target, null, false, version, null, excludesFieldsWithoutExpose);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ͨ������ת��ʹ�÷��͵Ķ���
	 * �÷���ֻ��ת������ {@literal @Expose} ע����ֶΣ�
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷�����ת������δ��ע���ѱ�ע {@literal @Since} ���ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSSS
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param targetType
	 *            Ŀ���������͡�
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Type targetType)
	{
		return toJson(target, targetType, false, null, null, true);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ͨ������ת��ʹ�÷��͵Ķ���
	 * �÷���ֻ��ת������ {@literal @Expose} ע����ֶΣ�
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param targetType
	 *            Ŀ���������͡�
	 * @param version
	 *            �ֶεİ汾��ע��({@literal @Since})��
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Type targetType, Double version)
	{
		return toJson(target, targetType, false, version, null, true);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ��˷���ͨ������ת��ʹ�÷��͵Ķ���
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷�����ת������δ��ע���ѱ�ע {@literal @Snce} ���ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param targetType
	 *            Ŀ���������͡�
	 * @param excludesFieldsWithoutExpose
	 *            �Ƿ��ų�δ��ע {@literal @Expose} ע����ֶΡ�
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Type targetType, boolean excludesFieldsWithoutExpose)
	{
		return toJson(target, targetType, false, null, null, excludesFieldsWithoutExpose);
	}

	/**
	 * �����Ŀ�����ת���� {@code JSON} ��ʽ���ַ�<strong>�˷���ͨ������ת��ʹ�÷��͵Ķ���
	 * �÷�������ת�� {@code null} ֵ�ֶΣ�
	 * �÷���ת��ʱʹ��Ĭ�ϵ� ����/ʱ�� ��ʽ��ģʽ - {@code yyyy-MM-dd HH:mm:ss SSS}��
	 * 
	 * @param target
	 *            Ҫת���� {@code JSON} ��Ŀ�����
	 * @param targetType
	 *            Ŀ���������͡�
	 * @param version
	 *            �ֶεİ汾��ע��({@literal @Since})��
	 * @param excludesFieldsWithoutExpose
	 *            �Ƿ��ų�δ��ע {@literal @Expose} ע����ֶΡ�
	 * @return Ŀ������ {@code JSON} ��ʽ���ַ�
	 */
	public static String toJson(Object target, Type targetType, Double version, boolean excludesFieldsWithoutExpose)
	{
		return toJson(target, targetType, false, version, null, excludesFieldsWithoutExpose);
	}

	/**
	 * ����� {@code JSON} �ַ�ת����ָ�������Ͷ���
	 * 
	 * @param <T>
	 *            Ҫת����Ŀ�����͡�
	 * @param json
	 *            ��� {@code JSON} �ַ�
	 * @param token
	 *            {@code com.google.gson.reflect.TypeToken} ������ָʾ�����
	 * @param datePattern
	 *            ���ڸ�ʽģʽ��
	 * @return ��� {@code JSON} �ַ��ʾ��ָ�������Ͷ���
	 */
	public static <T> T fromJson(String json, TypeToken<T> token, String datePattern)
	{
		if (TextUtils.isEmpty(json))
		{
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if (TextUtils.isEmpty(datePattern))
		{
			datePattern = DEFAULT_DATE_PATTERN;
		}
		/*builder.registerTypeAdapter(Map.class, new HashMapDeserializer());*/
		Gson gson = builder.create();
		try
		{
			return gson.fromJson(json, token.getType());
		}
		catch (Exception ex)
		{
			Log.e(TAG, json + " �޷�ת��Ϊ " + token.getRawType().getName() + " ����!", ex);
			return null;
		}
	}

	/**
	 * ����� {@code JSON} �ַ�ת����ָ�������Ͷ���
	 * 
	 * @param <T>
	 *            Ҫת����Ŀ�����͡�
	 * @param json
	 *            ��� {@code JSON} �ַ�
	 * @param token
	 *            {@code com.google.gson.reflect.TypeToken} ������ָʾ�����
	 * @return ��� {@code JSON} �ַ��ʾ��ָ�������Ͷ���
	 */
	public static <T> T fromJson(String json, TypeToken<T> token)
	{
		return fromJson(json, token, null);
	}

	/**
	 * ����� {@code JSON} �ַ�ת����ָ�������Ͷ��󡣴˷���ͨ������ת����ͨ�� {@code JavaBean}
	 * ����
	 * 
	 * @param <T>
	 *            Ҫת����Ŀ�����͡�
	 * @param json
	 *            ��� {@code JSON} �ַ�
	 * @param clazz
	 *            Ҫת����Ŀ���ࡣ
	 * @param datePattern
	 *            ���ڸ�ʽģʽ��
	 * @return ��� {@code JSON} �ַ��ʾ��ָ�������Ͷ���
	 */
	public static <T> T fromJson(String json, Class<T> clazz, String datePattern)
	{
		if (TextUtils.isEmpty(json))
		{
			return null;
		}
		GsonBuilder builder = new GsonBuilder();
		if (TextUtils.isEmpty(datePattern))
		{
			datePattern = DEFAULT_DATE_PATTERN;
		}
	/*	builder.registerTypeAdapter(Map.class, new HashMapDeserializer());*/
		Gson gson = builder.create();
		try
		{
			return gson.fromJson(json, clazz);
		}
		catch (Exception ex)
		{
			Log.e(TAG, json + " �޷�ת��Ϊ " + clazz.getName() + " ����!", ex);
			return null;
		}
	}

	/**
	 * ����� {@code JSON} �ַ�ת����ָ�������Ͷ��󡣴˷���ͨ������ת����ͨ�� {@code JavaBean}
	 * ����
	 * 
	 * @param <T>
	 *            Ҫת����Ŀ�����͡�
	 * @param json
	 *            ��� {@code JSON} �ַ�
	 * @param clazz
	 *            Ҫת����Ŀ���ࡣ
	 * @return ��� {@code JSON} �ַ��ʾ��ָ�������Ͷ���
	 */
	public static <T> T fromJson(String json, Class<T> clazz)
	{
		return fromJson(json, clazz, null);
	}
	
}
