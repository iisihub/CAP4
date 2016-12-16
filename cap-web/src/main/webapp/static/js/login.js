pageInit(function() {
    $(function() {
        // FIXME by sk
//        console.debug('window.setCloseConfirm : '+ window.setCloseConfirm);
        window.setCloseConfirm && window.setCloseConfirm(false);
        var agreeChange = false;
        function login(ignoreNotify) {
            $.ajax({
                url: url("j_spring_security_check"),
                dataType: 'html',
                data: {
                    j_username: $('#j_username').val(),
                    j_password: $('#j_password').val(),
                    captcha: $('#captcha').val(),
                    newPwd: $('#newPwd').val(),
                    confirm: $('#confirm').val(),
                    ignoreNotify: ignoreNotify,
                    agreeChange: agreeChange
                }
            }).done(function() {
                API.formSubmit({
                    url: url('page/index')
                });
            }).fail(function(jqXHR, status, errorThrown) {
                console.log('status: ' + status);
                console.log('text: ' + jqXHR.responseText);
                var result = JSON.parse(decodeURIComponent(errorThrown));
                console.log('msg: ' + result.msg);
                console.log('capchaEnabled: ' + result.capchaEnabled);
                console.log('forceChangePwd: ' + result.forceChangePwd);
                result.forceChangePwd ? $('#pwdchgArea').show() : $('#pwdchgArea').hide();
                result.capchaEnabled ? $('#captchaArea').show() : $('#captchaArea').hide();
                if (result.askChangePwd) {
                    API.showConfirmMessage(result.msg, function(confirm) {
                        // 不 reset 密碼
                        if (confirm) {
                            $('#pwdchgArea').show();
                            agreeChange = true;
                        } else {
                            $('#pwdchgArea').hide();
                            login(true);
                        }
                    });
                } else {
                    API.showErrorMessage(result.msg);
                    $('#j_password').val("");
                }
            }).always(function(jqXHR, status) {
                $("#captcha").trigger("refresh");
                $('#newPwd').val("");
                $('#confirm').val("");
            });
        }
        $('#submit').click(function() {
            login(false);
        });
    });
});
