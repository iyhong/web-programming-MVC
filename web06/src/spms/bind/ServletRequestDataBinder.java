package spms.bind;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletRequest;

public class ServletRequestDataBinder {
	public static Object bind(ServletRequest request, Class<?> dataType, String dataName) throws Exception {
		
		//기본타입이거나 date or String 타입이면..
		//					Member(class)
		if (isPrimitiveType(dataType)) {
			return createValueObject(dataType, request.getParameter(dataName));
		//							Member							member
		//즉 기본타입이면 그대로 기본타입으로 만들어서 리턴시켜줌(메서드끝남)
		}
		
		Set<String> paramNames = request.getParameterMap().keySet();
		Object dataObject = dataType.newInstance();
		Method m = null;
		//		/no,name,email/		keySet
		for (String paramName : paramNames) {	//setter메서드 찾아서 하나씩 넣어줌
			m = findSetter(dataType, paramName);
			if (m != null) {
				//m은 메서드이름이어서 메서드이름갖고 메서드호출하려면 invoke() 사용
				//dataObject(Member)에있는 메서드중 paramName과 같은 setter메서드를 찾아서
				//메서드의 매개변수타입을 찾아와 타입과 값으로 객체를 만들고
				//dataObject 와 해당메서드에 입력할 매개변수 기본형datatype을 이용해서 셋팅해줌
				//paramNames(request 에서 가져온 요청매개변수값)에 담겨있는 개수만큼 반복
				m.invoke(dataObject, createValueObject(m.getParameterTypes()[0], request.getParameter(paramName)));
			}
		}
		return dataObject;
	}

	//기본형데이터타입인지 확인해서 맞으면 true, 아니면 false 리턴
	private static boolean isPrimitiveType(Class<?> type) {
		if (type.getName().equals("int") || type == Integer.class
				|| type.getName().equals("long") || type == Long.class
				|| type.getName().equals("float") || type == Float.class
				|| type.getName().equals("double") || type == Double.class
				|| type.getName().equals("boolean")	|| type == Boolean.class
				|| type == Date.class 
				|| type == String.class) {
			return true;
		}
		return false;
	}

	//기본형타입이면 setter메서드가 없기때문에 바로 wrapper 타입으로 객체만들어서 리턴
	private static Object createValueObject(Class<?> type, String value) {
		if (type.getName().equals("int") || type == Integer.class) {
			return new Integer(value);
		} else if (type.getName().equals("float") || type == Float.class) {
			return new Float(value);
		} else if (type.getName().equals("double") || type == Double.class) {
			return new Double(value);
		} else if (type.getName().equals("long") || type == Long.class) {
			return new Long(value);
		} else if (type.getName().equals("boolean") || type == Boolean.class) {
			return new Boolean(value);
		} else if (type == Date.class) {
			return java.sql.Date.valueOf(value);
		} else {
			return value;
		}
	}

	//
	//										Member		no,name,email
	private static Method findSetter(Class<?> type, String name) {
		//getMethods() 는 해당객체가 가지고있는 모든 메서드목록을 배열로 반환
		Method[] methods = type.getMethods();

		String propName = null;
		for (Method m : methods) {
			if (!m.getName().startsWith("set"))
				continue;

			propName = m.getName().substring(3);
			if (propName.toLowerCase().equals(name.toLowerCase())) {
				return m;
			}
		}
		return null;
	}
}
