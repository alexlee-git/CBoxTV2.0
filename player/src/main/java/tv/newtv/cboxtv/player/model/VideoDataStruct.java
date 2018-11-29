package tv.newtv.cboxtv.player.model;

import java.io.Serializable;

public class VideoDataStruct implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7849637426813858127L;
	private int playType = 0;// 播放类型 0点播 1直播
	private String title = "";// 片名
	private String posterUrl = "";// 海报URL
	private String type = "";// 影片类型 电影电视剧
	private String programClass = "";// 影片类型
	private float price = 0.0f;// 影片价格
	private float score = 0.0f;// 评分
	private String year = "2015";// 年代
	private String actor = "";// 主演
	private String director = "";// 导演
	private String zone = "";// 地区
	private String desc = "";// 影片介绍
	private boolean isCollect;// 是否收藏
	private int dataSource = -1;// -1:不明 0:icntv 1:tencent 2:sohu
								// 3:icntv-tentcent-sohu 4......1000:other
	private String dataSourceStr = "";// -1:不明 0:icntv 1:tencent 2:sohu
										// 3:icntv-tentcent-sohu
										// 4......1000:other
	private int duration = -1;// 秒
	private String playUrl = "";// 视频地址
	private String programId;// 节目ID
	private String seriesId; // 节目集ID 只限ICNTV使用，为节目集ID 节目ID 为programid
	/**
	 * 直播的UUID
	 */
	private String contentUUID;

	// 以下是搜狐视频源播放参数 {'position':'0','sid':7046449,'definition':31
	// ,'cid':101,'vid':1925500,'catecode':123 }
	private String position = "";// 是影片开始播放的位置，‘0’ 为0毫秒，代表从头开始播放
	private int sid = -1;// 是专辑id
	private int definition = -1;// 是影片的清晰度定义，对应影片信息里的definition，不能为空；
	private int cid = -1;// 是栏目id,对应影片信息里的category_id
	private int catecode = -1;// 对应影片信息里的cate_code
								// -新分类号，100电影，101电视剧，106综艺，107纪录片
	// 以下是腾讯单独使用的参数
	private String tencentCid = "";// 节目集ID 只限腾讯使用，为节目集ID 节目ID 为vid

	/**
	 * icntv play SDK param
	 */
	private String deviceID = "";// 设备ID
	// private String usrID = ""; // 用户ID
	private String platformId = "";// 平台id
	private String deviceMac = "";// 设备mac
	// private String key = "";// 密钥
	private String epgid = "";// epgID 观看记录使用
	private String definitionStr = "";// 清晰度STR 观看记录使用
	private String setNumber = "";// 排序字段

	private String retain1 = "";// 预留字段 传节目ID  为了解决腾讯ID与icntvID上传日志的问题  
	private String retain2 = "";
	private String retain3 = "";
	private String retain4 = "";
	private String retain5 = "";
	
	private int programCount = 0;//电视剧时，时长显示集数
    /**
     * 解密key
     */
	private String key;

	private String categoryIds;
	/**
	 * 是否是试播类型
	 */
	private boolean isTrySee;
	private String freeDuration;

	/**
	 * 历史记录位置，在开始创建Player的时候传递给player，决定是否要在历史记录位置播放
	 */
	private int historyPosition;

	public String getFreeDuration() {
		return freeDuration;
	}

	public void setFreeDuration(String freeDuration) {
		this.freeDuration = freeDuration;
	}

	public boolean isTrySee() {
		return isTrySee;
	}

	public void setTrySee(boolean trySee) {
		isTrySee = trySee;
	}

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

	public String getContentUUID() {
		return contentUUID;
	}

	public void setContentUUID(String contentUUID) {
		this.contentUUID = contentUUID;
	}

	public int getProgramCount() {
		return programCount;
	}

	public void setProgramCount(int programCount) {
		this.programCount = programCount;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	public int getPlayType() {
		return playType;
	}

	public void setPlayType(int playType) {
		this.playType = playType;
	}

	public String getSetNumber() {
		return setNumber;
	}

	public void setSetNumber(String setNumber) {
		this.setNumber = setNumber;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getDefinition() {
		return definition;
	}

	public void setDefinition(int definition) {
		this.definition = definition;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getCatecode() {
		return catecode;
	}

	public void setCatecode(int catecode) {
		this.catecode = catecode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProgramClass() {
		return programClass;
	}

	public void setProgramClass(String programClass) {
		this.programClass = programClass;
	}

	public String getActor() {
		return actor;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public boolean isCollect() {
		return isCollect;
	}

	public void setCollect(boolean isCollect) {
		this.isCollect = isCollect;
	}

	public int getDataSource() {
		return dataSource;
	}

	public void setDataSource(int dataSource) {
		switch (dataSource) {
		case 0:
			this.dataSourceStr = "中国互联网电视";
			break;
		case 1:
			this.dataSourceStr = "腾讯视频";
			break;
		case 2:
			this.dataSourceStr = "搜狐视频";
			break;
		case 3:
			this.dataSourceStr = "icntv|腾讯|搜狐";
			break;
		case -1:
			this.dataSourceStr = "其它";
			break;

		default:
			this.dataSourceStr = "其它";
			break;
		}
		this.dataSource = dataSource;
	}

	public String getDataSourceStr() {
		return dataSourceStr;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

	public void setDataSourceStr(String dataSourceStr) {
		this.dataSourceStr = dataSourceStr;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	// public String getUsrID() {
	// return usrID;
	// }
	//
	// public void setUsrID(String usrID) {
	// this.usrID = usrID;
	// }

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	// public String getKey() {
	// return key;
	// }
	//
	// public void setKey(String key) {
	// this.key = key;
	// }

	public String getEpgid() {
		return epgid;
	}

	public void setEpgid(String epgid) {
		this.epgid = epgid;
	}

	public String getDefinitionStr() {
		return definitionStr;
	}

	public void setDefinitionStr(String definitionStr) {
		this.definitionStr = definitionStr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRetain1() {
		return retain1;
	}

	public void setRetain1(String retain1) {
		this.retain1 = retain1;
	}

	public String getRetain2() {
		return retain2;
	}

	public void setRetain2(String retain2) {
		this.retain2 = retain2;
	}

	public String getRetain3() {
		return retain3;
	}

	public void setRetain3(String retain3) {
		this.retain3 = retain3;
	}

	public String getRetain4() {
		return retain4;
	}

	public void setRetain4(String retain4) {
		this.retain4 = retain4;
	}

	public String getRetain5() {
		return retain5;
	}

	public void setRetain5(String retain5) {
		this.retain5 = retain5;
	}

	public String getTencentCid() {
		return tencentCid;
	}

	public void setTencentCid(String tencentCid) {
		this.tencentCid = tencentCid;
	}

	public void setHistoryPosition(int historyPosition) {
		this.historyPosition = historyPosition;
	}

	public int getHistoryPosition() {
		return historyPosition;
	}

	@Override
	public String toString() {
		return "VideoDataStruct{" +
				"playType=" + playType +
				", title='" + title + '\'' +
				", posterUrl='" + posterUrl + '\'' +
				", type='" + type + '\'' +
				", programClass='" + programClass + '\'' +
				", price=" + price +
				", score=" + score +
				", year='" + year + '\'' +
				", actor='" + actor + '\'' +
				", director='" + director + '\'' +
				", zone='" + zone + '\'' +
				", desc='" + desc + '\'' +
				", isCollect=" + isCollect +
				", dataSource=" + dataSource +
				", dataSourceStr='" + dataSourceStr + '\'' +
				", duration=" + duration +
				", playUrl='" + playUrl + '\'' +
				", programId='" + programId + '\'' +
				", seriesId='" + seriesId + '\'' +
				", contentUUID='" + contentUUID + '\'' +
				", position='" + position + '\'' +
				", sid=" + sid +
				", definition=" + definition +
				", cid=" + cid +
				", catecode=" + catecode +
				", tencentCid='" + tencentCid + '\'' +
				", deviceID='" + deviceID + '\'' +
				", platformId='" + platformId + '\'' +
				", deviceMac='" + deviceMac + '\'' +
				", epgid='" + epgid + '\'' +
				", definitionStr='" + definitionStr + '\'' +
				", setNumber='" + setNumber + '\'' +
				", retain1='" + retain1 + '\'' +
				", retain2='" + retain2 + '\'' +
				", retain3='" + retain3 + '\'' +
				", retain4='" + retain4 + '\'' +
				", retain5='" + retain5 + '\'' +
				", programCount=" + programCount +
				", key='" + key + '\'' +
				", categoryIds='" + categoryIds + '\'' +
				'}';
	}
}
