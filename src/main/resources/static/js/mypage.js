let timerInterval = null;
let clickLimit = null;

window.addEventListener('load', () => {

    // pop 종료
    const popList = document.querySelectorAll('.pop');
    popList.forEach(pop => {
        pop.addEventListener('click', (e) => {
            if (e.target.classList.contains('popCancel')) {
                initialPassword();
                initialPhone();
                initialDeleteMember();
                exitPop();
            }
        })
    })

    // 기본 정보 변경
    const editBtn = document.querySelector('.editBtn');
    editBtn.addEventListener('click', () => {
        
        let name = document.querySelector('input[name="name"]');

        let formData = new FormData();

        let defaultRadio = document.querySelector('input[name="defaultImage"]');
        if (!defaultRadio.checked && imgInput.files.item(0) != null) {
            formData.append('profile', imgInput.files.item(0));
        }
        formData.append('name', name.value);
        formData.append('nickname', nickname.value);

        fetchFormData('/member/editInfo', formData, editInfoResult);

    })

    // 비밀번호 변경 pop
    const passwordPop = document.querySelector('.passwordPop');
    if (passwordPop != null) {
        const pwdBtn = document.querySelector('#pwdBtn'); // 비밀번호 변경 버튼
        pwdBtn.addEventListener('click', () => {
            initialPassword();
            passwordPop.parentElement.classList.remove('disabled')
        });

        // 비밀번호 찾기 이동
        const findPassword = document.querySelector('#findPassword');
        findPassword.addEventListener('click', () => {
            modalFindPassword();
            initialPassword();
        });

        // 비밀번호 변경 로직
        const pwSaveBtn = document.querySelector('.saveBtn');
        pwSaveBtn.addEventListener('click', () => {
            let currPw = document.querySelector('input[name="currPassword"]').value;
            let changePw = document.querySelector('input[name="changePassword"]').value;
            let checkPw = document.querySelector('input[name="checkPassword"]').value;
    
            let json = {nowPassword : currPw, changePassword : changePw, checkPassword : checkPw};
            fetchPost('/change/password', json, passwordResult);
        })

        const currPw = document.querySelector('input[name="currPassword"]');
        currPw.addEventListener('keyup', () => messageInit(document.querySelector('#bfpw')));
        const changePw = document.querySelector('input[name="changePassword"]');
        changePw.addEventListener('keyup', () => validPassword(changePw));
        const checkPw = document.querySelector('input[name="checkPassword"]');
        checkPw.addEventListener('keyup', () => validPasswordCheck(changePw, checkPw));

    }

    // 계정 삭제 pop
    const deletePop = document.querySelector('.deletePop');
    if (deletePop != null) {
        const deleteBtn = document.querySelector('#deleteBtn');
        deleteBtn.addEventListener('click', () => deletePop.parentElement.classList.remove('disabled'));

        const checkBtn = document.querySelector('#check');
        const checkbox = document.querySelector('input[name="policy"]');
        checkBtn.addEventListener('click', () => checkbox.click());
        checkbox.addEventListener('change', () => {
            checkBtn.classList.toggle('confirm');
            deletePopBtn.setAttribute('data-is-allowed', deletePopBtn.getAttribute('data-is-allowed') != 'true');
        })

        const deletePopBtn = document.querySelector('#deletePopDeleteBtn');
        deletePopBtn.addEventListener('click', () => {
            if (!checkbox.checked) {
                al('error', '확인', '주의사항을 읽고 체크해주세요.');
                return;
            }
            let password = document.querySelector('input[name="deletePassword"]');
            fetchPost('/member/delete', {password : password != null ? password.value : null}, deleteResult);
        })

    }

    // 휴대폰 변경  pop
    const phonePop = document.querySelector('.phonePop');
    if (phonePop != null) {
        const changePassword = document.querySelector('.formEditBtn');
        changePassword.addEventListener('click', () => phonePop.parentElement.classList.remove('disabled'));

        // 휴대폰 타이핑 로직
        const phone = document.querySelector('input[name="phones"]');
        phone.addEventListener('keyup',() => phone.value = removeNotNumber(phone.value));

        // 인증번호 전송 로직
        const phoneBtn = document.querySelector('#phoneBtn');
        phoneBtn.addEventListener('click', () => {
            if (validPhone() != null) return;
    
            let phone = document.querySelector('input[name="phones"]').value;
            let json = {phone : phone};
    
            fetchPost('/sms/send', json, sendSMS);
        })

        // 인증번호 확인 로직
        const changePhone = document.querySelector('#changePhone');
        changePhone.addEventListener('click', () => {
    
            let phone = document.querySelector('input[name="phones"]');
            let certification = document.querySelector('input[name="phoneCheck"]');
            
            if (phone == null || phone.value.length == '') {
                let cPhone = document.querySelector('.confirmPhone');
                printFalse(cPhone, '휴대폰 번호를 입력해주세요');
                return;
            }
            if (certification == null || certification.value.length < 5) {
                let cPhoneCheck = document.querySelector('.confirmPhoneCheck');
                printFalse(cPhoneCheck, '인증번호를 입력해주세요');
                return;
            }
    
            let json = {phone : phone.value, certification : certification.value};
            fetchPost('/changePhone', json, resultPhone);
        }) 
    }

    const profileImgBtn = document.querySelector('#img');
    profileImgBtn.addEventListener('click', () => imgInput.click());
    
    const imgInput = document.querySelector('#img input[type="file"]');
    imgInput.addEventListener('change', () => {
        if (!printPreview()) return;
        let defaultImageInputRadio = document.querySelector('input[name="defaultImage"]');
        defaultImageInputRadio.checked = false;
    });

    const defaultImage = document.querySelector('input[name="defaultImage"]');
    defaultImage.addEventListener('click', () => defaultImageChange());

})

function editInfoResult(json) {
    if (json.result === 'ok') {
        al(json.result, '성공', json.message);
    } else if (json.result === 'error') {
        al(json.result, '에러', json.message);
    } else if (json.result === 'notLogin') {
        window.location.href = '/?redirectURI=/mypage';
    }
}
function deleteResult(json) {
    if (json.result === 'ok') {
        alert(json.message);
        window.location.href='/logout';
    } else if (json.result === 'error') {
        al(json.result, '에러', json.message);
    } else if (json.result === 'notLogin') {
        window.location.href = '/redirectURI=/mypage';
    }
}

/**
 * 예상 JSON
 * {
 *      result : 'error',
 *      message : '비밀번호 변경실패',
 *      errorList : {
 *                      {location : '위치', message : '메세지'},
 *                      {location : '위치', message : '메세지'},
 *                      ...
 *                  }
 * }
 */
function passwordResult(json) {

    if (json.result === 'ok') {
        al(json.result, '성공', json.message);
        initialPassword();
        exitPop();
    } else if (json.result === 'error') {
        let errorList = json.data;

        for (let i=0;i<errorList.length;i++) {
            let id = errorList[i].location;
            let message = errorList[i].message;
            let location = document.querySelector('#' + id);

            messageInit(location);
            location.classList.add('error');
            location.innerHTML = message;
            location.classList.remove('disabled');
        }

    } else if (json.result === 'notLogin') {
        window.location.href = '/?redirectURI=/mypage';
    }

}
function initialPassword() {
    let currPwError = document.querySelector('#bfpw');
    let changePwError = document.querySelector('#cpw');
    let checkPwError = document.querySelector('#cpwc');

    if (currPwError == null || changePwError == null || checkPwError == null) return;

    currPwError.innerHTML = '';
    changePwError.innerHTML = '';
    checkPwError.innerHTML = '';
}
function initialDeleteMember() {
    let password = document.querySelector('input[name="deletePassword"]');
    if (password == null) return;
    password.value = '';

    let checkBtn = document.querySelector('button#check');
    checkBtn.classList.remove('confirm');

    let policy = document.querySelector('input[name="policy"]');
    policy.checked = false;

    let deletePopDeleteBtn = document.querySelector('#deletePopDeleteBtn');
    deletePopDeleteBtn.setAttribute('data-is-allowed', 'false');
}

function initialPhone() {
    let phone = document.querySelector('input[name="phones"]');
    let phoneCheck = document.querySelector('input[name="phoneCheck"]');
    if (phone == null || phoneCheck == null) return;
    phone.value = '';
    phoneCheck.value = '';

    let limitTime = document.querySelector('.limitTime');
    limitTime.innerHTML = '05:00';

}

function validPassword(passwordInput) {
    let m_password = document.querySelector('#cpw');
    let password = passwordInput.value;

    if (password.length < 8 || !/[a-z]/.test(password) || !/[A-Z]/.test(password) || !/[0-9]/.test(password) || !/[!@#$%]/.test(password)) {
        let result = {result : 'error', message : '8자 이상 대,소문자, 숫자, 특수문자(!@#$%)를 포함해야 합니다.'};
        printMessage(result, m_password);
        return
    }
    messageInit(m_password);
}
function validPasswordCheck(passwordInput, passwordCheckInput) {
    let m_passwordCheck = document.querySelector('#cpwc');
    let password = passwordInput.value;
    let passwordCheck = passwordCheckInput.value;

    if (passwordCheck.length === 0) {
        let result = {result : 'error', message : '비밀번호를 다시 한번 입력해주세요.'};
        printMessage(result, m_passwordCheck);
        return
    }
    if (password !== passwordCheck) {
        let result = {result : 'error', message : '비밀번호가 일치하지 않습니다.'};
        printMessage(result, m_passwordCheck);
        return
    }
    messageInit(m_passwordCheck);
}

function printMessage(json, msgBox) {
    messageInit(msgBox);
    if (json.result == 'ok') {
        msgBox.classList.add('non-error');
    } else {
        msgBox.classList.add('error');
    }

    msgBox.innerHTML = json.message;
    msgBox.classList.remove('disabled');
}
function messageInit(messageTag) {
    messageTag.classList.add('disabled');
    messageTag.classList.remove('non-error');
    messageTag.classList.remove('error');
    messageTag.innerHTML = '';
}


function defaultImageChange() {
    let defaultImageInputRadio = document.querySelector('input[name="defaultImage"]');
    if (defaultImageInputRadio == null) return;

    let profile = document.querySelector('#myProfile');
    if (defaultImageInputRadio.checked) {
        let imageInput = document.querySelector('input[name="profileImage"]');
        imageInput.value = null;
        profile.setAttribute('src', '/images/member_profile/basic-member-profile.jpg');
    }
}
function printPreview() {
    let imgInput = document.querySelector('#img input[type="file"]');
    if (!isImage(imgInput.files)) return false;

    let image = imgInput.files[0];
    let myProfile = document.querySelector('#myProfile');
    myProfile.src = URL.createObjectURL(image);
    myProfile.alt = image.name;
    return true;
}
function isImage(files) {
    for (let i=0;i<files.length;i++){
        let file = files[i];

        if (!String(file.type).startsWith('image/')){
            al('error', '에러', '사진만 추가할 수 있습니다.');
            return false;
        }
    }
    return true;
}

function exitPop() {
    let popList = document.querySelectorAll('.pop');
    popList.forEach(pop => pop.classList.add('disabled'));
}

function modalFindPassword() {
    let modalList = document.querySelectorAll('.pop');
    modalList.forEach(el => el.classList.add('disabled'));
    
    const modal = document.querySelector('.modal');
    const modal_content = document.querySelector('.modal-content');
    
    insertModalSize('modal-find');
    modal_content.innerHTML = createFindPassword();
    modal.classList.remove('disabled');
}
function resultPhone(result) {
    if (result.result === 'ok') {
        alert(result.message);
        location.reload();
    } else if (result.result === 'error') {
        let cPhone = document.querySelector('.confirmPhoneCheck');
        printFalse(cPhone, result.message);
    }
    
}
function removeNotNumber(text) {
    return text.replace(/[^0-9]/, "");
}

function sendSMS(result) {
    if (result.result == 'ok') {
        if (clickLimit) {
            alert("잠시 후에 시도해주세요.");
            return;
        }
        // 타이머 색상 제거
        let timer = document.querySelector('.limitTime');
        timer.style.removeProperty('color');

        let cPhone = document.querySelector('.confirmPhone');
        printTrue(cPhone, result.message);
        clearInterval(timerInterval);

        timerInterval = limitTimer();
        clickLimit = limitClick();
    } else if (result.result === 'error') {
        al('error', '에러', result.message);
    }
}

function validPhone() {
    let cPhone = document.querySelector('.confirmPhone');
    if (checkPhone()){
        printTrue(cPhone, '');
        return null;
    } else {
        printFalse(cPhone, '입력정보가 잘못되었습니다');
        return document.querySelector('input[name="phones"]');
    }
}

function printTrue(cTag, message) {
    if (cTag != null ){
        cTag.innerHTML = message;
        cTag.classList.remove('disabled')
        cTag.classList.remove("error")
    }
}


function printFalse(cTag, message) {
    if (cTag != null) {
        cTag.innerHTML = message;
        cTag.classList.add("error");
        cTag.classList.remove('disabled');
    }
}

function checkPhone() {
    let validPhobe = document.querySelector('input[name="phones"]');
    let phoneRegex = /^(010)[0-9]{7,9}$/;
    return phoneRegex.test(validPhobe.value);
}

function limitClick() {
    let seconds = 10;
    let timerId = setInterval(function(){
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
    let minutes = 5;
    let seconds = 0;
    let timerId = setInterval(function(){
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
        let formattedMinutes = ('0' + minutes).slice(-2);
        let formattedSeconds = ('0' + seconds).slice(-2);
    
        // 타이머 업데이트
        timer.textContent = formattedMinutes + ':' + formattedSeconds;
    
        // 시간이 0이면 타이머 중지
        if (minutes === 0 && seconds === 0) {
            clearInterval(timerId);
            alert('타이머 종료!');
            return true;
        }
    }, 1000);

    return timerId; // 새로운 타이머 ID 반환
}