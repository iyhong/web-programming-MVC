package spms.bind;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletRequest;

public class ServletRequestDataBinder {
	//request의 parameter들로 객체의 setter메서드를 찾아 값을 넣어주고
	//객체를 만들어 리턴시켜주는 메서드
  public static Object bind(
      ServletRequest request, Class<?> dataType, String dataName) 
      throws Exception {
	  //만약 데이터타입이 기본타입이면
	  //createValueObject 메서드를 통해 기본타입의 wrapper클래스로 객체를 생성하고 
	  //requst의 parameter를 넣어주고 리턴시켜준다.
	  if (isPrimitiveType(dataType)) {
      return createValueObject(dataType, request.getParameter(dataName));
	  }
    //만약 기본타입이 아니면
	//request parameter에서 key값만 찾아 Set에 담고
    Set<String> paramNames = request.getParameterMap().keySet();
    //받은 타입으로 객체를 만들고
    Object dataObject = dataType.newInstance();
    Method m = null;
    //request의 parameter에 있던 key들의 배열을 반복
    for (String paramName : paramNames) {
    //findSetter메서드를 호출해서 parameter 키와 일치하는 set땡땡 메서드를 찾아 리턴해줌
    //모든 parameter들을 돌려가며 찾아보고 있으면 셋팅해주고 없으면 넘어감.
      m = findSetter(dataType, paramName);
      if (m != null) {
    	  //만약 메서드값이 null이 아니면 즉 set메서드가 있으면
    	  //그 메서드를 invoke(호출)하여 메서드의 매개변수에 기본형타입의 객체를 만들어 넣어줘서
    	  //set메서드로 객체의 프로퍼티를 셋팅한다.
        m.invoke(dataObject, createValueObject(m.getParameterTypes()[0], 
            request.getParameter(paramName)));
      }
    }
    //request에 있는 모든parameter들로 set메서드를 찾아 있으면 넣어주고 없으면 넘어가서
    //셋팅해준다음 셋팅된 객체를 리턴해준다.
    return dataObject;
  }
  //기본형타입인지 아닌지 확인
  private static boolean isPrimitiveType(Class<?> type) {
    if (type.getName().equals("int") || type == Integer.class ||
        type.getName().equals("long") || type == Long.class ||
        type.getName().equals("float") || type == Float.class ||
        type.getName().equals("double") || type == Double.class ||
        type.getName().equals("boolean") || type == Boolean.class ||
        type == Date.class || type == String.class) {
      return true;
    }
    return false;
  }
  //기본형데이터타입이면 기본형의wrapper 클래스로 객체만들어 리턴
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
  //클래스의 메서드들을 모두가져와서 request parameter key 와 동일한 set메서드를 찾아 리턴
  private static Method findSetter(Class<?> type, String name) {
    Method[] methods = type.getMethods();
    
    String propName = null;
    for (Method m : methods) {
      if (!m.getName().startsWith("set")) continue;
      
      propName = m.getName().substring(3);
      if (propName.toLowerCase().equals(name.toLowerCase())) {
        return m;
      }
    }
    return null;
  }
}
