package com.iisigroup.cap.security;

import com.iisigroup.cap.Constants;

public interface SecConstants extends Constants {
    public enum PwdPloicyKeys {
        PWD_EXPIRED_DAY, PWD_MIN_LENGTH, PWD_RULE, PWD_MAX_HISTORY, PWD_CAPTCHA_ENABLE, PWD_ACCOUNT_LOCK, PWD_FORCE_CHANGE_PWD, PWD_CHANGE_INTERVAL, PWD_NOTIFY_DAY, PWD_ACCOUNT_DISABLE, PWD_ACCOUNT_DELETE
    }
}
