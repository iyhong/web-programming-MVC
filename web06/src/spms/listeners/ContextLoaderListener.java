package spms.listeners;

// 서버에서 제공하는 DataSource 사용하기
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import spms.controls.MemberAddController;
import spms.controls.MemberListController;
import spms.dao.MemberDao;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			
			System.out.println("ContextLoaderListener 실행");
			ServletContext sc = event.getServletContext();

			InitialContext initialContext = new InitialContext();
			DataSource ds = (DataSource) initialContext.lookup("java:comp/env/jdbc/studydb");

			MemberDao memberDao = new MemberDao();
			memberDao.setDataSource(ds);
			/*
			 * 1. 공통저장소(sc) : dao -> controller(+dao)
			 */
			
			//sc.setAttribute("memberDao", memberDao);
			
			sc.setAttribute("/member/list.do", new MemberListController().setMemberDao(memberDao));
			sc.setAttribute("/member/add.do", new MemberAddController().setMemberDao(memberDao));

			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}
