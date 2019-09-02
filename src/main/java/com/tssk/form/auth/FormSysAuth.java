package com.tssk.form.auth;

import java.util.*;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.bean.BusinessConst;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.business.bi.BusinessInterceptor;
import com.tssk.fw.business.bi.BusinessInterceptorBefore;
import com.tssk.fw.cfg.base.CfgService;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;
import com.tssk.fw.log.base.Logger;
import com.tssk.fw.log.base.LoggerFactory;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.collection.CollectionUtils;
import com.tssk.fw.utils.date.DateUtils;
import com.tssk.fw.utils.lang.StringUtils;

@BusinessInterceptor(name = "FormSysAuth")
public class FormSysAuth implements BusinessInterceptorBefore {


	public SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}

	private List<String> noList = new ArrayList<>();
	// private final int sessionTimeout;// 单位分钟
	private static final Logger logger = LoggerFactory.getLogger(FormSysAuth.class);

	public FormSysAuth() {
		CfgService cfg = FwUtils.getCfgService();
		String noStr = StringUtils.trimNull(cfg.getCfgValue("auth", "auth.no"));
		noList = CollectionUtils.valueOfList(noStr);
		// sessionTimeout = LangUtils.parseInt(cfg.getCfgValue("auth",
		// "session.timeout"), 30 * 24 * 60);
	}

	@Override
	public BusinessResponse before(BusinessRequest request) {

		// 如果是非认证服务->直接跳过
		if (noList.contains(request.getKey())) {
			return new BusinessResponse();
		} else {
			BusinessResponse response = this.check(request);
			if (response.getCode() == BusinessConst.CODE_SUCCESS) {
				return new BusinessResponse();
			} else {
				return response;
			}
		}
	}

	public BusinessResponse check(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String json = request.getJson();
		logger.debug("=========" + json);
		if (json.length() > 0) {
			Map<String, Object> requestData = FwUtils.getJsonService().toMap(json);
			Set<String> keySet = requestData.keySet();
			for (String key : keySet) {
				request.putData(key, requestData.get(key));
			}
		}
		String page = StringUtils.trimNull(request.getData("page"));
		if (!StringUtils.isEmpty(page)) {
			try {
				int pageNum = Integer.parseInt(page);
				if (pageNum < 1) {
					pageNum = 1;
				}
				request.putData("page", pageNum - 1);
			} catch (Exception e) {

			}
		}

		String token = StringUtils.trimNull(request.getHeader("X-Token"));
//		String token = "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNpkj01OAzEMhe_i9Szy4zjJrNl0wwZxgCSTgYF2OpqkiFHVU3ABjoCEeiQQx8AFVKliZ_v5e37eQ9lFaIGk8ohkvCW0KIQnaGC9vRvG25JnaPdQlnIqV1fQ_l9uYMfaddhk9hJCSCeZr8vEvWkg3Yc5pHoyOrOepLNKO2wgpNDlzbLq_mQpkBC1RKuI5TLlNIR1PS8YLUh7JOmFQ3doYIw9tNKQlUY7y2mGUjhI3T7m8SbPT3z4IuHX8fj5-vLx_sbj_Dz9sN6i-WVDvTR7qAMzyuToO-oTiR59H6NKsg-BP7XKdCbA4RsAAP__.YrzExnG72pMxmvxvrW61efion4nYabSSNX2RdhD6VAQ";

		// 从redis获取登录用户信息
		if (StringUtils.isEmpty(token)) {
			response.setCode(BusinessConst.CODE_AUTH);
			response.setMsg("权限认证失败");
		}
		String sql = "select * from sys_token where token = ?";
		Map<String, Object> data = getSqlRunner().queryBySql(request, sql, token);
		if (data == null) {
			response.setCode(BusinessConst.CODE_AUTH);
			response.setMsg("权限认证失败");
		} else {
			request.putExt(BusinessConst.EXT_APP_ID, data.get("appId"));
			request.putExt("appCode", "tssk");
			request.putExt("roleType", 1);
			request.putExt("busiGroup1", "xuexiaobiaozhi");
			String sqlAcademy = "select * from sys_user where id = ?";
		    Map<String, Object> dataAcademy = getSqlRunner().queryBySql(request, sqlAcademy, data.get("userId"));
			request.putExt("busiGroup2", dataAcademy.get("academyId"));
			request.putExt("busiGroup3", dataAcademy.get("specialtyId"));
			request.putExt("userCode", "myCode");
			request.putExt(BusinessConst.EXT_USER_ID, data.get("userId"));
			request.putExt(BusinessConst.EXT_USER_CODE, data.get("userCode"));
			request.putExt(BusinessConst.EXT_USER_NAME, data.get("userName"));
		}
		return response;
	}

}
