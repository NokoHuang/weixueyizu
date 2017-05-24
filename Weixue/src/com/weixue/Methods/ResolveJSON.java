package com.weixue.Methods;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.weixue.Model.BookResultModel;
import com.weixue.Model.ChooseModel;
import com.weixue.Model.Comment;
import com.weixue.Model.Course;
import com.weixue.Model.CourseWare;
import com.weixue.Model.InforTypeModel;
import com.weixue.Model.Major;
import com.weixue.Model.PersonInformation;
import com.weixue.Model.Response;
import com.weixue.Model.Status;
import com.weixue.Model.Subject;
import com.weixue.Model.Title;
import com.weixue.Model.Units;

public class ResolveJSON {
	/**
	 * 把json字符串解析为Response
	 * 
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public static Response JSON_To_Response(String jsonStr) throws Exception {
		Response response = new Response();
		JSONObject object = new JSONObject(jsonStr);
		response.setMessage(object.getString("Message"));
		response.setResult(object.getString("Result"));
		response.setStatus(object.getInt("Status"));

		return response;
	}

	/**
	 * 判断返回的JSON数据是否有数据
	 * 
	 * @param jsonStr
	 * @return 1、有结果 0、无结果 -1、出错
	 * @throws Exception
	 */
	public static int IsHasResult(String jsonStr) throws Exception {
		if (jsonStr == null) {
			return -1;
		}
		int mark = -1;
		Response response = JSON_To_Response(jsonStr);
		int status = response.getStatus();
		switch (status) {
		case 0: {
			mark = 0;
			break;
		}
		case 1: {
			mark = 1;
			break;
		}
		default: {
			return -1;
		}
		}
		return mark;
	}

	/**
	 * 把json字符串解析为PersonInformation
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static PersonInformation JSON_To_PersonInformation(String jsonStr) {

		PersonInformation person = null;
		try {
			Response response = JSON_To_Response(jsonStr);

			person = new PersonInformation();

			JSONObject personInformationObject = new JSONObject(
					response.getResult());

			person.setAreasOfSpecificity(personInformationObject
					.getString("AreasOfSpecificity"));
			person.setBirthday(personInformationObject.getString("Birthday"));
			person.setCheckInCount(personInformationObject
					.getInt("CheckInCount"));
			person.setCoursesCount(personInformationObject
					.getInt("CoursesCount"));
			person.setDepartment(personInformationObject
					.getString("Department"));
			person.setEduStartDate(personInformationObject
					.getString("EduStartDate"));
			person.setEmail(personInformationObject.getString("Email"));
			person.setFavouritesCount(personInformationObject
					.getInt("FavouritesCount"));
			person.setFollowerCount(personInformationObject
					.getInt("FollowerCount"));
			person.setFriendsCount(personInformationObject
					.getInt("FriendsCount"));
			person.setGender(personInformationObject.getString("Gender"));
			person.setIntroductionOfSeniority(personInformationObject
					.getString("IntroductionOfSeniority"));
			person.setIsVIP(personInformationObject.getInt("IsVIP"));
			person.setLargePhotoPath(personInformationObject
					.getString("LargePhotoPath"));
			person.setLearnCents(personInformationObject.getInt("LearnCents"));
			person.setLearnMoney(personInformationObject.getInt("LearnMoney"));
			person.setLevel(personInformationObject.getInt("Level"));
			person.setLocation(personInformationObject.getString("Location"));
			person.setMajor(personInformationObject.getString("Major"));
			person.setOnlineStatus(personInformationObject
					.getInt("OnlineStatus"));
			person.setPhotoPath(personInformationObject.getString("PhotoPath"));
			person.setQQ(personInformationObject.getString("QQ"));
			person.setSchool(personInformationObject.getString("School"));
			person.setSignature(personInformationObject.getString("Signature"));
			person.setStatusesCount(personInformationObject
					.getInt("StatusesCount"));
			person.setUserId(personInformationObject.getInt("UserId"));
			person.setUsername(personInformationObject.getString("Username"));
			person.setWebsite(personInformationObject.getString("Website"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return person;
	}

	public static Title JSON_TO_Title(String jsonStr) {
		Title title;
		try {
			Response response = JSON_To_Response(jsonStr);
			title = new Title();
			JSONObject titleObject = new JSONObject(response.getResult());
			title.setqAnswer(titleObject.getString("QAnswer"));
			title.setqCategoryID(titleObject.getInt("QCategoryID"));
			title.setqContent(titleObject.getString("QContent"));
			title.setqID(titleObject.getInt("QID"));
			title.setqName(titleObject.getString("QName"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return title;
	}

	/**
	 * 把json字符串解析为PersonInformation数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<PersonInformation> JSON_TO_PersonInformationArray(
			String jsonStr) {

		List<PersonInformation> li_Person = new ArrayList<PersonInformation>();
		try {
			Response response = JSON_To_Response(jsonStr);

			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				JSONObject personInformationObject = arr.getJSONObject(i);
				PersonInformation person = new PersonInformation();
				person.setAreasOfSpecificity(personInformationObject
						.getString("AreasOfSpecificity"));
				person.setBirthday(personInformationObject
						.getString("Birthday"));
				person.setCheckInCount(personInformationObject
						.getInt("CheckInCount"));
				person.setCoursesCount(personInformationObject
						.getInt("CoursesCount"));
				person.setDepartment(personInformationObject
						.getString("Department"));
				person.setEduStartDate(personInformationObject
						.getString("EduStartDate"));
				person.setEmail(personInformationObject.getString("Email"));
				person.setFavouritesCount(personInformationObject
						.getInt("FavouritesCount"));
				person.setFollowerCount(personInformationObject
						.getInt("FollowerCount"));
				person.setFriendsCount(personInformationObject
						.getInt("FriendsCount"));
				person.setGender(personInformationObject.getString("Gender"));
				person.setIntroductionOfSeniority(personInformationObject
						.getString("IntroductionOfSeniority"));
				person.setIsVIP(personInformationObject.getInt("IsVIP"));
				person.setLargePhotoPath(personInformationObject
						.getString("LargePhotoPath"));
				person.setLearnCents(personInformationObject
						.getInt("LearnCents"));
				person.setLearnMoney(personInformationObject
						.getInt("LearnMoney"));
				person.setLevel(personInformationObject.getInt("Level"));
				person.setLocation(personInformationObject
						.getString("Location"));
				person.setMajor(personInformationObject.getString("Major"));
				person.setOnlineStatus(personInformationObject
						.getInt("OnlineStatus"));
				person.setPhotoPath(personInformationObject
						.getString("PhotoPath"));
				person.setQQ(personInformationObject.getString("QQ"));
				person.setSchool(personInformationObject.getString("School"));
				person.setSignature(personInformationObject
						.getString("Signature"));
				person.setStatusesCount(personInformationObject
						.getInt("StatusesCount"));
				person.setUserId(personInformationObject.getInt("UserId"));
				person.setUsername(personInformationObject
						.getString("Username"));
				person.setWebsite(personInformationObject.getString("Website"));

				li_Person.add(person);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_Person;
	}

	/**
	 * 把json字符串解析为Course(数组)
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Course> JSON_TO_CourseArray(String jsonStr) {
		List<Course> li_Course = new ArrayList<Course>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				Course course = new Course();
				JSONObject object = arr.getJSONObject(i);
				course.setCategory(object.getInt("Category"));
				course.setCourseID(object.getInt("CourseID"));
				course.setCourseName(object.getString("CourseName"));
				course.setCourse_ImgUrl(object.getString("Course_ImgUrl"));
				course.setCoursewareCount(object.getInt("CoursewareCount"));
				course.setDetailIntro(object.getString("DetailIntro"));
				course.setIntroduction(object.getString("Introduction"));
				course.setPraiseDegree(object.getInt("PraiseDegree"));
				course.setPublishDate(object.getString("PublishDate"));
				course.setStudyCount(object.getInt("StudyCount"));
				course.setTags(object.getString("Tags"));
				course.setType(object.getInt("Type"));
				course.setCourse_LearnCents(object.getInt("Course_LearnCents"));
				course.setCourse_LearnMoney(object.getInt("Course_LearnMoney"));
				li_Course.add(course);
			}
		} catch (Exception e) {
			return null;
		}

		return li_Course;
	}

	/**
	 * 把json字符串解析为Course(数组)
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Course> Book_JSON_TO_CourseArray(String jsonStr) {
		List<Course> li_Book = new ArrayList<Course>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {// Result-->>
				// BookResultModel book = new BookResultModel();
				JSONObject bookObject = arr.getJSONObject(i);

				// List<Course> li_Course = new ArrayList<Course>();

				JSONArray courseArr = null;
				try {
					courseArr = bookObject.getJSONArray("CourseList");
				} catch (Exception e) {
					e.printStackTrace();
					// Log.e("测试", "3>>");
					continue;
				}
				for (int j = 0; j < courseArr.length(); j++) {// CourseList-->>
					Course course = new Course();
					JSONObject object = courseArr.getJSONObject(j);
					course.setCategory(object.getInt("Category"));
					course.setCourseID(object.getInt("CourseID"));
					course.setCourseName(object.getString("CourseName"));
					course.setCourse_ImgUrl(object.getString("Course_ImgUrl"));
					course.setCoursewareCount(object.getInt("CoursewareCount"));
					course.setDetailIntro(object.getString("DetailIntro"));
					course.setIntroduction(object.getString("Introduction"));
					course.setPraiseDegree(object.getInt("PraiseDegree"));
					course.setPublishDate(object.getString("PublishDate"));
					course.setStudyCount(object.getInt("StudyCount"));
					course.setType(object.getInt("Type"));
					course.setCourse_LearnCents(object
							.getInt("Course_LearnCents"));
					course.setCourse_LearnMoney(object
							.getInt("Course_LearnMoney"));
					li_Book.add(course);
					// Log.e("测试", "add>>");
				}

				// book.setSubjectID(bookObject.getInt("SubjectID"));
				// Log.e("测试", "3>>");
				// book.setSubjectName(bookObject.getString("SubjectName"));
				// Log.e("测试", "4>>");
				// book.setIntroduce(bookObject.getString("Introduce"));
				// Log.e("测试", "5>>");
				// li_Book.add(book);
			}
		} catch (Exception e) {
			return null;
		}

		return li_Book;
	}

	/**
	 * 把json字符串解析为Course
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Course JSON_TO_Course(String jsonStr) {

		Course course = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			course = new Course();
			JSONObject object = new JSONObject(response.getResult());
			course.setCategory(object.getInt("Category"));
			course.setCourseID(object.getInt("CourseID"));
			course.setCourseName(object.getString("CourseName"));
			course.setCourse_ImgUrl(object.getString("Course_ImgUrl"));
			course.setCoursewareCount(object.getInt("CoursewareCount"));
			course.setDetailIntro(object.getString("DetailIntro"));
			course.setIntroduction(object.getString("Introduction"));
			course.setPraiseDegree(object.getInt("PraiseDegree"));
			course.setPublishDate(object.getString("PublishDate"));
			course.setStudyCount(object.getInt("StudyCount"));
			course.setTags(object.getString("Tags"));
			course.setType(object.getInt("Type"));
			course.setCourse_LearnCents(object.getInt("Course_LearnCents"));
			course.setCourse_LearnMoney(object.getInt("Course_LearnMoney"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}

		return course;
	}

	/**
	 * 把json字符串解析为Status数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Status> JSON_TO_StatusArray(String jsonStr) {
		List<Status> li_status = new ArrayList<Status>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				Status status = new Status();
				JSONObject object = arr.getJSONObject(i);
				status.setBigPicPath(object.getString("BigPicPath"));
				status.setCommentCount(object.getInt("CommentCount"));
				status.setContentText(object.getString("ContentText"));
				status.setCreateTime(object.getString("CreateTime"));
				status.setPicPath(object.getString("PicPath"));
				status.setStatusID(object.getInt("StatusID"));
				status.setStatusType(object.getInt("StatusType"));
				status.setUserID(object.getInt("UserID"));
				status.setUserName(object.getString("UserName"));
				status.setUserPicPath(object.getString("UserPicPath"));
				li_status.add(status);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_status;
	}

	/**
	 * 把json字符串解析为Status
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Status JSON_TO_Status(String jsonStr) {
		Status status = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			status = new Status();
			JSONObject object = new JSONObject(response.getResult());
			status.setBigPicPath(object.getString("BigPicPath"));
			status.setCommentCount(object.getInt("CommentCount"));
			status.setContentText(object.getString("ContentText"));
			status.setCreateTime(object.getString("CreateTime"));
			status.setPicPath(object.getString("PicPath"));
			status.setStatusID(object.getInt("StatusID"));
			status.setStatusType(object.getInt("StatusType"));
			status.setUserID(object.getInt("UserID"));
			status.setUserName(object.getString("UserName"));
			status.setUserPicPath(object.getString("UserPicPath"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return status;
	}

	/**
	 * 把json字符串解析为CourseWare数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<CourseWare> JSON_TO_CourseWareArray(String jsonStr) {

		List<CourseWare> li_ware = new ArrayList<CourseWare>();
		try {
			Response response = JSON_To_Response(jsonStr);
			
			Log.e("数据", response.getResult());
			
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				CourseWare ware = new CourseWare();
				JSONObject object = arr.getJSONObject(i);
				ware.setCategory(object.getInt("Category"));
				ware.setDownCount(object.getInt("DownCount"));
				ware.setFilePath(object.getString("FilePath"));
				ware.setLengthTime(object.getString("LengthTime"));
				ware.setName(object.getString("Name"));
				ware.setPraise(object.getInt("Praise"));
				ware.setShareCount(object.getInt("ShareCount"));
				ware.setWareID(object.getInt("WareID"));
				ware.setWatchCount(object.getInt("WatchCount"));
				li_ware.add(ware);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_ware;
	}

	/**
	 * 把json字符串解析为CourseWare
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static CourseWare JSON_TO_CourseWare(String jsonStr) {

		CourseWare ware = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			ware = new CourseWare();
			JSONObject object = new JSONObject(response.getResult());
			ware.setCategory(object.getInt("Category"));
			ware.setDownCount(object.getInt("DownCount"));
			ware.setFilePath(object.getString("FilePath"));
			ware.setLengthTime(object.getString("LengthTime"));
			ware.setName(object.getString("Name"));
			ware.setPraise(object.getInt("Praise"));
			ware.setShareCount(object.getInt("ShareCount"));
			ware.setWareID(object.getInt("WareID"));
			ware.setWatchCount(object.getInt("WatchCount"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return ware;
	}

	/**
	 * 把json字符串解析为Major数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Major> JSON_TO_MajorArray(String jsonStr) {

		List<Major> li_major = new ArrayList<Major>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				Major major = new Major();
				JSONObject object = arr.getJSONObject(i);
				major.setIntroduce(object.getString("Introduce"));
				major.setMajorID(object.getInt("MajorID"));
				major.setMajorName(object.getString("MajorName"));
				li_major.add(major);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_major;
	}

	/**
	 * 把json字符串解析为Major
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Major JSON_TO_Major(String jsonStr) {

		Major major = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			major = new Major();
			JSONObject object = new JSONObject(response.getResult());
			major.setIntroduce(object.getString("Introduce"));
			major.setMajorID(object.getInt("MajorID"));
			major.setMajorName(object.getString("MajorName"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return major;
	}

	/**
	 * 把json字符串解析为Comment数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Comment> JSON_TO_CommentArray(String jsonStr) {

		List<Comment> li_Comment = new ArrayList<Comment>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				Comment comment = new Comment();
				JSONObject object = arr.getJSONObject(i);
				comment.setContent(object.getString("Content"));
				comment.setCreateTime(object.getString("CreateTime"));
				comment.setID(object.getInt("ID"));
				comment.setTargetID(object.getInt("TargetID"));
				comment.setUID(object.getInt("UID"));
				li_Comment.add(comment);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_Comment;
	}

	/**
	 * 把json字符串解析为Comment
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Comment JSON_TO_Comment(String jsonStr) {

		Comment comment = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			comment = new Comment();
			JSONObject object = new JSONObject(response.getResult());
			comment.setContent(object.getString("Content"));
			comment.setCreateTime(object.getString("CreateTime"));
			comment.setID(object.getInt("ID"));
			comment.setTargetID(object.getInt("TargetID"));
			comment.setUID(object.getInt("UID"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return comment;
	}

	/**
	 * 把json字符串解析为Subject数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Subject> JSON_TO_SubjectArray(String jsonStr) {

		List<Subject> li_Subject = new ArrayList<Subject>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				Subject subject = new Subject();
				JSONObject object = arr.getJSONObject(i);
				subject.setSubjectID(object.getInt("SubjectID"));
				subject.setSubjectName(object.getString("SubjectName"));
				subject.setIntroduce(object.getString("Introduce"));
				li_Subject.add(subject);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_Subject;
	}

	public static List<ChooseModel> JSON_TO_Choose(String jsonStr) {
		List<ChooseModel> li_Choose = new ArrayList<ChooseModel>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				ChooseModel choose = new ChooseModel();
				JSONObject object = arr.getJSONObject(i);
				/*
				 * subject.setSubjectID(object.getInt("SubjectID"));
				 * subject.setSubjectName(object.getString("SubjectName"));
				 * subject.setIntroduce(object.getString("Introduce"));
				 */
				choose.set_subject_id(object.getInt("_subject_id"));
				choose.set_subject_introduce(object
						.getString("_subject_introduce"));
				choose.set_subject_name(object.getString("_subject_name"));

				li_Choose.add(choose);
			}
		} catch (Exception e) {
			return null;
		}
		return li_Choose;
	}

	public static List<InforTypeModel> JSON_TO_Type(String jsonStr) {
		List<InforTypeModel> li_Infor = new ArrayList<InforTypeModel>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				InforTypeModel typeModel = new InforTypeModel();
				JSONObject object = arr.getJSONObject(i);
				typeModel.set_po_context(object.getString("_po_context"));
				typeModel.set_po_datetime(object.getString("_po_datetime"));
				typeModel.set_po_image(object.getString("_po_image"));
				typeModel.set_po_title(object.getString("_po_title"));
				li_Infor.add(typeModel);
			}
		} catch (Exception e) {
			return null;
		}
		return li_Infor;
	}

	/**
	 * 把json字符串解析为Subject
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Subject JSON_TO_Subject(String jsonStr) {

		Subject subject = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			subject = new Subject();
			JSONObject object = new JSONObject(response.getResult());
			subject.setSubjectID(object.getInt("SubjectID"));
			subject.setSubjectName(object.getString("SubjectName"));
			subject.setIntroduce(object.getString("Introduce"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return subject;
	}

	/**
	 * 把json字符串解析为Units数组
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<Units> JSON_TO_UnitsArray(String jsonStr) {

		List<Units> li_Units = new ArrayList<Units>();
		try {
			Response response = JSON_To_Response(jsonStr);
			JSONArray arr = new JSONArray(response.getResult());
			for (int i = 0; i < arr.length(); i++) {
				Units units = new Units();
				JSONObject object = arr.getJSONObject(i);
				units.setCourseID(object.getInt("CourseID"));
				units.setUnitID(object.getInt("UnitID"));
				units.setUnitIntroduction(object.getString("UnitIntroduction"));
				units.setUnitName(object.getString("UnitName"));
				units.setUnitSort(object.getInt("UnitSort"));

				li_Units.add(units);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return li_Units;
	}

	/**
	 * 把json字符串解析为Subject
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Units JSON_TO_Units(String jsonStr) {

		Units units = null;
		try {
			Response response = JSON_To_Response(jsonStr);
			units = new Units();
			JSONObject object = new JSONObject(response.getResult());
			units.setCourseID(object.getInt("CourseID"));
			units.setUnitID(object.getInt("UnitID"));
			units.setUnitIntroduction(object.getString("UnitIntroduction"));
			units.setUnitName(object.getString("UnitName"));
			units.setUnitSort(object.getInt("UnitSort"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return units;
	}

}
