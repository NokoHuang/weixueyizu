package com.weixue.Utils;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 * 
 */
public class Constants_Url {//http://192.168.0.131/
//	public static final String HEAD_PATH = "http://wxyzinterface.zhanyun360.com/";
//	public static final String FIRST_PATH = "http://222.126.246.151:9120";
	public static final String HEAD_PATH = "http://wxyzinterface.zhanyun360.com/";
	public static final String FIRST_PATH = "http://wxyzinterface.zhanyun360.com";

	// http://222.126.246.151:9120/app/examservices/papers/topic?utid=1&uid=1
	/** 检查是否有版本的地址 */
	public static final String IS_NEW_VERSION = HEAD_PATH
			+ "Service1.svc/version";

	/** 下载新版本的地址 */
	public static final String DOWNLOAD_NEW_VERSION = HEAD_PATH
			+ "apk/Weixue.apk";

	/** 新版本的本机存放位置 要先判断是否有SD卡 */
	public static final String APK_PATH = (android.os.Environment
			.getExternalStorageDirectory()).getAbsolutePath() + "/weixue/apk/";

	/** 用户登录 */
	public static final String LOGIN_PATH = FIRST_PATH
			+ "/app/userservices/login_verify";

	/** 用户上传头像 */
	public static final String UPLOAD_PERSONHEAD = FIRST_PATH
			+ "/app/userservices/upimg";

	/** 用户信息修改 */
	public static final String UPDATE_PERSONINFO = FIRST_PATH
			+ "/app/userservices/renewal";

	/** 用户注册 */
	public static final String REGISTER = FIRST_PATH
			+ "/app/userservices/register";

	/** 用户签到 */
	public static final String PERSON_CHECKIN = FIRST_PATH
			+ "/app/userservices/checkin";

	/** 获取用户信息 */
	public static final String PERSON_INFO = FIRST_PATH
			+ "/app/userservices/userinfo";
	/** 所有文件的地址 */
	public static final String FILE_PATH = (android.os.Environment
			.getExternalStorageDirectory()).getAbsolutePath() + "/weixue";
	/** 图片下载到本机的总地址 */
	public static final String PIC_PATH = (android.os.Environment
			.getExternalStorageDirectory()).getAbsolutePath() + "/weixue/image";
	/** 图片SD卡缓存地址 */
	public static final String PIC_CACHE_PATH = PIC_PATH + "/cache";
	/** 拍照图片存放地址 */
	public static final String PIC_TAKEPHOTO_PATH = PIC_PATH + "";
	/** 用户头像存放地址 */
	public static final String PIC_USERHEAD_PATH = PIC_PATH + "/head";
	/** 其他SD卡存放地址 */
	public static final String OTHERFILE_PACH = (android.os.Environment
			.getExternalStorageDirectory()).getAbsolutePath() + "/weixue/other";

	/** 用户已经加入的课程 需要参数uid，pageindex，pagecount(get请求) */
	public static final String ADDED_COURSE = FIRST_PATH
			+ "/app/userservices/courses/join_list";

	/** 根据课程 ID 获取课程详细信息.get请求格式 需要参数cid=762738 */
	public static final String GETCOURSE_DETAIL = FIRST_PATH
			+ "/app/courseservices/courses/detail";

	/**
	 * 根据课程种类获取课程,如0:在线课程、1:直播课程。在线课程包括1:精品课程、2:推荐课程、3:公开课.get 请求格式：
	 * courses/category_list?type=1&online=0&pageIndex=2&pageCount=20
	 */
	public static final String GETCOURSE_BYTYPE = FIRST_PATH
			+ "/app/courseservices/courses/category_list";

	/**
	 * 根据课程ID获取所有课件(课件类型0:所有，1：视频，2：ppt，3：work，4：pdf，5：...).get请求格式：coursewares/
	 * list?cid=728393&type=0&pageIndex=2&pageCount=20
	 */
	public static final String GETCOURSEWARE_BYCOURSEID = FIRST_PATH
			+ "/app/courseservices/coursewares/list";

	/**
	 * 获取所有动态，全部(0:all) 、校园(1:sch)、好友(2:fri) 、个人(3:per)，根据不同 tag 进行获取.get 请求格式：
	 * status/list?uid=738293&tag=0&pageIndex=2&pageCount=20
	 */
	public static final String GETSTATUS = FIRST_PATH
			+ "/app/statusservices/status/list";

	/**
	 * 保存发表动态.(1:个人发表的动态，2：系统自动发布的动态).post 表单格式：uid=789218&content=我今天学习了很多知识！
	 * &type=1&picdata=LKBHH#@@#$
	 */
	public static final String PUBLISH_STATUS = FIRST_PATH
			+ "/app/statusservices/status/create";

	/** 获取所有专业.get请求格式：majors/list?pageIndex=2&pageCount=20 */
	public static final String MAJORS = FIRST_PATH
			+ "/app/examservices/majors/list";

	/** 根据专业 ID 获取所有课程.get请求格式：courses/list?mid=762683&pageIndex=2&pageCount=20 */
	public static final String MAJOR_BYID = FIRST_PATH
			+ "/app/courseservices/courses/list";

	/** 获取横幅 */
	public static final String BANNER = FIRST_PATH
			+ "/app/PositionServices/GetAllCarouselImage/list";
	// /** 获取横幅 */
	// public static final String BANNER = FIRST_PATH
	// + "/app/courseservices/courses/banner_list";

	/** 收藏课程 */
	public static final String COLLECT_COURSE = FIRST_PATH
			+ "/app/courseservices/coursewares/collect";

	/** 获取试题 **/
	public static final String GET_TITLE = FIRST_PATH
			+ "/app/examservices/papers/savenext";

	/** 获取收藏课程 */
	public static final String GETCOLLECT_COURSE = FIRST_PATH
			+ "/app/userservices/courses/college_list";

	/** 用户加入课程 */
	public static final String ADDCOURSE = FIRST_PATH
			+ "/app/courseservices/coursewares/revise";

	/** 添加好友 */
	public static final String ADD_FRIEND = FIRST_PATH
			+ "/app/userservices/friends/create";

	/** 搜索课程 */
	public static final String SEARCHCOURSE = FIRST_PATH
			+ "/app/courseservices/coursewares/search";

	/** 查看评论 */
	public static final String READCOMMENT = FIRST_PATH
			+ "/app/commentsservices/comments/status_list";

	/**
	 * 发表评论(type:评论内容类型1:课程、2：动态).
	 * post表单格式：uid=6628932&type=1&targetID=266382&content=你还是那么认真地学习.
	 */
	public static final String USER_COMMENT = FIRST_PATH
			+ "/app/commentsservices/comments/create";

	/** 获取某个专业下的所有科目.get请求格式： */
	public static final String GETSUBJECT = FIRST_PATH
			+ "/app/examservices/subjects/list";

	/** 根据科目ID获取所有课程.get请求格式： */
	public static final String GETCOURSE = FIRST_PATH
			+ "/app/courseservices/courses/list";

	/** 判断是否已经收藏过了课程 */
	public static final String ISCOLLECT_COURSE = FIRST_PATH
			+ "/app/courseservices/coursewares/iscc";

	/** 取消已经加入的课程 */
	public static final String CANCELCOLLECT_COURSE = FIRST_PATH
			+ "/app/courseservices/coursewares/cancel";

	/** 根据课程ID获取此课程所有单元名与单元ID */
	public static final String GETALLUNITS_BYCOURSEID = FIRST_PATH
			+ "/app/courseservices/coursewares/units";

	/** 根据单元ID获取课件URL */
	public static final String GETCOURSEWARE_BYUNITSID = FIRST_PATH
			+ "/app/courseservices/coursewares/uac";

	/** 课程评分 */
	public static final String COURSESCORE = FIRST_PATH
			+ "/app/courseservices/courses/save_score";

	/** 查找好友 */
	public static final String SEARCHFRIEND = FIRST_PATH
			+ "/app/userservices/search";

	// /**
	// * 服务端url
	// */
	// // public static final String SERVER_URL = "http://szboyi.com.cn";
	// public static final String SERVER_URL = "http://wxyzinterface.zhanyun360.com";

	/**
	 * 接口url
	 */
	// private static final String INTERFACE_URL = "http://app.szboyi.com.cn";
	private static final String INTERFACE_URL = "http://wxyzinterface.zhanyun360.com";

	/**
	 * 获取试卷列表
	 */
	public static final String CARRIES_LIST = INTERFACE_URL
			+ "/app/TestOnlineServices/TestInfor/GetTestInforByTypeId?typeId=";
	// http://222.126.246.151:9302/app/TestOnlineServices/TestInfor/GetTestInforByTypeId?typeId=1&pageSize=每页多少条&pageIndex=第几页

	/**
	 * 判断是否明天考试
	 */
	public static final String IS_CAN_TOTAL = INTERFACE_URL
			+ "/app/TestOnlineServices/TestInfor/JudgeData?userId=";
	// http://222.126.246.151:9302/app/TestOnlineServices/TestInfor/JudgeData?userId=1&testId=1

	/**
	 * 获取试卷试题
	 */
	public static final String CARRIES_QUE = INTERFACE_URL
			+ "/app/TestOnlineServices/TestInfor/GetTestWithTopic?testId=";
	// http://222.126.246.151:9302/app/TestOnlineServices/TestInfor/GetTestWithTopic?testId=1

	/**
	 * 保存用户考试结果.post表单格式：userId=会员编号&testId=试卷编号&score=考试分数
	 */
	public static final String SAVE_TEST_RESULT = INTERFACE_URL
			+ "/app/TestOnlineServices/TestInfor/SaveAnswerSheet";
	// http://222.126.246.151:9302/app/TestOnlineServices/TestInfor/SaveAnswerSheet

	/**
	 * 获取全部职业，get请求格式：GetAllSubjects/list?pageSize=每页多少条&pageIndex=第几页 Url:
	 * http://222.126.246.151:9120/app/PositionServices/GetAllSubjects/list
	 */
	public static final String GET_ALL_SUB = " http://wxyzinterface.zhanyun360.com/app/PositionServices/GetAllSubjects/list";

	/**
	 * 根据类型获取相应的信息，（1证书，2行情，3培训）get请求格式：GetInformationByType/list?typeid=类型id&
	 * pageSize=每页多少条&pageIndex=第几页 Url:
	 * http://222.126.246.151:9120/app/PositionServices
	 * /GetInformationByType/list
	 */
	public static final String GET_INFOR_TYPE = "http://wxyzinterface.zhanyun360.com/app/PositionServices/GetInformationByType/list";

	/**
	 * 学习园地，条目一：学习书籍 获取所有课程书籍
	 */
	public static final String GET_ALL_COURSE = FIRST_PATH
			+ "/app/courseservices/GetAllCoursesBySubjects/list";
	// http://222.126.246.151:9120/app/courseservices/GetAllCoursesBySubjects/list

	/**
	 * 新---根据课程id，获取该课程pdf文件
	 */
	public static final String GetcourseIDByPDF = FIRST_PATH
			+ "/app/courseservices/getcourseIDByPDF/revise?cid=";
}
