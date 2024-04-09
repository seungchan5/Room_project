window.addEventListener('load', () => {
    const modal_content = document.querySelector('.modal-content');

    // 부모에게 이벤트 위임
    modal_content.addEventListener('click', (e) => {
        let target = e.target;

        createRoomMessageInit();

        if (target.id === 'room-cancel') {
            modalExit();
            return;
        }

        if (target.id === 'room-edit') {
            roomEditSubmit();
        }

        if (target.id === 'defaultImage') {
            defaultImageChange();
        }

        if (target.id === 'img') {
            let changeImageInput = document.querySelector('input[name="roomImage"]');
            changeImageInput.click();
            imageInputAddEventListener(changeImageInput);
            return;
        }

        if (target.name === 'max') {
            clearLabel();
            let label = document.querySelector('label[for="' + target.id + '"]');
            label.setAttribute('aria-selected', 'true');
            return;
        }

        if (target.classList.contains('xSvg')) {
            let tagBox = target.parentElement;
            tagBox.remove();
            if (!isTagMax() && tagAddNotContains()) {
                createTagAddBox();
            }
            return;
        }

        if (target.getAttribute('for') === 'private') {
            let publicLabel = document.querySelector('label[for="public"]');
            publicLabel.setAttribute('aria-selected', 'false');
            target.setAttribute('aria-selected', 'true');
            let password_box = document.querySelector('.password-box');
            password_box.classList.remove('disabled');
        }
        if (target.getAttribute('for') === 'public') {
            let privateLabel = document.querySelector('label[for="private"]');
            privateLabel.setAttribute('aria-selected', 'false');
            target.setAttribute('aria-selected', 'true');
            let password_box = document.querySelector('.password-box');
            password_box.classList.add('disabled');
            let passwordInput = document.querySelector('input[name="room-password"]');
            passwordInput.value = '';

        }
    })

    modal_content.addEventListener('keydown', (e) => {

        // 엔터를 눌렀을 때
        if (e.keyCode === 13 && e.target.id == 'tag-add') {
            let value = validValue(e.target.value);
            if (!isTagMax()) {
                createTag(value);
            }
            let tagList = document.querySelectorAll('.tag-box');
            if (tagList.length == 5) {
                let tagAdd = document.querySelector('.tag-add-box');
                tagAdd.remove();
            }
            e.target.value = '';
        }
    })

})

function defaultImageChange() {
    let defaultImageInputRadio = document.querySelector('input[name="defaultImage"]');
    if (defaultImageInputRadio == null) return;

    let roomImage = document.querySelector('#roomProfile');
    if (defaultImageInputRadio.checked) {
        let imageInput = document.querySelector('input[name="roomImage"]');
        imageInput.value = null;
        roomImage.setAttribute('src', '/images/room_profile/basic-room-profile.jpg');
    }
}
function createRoomMessageInit() {
    let title = document.querySelector('input[name="title"]');
    let intro = document.querySelector('input[name="intro"]');
    let password = document.querySelector('input[name="room-password"]');

    if (title == null || intro == null || password == null) {
        return;
    }
    title.addEventListener('focus', () => messageInit(document.querySelector('.m-title')));
    intro.addEventListener('focus', () => messageInit(document.querySelector('.m-intro')));
    password.addEventListener('focus', () => messageInit(document.querySelector('.m-roomPassword')));
}

function fetchCreateRoom(url, formData, callback) {
    fetch(url , { 
                    method : 'post',
                    body: formData,
				})
    .then(res => res.json())
    .then(map => callback(map));
}

function roomEditSubmit() {
    let image = document.querySelector('input[name="roomImage"]');
    let title = document.querySelector('input[name="title"]');
    let intro = document.querySelector('input[name="intro"]');
    let max = document.querySelector('input[name="max"]:checked');
    let tags = document.querySelectorAll('span[name="tag"]');
    let public = document.querySelector('input[name="public"]:checked');
    let roomPassword = document.querySelector('input[name="room-password"]');
    let defaultImageInputRadio = document.querySelector('input[name="defaultImage"]');

    if (image == null || title == null || intro == null || public == null || roomPassword == null || defaultImageInputRadio == null) {
        alert('잘못된 접근입니다. 다시 시도해주세요');
        location.reload();
        return;
    }

    let errorList = [];
    let validImage = isImage(image.files);
    if (!validImage) {
        errorList.push(image);
    }
    validTitle(errorList, title);
    validIntro(errorList, intro);
    validMax(max);
    validPublic(errorList, public);

    if (errorList != 0) {
        errorList.forEach(errorInput => errorInput.classList.add('invalid'));
        setTimeout(() => {
            errorList.forEach(errorInput => errorInput.classList.remove('invalid'));
        }, 500);
        return;
    }
    // 자바스크립트 코드
    let formData = new FormData();
    if (image.files.length > 0) {
        formData.append('image', image.files.item(0));
    }
    formData.append('title', title.value);
    formData.append('intro', intro.value);
    formData.append('max', max.value);
    formData.append('tags', convertTags(tags));
    formData.append('roomPublic', public.value);
    formData.append('defaultImage', defaultImageInputRadio.checked);
    if (public.id != 'public') {
        formData.append('roomPassword', roomPassword.value);
    }

    fetchCreateRoom('/room/' + getRoomId() + '/edit', formData, roomEditResult);

}

function roomEditResult(json) {
    if (json.result == 'ok') {
        al(json.result, json.message, '');
        modalExit();
        sendRoomInfoUpdate();
    }
    if (json.result == 'error') {
        json.data.forEach(error => {
            let msgBox = document.querySelector('.m-' + error.location);
            let result = {result : 'error', message : error.message};
            printMessageFromJson(result, msgBox);
        })
    }
    if (json.result == 'notLogin') {
        al('error', '로그인 필요', json.message);
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
    }
}
function convertTags(tags) {
    let jsonTags = [];
    if (tags == null) return null;

    for (let i=0;i<tags.length;i++) {
        jsonTags.push(tags[i].textContent);
    }
    return jsonTags;
}
function validMax(max) {
    let message = document.querySelector('.m-max');
    if (max == null) {
        let json = {result : 'error', message : '인원 수를 설정해주세요.'};
        printMessageFromJson(json, message);
    }
}

function validTitle(errorList, title) {
    let message = document.querySelector('.m-title');
    if (title.value.length == 0) {
        let json = {result : 'error', message : '제목을 적어주세요'};
        printMessageFromJson(json, message);
        errorList.push(title);
        return;
    }
    if (title.value.length > 10) {
        let json = {result : 'error', message : '방 제목은 10자 이하만 가능합니다.'};
        printMessageFromJson(json, message);
        errorList.push(title);
        return;
    }
}
function validIntro(errorList, intro) {
    let message = document.querySelector('.m-intro');
    if (intro.value.length == 0) {
        let json = {result : 'error', message : '소개글을 작성해주세요.'};
        printMessageFromJson(json, message);
        errorList.push(intro);
        return;
    }
    if (intro.value.length > 50) {
        let json = {result : 'error', message : '소개글 50자 이하만 가능합니다.'};
        printMessage(json, message);
        errorList.push(intro);
        return;
    }
}
function validValue(value) {
    if (value.length > 10) {
        return value.substring(0, 10);
    } else {
        return value;
    }
}
function validPublic(errorList, public) {
    if (public.id != 'private') return;

    let message = document.querySelector('.m-roomPassword');

    let roomPassword = document.querySelector('input[name="room-password"]').value;
    if (roomPassword.length == 0) {
        let json = {result : 'error', message : '비밀번호를 설정해주세요.'};
        printMessageFromJson(json, message);
        errorList.push(intro);
        return;
    }
    if (roomPassword.length < 4 || roomPassword.length > 6) {
        let json = {result : 'error', message : '비밀번호 4~6자리를 입력해주세요.'};
        printMessageFromJson(json, message);
        errorList.push(intro);
        return;
    }
    
}

function createTagAddBox() {
    
    // 새로운 요소 생성
    let tagAddBox = document.createElement('div');
    tagAddBox.classList.add('tag-add-box');

    let spanElement = document.createElement('span');
    spanElement.textContent = '#';

    let inputElement = document.createElement('input');
    inputElement.setAttribute('type', 'text');
    inputElement.setAttribute('name', 'tag-add');
    inputElement.setAttribute('id', 'tag-add');
    inputElement.setAttribute('placeholder', '태그입력');
    inputElement.setAttribute('maxlength', '10');

    // 생성된 요소들을 조립하여 새로운 요소에 추가
    tagAddBox.appendChild(spanElement);
    tagAddBox.appendChild(inputElement);

    // 생성한 요소를 .tag-list의 자식으로 추가
    document.querySelector('.tag-list').appendChild(tagAddBox);

}
function tagAddNotContains() {
    let tagAdd = document.querySelector('.tag-add-box');
    return tagAdd == null;
}
function isTagMax() {
    let tagList = document.querySelectorAll('.tag-box');
    return tagList.length > 5;
}
function createTag(value) {

    let tagList = document.querySelector('.tag-list');
    let tagAddBox = document.querySelector('.tag-add-box');

    // 새로운 태그 상자 생성
    let newTagBox = document.createElement('div');
    newTagBox.classList.add('tag-box');

    // 태그 아이콘 추가
    let tagIcon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    tagIcon.classList.add('tagSvg');
    tagIcon.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
    tagIcon.setAttribute('viewBox', '0 0 448 512');
    let tagIconPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    tagIconPath.setAttribute('d', 'M0 80V229.5c0 17 6.7 33.3 18.7 45.3l176 176c25 25 65.5 25 90.5 0L418.7 317.3c25-25 25-65.5 0-90.5l-176-176c-12-12-28.3-18.7-45.3-18.7H48C21.5 32 0 53.5 0 80zm112 32a32 32 0 1 1 0 64 32 32 0 1 1 0-64z');
    tagIcon.appendChild(tagIconPath);
    newTagBox.appendChild(tagIcon);

    // 새로운 태그 텍스트 추가
    let newTagText = document.createElement('span');
    newTagText.setAttribute('type', 'text');
    newTagText.setAttribute('name', 'tag');
    newTagText.setAttribute('value', value);
    newTagText.textContent = value;
    newTagBox.appendChild(newTagText);

    // 삭제 아이콘 추가
    let deleteIcon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    deleteIcon.classList.add('xSvg');
    deleteIcon.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
    deleteIcon.setAttribute('viewBox', '0 0 384 512');
    let deleteIconPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    deleteIconPath.setAttribute('d', 'M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z');
    deleteIcon.appendChild(deleteIconPath);
    newTagBox.appendChild(deleteIcon);

    // 새로운 태그 상자를 tagList에 추가
    tagList.insertBefore(newTagBox, tagAddBox);
    
}
function clearLabel() {
    let labelList = document.querySelectorAll('.max-wrap label');
    labelList.forEach(label => label.setAttribute('aria-selected', 'false'));
}

function imageInputAddEventListener(input) {
    function handleChange() {
        let files = input.files;
    
        if (!isImage(files)) {
            input.value = '';
            return;
        } 
        /* 설정변경 시 기본이미지 라디오 처리 */
        let defaultImageInputRadio = document.querySelector('input[name="defaultImage"]');
        if (defaultImageInputRadio != null) {
            defaultImageInputRadio.checked = false;
        }
        
        printPreview(files.item(0));
        input.removeEventListener('change', handleChange); // 이벤트 제거
    }
    // change 이벤트에 대한 핸들러 함수 추가
    input.addEventListener('change', handleChange);
}
function printPreview(imageFile) {
    let roomProfile = document.querySelector('#roomProfile');
    roomProfile.src = URL.createObjectURL(imageFile);
    roomProfile.alt = imageFile.name;
}
function isImage(files) {
    for (let i=0;i<files.length;i++){
        let file = files[i];

        if (!String(file.type).startsWith('image/')){
            al('error', '사진에러', '사진만 추가할 수 있습니다.');
            return false;
        }
    }
    return true;
}

function printMessageFromJson(json, msgBox) {
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