package com.gjjy.basiclib.dao.entity;

import com.gjjy.basiclib.buried_point.LoginType;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserDetailEntity{
    @Id(autoincrement = true)
    private long id;                        //本地用户表自增id
    private long userId;                    //服务器用户表自增id
    private String loginId;                 //登录账号的id
    private String uid;                     //服务器生成唯一uid
    private String token;                   //登录标识
    private String nickname;                //昵称
    private String avatarUrl;               //头像
    private int gender;                     //性别：0未知，1男、2女
    private String phone;                   //手机号码
    private String email;                   //邮箱
    private int lightning;                  //闪电数量
    private int heart;                      //心形数量
    private boolean isVip;                  //是否会员
    private String createTime;              //创建时间
    private long lastTime;                  //最后访问时间
    private long vipTime;                   //开通会员时间
    private long vipEndTime;                //Vip过期时间
    private int vipStatus;                  //1试用会员、2正式会员、0不是会员
    private String subscriptionPeriod;      //会员认购期
    private boolean isPopVipInvalid;        //是否弹过会员已过期
    private String invitationCode;          //邀请码
    private String friendInvitationCode;    //好友邀请码
    private boolean isLock;                 //是否冻结
    private String bindType;                //登录绑定类型：FACEBOOK、GOOGLE、APPLE
    private String bindUid;                 //登录的平台uid
    private int visitCount;                 //访问总天数
    private int wordCount;                  //已学关键词总数
    private int expCount;                   //经验值
    private boolean isAddAnswer;            //是否填写过新增用户程度和目的
    private boolean isBindAccount;          //是否绑定过账号
    private boolean isDelete;               //账号是否被删除（伪删除）
    @LoginType
    private String loginType;               //登录类型
    private int integral;                   //积分数量

    @Generated(hash = 664382399)
    public UserDetailEntity(long id, long userId, String loginId, String uid,
            String token, String nickname, String avatarUrl, int gender,
            String phone, String email, int lightning, int heart, boolean isVip,
            String createTime, long lastTime, long vipTime, long vipEndTime,
            int vipStatus, String subscriptionPeriod, boolean isPopVipInvalid,
            String invitationCode, String friendInvitationCode, boolean isLock,
            String bindType, String bindUid, int visitCount, int wordCount,
            int expCount, boolean isAddAnswer, boolean isBindAccount,
            boolean isDelete, String loginType, int integral) {
        this.id = id;
        this.userId = userId;
        this.loginId = loginId;
        this.uid = uid;
        this.token = token;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.lightning = lightning;
        this.heart = heart;
        this.isVip = isVip;
        this.createTime = createTime;
        this.lastTime = lastTime;
        this.vipTime = vipTime;
        this.vipEndTime = vipEndTime;
        this.vipStatus = vipStatus;
        this.subscriptionPeriod = subscriptionPeriod;
        this.isPopVipInvalid = isPopVipInvalid;
        this.invitationCode = invitationCode;
        this.friendInvitationCode = friendInvitationCode;
        this.isLock = isLock;
        this.bindType = bindType;
        this.bindUid = bindUid;
        this.visitCount = visitCount;
        this.wordCount = wordCount;
        this.expCount = expCount;
        this.isAddAnswer = isAddAnswer;
        this.isBindAccount = isBindAccount;
        this.isDelete = isDelete;
        this.loginType = loginType;
        this.integral = integral;
    }

    @Generated(hash = 273965298)
    public UserDetailEntity() {
    }

    public void delete() {
        id = 0;
        userId = 0;
        loginId = null;
        uid = null;
        token = null;
        nickname = null;
        avatarUrl = null;
        gender = 0;
        phone = null;
        email = null;
        lightning = 0;
        heart = 0;
        isVip = false;
        createTime = null;
        lastTime = 0;
        vipTime = 0;
        vipEndTime = 0;
        vipStatus = 0;
        subscriptionPeriod = null;
        isPopVipInvalid = false;
        invitationCode = null;
        friendInvitationCode = null;
        isLock = false;
        bindType = null;
        bindUid = null;
        visitCount = 0;
        wordCount = 0;
        expCount = 0;
        isAddAnswer = false;
        isBindAccount = false;
        isDelete = false;
        loginType = null;
        integral = 0;
    }

    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getAvatarUrl() {
        return this.avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public int getGender() {
        return this.gender;
    }
    public void setGender(int gender) {
        this.gender = gender;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getLightning() {
        return this.lightning;
    }
    public void setLightning(int lightning) {
        this.lightning = lightning;
    }
    public int getHeart() {
        return this.heart;
    }
    public void setHeart(int heart) {
        this.heart = heart;
    }
    public boolean getIsVip() {
        return this.isVip;
    }
    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public long getLastTime() {
        return this.lastTime;
    }
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
    public long getVipTime() {
        return this.vipTime;
    }
    public void setVipTime(long vipTime) {
        this.vipTime = vipTime;
    }
    public boolean getIsLock() {
        return this.isLock;
    }
    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }
    public String getBindType() {
        return this.bindType;
    }
    public void setBindType(String bindType) {
        this.bindType = bindType;
    }
    public String getBindUid() {
        return this.bindUid;
    }
    public void setBindUid(String bindUid) {
        this.bindUid = bindUid;
    }
    public int getVisitCount() {
        return this.visitCount;
    }
    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
    public int getWordCount() {
        return this.wordCount;
    }
    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
    public boolean getIsAddAnswer() {
        return this.isAddAnswer;
    }
    public void setIsAddAnswer(boolean isAddAnswer) {
        this.isAddAnswer = isAddAnswer;
    }
    public boolean getIsBindAccount() {
        return this.isBindAccount;
    }
    public void setIsBindAccount(boolean isBindAccount) {
        this.isBindAccount = isBindAccount;
    }
    public boolean getIsDelete() {
        return this.isDelete;
    }
    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getSubscriptionPeriod() {
        return this.subscriptionPeriod;
    }

    public void setSubscriptionPeriod(String subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public boolean getIsPopVipInvalid() {
        return this.isPopVipInvalid;
    }

    public void setIsPopVipInvalid(boolean isPopVipInvalid) {
        this.isPopVipInvalid = isPopVipInvalid;
    }

    public int getExpCount() {
        return this.expCount;
    }

    public void setExpCount(int expCount) {
        this.expCount = expCount;
    }

    public String getInvitationCode() {
        return this.invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getFriendInvitationCode() {
        return this.friendInvitationCode;
    }

    public void setFriendInvitationCode(String friendInvitationCode) {
        this.friendInvitationCode = friendInvitationCode;
    }

    public int getVipStatus() {
        return this.vipStatus;
    }

    public void setVipStatus(int vipStatus) {
        this.vipStatus = vipStatus;
    }

    public long getVipEndTime() {
        return this.vipEndTime;
    }

    public void setVipEndTime(long vipEndTime) {
        this.vipEndTime = vipEndTime;
    }

    public int getIntegral() {
        return this.integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getLoginId() {
        return this.loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
