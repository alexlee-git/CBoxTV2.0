package tv.newtv.cboxtv.player.model;

/**
 * Created by TCP on 2018/5/10.
 */

public class LivePermissionCheckBean {
    private String errorMessage;
    private String errorCode;
    private Data data;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        private boolean encryptFlag;
        private String decryptKey;

        public boolean isEncryptFlag() {
            return encryptFlag;
        }

        public void setEncryptFlag(boolean encryptFlag) {
            this.encryptFlag = encryptFlag;
        }

        public String getDecryptKey() {
            return decryptKey;
        }

        public void setDecryptKey(String decryptKey) {
            this.decryptKey = decryptKey;
        }
    }
}
