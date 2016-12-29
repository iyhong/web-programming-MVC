package spms.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.bind.DataBinding;
import spms.bind.ServletRequestDataBinder;
import spms.controls.Controller;

@SuppressWarnings("serial")
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet {
	
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		String servletPath = request.getServletPath();
		try {
			Controller controller = null;
			Map<String, Object> model = new HashMap<String, Object>();
			ServletContext sc = this.getServletContext();
			//model.put("memberDao", sc.getAttribute("memberDao"));
			System.out.println("ServletPath:"+request.getServletPath());
			
			controller = (Controller) sc.getAttribute(servletPath);
			
			//만약 controller 가 DataBinding interface를 구현했다면
			if(controller instanceof DataBinding){
				
				// pageController.getDataBinders()호출하여 필요한 모델타입을 푼다 
				Object[] dataBinders = ((DataBinding) controller).getDataBinders();
				String dataName = null;
				Class<?> dataType = null;
				Object dataObj = null;
				
				for(Object o : dataBinders){
					System.out.println(o);
				}
				
				for(int i=0 ;i<dataBinders.length;i+=2){
					dataName = (String)dataBinders[i]; //member
					dataType = (Class)dataBinders[i+1]; //Member(class)
					dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
				}
				// 모델객체를 자동으로 만들어 주는 메서드를 호출
				model.put(dataName, dataObj);
			}
			
			
			// 컨트롤러를 호출을 통해  View이름을 리턴받음
			String viewUrl = controller.execute(model);
			// Map -> request.attribute
			for(String key : model.keySet()){
				request.setAttribute(key, model.get(key));
			}
			if (viewUrl.startsWith("redirect:")) {
				response.sendRedirect(viewUrl.substring(9));
				System.out.println("redirect url : "+viewUrl.substring(9));
				return;

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
}
