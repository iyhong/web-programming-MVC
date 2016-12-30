package spms.context;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.reflections.Reflections;

import spms.annotation.Component;


// 프로퍼티 파일을 이용한 객체 준비
public class ApplicationContext {
  Hashtable<String,Object> objTable = new Hashtable<String,Object>();
  
  public Object getBean(String key) {
    return objTable.get(key);
  }
  
  public ApplicationContext(String propertiesPath) throws Exception {
    Properties props = new Properties(); // Properties 는 Map 의 자식 <String, String>
    props.load(new FileReader(propertiesPath));	// FileReader 는 경로를 찾아가 문자열일때 inputStream 을해서 다시 파일로 만들어주고, 그파일을 Properties 에 로드
    // 1. Properties -> Hashtable
    prepareObjects(props);
    
    // 2. @Component search -> Hashtable
    prepareAnnotationObjects();
    
    // 3. setter 를 통해서 필요한 의존성 주입
    injectDependency();
  
  }
  private void prepareAnnotationObjects() throws InstantiationException, IllegalAccessException{
	 //1. @Component search
	  Reflections reflector = new Reflections("");	//생성자에 공백이 들어가면 모든 클래스를 검색하겟다임(src). 출발하는 패키지임
	  Set<Class <?>> list = reflector.getTypesAnnotatedWith(Component.class);
	 //2. value -> key , 객체 -> value
	  String key;
	  for(Class<?> clazz : list){
		  key = clazz.getAnnotation(Component.class).value();
		  objTable.put(key, clazz.newInstance()); 
	  }
	  
  }
  private void prepareObjects(Properties props) throws Exception {
    Context ctx = new InitialContext();
    String key = null;
    String value = null;
    
    for (Object item : props.keySet()) {
      key = (String)item;
      value = props.getProperty(key);
      if (key.startsWith("jndi.")) {
        objTable.put(key, ctx.lookup(value));
      } else {
        objTable.put(key, Class.forName(value).newInstance());
      }
    }
  }
  
  // injection 시작
  
  private void injectDependency() throws Exception {
    for (String key : objTable.keySet()) {
      //jndi로 시작하는 key는 값이 클래스를 가리키는 문자열이 아니라서 제외시킴
      if (!key.startsWith("jndi.")) {
        callSetter(objTable.get(key));
      }
    }
  }

  //setter 를 찾는데 setter 에 주입될 객체가 objTable 에 있는경우에만 주입..(없으면 주입할수 없자낭~)
  //주입하는순서는 중요하지 않다. 참조타입이므로 DataSource -> MemberDao -> 각Controller 로 받아야하는데,
  //dao가 DataSource를 주입받기전에 Controller에 주입했더라도 나중에 Dao에 DataSource에 주입하더라도 참조타입이어서 상관없음.
  private void callSetter(Object obj) throws Exception {
    Object dependency = null;
    for (Method m : obj.getClass().getMethods()) {
      if (m.getName().startsWith("set")) {
        dependency = findObjectByType(m.getParameterTypes()[0]);
        if (dependency != null) {
          m.invoke(obj, dependency);
        }
      }
    }
  }
  
  //objTable 에서 매개변수에 들어가야할 타입인 객체를 찾아서 리턴해줌
  private Object findObjectByType(Class<?> type) {
    for (Object obj : objTable.values()) {
      if (type.isInstance(obj)) {
        return obj;
      }
    }
    return null;
  }
  // injection 끝
}
