package com.tssk.form.business;

import com.tssk.form.consts.FormConsts;
import com.tssk.fw.business.base.BusinessMethod;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class FormDictQuestionBusiness {

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
}
