
package com.tssk.form.business;

import com.tssk.form.consts.FormConsts;
import com.tssk.form.utils.question.*;
import com.tssk.fw.business.base.Business;
import com.tssk.fw.business.base.BusinessMethod;
import com.tssk.fw.business.bean.BusinessConst;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.business.utils.BusinessUtils;
import com.tssk.fw.dao.bean.PageObj;
import com.tssk.fw.dao.bean.QueryBean;
import com.tssk.fw.dao.runner.SqlRunner;
import com.tssk.fw.dao.utils.DaoUtils;
import com.tssk.fw.service.utils.FwUtils;
import com.tssk.fw.utils.date.DateUtils;
import com.tssk.fw.utils.lang.LangUtils;
import com.tssk.fw.utils.lang.StringUtils;

import java.lang.reflect.Array;
import java.util.*;

@Business(name="FormAnserBusiness")
public class FormAnswerBusiness {

	public SqlRunner getSqlRunner() {
		SqlRunner sqlRunner = DaoUtils.getSqlRunnerManger().getSqlRunner(FormConsts.DB_NAME);
		return sqlRunner;
	}

	/**
	 * 调查结果分析
	 */
	@BusinessMethod
	public BusinessResponse load(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		long formId = LangUtils.parseLong(request.getData("formId"));
		String appCode = BusinessUtils.getCurrentAppCode(request);
		String sql = "";
		sql = "select * from  form_question_base where form_id = ? and app_code = ?";
		List<Map<String, Object>> list = getSqlRunner().listBySql(request, sql, formId, appCode);
		sql = "select * from  form_question_grid where form_id = ? and app_code = ?";
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
				Map<String, Object> oneMap = GradeRadioUtils.wrapResult(bean,request);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_GRADE_CHECKBOX)) {
				Map<String, Object> oneMap = GradeCheckboxUtils.wrapResult(bean,request);
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
				Map<String, Object> oneMap = BaseGaugeUtils.wrapResult(bean,request);
				rsList.add(oneMap);
			} else if (type.equals(FormConsts.QUESITON_TYPE_MATRIX_GAUGE)) {
				Map<String, Object> oneMap = MatrixGaugeUtils.wrapResult(bean,request);
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
		rsMap.put("questions", rsList);
		response.setData(rsMap);
		return response;
	}

	/**
	 * 查看简答题内容
	 */
	@BusinessMethod
	public BusinessResponse listPage(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		// 获取用户所属租户编码
		long formId = LangUtils.parseLong(request.getData("formId"));
		long questionId = LangUtils.parseLong(request.getData("questionId"));
		String appCode = BusinessUtils.getCurrentAppCode(request);
		if (!StringUtils.isEmpty(appCode)) {
			Integer pageSize = LangUtils.parseInt(StringUtils.trimNull(request.getData("pageSize")),-1);
			int page = LangUtils.parseInt(StringUtils.trimNull(request.getData("page")),-1);
			if(pageSize<0 || page<0){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分页参数page,pageSize错误！");
			}else{
				int startRow = page*pageSize;
				String sql = "SELECT d.question_value FROM form_answer_question d WHERE d.form_id = ? AND d.question_id = ?";
				PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPageBySql(request, sql, startRow, pageSize,formId,questionId);
				response.setData(pageObj);
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 提交问卷
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse save(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		long formId = LangUtils.parseLong(request.getData("formId"));
		long userId = LangUtils.parseLong(request.getData("userId"));
		long createUser = LangUtils.parseLong(request.getData("createUser"));
		String data = StringUtils.trimNull(request.getData("data"));
		//准备数据
		int num = 0;
		long id = 0;
		Map<String, Object> insertMap = new HashMap<String, Object>();
		if (userId == 0){
			id = FwUtils.getIdService().nextId();
			String createTime = DateUtils.getTime();
			insertMap.put("id",id);
			insertMap.put("appCode",appCode);
			insertMap.put("formId",formId);
			insertMap.put("status",1);
			if (!StringUtils.isEmpty(createUser)){
				insertMap.put("sendUserId",createUser);
			}
			insertMap.put("createTime",createTime);
			this.getSqlRunner().insert(request, "form_answer", insertMap);
		}else{
			String sql = "UPDATE form_answer t SET t.status=1 WHERE t.form_id = ? AND t.send_user_id = ?";
			getSqlRunner().execBySql(request,sql,formId,userId);
			String sqlqer = "SELECT t.id as id FROM form_answer t  WHERE t.form_id = ? AND t.send_user_id = ?";
			id = LangUtils.parseLong(getSqlRunner().queryBySql(request,sqlqer,formId,userId).get("id"));
		}

		int count = 0;
		List<Object> dataMap = FwUtils.getJsonService().toList(data);
		//写用户答案(新的方式)
		Map<String, Object> insertQuestionMap = new HashMap<String, Object>();
		insertQuestionMap.put("formId",formId);
		insertQuestionMap.put("appCode",appCode);
		insertQuestionMap.put("answerId",id);
		insertQuestionMap.put("questionId",123);
		String sql = "SELECT t.receive_num AS renum  FROM form_main_base t WHERE t.id = ?";
		int renum = LangUtils.parseInt(getSqlRunner().queryBySql(request,sql,formId).get("renum"));
		renum = renum+1;
		String sql1 = "update form_main_base set receive_num=? where id = ?";
		getSqlRunner().execBySql(request,sql1,renum,formId);
		for (Object object : dataMap){
			Map<String, Object> oneMap = (Map<String, Object>) object;
			String sqlQutionType = "select t.base_type as type from form_question_base t where t.id = ? ";
			Map<String, Object> mapQutionType = getSqlRunner().queryBySql(request,sqlQutionType,oneMap.get("questionId"));

			//插入新的表
			String sqlQutionGrid = "select * from form_question_grid t where t.form_id = ? and t.question_id = ?";
			List<Map<String, Object>> listBySql = getSqlRunner().listBySql(request,sqlQutionGrid,formId,oneMap.get("questionId"));
			for (Map<String, Object> map:listBySql){
				map.put("id",FwUtils.getIdService().nextId());
				map.put("answer_id",id);
				if(mapQutionType.get("type").equals("gradeCheckbox")){
					List<Object> listArry = FwUtils.getJsonService().toList(oneMap.get("questionValue").toString());
					for (Object str:listArry){
						long ids = FwUtils.getIdService().nextId();
						insertQuestionMap.put("id",ids);
						insertQuestionMap.put("questionId",oneMap.get("questionId"));
						insertQuestionMap.put("questionValue",str);
						this.getSqlRunner().insert(request, "form_answer_question", insertQuestionMap);
						if (map.get("propValue1").equals(str)){
							map.put("prop_value15",1);
							break;
						}else{
							map.put("prop_value15",str);
						}
					}
				}else if (mapQutionType.get("type").equals("matrixGauge")){
					map.put("prop_value15",oneMap.get("questionValue").toString());
				}else {
					long ids = FwUtils.getIdService().nextId();
					insertQuestionMap.put("id",ids);
					insertQuestionMap.put("questionId",oneMap.get("questionId"));
					insertQuestionMap.put("questionValue",oneMap.get("questionValue").toString());
					this.getSqlRunner().insert(request, "form_answer_question", insertQuestionMap);
					if (null != map.get("propValue1") && map.get("propValue1").toString().length()>0){
						if (map.get("propValue1").equals(oneMap.get("questionValue").toString())){
							map.put("prop_value15",1);
						}
					}else{
						map.put("prop_value15",oneMap.get("questionValue").toString());
					}
				}
				getSqlRunner().insert(request,"form_answer_question_grid",map);
			}
			count++;
		}

		if (count != dataMap.size()) {
			response.setCodeAndMsg(BusinessConst.CODE_UNKNOW, "问卷提交失败，请稍后重试！");
		}else{
			response.setCodeAndMsg(200,"问卷提交成功！");
		}
		return response;
	}


	/**
	 * 原始答卷列表
	 */
	@BusinessMethod
	public BusinessResponse listOriginalPage(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		long formId = LangUtils.parseLong(request.getData("formID"));

		if (!StringUtils.isEmpty(appCode)) {
			Integer pageSize = LangUtils.parseInt(StringUtils.trimNull(request.getData("pageSize")),-1);
			int page = LangUtils.parseInt(StringUtils.trimNull(request.getData("page")),-1);
			if(pageSize<0 || page<0){
				response.setCodeAndMsg(BusinessConst.CODE_PARAM, "分页参数page,pageSize错误！");
			}else{
				long startRow = page * pageSize;
				String sql = "SELECT user_student.name as userName, cam_classes.name as className, cam_specialty.name as specialtyName, user_student.code as userCode, cam_classes.grade as grade, form_answer.id as reformId FROM form_answer LEFT JOIN user_student ON form_answer.send_user_id = user_student.sys_user_id LEFT JOIN cam_specialty ON user_student.specialty_id = cam_specialty.id LEFT  JOIN cam_classes ON user_student.class_id = cam_classes.id WHERE form_answer.form_id = ?" ;
				PageObj<List<Map<String, Object>>> pageObj = getSqlRunner().listPageBySql(request,sql,startRow,pageSize,formId);
				response.setData(pageObj);
			}
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 毕业要求调查分析
	 */
	@BusinessMethod
	public BusinessResponse resultRequirementsMet(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		long formId = LangUtils.parseLong(request.getData("formId"));
		if (!StringUtils.isEmpty(appCode)) {
			String sql = "SELECT\n" +
					"\ttra_graduation_claim.`code` AS code,\n" +
					"\ttra_graduation_claim.`id` AS id,\n" +
					"\ttra_graduation_claim.`name` AS name,\n" +
					"\ttra_graduation_claim.content AS content\n" +
					"FROM\n" +
					"\ttra_graduation_claim\n" +
					"WHERE\n" +
					"\ttra_graduation_claim.version_id = (\n" +
					"\t\tSELECT\n" +
					"\t\t\tt.ext1\n" +
					"\t\tFROM\n" +
					"\t\t\tform_main_base t\n" +
					"\t\tWHERE\n" +
					"\t\t\tt.id = ?" +
					"\t) ORDER BY (tra_graduation_claim.`code`+0);";
			List<Map<String, Object>> list = getSqlRunner().listBySql(request,sql,formId);
			List<Map<String, Object>> listResult = new ArrayList<>();
			List<Map<String, Object>> listjuzhen = new ArrayList<>();
			for(Map<String, Object> listObject : list){
				Map<String, Object> mapTitle = new HashMap<>();
				Map<String, Object> mapjuzhen = new HashMap<>();
				mapTitle.put("title","毕业要求"+listObject.get("code")+"-"+listObject.get("content"));
				mapjuzhen.put("title",listObject.get("code")+"."+listObject.get("name"));
				mapjuzhen.put("score1",0.68);
				double gameMax = 0;
				int gameCount = 0;
				//统计获取每个指标点的得分
				String sqlPoint = "SELECT v.id, v.name,v.content,v.code FROM tra_index_point v WHERE v.graduation_claim_id = ? ORDER BY (v.`code`+0);";
				List<Map<String, Object>> listPoint = getSqlRunner().listBySql(request,sqlPoint,listObject.get("id"));
				for (Map<String, Object> mapPoint:listPoint){
					mapPoint.put("code","指标点"+listObject.get("code")+"-"+mapPoint.get("code"));
					//开始游戏
					double gameScore = 0;
					double gameSelectScore = 0;
					String gameSql = "SELECT SUM(t.prop_value2) as gameScore FROM form_answer_question_grid t WHERE t.form_id = ?  and t.prop_key != 'gauge' and (t.prop_value12 = ? OR t.prop_value12 is NULL and t.prop_value12 = ?)";
					Map<String,Object> gameMap = getSqlRunner().queryBySql(request,gameSql,formId,mapPoint.get("id"),listObject.get("id"));
					if (null != gameMap.get("gamescore")){
						gameScore = Double.parseDouble(gameMap.get("gamescore").toString());
					}
					String gameSelectSql = "SELECT SUM(t.prop_value2) as gameScore FROM form_answer_question_grid t WHERE  t.prop_value15 IS NOT NULL and t.form_id = ?  and t.prop_key != 'gauge' and (t.prop_value12 = ? OR t.prop_value12 is NULL and t.prop_value12 = ?) LIMIT 1;";
					List<Map<String,Object>> gameSelectMap = getSqlRunner().listBySql(request,gameSelectSql,formId,mapPoint.get("id"),listObject.get("id"));
					if (null != gameMap.get("gamescore")){
						gameSelectScore = Double.parseDouble(gameSelectMap.get(0).get("gamescore").toString());
					}
					if (gameScore == 0){
						mapPoint.put("score",0);
					}else{
						mapPoint.put("score",(double)Math.round((gameSelectScore/gameScore)*100)/100);
					}
					gameMax += (double)Math.round((gameSelectScore/gameScore)*100)/100;
					gameCount++;
				}
				mapjuzhen.put("score2",(double)Math.round(gameMax*100)/gameCount/100);
				mapTitle.put("content",listPoint);
				listResult.add(mapTitle);
				listjuzhen.add(mapjuzhen);
			}

			Map<String, Object> result = new HashMap<>();
			result.put("obj1",listjuzhen);
			result.put("obj2",listResult);
			response.setData(result);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}

	/**
	 * 课程调查分析
	 */
	@BusinessMethod
	public BusinessResponse resultCourseQuality(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		String appCode = BusinessUtils.getCurrentAppCode(request);
		long formId = LangUtils.parseLong(request.getData("formId"));
		if (!StringUtils.isEmpty(appCode)) {
			String sql = "SELECT\n" +
					"\tt.`name`,\n" +
					"\tt.id\n" +
					"FROM\n" +
					"\ttra_course t\n" +
					"WHERE\n" +
					"\tt.id = (\n" +
					"\t\tSELECT\n" +
					"\t\t\tv.ext3\n" +
					"\t\tFROM\n" +
					"\t\t\tform_main_base v\n" +
					"\t\tWHERE\n" +
					"\t\t\tv.id = ?\n" +
					"\t);";
			List<Map<String, Object>> map = getSqlRunner().listBySql(request,sql,formId);
			Map<String, Object> result = new HashMap<>();
			result.put("title",map.get(0).get("name")+"课程目标达成情况");
			String sqlGoal = "SELECT t.`name`,t.content,t.id,t.course_id FROM tra_course_goal t WHERE t.course_id = ? AND t.is_deleted = 0;";
			List<Map<String, Object>>  listRe = getSqlRunner().listBySql(request,sqlGoal,map.get(0).get("id"));
			List<Map<String, Object>>  listResult = new ArrayList<>();
			for (Map<String, Object> map1 :listRe){
				//开始游戏
				double gameScore = 0;
				double gameSelectScore = 0;
				String gameSql = "SELECT SUM(t.prop_value2) as gameScore FROM form_answer_question_grid t WHERE t.form_id = ?  and t.prop_key != 'gauge' and (t.prop_value14 = ? OR t.prop_value14 is NULL and t.prop_value13 = ?)";
				Map<String,Object> gameMap = getSqlRunner().queryBySql(request,gameSql,formId,map1.get("id"),map1.get("course_id"));
				if (null != gameMap.get("gamescore")){
					gameScore = Double.parseDouble(gameMap.get("gamescore").toString());
				}
				String gameSelectSql = "SELECT SUM(t.prop_value2) as gameScore FROM form_answer_question_grid t WHERE  t.prop_value15 IS NOT NULL and t.form_id = ?  and t.prop_key != 'gauge' and (t.prop_value14 = ? OR t.prop_value14 is NULL and t.prop_value13 = ?) LIMIT 1;";
				List<Map<String,Object>> gameSelectMap = getSqlRunner().listBySql(request,gameSelectSql,formId,map1.get("id"),map1.get("course_id"));
				if (null != gameMap.get("gamescore")){
					gameSelectScore = Double.parseDouble(gameSelectMap.get(0).get("gamescore").toString());
				}
				if (gameScore == 0){
					map1.put("score",0);
				}else{
					map1.put("score",(double)Math.round((gameSelectScore/gameScore)*100)/100);
				}
				listResult.add(map1);
			}
			result.put("reGoal",listResult);
			response.setData(result);
		} else {
			response.setCodeAndMsg(BusinessConst.CODE_AUTH, "请先登录系统！");
		}
		return response;
	}


	/**
	 * 查看答题结果
	 * @param request
	 * @return
	 */
	@BusinessMethod
	public BusinessResponse queryQuestionAndAnswerAll(BusinessRequest request) {
		BusinessResponse response = new BusinessResponse();
		long formId = LangUtils.parseLong(request.getData("formId"));
		long userId = LangUtils.parseLong(request.getData("formIdById"));
		if(userId == 0){
			userId = LangUtils.parseLong(request.getData("userId"));
		}
		String appCode = BusinessUtils.getCurrentAppCode(request);
		String sql = "select * from  form_main_base where id = ? and app_code = ?";
		Map<String, Object> manBase = getSqlRunner().queryBySql(request, sql, formId, appCode);

		sql = "select * from  form_question_base where form_id = ? and app_code = ?";
		List<Map<String, Object>> list = getSqlRunner().listBySql(request, sql, formId, appCode);
		sql = "select * from  form_answer_question_grid where form_id = ? and app_code = ? and answer_id = ?";
		List<Map<String, Object>> attrs = getSqlRunner().listBySql(request, sql, formId, appCode,userId);
		List<Map<String, Object>> rsList = new ArrayList<>();
		for (Map<String, Object> map : list) {
			long id = LangUtils.parseLong(map.get("id"));
			QuestionBean bean = new QuestionBean();
			bean.setBase(map);
			bean.setFormType(Integer.parseInt(manBase.get("formType")+""));
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
}
