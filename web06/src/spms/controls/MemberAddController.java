package spms.controls;

import java.util.Map;

import spms.bind.DataBinding;
import spms.dao.MemberDao;
import spms.vo.Member;

public class MemberAddController implements Controller, DataBinding {
	private MemberDao memberDao;
	
	public MemberAddController setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
		return this;
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		Member member = (Member) model.get("member");
		
		if (member.getEmail() == null) { // 입력폼을 요청할 때
			return "/member/MemberForm.jsp";

		} else { // 회원 등록을 요청할 때
			
			//MemberDao memberDao = (MemberDao) model.get("memberDao");

			memberDao.insert(member);

			return "redirect:list.do";
		}
	}

	@Override
	public Object[] getDataBinders() {
		
		return new Object[]{
			"member",spms.vo.Member.class
		};
	}
}
