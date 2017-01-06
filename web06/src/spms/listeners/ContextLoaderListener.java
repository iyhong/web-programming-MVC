package spms.listeners;

// 프로퍼티 파일 적용 : ApplicationContext 사용
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import spms.context.ApplicationContext;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
  static ApplicationContext applicationContext;
  //ApplicateionContext 값을 주는 메서드
  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }
  
  @Override
  //톰캣이 실행되고나면 바로 실행되는 메서드
  public void contextInitialized(ServletContextEvent event) {
    try {
      ServletContext sc = event.getServletContext();
      //web.xml에 설정해놓은 context 에 저장된 문자열(/WEB-INF/application-context.properties) properties목록들
      //properties목록들은 컨트롤러 목록
      //목록들을 가져와서 ApplicationContext 객체를 생성하는데 생성자메서드에 propertiesPath를 넘겨주어
      //Path에 있는 properties 파일에 있는 문자열로 객체들을 생성하고 가지고있고
      //가지고있는 applicationContext 객체를 가진다.
      String propertiesPath = sc.getRealPath(
          sc.getInitParameter("contextConfigLocation"));
      applicationContext = new ApplicationContext(propertiesPath);
      
    } catch(Throwable e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void contextDestroyed(ServletContextEvent event) {}
}
