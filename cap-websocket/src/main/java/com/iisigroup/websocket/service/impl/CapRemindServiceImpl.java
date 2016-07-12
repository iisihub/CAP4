package com.iisigroup.websocket.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.iisigroup.cap.auth.model.User;
import com.iisigroup.cap.base.dao.RemindDao;
import com.iisigroup.cap.base.dao.RemindsDao;
import com.iisigroup.cap.base.model.Remind;
import com.iisigroup.cap.base.model.Reminds;
import com.iisigroup.cap.operation.simple.SimpleContextHolder;
import com.iisigroup.cap.security.dao.IUserDao;
import com.iisigroup.cap.security.model.CapUserDetails;
import com.iisigroup.cap.service.AbstractService;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.cap.utils.CapWebUtil;
import com.iisigroup.websocket.service.CapRemindService;

@Service
public class CapRemindServiceImpl extends AbstractService implements
		CapRemindService {

	// private static final Logger logger = LoggerFactory
	// .getLogger(CapReminderService.class);

	@Resource
	RemindDao remindDao;

	@Resource
	RemindsDao remindsDao;

	@Resource
	private CapSystemConfig config;

	@Override
	public List<Reminds> getRemindItems(String[] styleTyp, String locale) {
		if (CapString.isEmpty(locale)) {
			locale = getLocale();
		}
		return remindsDao.findCurrentRemindItem(styleTyp, locale);
	}

	@Override
	public void saveReminds(Reminds remind) {
		remindsDao.merge(remind);
	}

	public String getLocale() {
		return SimpleContextHolder.get(CapWebUtil.localeKey) == null ? "zh_TW"
				: SimpleContextHolder.get(CapWebUtil.localeKey).toString();
	}

	// 當有security 由SessionRegistry取得所有session資料
	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	@Override
	public HashMap<String, CapUserDetails> getCurrentUser() {
		HashMap<String, CapUserDetails> allPrincipal = new HashMap<String, CapUserDetails>();
		List<Object> principals = sessionRegistry.getAllPrincipals();
		if (!CollectionUtils.isEmpty(principals)) {
			for (Object principal : principals) {
				if (principal instanceof CapUserDetails) {
					CapUserDetails userDetails = (CapUserDetails) principal;
					allPrincipal.put(userDetails.getUserId(), userDetails);
				}
			}
		}
		return allPrincipal;
	}

	@Resource
	IUserDao<User> usrDao;

	@Override
	public String getUsrEmail(String usrId) {
		User user = usrDao.getUserByLoginId(usrId, null);
		if (user != null) {
			return user.getEmail();
		}
		return "";
	}

	@Override
	public Remind findRemind(String pid) {
		return remindDao.findByPid(pid);
	}

}
