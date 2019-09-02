package com.tssk.form.business;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.base.Business;
import com.tssk.fw.business.base.BusinessMethod;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;

@Business(name="FormMyDataBusiness")
public class FormMyDataBusiness {

	public SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}

	@BusinessMethod
	public BusinessResponse load(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		return response;
	}

	@BusinessMethod
	public BusinessResponse save(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		return response;
	}


	/**
	 * 毕业要求达成调查分析
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse graduationAnalysis(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		return response;
	}
}
