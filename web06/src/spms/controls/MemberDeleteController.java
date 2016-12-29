package spms.controls;

import java.util.Map;

import spms.dao.MemberDao;

public class MemberDeleteController implements Controller {
	MemberDao memberDao;
	
	public MemberDeleteController setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
		return this;
	}



	@Override
	public String execute(Map<String, Object> model) throws Exception {
		//MemberDao memberDao = (MemberDao) model.get("memberDao");
		System.out.println("delete memberDao :"+memberDao);
		Integer no = (Integer) model.get("no");
		memberDao.delete(no);

		return "redirect:list.do";
	}
}
