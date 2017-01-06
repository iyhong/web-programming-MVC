package spms.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.bind.DataBinding;
import spms.bind.ServletRequestDataBinder;
import spms.context.ApplicationContext;
import spms.controls.Controller;
import spms.listeners.ContextLoaderListener;

// 페이지 컨트롤러를 찾을 때 ApplicationContext의 사용
@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		// 요청 url 에서 context명 제외한 문자열을 받는다
		String servletPath = request.getServletPath();
		System.out.println("servletPath:"+servletPath);
		try {
			// ContextLoaderListener의 getApplicationContext()메서드를 호출해
			// applicationContext를 얻어온다.
			// ContextLoaderListener는 ApplicationContext에서 properties에 등록된 것들을
			// Hashtable에 객체로 만들어 담아두고 Dependency Injection까지 완료시킨다.
			ApplicationContext ctx = ContextLoaderListener.getApplicationContext();

			// 페이지 컨트롤러에게 전달할 Map model 객체를 준비한다.
			HashMap<String, Object> model = new HashMap<String, Object>();
			// 서블릿에서 사용할 session을 model Map에 담는다.
			model.put("session", request.getSession());

			// 컨트롤러 객체 가져오기
			// 요청주소에 해당되는 객체(controller)의 객체를 applicationContext에있는 Hashtable에서 가져온다.
			Controller pageController = (Controller) ctx.getBean(servletPath);
			if (pageController == null) {
				throw new Exception("요청한 서비스를 찾을 수 없습니다.");
			}
			// 만약 컨트롤러 객체가 Databinding 을 상속받앗으면
			// prepareRequestData메서드를 호출해서
			// 컨트롤러가 자신이 필요한 객체목록을 보내준 것들의 객체를 만들어서 
			// model에 넣어줌.
			if (pageController instanceof DataBinding) {
				prepareRequestData(request, model, (DataBinding) pageController);
			}

			// 페이지 컨트롤러의 execute메서드에 model을 매개변수로 호출한다.
			// 여기 model에는 session 과 각 controller가 필요하다고 binding 한 객체들이 담겨있고
			// binding한 객체들은 request에 있는 값들 과 binding한 객체가 가지고 있는 set메서드와 비교해서
			// request값을 넣어주어 값이 들어있는 객체가 담겨있는것임.
			// 그model을 controller에게 넘겨주어 컨트롤러가 수행하고
			// redirect 또는 forward 할지 문자열로 넘겨준다.
			String viewUrl = pageController.execute(model);

			// Map model 객체에 저장된 값들을 request에 전부 복사한다.
			for (String key : model.keySet()) {
				request.setAttribute(key, model.get(key));
			}
			// 컨트롤러로부터 리턴받은 문자열에 redirect: 문자열이 있으면 redirect시키고
			if (viewUrl.startsWith("redirect:")) {
				response.sendRedirect(viewUrl.substring(9));
				return;
			// redirect: 문자열이 없으면 include(or forward) 시킨다.
			} else {
				RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
				rd.include(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e);
			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
			rd.forward(request, response);
		}
	}
	// request parameter 를 통해 얻어온 값들과 요청한주소의 클래스 객체를 가지고 (DataBinding 구현체)
	// 요청한주소의 클래스가 필요한 객체를 만들어주는데
	// 필요한 객체가 비어있으면 의미가없으므로
	// request parameter 와 필요한객체가 가지고있는 set메서드를 찾아 request parameter값을 넣어줘서
	// 필요한 객체를 채워주고 그 객체를 model 이라는 HashMap에 담아 준다.
	private void prepareRequestData(HttpServletRequest request, HashMap<String, Object> model, DataBinding dataBinding)
			throws Exception {
		// dataBinding 은 Controller 객체를 DataBinding 타입으로 담은것
		// 즉 Controller객체의 getDataBinders() 메서드를 호출하여 자신에게 필요한 매개변수의 타입, 변수명을 넘겨받는다.
		Object[] dataBinders = dataBinding.getDataBinders();
		String dataName = null;
		Class<?> dataType = null;
		Object dataObj = null;
		
		// 넘겨받은 매개변수의 타입, 변수명의 갯수만큼 반복해서
		// 첫번째(홀수번)에 변수명을 dataName에 넣고 두번째것(짝수번)을 dataType에 넣는다.
		// 얻은 dataName(변수명) 과 dataType(클래스명) 으로 객체를 생성하고
		// model에 변수명과 생성한 객체를 넣는다.
		// 이를 반복..
		for (int i = 0; i < dataBinders.length; i += 2) {
			dataName = (String) dataBinders[i];
			dataType = (Class<?>) dataBinders[i + 1];
			// ServletRequestDatabinder의 bind메서드는 request, 클래스명, 변수명 을 매개변수로 넘겨주면
			// 넘겨받은 클래스명(dataType), 변수명(dataName)으로 객체를 만들고
			// request에서 담겨있는 parameter들의 key로 객체의 setter메서드를 찾아 setting해준다.
			// 즉 request에서 넘겨받은 값들이 담긴 객체를 만들어 리턴해준다.
			dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
			model.put(dataName, dataObj);
		}
	}
}
