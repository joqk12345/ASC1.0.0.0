package com.asc.app.webservice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

/**
 * webservice工具类
 * @author zhanglei
 *
 */
public class WebServiceUtil {
	private static String SERVICE_NAMESPACE = "https://192.168.20.159:8443/RadiusWeb/services/SmartClientService";
	//"https://12.1.1.1:31658/AuteView2RadiusWeb/services/SmartClientService";

	private static String SERVICE_URL = "https://192.168.20.159:8443/AuteView2RadiusWeb/services/SmartClientService";
	
	/**
	 * @param method
	 * @param paramNames
	 * @param paramValues
	 * @param responseClass
	 * @return
	 */
	public static List<Object> callService(String method, String [] paramNames, String [] paramValues, Class responseClass, boolean isArray){
		HttpTransportSE httpTranstation = new HttpTransportSE(SERVICE_URL);
		httpTranstation.debug = true;
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		
		SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE, method);
		
		for (int i = 0; i < paramNames.length; i++) {
			soapObject.addProperty(paramNames[i], paramValues[i]);
		}
		
		envelope.bodyOut = soapObject;
		try
		{
			httpTranstation.call(null, envelope);
			if (envelope.getResponse() != null)
			{
				SoapObject bodyIn = (SoapObject) envelope.bodyIn;
				SoapObject detail = (SoapObject) bodyIn.getProperty(method + "Return");
				return parseResponse(detail, responseClass, method, isArray);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param result
	 * @param classes
	 * @param method
	 * @return List<Object>
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static List<Object> parseResponse(SoapObject result, Class classes,String method, boolean isArray)
 throws InstantiationException,
			IllegalAccessException {
		List<Object> results = new ArrayList<Object>();

		if (isArray) {
			for (int i = 0; i < result.getPropertyCount(); i++) {
				Object instance = classes.newInstance();
				Field[] fields = classes.getDeclaredFields();

				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					String value = ((SoapObject) result.getProperty(i)).getProperty(fields[j].getName()).toString();
					setPropValue(instance, fields[j].getName(), value);
				}
				results.add(instance);
			}
		} else {
			Object instance = classes.newInstance();
			Field[] fields = classes.getDeclaredFields();

			for (int j = 0; j < fields.length; j++) {
				fields[j].setAccessible(true);
				if (result.getProperty(fields[j].getName()) == null) {
					return null;
				}
				String value = result.getProperty(fields[j].getName()).toString();
				setPropValue(instance, fields[j].getName(), value);
			}
			results.add(instance);
		}
		return results;
	}
	
	/**
	 * @param targetObj
	 * @param propName
	 * @param propValue
	 */
	public static void setPropValue(Object targetObj, String propName, Object propValue) {
		Class targetClass = targetObj.getClass();
		try {
			Class targetC = Class.forName(targetClass.getName());
			Field field = targetC.getDeclaredField(propName);
			field.setAccessible(true);
			if (field.getType().equals(Integer.class)) {
				field.set(targetObj,
						new Integer(Integer.valueOf(propValue.toString())));
			}
			if (field.getType().equals(int.class)) {
				field.setInt(targetObj, Integer.valueOf(propValue.toString()));
			}
			if (field.getType().equals(String.class)) {
				field.set(targetObj, propValue.toString());
			}
			if (field.getType().equals(double.class)) {
				field.setDouble(targetObj, Double.valueOf(propValue.toString()));
			}
			if (field.getType().equals(Double.class)) {
				field.set(targetObj,
						new Double(Double.valueOf(propValue.toString())));
			}
			if (field.getType().equals(float.class)) {
				field.setFloat(targetObj, Float.valueOf(propValue.toString()));
			}
			if (field.getType().equals(Float.class)) {
				field.set(targetObj,
						new Float(Float.valueOf(propValue.toString())));
			}
			if (field.getType().equals(Long.class)) {
				field.set(targetObj,
						new Long(Long.valueOf(propValue.toString())));
			}
			if (field.getType().equals(long.class)) {
				field.setFloat(targetObj, Long.valueOf(propValue.toString()));
			}
			if (field.getType().equals(short.class)) {
				field.setShort(targetObj, Short.valueOf(propValue.toString()));
			}
			if (field.getType().equals(Short.class)) {
				field.set(targetObj,
						new Short(Short.valueOf(propValue.toString())));
			}
			if (field.getType().equals(java.util.Date.class)) {
				field.set(targetObj, new Date(Date.parse(propValue.toString())));
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	

	public static void setGateWay(String gateWay) {
		SERVICE_NAMESPACE = "https://" + gateWay + ":31658/AuteView2RadiusWeb/services/SmartClientService";
		SERVICE_URL = "https://" + gateWay + ":31658/AuteView2RadiusWeb/services/SmartClientService";
	}
	

}
