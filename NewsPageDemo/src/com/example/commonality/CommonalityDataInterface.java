package com.example.commonality;

public class CommonalityDataInterface {
	public static String BaseUrl="http://wxyzinterface.zhanyun360.com/";
	public static String getInformationType="app/InformationServices/Information/GetInformationType";//获取资讯类型
	public static String getNewsInformationById="app/InformationServices/Information/GetNewsInformationById";//根据ID获取资讯信息
	public static String getNewsInformationListByTypeId="app/InformationServices/Information/GetNewsInformationListByTypeId";//获取资讯列表
}//http://222.126.246.151:9120/app/InformationServices/Information/GetInformationType
//http://222.126.246.151:9120/app/InformationServices/Information/GetInformationType?pageSize=10&pageIndex=1
//http://222.126.246.151:9120/app/InformationServices/Information/GetNewsInformationListByTypeId?typeid=2&pageSize=10&pageIndex=1