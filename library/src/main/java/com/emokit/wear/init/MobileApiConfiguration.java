package com.emokit.wear.init;

import com.emokit.sdk.util.SDKConstant;

/**
 * 手机端api配置类
 * Created by jiahongfei on 16/3/17.
 */
public final class MobileApiConfiguration {

    /**返回情绪结果24种情绪**/
    public static final String RC_TYPE_24 = SDKConstant.RC_TYPE_24;
    /**返回情绪结果7种情绪**/
    public static final String RC_TYPE_7 = SDKConstant.RC_TYPE_7;
    /**返回情绪结果3种情绪**/
    public static final String RC_TYPE_5 = SDKConstant.RC_TYPE_5;

    public final String mPlatflag;
    public final String mUserName;
    public final String mPassword;
    public final String mRcType;

    private MobileApiConfiguration(Builder builder){
        this.mPlatflag = builder.mPlatflag;
        this.mUserName = builder.mUserName;
        this.mPassword = builder.mPassword;
        this.mRcType = builder.mRcType;
    }

    public static final class Builder{
        private String mPlatflag;
        private String mUserName;
        private String mPassword;
        private String mRcType = RC_TYPE_7;

        public Builder setPlatflag(String platflag){
            mPlatflag = platflag;
            return this;
        }

        public Builder setUserName(String mUserName) {
            this.mUserName = mUserName;
            return this;
        }

        public Builder setPassword(String mPassword) {
            this.mPassword = mPassword;
            return this;
        }

        /**
         * 设置返回情绪结果类型
         * RC_TYPE_24 返回24种情绪
         * RC_TYPE_7  返回7种情绪
         * RC_TYPE_5 返回5种情绪
         * @param rcType
         * @return
         */
        public Builder setRcType(String rcType){
            this.mRcType = rcType;
            return this;
        }

        public MobileApiConfiguration create(){
            return new MobileApiConfiguration(this);
        }

    }
}
