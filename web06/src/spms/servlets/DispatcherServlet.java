package spms.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import spms.controls.Controller;
import spms.controls.LogInController;
import spms.controls.LogOutController;
import spms.controls.MemberAddController;
import spms.controls.MemberDeleteController;
import spms.controls.MemberListController;
import spms.controls.MemberUpdateController;
import spms.dao.MemberDao;
import spms.vo.Member;

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
			
			if ("/member/list.do".equals(servletPath)) {
			} else if ("/member/add.do".equals(servletPath)) {
				//controller = (Controller) sc.getAttribute(servletPath);
				if (request.getParameter("email") != null) {
					model.put("member", new Member()
							.setEmail(request.getParameter("email"))
							.setPassword(request.getParameter("password"))
							.setName(request.getParameter("name")));
				}
			} else if ("/member/update.do".equals(servletPath)) {
				//controller = new MemberUpdateController();
				model.put("no", Integer.parseInt(request.getParameter("no")));
				if (request.getParameter("email") != null) {
					model.put("member", new Member()
							.setNo(Integer.parseInt(request.getParameter("no")))
							.setEmail(request.getParameter("email"))
							.setName(request.getParameter("name")));
				}
			} else if ("/member/delete.do".equals(servletPath)) {
				model.put("no", Integer.parseInt(request.getParameter("no")));
				//controller = new MemberDeleteController();
			} else if ("/auth/login.do".equals(servletPath)) {
				//controller = new LogInController();
				if(request.getParameter("email") != null){
					model.put("loginInfo", new Member()
							.setEmail(request.getParameter("email"))
							.setPassword(request.getParameter("password")));
					HttpSession session = request.getSession();
					model.put("session", session);
				}
			} else if ("/auth/logout.do".equals(servletPath)) {
				//controller = new LogOutController();
				HttpSession session = request.getSession();
				model.put("session", session);
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
