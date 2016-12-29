package spms.controls;

import java.util.Map;

import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.vo.Member;

public class MemberUpdateController implements Controller, DataBinding {
	private MemberDao memberDao;
	
	public MemberUpdateController setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
		return this;
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		//MemberDao memberDao = (MemberDao) model.get("memberDao");
		Integer no = (Integer) model.get("no");
		Member member = (Member) model.get("member");
		
		if (member.getEmail() == null) {
			System.out.println("no : "+no);
			member = memberDao.selectOne(no);
			model.put("member", member);
			System.out.println(member.getEmail()+", "+member.getName());
			return "/member/MemberUpdateForm.jsp";

		} else {
			memberDao.update(member);
			return "redirect:list.do";
		}
	}

	@Override
	public Object[] getDataBinders() {
		return new Object[]{
				"no", Integer.class, "member", spms.vo.Member.class
			};
	}
}
