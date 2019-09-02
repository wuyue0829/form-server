package com.tssk.form.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tssk.form.consts.FormConsts;
import com.tssk.form.utils.question.BaseGaugeUtils;
import com.tssk.form.utils.question.BaseRemarkUtils;
import com.tssk.form.utils.question.BaseTextAreaUtils;
import com.tssk.form.utils.question.BaseTextUtils;
import com.tssk.form.utils.question.GradeCheckboxUtils;
import com.tssk.form.utils.question.GradeRadioUtils;
import com.tssk.form.utils.question.MatrixGaugeUtils;
import com.tssk.form.utils.question.QuestionBean;
import com.tssk.form.utils.question.SplitLineUtils;
import com.tssk.form.utils.question.UploadFileUtils;
import com.tssk.fw.business.base.Business;
import com.tssk.fw.business.base.BusinessMethod;
import com.tssk.fw.business.bean.BusinessConst;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.business.utils.BusinessUtils;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.ts.Transactional;
import com.tssk.fw.dao.utils.DaoUtils;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

@Business(name = "FormTemplateQuestionBusiness")
public class FormTemplateQuestionBusiness {

	public SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}

	@BusinessMethod
	public BusinessResponse queryQuestionOne(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		long formId = LangUtils.parseLong(request.getData("formId"));
		long questionId = LangUtils.parseLong(request.getData("questionId"));
		String appCode = BusinessUtils.getCurrentAppCode(request);
		String sql = "select * from  form_template_question_base where id = ? and app_code = ? and id = ?";
		List<Map<String, Object>> list = getSqlRunner().listBySql(request, sql, formId, appCode, questionId);
		sql = "select * from  form_template_question_grid where id = ? and app_code = ? and question_id = ?";
		List<Map<String, Object>> attrs = getSqlRunner().listBySql(request, sql, formId, appCode, questionId);
		List<Map<String, Object>> rsList = new ArrayList<>();
		for (Map<String, Object> map : list) {
			long id = LangUtils.parseLong(map.get("id"));
			QuestionBean bean = new QuestionBean();
			bean.setBase(map);
			bean.setGrids(QuestionBean.getAttrList(attrs, id));
			String type = StringUtils.trimNull(map.get("baseType"));
			if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXT)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXTAREA)) {
				Map<String, Object> oneMap = BaseTextAreaUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_RADIO)) {
				Map<String, Object> oneMap = GradeRadioUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_CHECKBOX)) {
				Map<String, Object> oneMap = GradeCheckboxUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_UPLOAD_FILE)) {
				Map<String, Object> oneMap = UploadFileUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_SPLIT_LINE)) {
				Map<String, Object> oneMap = SplitLineUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_REMARK)) {
				Map<String, Object> oneMap = BaseRemarkUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_GAUGE)) {
				Map<String, Object> oneMap = BaseGaugeUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_MATRIX_GAUGE)) {
				Map<String, Object> oneMap = MatrixGaugeUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_NAME)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PHONE)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_EMAIL)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SEX)) {
				Map<String, Object> oneMap = GradeRadioUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_ACADEMY)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SPECIALTY)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_INDUSTRY)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PROFESSION)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			}
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		if (rsMap.size() > 0) {
			rsMap = rsList.get(0);
		}
		response.setData(rsMap);
		return response;
	}

	@BusinessMethod
	public BusinessResponse saveQuestionOne(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		long formId = LangUtils.parseLong(request.getData("formId"));
		long questionId = LangUtils.parseLong(request.getData("questionId"));
		String data = StringUtils.trimNull(request.getData("data"));
		Map<String, Object> oneMap = FwUtils.getJsonService().toMap(data);
		String sql = "delete from form_template_question_base where app_code = ? and form_id = ? and id = ?";
		getSqlRunner().execBySql(request, sql, appCode, formId, questionId);
		sql = "delete from form_template_question_grid where app_code = ? and form_id = ? and question_id = ?";
		getSqlRunner().execBySql(request, sql, appCode, formId, questionId);
		List<QuestionBean> beans = new ArrayList<QuestionBean>();
		String type = StringUtils.trimNull(oneMap.get("type"));
		if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXT)) {
			oneMap.put("baseType", type);
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXTAREA)) {
			oneMap.put("baseType", type);
			QuestionBean bean = BaseTextAreaUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_RADIO)) {
			oneMap.put("baseType", type);
			QuestionBean bean = GradeRadioUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_CHECKBOX)) {
			oneMap.put("baseType", type);
			QuestionBean bean = GradeCheckboxUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_UPLOAD_FILE)) {
			oneMap.put("baseType", type);
			QuestionBean bean = UploadFileUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_SPLIT_LINE)) {
			oneMap.put("baseType", type);
			QuestionBean bean = SplitLineUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_REMARK)) {
			oneMap.put("baseType", type);
			QuestionBean bean = BaseRemarkUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_GAUGE)) {
			oneMap.put("baseType", type);
			QuestionBean bean = BaseGaugeUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_MATRIX_GAUGE)) {
			oneMap.put("baseType", type);
			QuestionBean bean = MatrixGaugeUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_NAME)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PHONE)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_EMAIL)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SEX)) {
			oneMap.put("baseType", "gradeRadio");
			QuestionBean bean = GradeRadioUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_ACADEMY)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SPECIALTY)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_INDUSTRY)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PROFESSION)) {
			oneMap.put("baseType", "baseText");
			QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
			beans.add(bean);
		}
		for (QuestionBean questionBean : beans) {
			getSqlRunner().insert(request, "form_template_question_base", questionBean.getBase());
			List<Map<String, Object>> list = questionBean.getGrids();
			for (Map<String, Object> tempMap : list) {
				getSqlRunner().insert(request, "form_template_question_grid", tempMap);
			}
		}
		return response;

	}

	@BusinessMethod
	public BusinessResponse queryQuestionAll(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		long formId = LangUtils.parseLong(request.getData("formId"));
		String appCode = BusinessUtils.getCurrentAppCode(request);

		String sql = "select * from  form_template_main_base where id = ? and app_code = ?";
		Map<String, Object> manBase = getSqlRunner().queryBySql(request, sql, formId, appCode);
		if(manBase == null ){
			sql = "select * from  form_main_base where id = ? and app_code = ?";
			manBase = getSqlRunner().queryBySql(request, sql, formId, appCode);
			formId = LangUtils.parseLong(manBase.get("templateId"));
			sql = "select * from  form_template_main_base where id = ? and app_code = ?";
			manBase = getSqlRunner().queryBySql(request, sql, formId, appCode);
		}

		sql = "select * from  form_template_question_base where form_id = ? and app_code = ?";
		List<Map<String, Object>> list = getSqlRunner().listBySql(request, sql, formId, appCode);
		sql = "select * from  form_template_question_grid where form_id = ? and app_code = ?";
		List<Map<String, Object>> attrs = getSqlRunner().listBySql(request, sql, formId, appCode);
		List<Map<String, Object>> rsList = new ArrayList<>();
		for (Map<String, Object> map : list) {
			long id = LangUtils.parseLong(map.get("id"));
			QuestionBean bean = new QuestionBean();
			bean.setBase(map);
			bean.setGrids(QuestionBean.getAttrList(attrs, id));
			String type = StringUtils.trimNull(map.get("baseType"));
			if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXT)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXTAREA)) {
				Map<String, Object> oneMap = BaseTextAreaUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_RADIO)) {
				Map<String, Object> oneMap = GradeRadioUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_CHECKBOX)) {
				Map<String, Object> oneMap = GradeCheckboxUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_UPLOAD_FILE)) {
				Map<String, Object> oneMap = UploadFileUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_SPLIT_LINE)) {
				Map<String, Object> oneMap = SplitLineUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_REMARK)) {
				Map<String, Object> oneMap = BaseRemarkUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_GAUGE)) {
				Map<String, Object> oneMap = BaseGaugeUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_MATRIX_GAUGE)) {
				Map<String, Object> oneMap = MatrixGaugeUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_NAME)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PHONE)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_EMAIL)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SEX)) {
				Map<String, Object> oneMap = GradeRadioUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_ACADEMY)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SPECIALTY)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_INDUSTRY)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PROFESSION)) {
				Map<String, Object> oneMap = BaseTextUtils.wrap(bean);
				rsList.add(oneMap);
			}
		}
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("form", manBase);
		rsMap.put("questions", rsList);
		response.setData(rsMap);
		return response;
	}

	@BusinessMethod
	@Transactional(name = FormConsts.DB_NAME)
	public BusinessResponse saveQuestionAll(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		long formId = LangUtils.parseLong(request.getData("formId"));
		String data = StringUtils.trimNull(request.getData("data"));
		if (data.length() == 0) {
			response.setCodeAndMsg(BusinessConst.CODE_PARAM, "问卷数据为空！");
			return response;
		}
		String formTitle = StringUtils.trimNull(request.getData("formTitle"));
		String remarks = StringUtils.trimNull(request.getData("remarks"));
		String endWord = StringUtils.trimNull(request.getData("endWord"));
		String sql = "update form_main_base set form_name=?,remarks = ?,end_word = ? where app_code = ? and id = ?";
		int result = getSqlRunner().execBySql(request, sql, formTitle, remarks, endWord, appCode, formId);

		if(result == 0){
			List<Object> dataMap = FwUtils.getJsonService().toList(data);
			sql = "delete from form_template_question_base where app_code = ? and form_id = ?";
			getSqlRunner().execBySql(request, sql, appCode, formId);
			sql = "delete from form_template_question_grid where app_code = ? and form_id = ?";
			getSqlRunner().execBySql(request, sql, appCode, formId);
			List<QuestionBean> beans = new ArrayList<QuestionBean>();
			for (Object object : dataMap) {
				@SuppressWarnings("unchecked")
				Map<String, Object> oneMap = (Map<String, Object>) object;
				String type = StringUtils.trimNull(oneMap.get("type"));
				if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXT)) {
					oneMap.put("baseType", type);
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_TEXTAREA)) {
					oneMap.put("baseType", type);
					QuestionBean bean = BaseTextAreaUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_RADIO)) {
					oneMap.put("baseType", type);
					QuestionBean bean = GradeRadioUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_CHECKBOX)) {
					oneMap.put("baseType", type);
					QuestionBean bean = GradeCheckboxUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_UPLOAD_FILE)) {
					oneMap.put("baseType", type);
					QuestionBean bean = UploadFileUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_SPLIT_LINE)) {
					oneMap.put("baseType", type);
					QuestionBean bean = SplitLineUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_REMARK)) {
					oneMap.put("baseType", type);
					QuestionBean bean = BaseRemarkUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_BASE_GAUGE)) {
					oneMap.put("baseType", type);
					QuestionBean bean = BaseGaugeUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_MATRIX_GAUGE)) {
					oneMap.put("baseType", type);
					QuestionBean bean = MatrixGaugeUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_NAME)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PHONE)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_EMAIL)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SEX)) {
					oneMap.put("baseType", "gradeRadio");
					QuestionBean bean = GradeRadioUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_ACADEMY)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_SPECIALTY)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_INDUSTRY)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				} else if (type.equals(FormConsts.QUESITON_TYPE_EXT_PROFESSION)) {
					oneMap.put("baseType", "baseText");
					QuestionBean bean = BaseTextUtils.unwrap(appCode, formId, oneMap);
					beans.add(bean);
				}
			}
			for (QuestionBean questionBean : beans) {
				getSqlRunner().insert(request, "form_template_question_base", questionBean.getBase());
				List<Map<String, Object>> list = questionBean.getGrids();
				for (Map<String, Object> tempMap : list) {
					getSqlRunner().insert(request, "form_template_question_grid", tempMap);
				}
			}
		}
		return response;
	}
}
