package com.xxnr.operation.protocol.bean;

import com.xxnr.operation.protocol.ResponseResult;

/**
 * Created by hehuanyu on 2016/4/28.
 */
public class LoginResult extends ResponseResult {

    /**
     * account : kefu
     * password : IdmWskR+WEX191XUQzOR1zQkKk51YgHeYtENP26YdOZTMENpKSkUMo8zVIRH7uiwjs7IM29/dY+EyAKfEHZIeq+Bv90bJwo8njIqLjHs7zLR+A56ShlUMJ854iroaygSt/tT8RmbU1CNOO898NgsNoCS1JGbVI2I5Gy2VEOiBjX0nrnN1sMjiRHeRdCYqs71QosD1+raPpao12BMFsvWN9hR9EJ1Wkh+7flNBQPXdX3XhWqsNZfsU31/otOAN4wXfS5D+HKDWOAZSij3CIKNETUrbLlkOMqWDBts3l/LrxqVNwanbyK1JChcNEZheQ1zBEa3nOmQMFXBjy8LAyvw==
     */

    private String account;
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
