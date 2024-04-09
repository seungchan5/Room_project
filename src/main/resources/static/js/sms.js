let timerInterval = null;
let clickLimit = null;

window.addEventListener('load', () => {
    const modal_content = document.querySelector('.modal-content');

    modal_content.addEventListener('keyup', (e) => {
        let target = e.target;

        if (target.name == 'findPhone') {
            let phone = target.value;
            if (!/^\d+$/.test(phone)) {
                target.value = phone.replaceAll(/\D/g, '');
            }
        }

        if (target.name == 'changePassword') {
            validChangePassword(target);
            return;
        }
        
        if (target.name == 'changePasswordCheck') {
            validChangePasswordCheck(document.querySelector('input[name="changePassword"]'), target);
            return;
        }
    })


    // 부모에게 이벤트 위임
    modal_content.addEventListener('click', (e) => {
        let target = e.target;

        // 아이디 찾기에서 인증요청
        if (target.id == 'request-account-certification') {
            requestSendSMS();
            return;
        }
        // 아이디 찾기에서 인증확인
        if (target.id == 'request-account-certification-confirm') {
            findAccountCertificationConfirm();
            return;
        }

        // 비밀번호 찾기에서 인증요청
        if (target.id == 'request-password-certification') {
            requestSendSMS();
            return;
        }
        // 비밀번호 찾기에서 인증확인
        if (target.id == 'request-password-certification-confirm') {
            findPasswordCertificationConfirm();
            return;
        }

        if (target.id == 'change-password') {
            changePassword();
            return;
        }
        
    })
})
// 문자인증 텀
function blocker() {
    if (clickLimit) {
        al('error', '메시지', "잠시 후에 시도해주세요.");
        return false;
    }
    clearInterval(timerInterval);

    timerInterval = limitTimer();
    clickLimit = limitClick();
    return true;
}

function fetchPostSMS(url, json, callback) {
    fetch(url , { method : 'post'
					, headers : {'Content-Type' : 'application/json'}
					, body : JSON.stringify(json)
				})
    .then(res => res.json())
    .then(map => callback(map));
}

function requestSendSMS() {
    let name = document.querySelector('input[name="findName"]');
    let phone = document.querySelector('input[name="findPhone"]');
    if (name == null || phone == null) {
        alert('잘못된 접근방식입니다. 다시 시도해주세요');
        location.reload();
        return;
    }
    let errorList = [];

    validFindName(errorList, name);
    validFindPhone(errorList, phone);
    if (errorList.length != 0) return;
    
    let json = {name : name.value, phone : phone.value};

    if (!blocker()) return;

    // smsSend({result : 'ok', message : '인증번호를 발송했습니다.'});
    fetchPostSMS('/sms/send', json, smsSend);

}

function smsSend(json) {
    let m_findPhone = document.querySelector('.m-findPhone');
    printMessage(json, m_findPhone);

    if (json.result == 'ok') {
        let certificationBox = document.querySelector('.certification-box');
        certificationBox.classList.remove('disabled');
        let findAccount_result = document.querySelector('.findAccount-result');
        if (findAccount_result != null) {
            findAccount_result.classList.add('disabled');
        }
    }
    else if (json.result == 'NotConnected') {
        al('error', 'SMS 서버 연결 실패', error.message);
    }
    else if (json.result === 'error') {
        al('error', '에러', json.message);
    }
}
function accountConfirm(json) {
    let m_findCertification = document.querySelector('.m-findCertification');

    /* 
        예상 JSON 형식
        {
            result : 'ok',
            message : '인증이 완료되었습니다.',
            findAccount :   {
                                social : 'KAKAO',
                                account : 'tmd8633@naver.com'
                            }
        }
        {
            result : 'ok',
            message : '인증이 완료되었습니다.',
            findAccount :   {
                                social : null,
                                account : '회원 정보가 없습니다.'
                            }
        }
    */
    let findAccount_result = document.querySelector('.findAccount-result');
    findAccount_result.innerHTML = '';

    if (json.result == 'ok') {
        clearInterval(timerInterval);
        clearInterval(clickLimit);
        clickLimit = undefined;

        if (json.findAccount.social != null) {

            if (json.findAccount.social == 'KAKAO') {
                findAccount_result.innerHTML = '<span class="social kakao">KAKAO</span>'
            }
        }
        findAccount_result.innerHTML += '<span id="result">' + json.findAccount.account + '</span>'

        findAccount_result.classList.remove('disabled');
    }
    if (json.result == 'error') {
        al('error', '에러', json.message);
        changeToFindAccount();
    }
}

function findAccountCertificationConfirm() {
    let name = document.querySelector('input[name="findName"]');
    let phone = document.querySelector('input[name="findPhone"]');
    let certification = document.querySelector('input[name="findCertification"]');
    if (name == null || phone == null || certification == null) {
        alert('잘못된 접근방식입니다. 다시 시도해주세요');
        location.reload();
        return;
    }
    let errorList = [];

    validFindName(errorList, name);
    validFindPhone(errorList, phone);

    if (errorList.length != 0) return;
    
    let json = {name : name.value, phone : phone.value, certification : certification.value};

    // accountConfirm(        
    //     {
    //         result : 'ok',
    //         message : '인증이 완료되었습니다.',
    //         findAccount :   {
    //                         social : null,
    //                         account : '회원 정보가 없습니다.'
    //                         }
    //     }
    // );
    fetchPostSMS('/sms/account/confirm', json, accountConfirm);

}

function findPasswordCertificationConfirm() {
    let id = document.querySelector('input[name="findId"]');
    let name = document.querySelector('input[name="findName"]');
    let phone = document.querySelector('input[name="findPhone"]');
    let certification = document.querySelector('input[name="findCertification"]');
    if (id == null || name == null || phone == null || certification == null) {
        alert('잘못된 접근방식입니다. 다시 시도해주세요');
        location.reload();
        return;
    }
    let errorList = [];

    validFindId(errorList, id);
    validFindName(errorList, name);
    validFindPhone(errorList, phone);
    if (errorList.length != 0) return;
    
    let json = {account : id.value, name : name.value, phone : phone.value, certification : certification.value};
    // passwordConfirm({result : 'ok', message : '인증되었습니다.'});
    fetchPostSMS('/sms/password/confirm', json, passwordConfirm);
}

function passwordConfirm(json) {
    let m_findCertification = document.querySelector('.m-findCertification');
    
    clearInterval(timerInterval);
    clearInterval(clickLimit);

    if (json.result == 'ok') {
        clearInterval(timerInterval);
        clearInterval(clickLimit);
        clickLimit = undefined;

        let inputBoxList = document.querySelectorAll('.find .input-box');
        inputBoxList.forEach(inputBox => inputBox.classList.add('disabled'));
        let divFind = document.querySelector('.find');
        createChangePasswordForm(divFind);
        let buttons = document.querySelector('.buttons');
        buttons.innerHTML += '<button type="button" id="change-password">변경</button>';
    }
    if (json.result == 'error') {
        al('error', '에러', json.message);
        changeToFindPassword();
    }
}
function createChangePasswordForm(divFind) {
    // '<div class="input-box">' +
    //     '<h4>변경 비밀번호</h4>' +
    //     '<input type="password" name="changePassword" id="changePassword">' +
    //     '<span class="msg disabled m-changePassword"></span>' +
    // '</div>' +
    // '<div class="input-box">' +
    //     '<h4>변경 비밀번호 확인</h4>' +
    //     '<input type="password" name="changePasswordCheck" id="changePasswordCheck">' +
    //     '<span class="msg disabled m-changePasswordCheck"></span>' +
    // '</div>'

    let div1 = document.createElement('div');
    let div2 = document.createElement('div');

    div1.classList.add('input-box');
    div2.classList.add('input-box');

    let h1 = document.createElement('h4');
    let h2 = document.createElement('h4');

    h1.innerHTML = '변경 비밀번호';
    h2.innerHTML = '변경 비밀번호 확인';

    let input1 = document.createElement('input');
    let input2 = document.createElement('input');

    input1.setAttribute('type', 'password');
    input2.setAttribute('type', 'password');

    input1.setAttribute('name', 'changePassword');
    input1.setAttribute('id', 'changePassword');

    input2.setAttribute('name', 'changePasswordCheck');
    input2.setAttribute('id', 'changePasswordCheck');

    let span1 = document.createElement('span');
    let span2 = document.createElement('span');

    span1.classList.add('msg');
    span1.classList.add('disabled');
    span2.classList.add('msg');
    span2.classList.add('disabled');

    span1.classList.add('m-changePassword');
    span2.classList.add('m-changePasswordCheck');

    div1.appendChild(h1);
    div1.appendChild(input1);
    div1.appendChild(span1);

    div2.appendChild(h2);
    div2.appendChild(input2);
    div2.appendChild(span2);

    divFind.appendChild(div1);
    divFind.appendChild(div2);
}
function changePassword() {
    let id = document.querySelector('input[name="findId"]');
    let name = document.querySelector('input[name="findName"]');
    let phone = document.querySelector('input[name="findPhone"]');
    let certification = document.querySelector('input[name="findCertification"]');
    let changePassword = document.querySelector('input[name="changePassword"]');
    let changePasswordCheck = document.querySelector('input[name="changePasswordCheck"]');
    if (id == null || name == null || phone == null || certification == null || changePassword == null || changePasswordCheck == null) {
        alert('잘못된 접근방식입니다. 다시 시도해주세요');
        location.reload();
        return;
    }

    let check1 = validChangePassword(changePassword);
    let check2 = validChangePasswordCheck(changePassword, changePasswordCheck);

    if (!check1 || !check2) {
        return;
    }
    let json = {account : id.value, name : name.value, phone : phone.value, certification : certification.value, changePassword : changePassword.value, changePasswordCheck : changePasswordCheck.value};

    // changePasswordResult({result : 'ok', message : '비밀번호 변경 완료'});
    fetchPost('/changePassword', json, changePasswordResult);
}

function changePasswordResult(json) {
    if (json == null || (json.result != 'ok' && json.reult != 'error')) {
        al('error', '오류', '예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요.');
        return;
    }   
    al(json.result, json.message, '');
    changeToLogin();
}

function validFindId(errorList, idInput) {
    let m_id = document.querySelector('.m-findId');
    let id = idInput.value;

    if (id.length == 0) {
        let result = {result : 'error', message : '아이디를 입력해주세요.'};
        printMessage(result, m_id);
        if (errorList != null) {
            errorList.push(idInput);
        }
        return;
    }
    messageInit(m_id);
}
function validFindName(errorList, nameInput) {
    let m_name = document.querySelector('.m-findName');
    let name = nameInput.value;

    if (name.length == 0) {
        let result = {result : 'error', message : '이름을 입력해주세요.'};
        printMessage(result, m_name);
        if (errorList != null) {
            errorList.push(nameInput);
        }
        return;
    }
    messageInit(m_name);
}
function validFindPhone(errorList, phoneInput) {
    let m_phone = document.querySelector('.m-findPhone');
    let phone = phoneInput.value;

    if (phone.length == 0) {
        let result = {result : 'error', message : '휴대폰 번호를 입력해주세요.'};
        printMessage(result, m_phone);
        if (errorList != null) {
            errorList.push(phoneInput);
        }
        return;
    }

    if (!/^\d+$/.test(phone)) {
        let result = {result : 'error', message : '숫자만 입력할 수 있습니다.'};
        printMessage(result, m_phone);
        if (errorList != null) {
            errorList.push(phoneInput);
        }
        return;
    }
    messageInit(m_phone);
}

function limitClick() {
    var seconds = 3;
    var timerId = setInterval(function(){
        // 시간 감소
        seconds--;
        // 시간이 0이면 타이머 중지
        if (seconds === 0) {
            clearInterval(timerId);
            clickLimit = undefined; // 타이머 중지 후 초기화
            return true;
        }
    }, 1000);

    return timerId; // 새로운 타이머 ID 반환
}
function limitTimer() {
    let timer = document.querySelector('.limitTime');
    var minutes = 5;
    var seconds = 0;
    var timerId = setInterval(function(){
        // 시간 감소
        seconds--;
    
        // 시간이 음수가 되면 분 감소
        if (seconds < 0) {
            minutes--;
            seconds = 59;
        }
        if (minutes == 0) {
            timer.style.color = 'red';
        }
    
        // 분과 초를 2자리 숫자로 표시
        var formattedMinutes = ('0' + minutes).slice(-2);
        var formattedSeconds = ('0' + seconds).slice(-2);
    
        // 타이머 업데이트
        timer.textContent = formattedMinutes + ':' + formattedSeconds;
    
        // 시간이 0이면 타이머 중지
        if (minutes === 0 && seconds === 0) {
            clearInterval(timerId);
            return true;
        }
    }, 1000);

    return timerId; // 새로운 타이머 ID 반환
}

function validChangePassword(passwordInput) {
    let m_password = document.querySelector('.m-changePassword');
    let password = passwordInput.value;

    if (password.length < 8 || !/[a-z]/.test(password) || !/[A-Z]/.test(password) || !/[0-9]/.test(password) || !/[!@#$%]/.test(password)) {
        let result = {result : 'error', message : '8자 이상 대,소문자, 숫자, 특수문자(!@#$%)를 포함해야 합니다.'};
        printMessage(result, m_password);
        return false;
    }
    messageInit(m_password);
    return true;
}

function validChangePasswordCheck(passwordInput, passwordCheckInput) {
    let m_passwordCheck = document.querySelector('.m-changePasswordCheck');
    let password = passwordInput.value;
    let passwordCheck = passwordCheckInput.value;

    if (passwordCheck.length == 0) {
        let result = {result : 'error', message : '비밀번호를 다시 한번 입력해주세요.'};
        printMessage(result, m_passwordCheck);
        return false;
    }
    if (password !== passwordCheck) {
        let result = {result : 'error', message : '비밀번호가 일치하지 않습니다.'};
        printMessage(result, m_passwordCheck);
        return false;
    }
    messageInit(m_passwordCheck);
    return true;
}
