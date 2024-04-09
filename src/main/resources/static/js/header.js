const disabled = 'disabled';
window.addEventListener('load', () => {

    const mypage = document.querySelector('#btn-mypage');
    const mypageMenu = document.querySelector('.mypage-menu-list');
    if (mypage != null) {
        mypage.addEventListener('click', () => {
            mypageMenu.classList.toggle('disabled');
        })
    }
    const logout = document.querySelector('#logout');
    if (logout != null) {
        logout.addEventListener('click', () => fetchGet('/logout', logoutResult));
    }

    const modal = document.querySelector('.modal');
    const modal_content = document.querySelector('.modal-content');

    // 타임리프로 Model에서 로그인여부 확인 후 로그인 페이지 이동
    if (getRedirectURI() != null) {
        insertModalSize('modal-login');
        modal_content.innerHTML = createLoginModal();
        modal.classList.remove(disabled);
    }

    const loginBtn = document.querySelector('#btn-login');
    if (loginBtn != null)  {
        loginBtn.addEventListener('click', () => {
            insertModalSize('modal-login');
            modal_content.innerHTML = createLoginModal();
            modal.classList.remove(disabled);
        })
    }

    const createRoomBtn = document.querySelector('#btn-create-room');
    if (createRoomBtn != null) {
        createRoomBtn.addEventListener('click', () => {
            insertModalSize('modal-create-room')
            modal_content.innerHTML = createCreateRoomModal();
            modal.classList.remove(disabled);
        })
    }


    const modalExitList = document.querySelector('.modal-exit');
    if (modalExitList != null) {
        modalExitList.addEventListener('click', () => {
            modalExit();
        })
    }

    // 아이디, 이름 중복체크
    modal_content.addEventListener('focusout', (e) => {
        let target = e.target;

        if (target.name == 'account') {
            fetchDistinct('/distinct/account', target.value, distinctAccountResult);
            return;
        }
        if (target.name == 'nickname') {
            fetchDistinct('/distinct/nickname', target.value, distinctNameResult);
            return;
        }
        if (target.name == 'password') {
            validPassword(null, target);
            return
        }
        if (target.name == 'passwordCheck') {
            validPasswordCheck(null, document.querySelector('input[name="password"]'), target);
            return
        }
    })

    modal_content.addEventListener('keyup', (e) => {
        let target = e.target;
        if (target.name == 'password') {
            validPassword(null, target);
            return;
        }
        
        if (target.name == 'passwordCheck') {
            validPasswordCheck(null, document.querySelector('input[name="password"]'), target);
            return;
        }

        if ((target.id === 'loginAccount' || target.id === 'loginPassword') && e.key === 'Enter') {
            login();
        }

    })


    // 부모에게 이벤트 위임
    modal_content.addEventListener('click', (e) => {
        let target = e.target;

        if (target.id == 'signup') { // 로그인페이지에서 회원가입 버튼클릭
            changeToSignup();
            return;
        }
        if (target.id == 'go-to-login') { // 이전 버튼 클릭 시 로그인화면으로 이동
            changeToLogin();
            return;
        }
        if (target.id == 'go-to-find-password') {
            changeToFindPassword();
        }
        if (target.id == 'findAccount') { // 아이디 찾기 버튼 클릭
            changeToFindAccount();
            return;
        }
        if (target.id =='findPassword') { // 비밀번호 찾기 버튼 클릭
            changeToFindPassword();
            return;
        }
        if (target.id == 'signup-confirm') { // 회원가입하기 버튼 클릭
            signup();
            return;
        }
        if (target.id == 'login') {
            login();
        }
        if (target.id == 'kakao') {
            // TODO
            // 배포용코드
            al('error', '배포용', '이 기능은 사용할 수 없습니다.');

            // var options = 'width=500, height=600, top=100, left=100, resizable=yes, scrollbars=yes';
            // window.open('https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=fa213830a457faaa52a9dbf35a13500e&redirect_uri=http://localhost:8080/login/kakao', '_blank', options);
        }

        // 인풋 포커스 (포커스 했을 때 메세지 삭제)
        focusEventListener();
    })


})

function logoutResult(json) {
    if (json.result == 'ok') {
        window.location.reload();
    }
}

function modalExit() {
    const modal = document.querySelector('.modal');
    const modal_content = document.querySelector('.modal-content');

    modal.classList.toggle(disabled);
    setTimeout(() => {
        modal_content.innerHTML = '';
        initModalSize();
    }, 200);
}
function login() {
    let loginAccount = document.querySelector('input[name="loginAccount"]');
    let loginPassword = document.querySelector('input[name="loginPassword"]');
    if (loginAccount == null || loginPassword == null) {
        alert('잘못된 접근입니다. 다시 시도해주세요');
        location.reload();
        return;
    }

    let json = {account : loginAccount.value, password : loginPassword.value};
    fetchPost('/login', json, loginResult);
}
function loginResult(json) {
    if (json.result == 'ok') {
        modalExit();
        let redirectURI = getRedirectURI();
        if (redirectURI != null) {
            window.location.href= redirectURI;
        } else {
            location.reload();
        }
    } else if (json.result == 'error') {
        let m_login = document.querySelector('.m-login');
        let loginAccount = document.querySelector('input[name="loginAccount"]');
        let loginPassword = document.querySelector('input[name="loginPassword"]');
        loginAccount.value = '';
        loginPassword.value = '';
        printMessage(json, m_login);
    }
}
function getRedirectURI() {
    let queryString = window.location.search;
    let searchParam = new URLSearchParams(queryString);
    return searchParam.get('redirectURI');
}

function focusEventListener() {
    let account = document.querySelector('input[name="account"]');
    let password = document.querySelector('input[name="password"]');
    let passwordCheck = document.querySelector('input[name="passwordCheck"]');
    let name = document.querySelector('input[name="name"]');
    let nickname = document.querySelector('input[name="nickname"]');

    if (account == null || password == null || passwordCheck == null || name == null || nickname == null) {
        return;
    }
    account.addEventListener('focus', () => messageInit(document.querySelector('.m-account')));
    password.addEventListener('focus', () => messageInit(document.querySelector('.m-password')));
    passwordCheck.addEventListener('focus', () => messageInit(document.querySelector('.m-passwordCheck')));
    name.addEventListener('focus', () => messageInit(document.querySelector('.m-name')));
    nickname.addEventListener('focus', () => messageInit(document.querySelector('.m-nickname')));
}

function messageInit(messageTag) {
    messageTag.classList.add('disabled');
    messageTag.classList.remove('non-error');
    messageTag.classList.remove('error');
    messageTag.innerHTML = '';
}

function distinctNameResult(json) {
    const m_nickname = document.querySelector('.m-nickname');
    printMessage(json, m_nickname);
}

function distinctAccountResult(json) {
    const m_account = document.querySelector('.m-account');
    printMessage(json, m_account);
}

function printMessage(json, msgBox) {
    if (json.result === 'none') return;
    messageInit(msgBox);
    if (json.result === 'ok') {
        msgBox.classList.add('non-error');
    } else {
        msgBox.classList.add('error');
    }

    msgBox.innerHTML = json.message;
    msgBox.classList.remove('disabled');
}

function fetchPost(url, json, callback) {
    fetch(url , { method : 'post'
					, headers : {'Content-Type' : 'application/json'}
					, body : JSON.stringify(json)
				})
    .then(res => res.json())
    .then(map => callback(map));
}
function fetchDistinct(url, data, callback) {
    fetch(url , { method : 'post'
					, headers : {'Content-Type' : 'application/json'}
					, body : data
				})
    .then(res => res.json())
    .then(map => callback(map));
}

function signup() {
    let errorList = [];
    signupValidation(errorList);
    if (errorList.length !== 0) return;
    signupPost();
    
    
}
function signupPost() {
    // TODO 배포용
    al('error', '배포용', '이 기능은 사용할 수 없습니다.');
    return;

    let account = document.querySelector('input[name="account"]');
    let password = document.querySelector('input[name="password"]');
    let passwordCheck = document.querySelector('input[name="passwordCheck"]');
    let name = document.querySelector('input[name="name"]');
    let nickname = document.querySelector('input[name="nickname"]');
    if (account == null || password == null || passwordCheck == null || name == null || nickname == null) {
        alert('잘못된 접근입니다. 다시 시도해주세요');
        location.reload();
        return;
    }
    let json ={account : account.value, password : password.value, passwordCheck : passwordCheck.value, name : name.value, nickName : nickname.value};
    fetchPost('/signup', json, signupResult);
}
function signupResult(json) {
    if (json.result === 'error') {
        json.errorList.forEach(error => {
            let msgBox = document.querySelector('.m-' + error.location);
            let result = {result : 'error', message : error.message};
            printMessage(result, msgBox);
        })
        return;
    }
    if (json.result === 'ok') {
        al('ok', '성공', json.message);
        setTimeout(() => changeToLogin(), 500);
    }
}
function signupValidation(errorList) {
    let account = document.querySelector('input[name="account"]');
    let password = document.querySelector('input[name="password"]');
    let passwordCheck = document.querySelector('input[name="passwordCheck"]');
    let name = document.querySelector('input[name="name"]');
    let nickname = document.querySelector('input[name="nickname"]');
    if (account == null || password == null || passwordCheck == null || name == null || nickname == null) {
        alert('잘못된 접근방식입니다. 다시 시도해주세요');
        location.reload();
        return;
    }
    validAccount(errorList, account);
    validPassword(errorList, password);
    validPasswordCheck(errorList, password, passwordCheck);
    validName(errorList, name);
    validNickname(errorList, nickname);

    errorList.forEach(errorInput => errorInput.classList.add('invalid'));
    setTimeout(() => {
        errorList.forEach(errorInput => errorInput.classList.remove('invalid'));
    }, 500);
}
function validAccount(errorList, accountInput) {
    let m_account = document.querySelector('.m-account');
    let account = accountInput.value;
    if (account.length === 0) {
        let result = {result : 'error', message : '아이디를 입력해주세요.'};
        printMessage(result, m_account);
        if (errorList != null) {
            errorList.push(accountInput);
        }
        return;
    }
    if (account.length > 15 || /[^a-zA-Z0-9]/.test(account)) {
        let result = {result : 'error', message : '15자 이하의 영문자와 숫자로만 이루어져야 합니다.'};
        printMessage(result, m_account);
        if (errorList != null) {
            errorList.push(accountInput);
        }
        return;
    }
    messageInit(m_account);
}

function validPassword(errorList, passwordInput) {
    let m_password = document.querySelector('.m-password');
    let password = passwordInput.value;

    if (password.length < 8 || !/[a-z]/.test(password) || !/[A-Z]/.test(password) || !/[0-9]/.test(password) || !/[!@#$%]/.test(password)) {
        let result = {result : 'error', message : '8자 이상 대,소문자, 숫자, 특수문자(!@#$%)를 포함해야 합니다.'};
        printMessage(result, m_password);
        if (errorList != null) {
            errorList.push(passwordInput);
        }
        return
    }
    messageInit(m_password);
}

function validPasswordCheck(errorList, passwordInput, passwordCheckInput) {
    let m_passwordCheck = document.querySelector('.m-passwordCheck');
    let password = passwordInput.value;
    let passwordCheck = passwordCheckInput.value;

    if (passwordCheck.length == 0) {
        let result = {result : 'error', message : '비밀번호를 다시 한번 입력해주세요.'};
        printMessage(result, m_passwordCheck);
        if (errorList != null) {
            errorList.push(passwordCheckInput);
        }
        return
    }
    if (password !== passwordCheck) {
        let result = {result : 'error', message : '비밀번호가 일치하지 않습니다.'};
        printMessage(result, m_passwordCheck);
        if (errorList != null) {
            errorList.push(passwordCheckInput);
        }
        return
    }
    messageInit(m_passwordCheck);
}

function validName(errorList, nameInput) {
    let m_name = document.querySelector('.m-name');
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

function validNickname(errorList, nicknameInput) {
    let m_nickname = document.querySelector('.m-nickname');
    let nickname = nicknameInput.value;
    if (nickname.length > 8 || nickname.length < 2) {
        let result = {result : 'error', message : '2 ~ 8자의 닉네임을 적어주세요.'};
        printMessage(result, m_nickname);
        if (errorList != null) {
            errorList.push(nicknameInput);
        }
        return
    }
    messageInit(m_nickname);
}

function changeToFindAccount() {
    const modal_content = document.querySelector('.modal-content');
    const modal_wrap = document.querySelector('.modal-wrap');
    
    modal_content.classList.add('change');
    setTimeout(() => {
        modal_wrap.classList.remove('modal-login');
        modal_content.innerHTML = createFindAccount();
        modal_wrap.classList.add('modal-find');
        modal_content.classList.remove('change');
    }, 800);
}
function changeToFindPassword() {
    const modal_content = document.querySelector('.modal-content');
    const modal_wrap = document.querySelector('.modal-wrap');
    
    modal_content.classList.add('change');
    setTimeout(() => {
        modal_wrap.classList.remove('modal-login');
        modal_content.innerHTML = createFindPassword();
        modal_wrap.classList.add('modal-find');
        modal_content.classList.remove('change');
    }, 800);
}
function changeToCreateRoom() {
    const modal_content = document.querySelector('.modal-content');
    const modal_wrap = document.querySelector('.modal-wrap');
    
    modal_content.classList.add('change');
    setTimeout(() => {
        modal_wrap.classList.remove('modal-create-room');
        modal_content.innerHTML = createCreateRoomModal();
        modal_wrap.classList.add('modal-create-room');
        modal_content.classList.remove('change');
    }, 800);
}

function changeToSignup() {
    const modal_content = document.querySelector('.modal-content');
    const modal_wrap = document.querySelector('.modal-wrap');
    
    modal_content.classList.add('change');
    setTimeout(() => {
        modal_wrap.classList.remove('modal-login');
        modal_content.innerHTML = createSignupModal();
        modal_wrap.classList.add('modal-signup');
        modal_content.classList.remove('change');
    }, 800);
}

function changeToLogin() {
    const modal_content = document.querySelector('.modal-content');
    const modal_wrap = document.querySelector('.modal-wrap');
    
    modal_content.classList.add('change');
    setTimeout(() => {
        modal_wrap.classList.remove('modal-signup');
        modal_wrap.classList.remove('modal-find');
        modal_wrap.classList.remove('enter-room-confirm');
        modal_content.innerHTML = createLoginModal();
        modal_wrap.classList.add('modal-login');
        modal_content.classList.remove('change');
    }, 800);
}

function insertModalSize(className) {
    initModalSize();
    const modal_wrap = document.querySelector('.modal-wrap');
    modal_wrap.classList.add(className);
}

function initModalSize() {
    const modal_wrap = document.querySelector('.modal-wrap');
    modal_wrap.classList.forEach(cl => {
        if (cl != 'modal-wrap') {
            modal_wrap.classList.remove(cl);
        }
    })
}

function createFindAccount() {
    return  '<div class="find">' +
                '<div class="input-box">' +
                    '<h4>이름</h4>' +
                    '<input type="text" name="findName" id="findName">' +
                    '<span class="msg disabled m-findName"></span>' +
                '</div>' +
                '<div class="input-box">' +
                    '<h4>핸드폰 번호</h4>' +
                    '<div class="find-phone-wrap">' +
                        '<input type="text" name="findPhone" id="findPhone">' +
                        '<button type="button" class="request-certification" id="request-account-certification">인증요청</button>' +
                    '</div>' +
                    '<span class="msg disabled m-findPhone"></span>' +
                '</div>' +
                '<div class="input-box certification-box disabled">' +
                    '<h4>인증번호</h4>' +
                    '<div class="find-phone-wrap">' +
                        '<input type="text" name="findCertification" id="findCertification">' +
                        '<span class="limitTime">05:00</span>' +
                        '<button type="button" class="request-certification-confirm" id="request-account-certification-confirm">인증확인</button>' +
                    '</div>' +
                    '<span class="msg disabled m-findCertification"></span>' +
                '</div>' +
                '<div class="findAccount-result disabled">' +
                '</div>' +
            '</div>' +
            '<div class="buttons">' +
                '<button type="button" id="go-to-login">이전</button>' +
                '<button type="button" id="go-to-find-password">비밀번호 찾기</button>' +
            '</div>';
    
}
function createFindPassword() {
    return  '<div class="find">' +
                '<div class="input-box">' +
                    '<h4>아이디</h4>' +
                    '<input type="text" name="findId" id="findId">' +
                    '<span class="msg disabled m-findId"></span>' +
                '</div>' +
                '<div class="input-box">' +
                    '<h4>이름</h4>' +
                    '<input type="text" name="findName" id="findName">' +
                    '<span class="msg disabled m-findName"></span>' +
                '</div>' +
                '<div class="input-box">' +
                    '<h4>핸드폰 번호</h4>' +
                    '<div class="find-phone-wrap">' +
                        '<input type="text" name="findPhone" id="findPhone">' +
                        '<button type="button" class="request-certification" id="request-password-certification">인증요청</button>' +
                    '</div>' +
                    '<span class="msg disabled m-findPhone"></span>' +
                '</div>' +
                '<div class="input-box certification-box disabled">' +
                    '<h4>인증번호</h4>' +
                    '<div class="find-phone-wrap">' +
                        '<input type="text" name="findCertification" id="findCertification">' +
                        '<span class="limitTime">05:00</span>' +
                        '<button type="button" class="request-certification-confirm" id="request-password-certification-confirm">인증확인</button>' +
                    '</div>' +
                    '<span class="msg disabled m-findCertification"></span>' +
                '</div>' +
            '</div>' +
            '<div class="buttons">' +
                '<button type="button" id="go-to-login">이전</button>' +
            '</div>';
}

function createSignupModal() {
    return  `<div class="modal-logo">
                <div>회원가입</div>
                <img src="/images/DAGONG.png" alt="" style="width: 50%;">
            </div>
            <div class="signup">
                <div class="input-box">
                    <h4>아이디</h4>
                    <input type="text" name="account" id="account" maxlength="15" placeholder="아이디를 입력해주세요">
                    <span class="msg disabled m-account"></span>
                </div>
                <div class="input-box">
                    <h4>비밀번호</h4>
                    <input type="password" name="password" id="password" minlength="8" placeholder="대,소문자, 특수문자 포함 8자 이상">
                    <span class="msg disabled m-password"></span>
                </div>
                <div class="input-box">
                    <h4>비밀번호 확인</h4>
                    <input type="password" name="passwordCheck" id="passwordCheck" minlength="8" placeholder="비밀번호 확인">
                    <span class="msg disabled m-passwordCheck"></span>
                </div>
                <div class="input-box">
                    <h4>닉네임</h4>
                    <input type="text" name="nickname" id="nickname" maxlength="8" placeholder="닉네임을 입력해주세요">
                    <span class="msg disabled m-nickname"></span>
                </div>
                <div class="input-box">
                    <h4>이름</h4>
                    <input type="text" name="name" id="name" maxlength="8" placeholder="이름을 입력해주세요">
                    <span class="msg disabled m-name"></span>
                </div>
            </div>
            <div class="buttons">
                <button type="button" id="go-to-login">이전</button>
                <button type="button" id="signup-confirm">가입하기</button>
            </div>
            </div>`
    
}

function createLoginModal() {
return `<div class="modal-logo">
            <div>시작하기</div>
            <img src="/images/DAGONG.png" alt="">
        </div>
        <div class="modal-input-box">
            <input type="text" name="loginAccount" id="loginAccount" placeholder="아이디">
            <input type="password" name="loginPassword" id="loginPassword" placeholder="비밀번호" minlength="8">
        </div>
        <div class="error-box">
            <span class="error m-login"></span>
        </div>
        <button type="button" id="login">로그인</button>
        <div class="modal-sub-menu">
            <div>
                <a id="signup">아직 회원이 아니신가요?</a>
            </div>
            <div class="go-to-find">
                <a class="find" id="findAccount">아이디 찾기</a>
                <a class="find" id="findPassword">비밀번호 찾기</a>
            </div>
        </div>
        <div class="social-box">
            <button type="button" class="social" id="kakao">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none">
                    <path d="M9.98613 0.909058C4.47157 0.909058 0 4.28535 0 8.45987C0 11.0379 1.71984 13.314 4.34398 14.6731L3.29543 18.4485C3.29543 18.4485 3.12344 18.8207 3.39528 18.9879C3.47157 19.0542 3.57033 19.0909 3.67268 19.0909C3.77502 19.0909 3.87378 19.0542 3.95007 18.9879L8.74897 15.8974C9.16506 15.9352 9.58113 15.9621 10.0139 15.9621C15.5284 15.9621 20 12.5912 20 8.41131C20 4.2314 15.5007 0.909058 9.98613 0.909058Z" fill="#181602"/>
                </svg>
                <span>카카오톡으로 로그인하기</span>
            </button>
        </div>`;
}

function createCreateRoomModal() {
    return  '<div class="modal-wrapper">' +
                '<div class="create-room-image-box">' +
                    '<button type="button" id="img">' +
                        '<img src="/images/room_profile/basic-room-profile.jpg" width="100%" height="100%" id="roomProfile" name="roomProfile">' +
                        '<input type="file" name="roomImage" id="roomImage" accept="image/*">' +
                        '<svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg" class="g68VV5Ghc0ymGpbFWhEx"><circle cx="18" cy="18" r="18" fill="#000"></circle><path d="M11.375 22.658v2.969h2.969l8.756-8.756-2.97-2.969-8.755 8.756zm14.02-8.083a.788.788 0 000-1.116l-1.852-1.852a.788.788 0 00-1.116 0l-1.45 1.448 2.97 2.97 1.448-1.45z" fill="#fff"></path></svg>' +
                    '</button>' +
                '</div>' +
                '<div class="input-box">' +
                    '<h4>방 제목</h4>' +
                    '<input type="text" name="title" id="title" maxlength="10">' +
                    '<span class="msg disabled m-title"></span>' +
                '</div>' +
                '<div class="input-box">' +
                    '<h4>소개글</h4>' +
                    '<input type="text" name="intro" id="intro" maxlength="50">' +
                    '<span class="msg disabled m-intro"></span>' +
                '</div>' +
                '<div class="input-wrap">' +
                    '<h4>인원 수' +
                        '<input type="radio" name="max" id="m2" value="2">' +
                        '<input type="radio" name="max" id="m3" value="3">' +
                        '<input type="radio" name="max" id="m4" value="4" checked>' +
                        '<input type="radio" name="max" id="m5" value="5">' +
                        '<input type="radio" name="max" id="m6" value="6">' +
                    '</h4>' +
                    '<div class="radio-wrap max-wrap">' +
                        '<label for="m2" aria-selected="false">2명</label>' +
                        '<label for="m3" aria-selected="false">3명</label>' +
                        '<label for="m4" aria-selected="true">4명</label>' +
                        '<label for="m5" aria-selected="false">5명</label>' +
                        '<label for="m6" aria-selected="false">6명</label>' +
                    '</div>' +
                '</div>' +
                '<span class="msg disabled m-max"></span>' +
                '<div class="input-wrap">' +
                    '<h4>공개여부' +
                        '<input type="radio" name="public" id="public" value="PUBLIC" checked>' +
                        '<input type="radio" name="public" id="private" value="PRIVATE">' +
                    '</h4>' +
                    '<div class="radio-wrap">' +
                        '<label for="public" aria-selected="true">공개방</label>' +
                        '<label for="private" aria-selected="false">비공개방</label>' +
                    '</div>' +
                '</div>' +
                '<span class="msg disabled m-roomPublic"></span>' +
                '<div class="input-box password-box disabled">' +
                    '<h4>비밀번호 설정</h4>' +
                    '<input type="password" name="room-password" id="room-password" placeholder="비밀번호 4~6자리를 설정해주세요." minlength="4" maxlength="6">' +
                    '<span class="msg disabled m-roomPassword"></span>' +
                '</div>' +
                '<span class="msg disabled m-max"></span>' +
                '<div class="input-wrap tag-wrap">' +
                    '<h4>태그</h4>' +
                    '<div class="tag-list">' +
                        '<div class="tag-add-box">' +
                            '<span>#</span>' +
                            '<input type="text" name="tag-add" id="tag-add" placeholder="태그입력" maxlength="10">' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<span class="msg disabled m-tags"></span>' +
            '</div>' +
            '<div class="buttons">' +
                '<button type="button" id="room-cancel">취소</button>' +
                '<button type="button" id="room-create">생성</button>' +
            '</div>';
}

