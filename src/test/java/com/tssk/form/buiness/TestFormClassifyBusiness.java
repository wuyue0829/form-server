package com.tssk.form.buiness;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tssk.form.http.HttpUtils;
import com.tssk.fw.boot.Launcher;
import com.tssk.fw.business.bean.BusinessRequest;
import com.tssk.fw.business.bean.BusinessResponse;
import com.tssk.fw.http.consts.HttpConst;
import com.tssk.fw.service.utils.FwUtils;

public class TestFormClassifyBusiness {

	public static void main(String[] args) {
		boolean isLocal =  true;
		if(isLocal){
			init();
		}
		String service = "FormClassifyBusiness";
		add(isLocal,service);
//		modify(isLocal,service);
//		remove(isLocal,service);
//		query(isLocal,service);
//		listPage(isLocal,service);
//		listAll(isLocal,service);
	}

	public static void init() {
		Map<String, Object> cfg = new HashMap<String, Object>();
		cfg.put(HttpConst.HTTP_MODULE_ENABLE, "false");
		Launcher.start(cfg);
	}
	
	public static void add(boolean isLocal,String service){
		String method = "add";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("classifyCode", "biyedacheng");
		param.put("classifyName", "课程质量分类");
		param.put("flag", 2);
		param.put("status", 1);
		param.put("rowSort", 100);
		param.put("remarks", "我是课程质量分类备注信息");
		test(isLocal, service, method, param);
	}
	
	public static void modify(boolean isLocal,String service){
		String method = "modify";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", 676810636247699456l);
		param.put("classifyCode", "ceshifenlei001");
		param.put("classifyName", "测试分类001修改");
		param.put("receiveNum", "98");
		param.put("status", 1);
		param.put("rowSort", 10);
		param.put("remarks", "我是测试分类001修改备注信息");
		test(isLocal, service, method, param);
	}
	
	public static void remove(boolean isLocal,String service){
		String method = "remove";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", 674389438385623040l);
		test(isLocal, service, method, param);
	}
	
	public static void query(boolean isLocal,String service){
		String method = "query";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", 676810636247699456l);
		test(isLocal, service, method, param);
	}
	
	public static void listPage(boolean isLocal,String service){
		String method = "listPage";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("page", 1);
		param.put("pageSize", 15);
		param.put("keyWord","分类");
		test(isLocal, service, method, param);
	}

	public static void listAll(boolean isLocal,String service){
		String method = "listAll";
		Map<String, Object> param = new HashMap<String, Object>();
		test(isLocal, service, method, param);
	}
	
	public static void test(boolean isLocal,String service,String method,Map<String, Object> param) {
		if(isLocal){
			BusinessRequest request = new BusinessRequest();
			request.setService(service);
			request.setMethod(method);
			request.setVersion("v1");
			if(param!=null && param.size()>0){
				for(Entry<String, Object> entry:param.entrySet()){
					request.putData(entry.getKey(), entry.getValue());
				}
			}
			BusinessResponse response = FwUtils.getBusinessService().doHandler(request);
			System.out.println(FwUtils.getJsonService().toJson(response));
		}else{
			String url = "/"+service+"/"+method+"/v1";
			String str = HttpUtils.doHandler(url, param);
			System.out.println(str);
		}
	}

}
