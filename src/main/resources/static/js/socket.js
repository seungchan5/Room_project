let stompClient = null;
let token = null;
let isNewMessage = false;
let page = 0;
let loadingChatHistory = false;

window.addEventListener('load', () => {
    initialSetting();

    managerCheck();

    const chatHistory = document.querySelector('.chat-history');
    chatHistory.addEventListener('scroll', () => {

        if (isBottom()) { // 스크롤이 맨 밑에 있을 때
            isNewMessage = false;
            let newMessage = document.querySelector('.newMessageWrap');
            newMessage.classList.add('disabled');
        } else if (isTop()) {
            fetchGet(`/room/${getRoomId()}/history?token=${token}&page=${page}`, additionalHistoryResult) // 이전 기록 추가로 불러옴
        }
    })

    const newMessage = document.querySelector('#newMessage');
    newMessage.addEventListener('click', () => {
        scrollToBottom();
        deleteNewMessageAlert();
    })

})

function managerCheck() {
    if (!isManager()) {
        let kickBoxList = document.querySelectorAll('.member-kick-box');
        if (kickBoxList != null) {
            kickBoxList.forEach(kickBox => kickBox.remove());
        }
        let entrustList = document.querySelectorAll('.member-entrust-box');
        if (entrustList != null) {
            entrustList.forEach(entrust => entrust.remove());
        }
    }
}
function isManager() {
    let meTagList = document.querySelectorAll('.meTag');
    for (let i=0;i<meTagList.length;i++) {
        let me = meTagList[i].querySelector('.isMe');
        let manager = meTagList[i].querySelector('.manager');

        if (me != null && manager != null) return true;
    }
    return false;
}
function noticeResult(json) {
    updateNotice(json.data);
}
function updateNotice(notice) {
    let history = document.querySelector('.chat-history');
    let noticeWrap = document.querySelector('.room-notice');
    if (notice == null) {
        history.style.paddingTop = '1rem';
        noticeWrap.innerHTML = '';
    } else {
        history.style.paddingTop = '8rem';
        noticeWrap.innerHTML = createNotice(notice);
    }
}

function createNotice(notice) {
    return `<div class="notice">
                <svg class="speaker" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M544 32c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32s-32-14.3-32-32V64c0-17.7 14.3-32 32-32zM64 190.3L480 64V448L348.9 408.2C338.2 449.5 300.7 480 256 480c-53 0-96-43-96-96c0-11 1.9-21.7 5.3-31.5L64 321.7C63.1 338.6 49.1 352 32 352c-17.7 0-32-14.3-32-32V192c0-17.7 14.3-32 32-32c17.1 0 31.1 13.4 32 30.3zm239 203.9l-91.6-27.8c-2.1 5.4-3.3 11.4-3.3 17.6c0 26.5 21.5 48 48 48c23 0 42.2-16.2 46.9-37.8z"/></svg>
                <div class="room-notice-content">
                    <pre class="notice-text">${notice.content}</pre>
                    <div class="notice-time">${formatDay(notice.time) + ' ' + formatTime(notice.time)}</div>
                </div>
                <button type="button" class="folder">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><path d="M201.4 342.6c12.5 12.5 32.8 12.5 45.3 0l160-160c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L224 274.7 86.6 137.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3l160 160z"/></svg>
                </button>
            </div>`
}
function isBottom() {
    const chatHistory = document.querySelector('.chat-history');

    const scrollPosition = chatHistory.scrollTop;
    const scrollHeight = chatHistory.scrollHeight;
    const clientHeight = chatHistory.clientHeight;

    // 맨 위에서부터 100px까지의 스크롤 영역을 감지합니다.
    const threshold = 100;
    const triggerScroll = scrollPosition >= (scrollHeight - clientHeight - threshold);
    return triggerScroll;
}
function isTop() {
    const chatHistory = document.querySelector('.chat-history');

    const scrollPosition = chatHistory.scrollTop;

    return scrollPosition === 0;
}

function deleteNewMessageAlert() {
    isNewMessage = false;
    let newMessage = document.querySelector('.newMessageWrap');
    newMessage.classList.add('disabled');
}
function newMessageAlert() {
    const triggerScroll = isBottom();

    if (!triggerScroll) { // 스크롤이 위에있을 때
        let newMessage = document.querySelector('.newMessageWrap');
        newMessage.classList.remove('disabled');
        isNewMessage = true;
    }
    if (triggerScroll) { // 스크롤이 맨 밑에있을 때
        scrollToBottom();
    }
}

function  historyResult(json) {
    let list = json.data;
    for (let i=0;i<list.length;i++) {
        printMessage(list[i]);
    }
    if (list != undefined && list.length > 0) {
        page = list[0].pageValue;
    }
    scrollToBottom(); 
}
function additionalHistoryResult(json) {
    let list = json.data;

    for (let i=list.length-1;i>=0;i--) {
        insertChat(list[i]);
    }
    if (list !== undefined && list.length > 0) {
        page = list[0].pageValue;
    }
}
function insertChat(chat) {
    const chatHistory = document.querySelector('.chat-history');
    let firstChild = chatHistory.firstElementChild;

    let onlyChat = document.querySelector('.chat-history > *:not(.date, .alram)');

    if (chat.me || chat.token === token) { // 내 메세지
        if (onlyChat.classList.contains('me')) { // 마지막 채팅내역이 나일 경우
            onlyChat.insertBefore(createMeMessageBoxNode(chat, onlyChat), onlyChat.firstElementChild);
        } else { // 마지막 채팅내역이 내가 아닌경우
            chatHistory.insertBefore(createMeNode(chat), firstChild);
        }
    } else { // 상대방 메세지
        let name = onlyChat.querySelector('.name');
        if (name != null && name.textContent === chat.sender) { // 마지막 채팅내역이 보낸사람과 일치
            let messageWarpIntoFirstChild = onlyChat.querySelector('.message-wrap');
            messageWarpIntoFirstChild.insertBefore(createYouMessageBoxNode(chat, onlyChat), messageWarpIntoFirstChild.querySelector('.message-box'));
        } else {
            chatHistory.insertBefore(createYouNode(chat, firstChild), firstChild);
        }
    }
    nextDateReverse(chat);
}

function initialSetting() {
    fetch('/room/' + getRoomId() + '/access')
    .then(res => res.json())
    .then(map => {
        token = map.message;
        fetchGet('/room/' + getRoomId() + '/history?token=' + token, historyResult) // 이전 기록 불러옴
        fetchGet('/room/' + getRoomId() + '/notice', noticeResult) // 공지사항 불러옴
        connect(); // 웹소켓 연결
    });
}

function connect() {
    let socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

}
function sendRoomInfoUpdate() {
    stompClient.send("/pub/chat/update",
        {},
        JSON.stringify({
            roomId : Number(getRoomId()),
            token : token,
            type: 'UPDATE'
        })
    )
}

function onConnected() {
    let roomId = getRoomId();
    // sub 할 url => /sub/chat/room/enter/roomId 로 구독한다
    stompClient.subscribe('/sub/chat/room/' + roomId, onMessageReceived);

    // 서버에 username 을 가진 유저가 들어왔다는 것을 알림
    // /pub/chat/enterUser 로 메시지를 보냄
    stompClient.send("/pub/chat/enterUser",
        {},
        JSON.stringify({
            roomId : Number(roomId),
            token : token,
            type: 'ENTER'
        })
    )
}
function getRoomId() {
    // 현재 페이지의 URL을 가져옴
    let currentURL = window.location.href;

    // "room/" 다음에 있는 숫자 값을 가져오기 위한 정규식 사용
    let match = currentURL.match(/\/room\/(\d+)/);

    // match에서 추출된 값 확인
    return match ? match[1] : null;
}

function sendMessage() {
    let messageInput = document.querySelector('textarea[name="message"]');

    if (messageInput.value.trim() && stompClient) {
        let chatMessage = {
            roomId : getRoomId(),
            message : messageInput.value.trim(),
            token : token,
            type : 'TALK'
        }

        stompClient.send('/pub/chat/sendMessage', {}, JSON.stringify(chatMessage));
        message.value = '';
    }
}

function onMessageReceived(payload) {

    const history = document.querySelector('.chat-history');

    let chat = JSON.parse(payload.body);

    if (chat.type === 'ENTER') {
        history.innerHTML += centerMessage(chat.message);
        initialMemberCheck(chat);
        managerCheck();
        moveToOnline(chat.data);
    }

    if (chat.type === 'LEAVE') {
        history.innerHTML += centerMessage(chat.message);
        moveToOffline(chat.data);
    }

    if (chat.type === 'TALK') {
        printMessage(chat);
    }

    if (chat.type === 'UPDATE') {
        updateApply(chat.data);
        history.innerHTML += centerMessage(chat.message);
    }

    if (chat.type === 'EXIT') {
        removeMember(chat.sender);
        changeManager(chat.nextManager);
        setting(chat.token);
        updateMemberOption(chat.token);
        history.innerHTML += centerMessage(chat.message);
    }

    if (chat.type === 'NOTICE' || chat.type === 'NOTICE_DELETE') {
        updateNotice(chat.data);
        history.innerHTML += centerMessage(chat.message);
    }

    if (chat.type === 'KICK') {
        kickMemberCheck(chat.token);
        removeMember(chat.sender);
        history.innerHTML += centerMessage(chat.message);
    }
    
    if (chat.type === 'ENTRUST') {
        changeManager(chat.sender);
        setting(chat.token);
        updateMemberOption(chat.token);
        history.innerHTML += centerMessage(chat.message);
    }

    newMessageAlert();
}
function kickMemberCheck(kickToken) {
    if (String(token) === String(kickToken)) {
        alert('강퇴되었습니다.');
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
    }
}
function updateMemberOption(newToken) {
    if (newToken == null) return;

    let optionMenus = document.querySelectorAll('.member-option-menu');
    if (String(token) === String(newToken)) {
        optionMenus.forEach(optionMenu => {
            let box = optionMenu.querySelector('.member-option-box');
            optionMenu.insertBefore(createKickMenu(), box);
            optionMenu.insertBefore(createEntrustMenu(), box);
        })
    } else {
        let kickBoxAll = document.querySelectorAll('.member-kick-box');
        if (kickBoxAll != null) {
            kickBoxAll.forEach(el => el.remove());
        }
        let entrustAll = document.querySelectorAll('.member-entrust-box');
        if (entrustAll != null) {
            entrustAll.forEach(el => el.remove());
        }
    }
}
function createEntrustMenu() {
    let newLi = document.createElement("li");
    newLi.className = "member-option-box member-entrust-box";
    // svg 요소 생성
    let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
    svg.setAttribute("viewBox", "0 0 448 512");
    let path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", "M224 0a128 128 0 1 1 0 256A128 128 0 1 1 224 0zM178.3 304h91.4c11.8 0 23.4 1.2 34.5 3.3c-2.1 18.5 7.4 35.6 21.8 44.8c-16.6 10.6-26.7 31.6-20 53.3c4 12.9 9.4 25.5 16.4 37.6s15.2 23.1 24.4 33c15.7 16.9 39.6 18.4 57.2 8.7v.9c0 9.2 2.7 18.5 7.9 26.3H29.7C13.3 512 0 498.7 0 482.3C0 383.8 79.8 304 178.3 304zM436 218.2c0-7 4.5-13.3 11.3-14.8c10.5-2.4 21.5-3.7 32.7-3.7s22.2 1.3 32.7 3.7c6.8 1.5 11.3 7.8 11.3 14.8v17.7c0 7.8 4.8 14.8 11.6 18.7c6.8 3.9 15.1 4.5 21.8 .6l13.8-7.9c6.1-3.5 13.7-2.7 18.5 2.4c7.6 8.1 14.3 17.2 20.1 27.2s10.3 20.4 13.5 31c2.1 6.7-1.1 13.7-7.2 17.2l-14.4 8.3c-6.5 3.7-10 10.9-10 18.4s3.5 14.7 10 18.4l14.4 8.3c6.1 3.5 9.2 10.5 7.2 17.2c-3.3 10.6-7.8 21-13.5 31s-12.5 19.1-20.1 27.2c-4.8 5.1-12.5 5.9-18.5 2.4l-13.8-7.9c-6.7-3.9-15.1-3.3-21.8 .6c-6.8 3.9-11.6 10.9-11.6 18.7v17.7c0 7-4.5 13.3-11.3 14.8c-10.5 2.4-21.5 3.7-32.7 3.7s-22.2-1.3-32.7-3.7c-6.8-1.5-11.3-7.8-11.3-14.8V467.8c0-7.9-4.9-14.9-11.7-18.9c-6.8-3.9-15.2-4.5-22-.6l-13.5 7.8c-6.1 3.5-13.7 2.7-18.5-2.4c-7.6-8.1-14.3-17.2-20.1-27.2s-10.3-20.4-13.5-31c-2.1-6.7 1.1-13.7 7.2-17.2l14-8.1c6.5-3.8 10.1-11.1 10.1-18.6s-3.5-14.8-10.1-18.6l-14-8.1c-6.1-3.5-9.2-10.5-7.2-17.2c3.3-10.6 7.7-21 13.5-31s12.5-19.1 20.1-27.2c4.8-5.1 12.4-5.9 18.5-2.4l13.6 7.8c6.8 3.9 15.2 3.3 22-.6c6.9-3.9 11.7-11 11.7-18.9V218.2zm92.1 133.5a48.1 48.1 0 1 0 -96.1 0 48.1 48.1 0 1 0 96.1 0z");
    svg.appendChild(path);

    // span 요소 생성
    let span = document.createElement("span");
    span.textContent = "방장 위임";

    // 생성된 요소들을 새로운 li 요소에 추가
    newLi.appendChild(svg);
    newLi.appendChild(span);
    return newLi;
}
function createKickMenu() {

    let newLi = document.createElement("li");
    newLi.className = "member-option-box member-kick-box";
    // svg 요소 생성
    let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
    svg.setAttribute("viewBox", "0 0 448 512");
    let path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", "M96 128a128 128 0 1 1 256 0A128 128 0 1 1 96 128zM0 482.3C0 383.8 79.8 304 178.3 304h91.4C368.2 304 448 383.8 448 482.3c0 16.4-13.3 29.7-29.7 29.7H29.7C13.3 512 0 498.7 0 482.3zM472 200H616c13.3 0 24 10.7 24 24s-10.7 24-24 24H472c-13.3 0-24-10.7-24-24s10.7-24 24-24z");
    svg.appendChild(path);

    // span 요소 생성
    let span = document.createElement("span");
    span.textContent = "내보내기";

    // 생성된 요소들을 새로운 li 요소에 추가
    newLi.appendChild(svg);
    newLi.appendChild(span);
    return newLi;
}
function setting(newToken) {
    if (newToken == null) return;
    if (String(token) === String(newToken)) {
        let roomTitleWrap = document.querySelector('.room-title-wrap');
        roomTitleWrap.innerHTML += createSettingBtn();
    } else {
        let roomSetting = document.querySelector('.room-setting');
        if (roomSetting != null) {
            roomSetting.remove();
        }
    }
}
function createSettingBtn() {
    return `<button type="button" id="setting" class="room-setting">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M495.9 166.6c3.2 8.7 .5 18.4-6.4 24.6l-43.3 39.4c1.1 8.3 1.7 16.8 1.7 25.4s-.6 17.1-1.7 25.4l43.3 39.4c6.9 6.2 9.6 15.9 6.4 24.6c-4.4 11.9-9.7 23.3-15.8 34.3l-4.7 8.1c-6.6 11-14 21.4-22.1 31.2c-5.9 7.2-15.7 9.6-24.5 6.8l-55.7-17.7c-13.4 10.3-28.2 18.9-44 25.4l-12.5 57.1c-2 9.1-9 16.3-18.2 17.8c-13.8 2.3-28 3.5-42.5 3.5s-28.7-1.2-42.5-3.5c-9.2-1.5-16.2-8.7-18.2-17.8l-12.5-57.1c-15.8-6.5-30.6-15.1-44-25.4L83.1 425.9c-8.8 2.8-18.6 .3-24.5-6.8c-8.1-9.8-15.5-20.2-22.1-31.2l-4.7-8.1c-6.1-11-11.4-22.4-15.8-34.3c-3.2-8.7-.5-18.4 6.4-24.6l43.3-39.4C64.6 273.1 64 264.6 64 256s.6-17.1 1.7-25.4L22.4 191.2c-6.9-6.2-9.6-15.9-6.4-24.6c4.4-11.9 9.7-23.3 15.8-34.3l4.7-8.1c6.6-11 14-21.4 22.1-31.2c5.9-7.2 15.7-9.6 24.5-6.8l55.7 17.7c13.4-10.3 28.2-18.9 44-25.4l12.5-57.1c2-9.1 9-16.3 18.2-17.8C227.3 1.2 241.5 0 256 0s28.7 1.2 42.5 3.5c9.2 1.5 16.2 8.7 18.2 17.8l12.5 57.1c15.8 6.5 30.6 15.1 44 25.4l55.7-17.7c8.8-2.8 18.6-.3 24.5 6.8c8.1 9.8 15.5 20.2 22.1 31.2l4.7 8.1c6.1 11 11.4 22.4 15.8 34.3zM256 336a80 80 0 1 0 0-160 80 80 0 1 0 0 160z"/></svg>
                <ul class="setting-menu-list disabled">
                    <li class="setting-menu" id="notice-setting">
                        <svg class="speaker" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 576 512"><path d="M544 32c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32s-32-14.3-32-32V64c0-17.7 14.3-32 32-32zM64 190.3L480 64V448L348.9 408.2C338.2 449.5 300.7 480 256 480c-53 0-96-43-96-96c0-11 1.9-21.7 5.3-31.5L64 321.7C63.1 338.6 49.1 352 32 352c-17.7 0-32-14.3-32-32V192c0-17.7 14.3-32 32-32c17.1 0 31.1 13.4 32 30.3zm239 203.9l-91.6-27.8c-2.1 5.4-3.3 11.4-3.3 17.6c0 26.5 21.5 48 48 48c23 0 42.2-16.2 46.9-37.8z"/></svg>
                        <span>공지사항</span>
                    </li>
                    <li class="setting-menu" id="default-setting">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M495.9 166.6c3.2 8.7 .5 18.4-6.4 24.6l-43.3 39.4c1.1 8.3 1.7 16.8 1.7 25.4s-.6 17.1-1.7 25.4l43.3 39.4c6.9 6.2 9.6 15.9 6.4 24.6c-4.4 11.9-9.7 23.3-15.8 34.3l-4.7 8.1c-6.6 11-14 21.4-22.1 31.2c-5.9 7.2-15.7 9.6-24.5 6.8l-55.7-17.7c-13.4 10.3-28.2 18.9-44 25.4l-12.5 57.1c-2 9.1-9 16.3-18.2 17.8c-13.8 2.3-28 3.5-42.5 3.5s-28.7-1.2-42.5-3.5c-9.2-1.5-16.2-8.7-18.2-17.8l-12.5-57.1c-15.8-6.5-30.6-15.1-44-25.4L83.1 425.9c-8.8 2.8-18.6 .3-24.5-6.8c-8.1-9.8-15.5-20.2-22.1-31.2l-4.7-8.1c-6.1-11-11.4-22.4-15.8-34.3c-3.2-8.7-.5-18.4 6.4-24.6l43.3-39.4C64.6 273.1 64 264.6 64 256s.6-17.1 1.7-25.4L22.4 191.2c-6.9-6.2-9.6-15.9-6.4-24.6c4.4-11.9 9.7-23.3 15.8-34.3l4.7-8.1c6.6-11 14-21.4 22.1-31.2c5.9-7.2 15.7-9.6 24.5-6.8l55.7 17.7c13.4-10.3 28.2-18.9 44-25.4l12.5-57.1c2-9.1 9-16.3 18.2-17.8C227.3 1.2 241.5 0 256 0s28.7 1.2 42.5 3.5c9.2 1.5 16.2 8.7 18.2 17.8l12.5 57.1c15.8 6.5 30.6 15.1 44 25.4l55.7-17.7c8.8-2.8 18.6-.3 24.5 6.8c8.1 9.8 15.5 20.2 22.1 31.2l4.7 8.1c6.1 11 11.4 22.4 15.8 34.3zM256 336a80 80 0 1 0 0-160 80 80 0 1 0 0 160z"/></svg>
                        <span>설정</span>
                    </li>
                </ul>
            </button>`;
}
function changeManager(nextManager) {
    if (nextManager == null) return;
    let offline =  document.querySelectorAll('.member-list[data-is-online="false"] .member-data span');
    let online = document.querySelectorAll('.member-list[data-is-online="true"] .member-data span');

    let managerTagList = document.querySelectorAll('.manager');
    managerTagList.forEach(tag => tag.remove());


    for (let i=0;i<offline.length;i++) {
        if (offline[i].getAttribute('name') == nextManager) {
            let meTag = offline[i].parentElement.querySelector('.meTag');
            meTag.innerHTML += '<div class="manager">방장</div>';
            return;
        }
    }
    for (let i=0;i<online.length;i++) {
        if (online[i].getAttribute('name') == nextManager) {
            let meTag = online[i].parentElement.querySelector('.meTag');
            meTag.innerHTML += '<div class="manager">방장</div>';
            return;
        }
    }

}
function removeMember(sender) {
    let current = document.querySelector('#current');
    let currentCount = current.textContent;
    if (!isNaN(currentCount)) current.innerHTML = Math.max(Number(currentCount) - 1, 0);

    let offline =  document.querySelectorAll('.member-list[data-is-online="false"] .member-data span');
    let online = document.querySelectorAll('.member-list[data-is-online="true"] .member-data span');

    offline.forEach(el => {
        if (el.getAttribute('name') == sender) el.parentElement.parentElement.remove();
    })

    online.forEach(el => {
        if (el.getAttribute('name') == sender) el.parentElement.parentElement.remove();
    })
}
function updateApply(roomInfo) {
     changeLockSvg(roomInfo.public);

    let title = document.querySelector('#roomTitle');
    title.innerHTML = roomInfo.title;

    let maximum = document.querySelector('#maximum');
    maximum.innerHTML = roomInfo.max;
}
function changeLockSvg(isPublic) {
    let roomTitleWrap = document.querySelector('.room-title');
    let roomTitle = document.querySelector('#roomTitle');

    let privateSvg = roomTitleWrap.children.private;

    if (isPublic && privateSvg !== undefined) {
        privateSvg.remove();
        return;
    }
    if (!isPublic && privateSvg === undefined) {
        roomTitleWrap.insertBefore(getPrivateSvg(), roomTitle);
    }
}
function getPrivateSvg() {
    let svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
    svgElement.setAttribute("id", "private");
    svgElement.setAttribute("viewBox", "0 0 448 512");

    // Create the path element
    let pathElement = document.createElementNS("http://www.w3.org/2000/svg", "path");
    pathElement.setAttribute("d", "M144 144v48H304V144c0-44.2-35.8-80-80-80s-80 35.8-80 80zM80 192V144C80 64.5 144.5 0 224 0s144 64.5 144 144v48h16c35.3 0 64 28.7 64 64V448c0 35.3-28.7 64-64 64H64c-35.3 0-64-28.7-64-64V256c0-35.3 28.7-64 64-64H80z");

    // Append the path element to the SVG element
    svgElement.appendChild(pathElement);
    return svgElement;
}
function initialMemberCheck(chat) { // 처음들어온 회원인지 확인
    let offline =  document.querySelector('.member-list[data-is-online="false"]');
    let online =  document.querySelector('.member-list[data-is-online="true"]');
    let memberInOffline = offline.querySelector('span[name="' + chat.sender + '"]');
    let memberInOnline = online.querySelector('span[name="' + chat.sender + '"]');

    if (memberInOffline == null && memberInOnline == null) {
        let current = document.querySelector('#current');
        let currentCount = current.textContent;
        if (!isNaN(currentCount)) {
            current.innerHTML = Number(currentCount) + 1;
        }
        offline.innerHTML += createMember(chat);
    }
}
function moveToOnline(currentMemberList) {
    let online = document.querySelector('.member-list[data-is-online="true"]');
    for (let i=0;i<currentMemberList.length; i++) {
        let memberTag = document.querySelector('.member-list[data-is-online="false"] span[name="' + currentMemberList[i] + '"]');

        let alreadyExitsInOnline = online.querySelector('span[name="' + currentMemberList[i] + '"]');
        if (alreadyExitsInOnline) continue;
        if (memberTag != null) {
            let member = memberTag.parentElement.parentElement;
            online.appendChild(member);
        }
    }
}
function moveToOffline(currentMemberList) {
    let offline = document.querySelector('.member-list[data-is-online="false"]');

    let onlineList = document.querySelectorAll('.member-list[data-is-online="true"] .member-data span');

    a:for (let i=0;i<onlineList.length;i++) {
        for (let j=0;j<currentMemberList.length;j++) {
            if (onlineList[i].textContent == currentMemberList[j]) {
                continue a;
            }
        }
        let member = onlineList[i].parentElement.parentElement;
        offline.appendChild(member);
    }
}

function timeMessage(message) {
    return `<div class="date">
                <span>${message}</span>
            </div>`
}
function centerMessage(message) {
    return `<div class="alram">
                <span>${message}</span>
            </div>`
}

function onError() {
    al('error', '에러', '서버와 연결이 끊겼습니다. 다시 시도해주세요.');
}

function printMessage(chat) {
    const chatHistory = document.querySelector('.chat-history');
    let lastElement = chatHistory.lastElementChild;

    let scrollLocation = isBottom();

    nextDate(chat, lastElement);

    if (chat.me || chat.token === token) { // 내 메세지
        if (lastElement == null) {
            chatHistory.innerHTML += createMe(chat);
            return;
        }
        if (lastElement.classList.contains('me')) { // 마지막 채팅내역이 나일 경우
            distinctTime(lastElement, chat);
            lastElement.innerHTML += createMeMessageBox(chat); 
        } else { // 마지막 채팅내역이 내가 아닌경우
            chatHistory.innerHTML += createMe(chat);
        }
        scrollToBottom();
    } else { // 상대방 메세지
        if (lastElement == null) {
            chatHistory.innerHTML += createYou(chat);
            return;
        }
        let name = lastElement.querySelector('.name');
        if (name != null && name.textContent === chat.sender) { // 마지막 채팅내역이 보낸사람과 일치
            distinctTime(lastElement, chat);
            lastElement.querySelector('.message-wrap').innerHTML += createYouMessageBox(chat);
        } else {
            chatHistory.innerHTML += createYou(chat);
        }
    }
    if (scrollLocation) {
        scrollToBottom();
    }
}

function distinctTime(lastElement, chat) {
    let beforeTimeDiv = lastElement.querySelector('.message-box:last-child .time');

    let nowTime = formatTime(chat.time);
    if (beforeTimeDiv.textContent === nowTime) {
        beforeTimeDiv.remove();
    }
}
function distinctTimeReverse(chat, firstChild) {
    let beforeTimeDiv = firstChild.querySelector('.time');
    let nowTime = formatTime(chat.time);
    return beforeTimeDiv.textContent === nowTime;
}

function nextDate(chat, lastElement) {
    const chatHistory = document.querySelector('.chat-history');
    let day = formatDay(chat.time);

    if (lastElement == null) { // 태그가 하나도 없으면 날짜 표시
        chatHistory.innerHTML += timeMessage(day); // 2021년 1월 1일 월요일 출력
        return;
    }
    if (lastElement.classList.contains('date')) return; // 마지막 태그가 날짜면 표시하지 않음

    let dayTag = chatHistory.querySelector('.message-box:last-child .day');

    if (dayTag == null || dayTag.textContent !== day) { // 날짜태그가 존재하지않거나 변경되었으면 날짜 표시
        chatHistory.innerHTML += timeMessage(day); // 2021년 1월 1일 월요일 출력
    }
}

function nextDateReverse(chat) {
    const chatHistory = document.querySelector('.chat-history');
    let firstElement = chatHistory.firstElementChild;
    while (firstElement.classList.contains('date')) {
        firstElement = firstElement.nextElementSibling;
    }
    let day = formatDay(chat.time);

    let dayTag = chatHistory.querySelector('.date span');
    if (dayTag != null && dayTag.textContent === day) {
        chatHistory.insertBefore(chatHistory.querySelector('.date'), firstElement);
    } else {
        chatHistory.insertBefore(newDate(day), firstElement);
    }

}
function newDate(day) {
    let dateDiv = document.createElement('div');
    dateDiv.classList.add('date');

    let span = document.createElement('span');
    span.innerHTML = day;
    dateDiv.append(span);
    return dateDiv;
}

function formatDay(time) {
    let dateObject = new Date(time);
    return new Intl.DateTimeFormat('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                weekday: 'long'
            }).format(dateObject);
}
function formatTime(time) {
    let dateObject = new Date(time);
    return ('0' + dateObject.getHours()).slice(-2) + ':' + ('0' + dateObject.getMinutes()).slice(-2);
}
function createMeMessageBox(chat) {
    return `<div class="message-box">
                <span class="day" style="display: none;">` + formatDay(chat.time) +`</span>
                <span class="time">` + formatTime(chat.time) + `</span>
                <pre class="message-content">
                    ${chat.message}
                </pre>
            </div>`
}
function createMeMessageBoxNode(chat, firstChild) {
    let messageBox = document.createElement('div');
    messageBox.classList.add('message-box');

    let daySpan = document.createElement('span');
    daySpan.style.display = 'none';
    daySpan.innerHTML = formatDay(chat.time);

    let timeSpan = document.createElement('span');
    timeSpan.classList.add('time');
    timeSpan.innerHTML = formatTime(chat.time);

    let contentPre = document.createElement('pre');
    contentPre.classList.add('message-content');
    contentPre.innerHTML = chat.message;

    // messageBox.append(daySpan, timeSpan, contentPre);
    messageBox.append(daySpan);
    if (!distinctTimeReverse(chat, firstChild)) {
        messageBox.append(timeSpan);
    }
    messageBox.append(contentPre);
    return messageBox;
}
function createYouMessageBox(chat) {
    return `<div class="message-box">
                <pre class="message-content">
                    ${chat.message}
                </pre>
                <span class="time">` + formatTime(chat.time) + `</span>
                <span class="day" style="display: none;">` + formatDay(chat.time) +`</span>
            </div>`
}
function createYouMessageBoxNode(chat, firstChild) {
    let messageBoxDiv = document.createElement('div');
    messageBoxDiv.classList.add('message-box');

    let contentPre = document.createElement('pre');
    contentPre.classList.add('message-content');
    contentPre.innerHTML = chat.message;

    let timeSpan = document.createElement('span');
    timeSpan.classList.add('time');
    timeSpan.innerHTML = formatTime(chat.time);

    let daySpan = document.createElement('span');
    daySpan.style.display = 'none';
    daySpan.innerHTML = formatDay(chat.time);

    messageBoxDiv.append(contentPre);
    if (!distinctTimeReverse(chat, firstChild)) {
        messageBoxDiv.append(timeSpan);
    }
    messageBoxDiv.append(daySpan);
    return messageBoxDiv;
}

function createMe(chat) {
    return `<div class="me">
                ` + createMeMessageBox(chat) + `
            </div>`
}
function createMeNode(chat) {
    let div = document.createElement('div');
    div.classList.add('me');
    div.innerHTML = createMeMessageBox(chat);
    return div;
}
function createYou(chat) {
    return `<div class="you">
                <div class="profile-box">
                    <img src="/images/member_profile/${chat.senderImage}" alt="">
                </div>
                <div class="message-wrap">
                    <div class="name-box">
                        <span class="name">${chat.sender}</span>
                    </div>
                    ` + createYouMessageBox(chat) + `
                </div>
            </div>`
}
function createYouNode(chat, firstElement) {
    let youDiv = document.createElement('div');
    youDiv.classList.add('you');

    let profileDiv = document.createElement('div');
    profileDiv.classList.add('profile-box');
    let img = document.createElement('img');
    img.setAttribute('src', '/images/member_profile/' + chat.senderImage);
    profileDiv.append(img);

    let messageWrapDiv = document.createElement('div');
    messageWrapDiv.classList.add('message-wrap');
    let nameBoxDiv = document.createElement('div');
    nameBoxDiv.classList.add('name-box');
    let nameSpan = document.createElement('span');
    nameSpan.classList.add('name');
    nameSpan.innerHTML = chat.sender;
    nameBoxDiv.append(nameSpan);
    messageWrapDiv.append(nameBoxDiv);

    messageWrapDiv.append(createYouMessageBoxNode(chat, firstElement));

    return messageWrapDiv;
}

function createMember(chat) {
    return `<div class="member">
                <div class="member-data">
                    <img src="/images/member_profile/${chat.senderImage}" alt="">
                    <span name="${chat.sender}">${chat.sender}</span>
                    <div class="meTag"></div>
                </div>
                <button type="button" class="member-more">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><path d="M8 256a56 56 0 1 1 112 0A56 56 0 1 1 8 256zm160 0a56 56 0 1 1 112 0 56 56 0 1 1 -112 0zm216-56a56 56 0 1 1 0 112 56 56 0 1 1 0-112z"/></svg>
                <ul class="member-option-menu disabled" name="option-menu">
                    <li class="member-option-box member-entrust-box">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512"><path d="M224 0a128 128 0 1 1 0 256A128 128 0 1 1 224 0zM178.3 304h91.4c11.8 0 23.4 1.2 34.5 3.3c-2.1 18.5 7.4 35.6 21.8 44.8c-16.6 10.6-26.7 31.6-20 53.3c4 12.9 9.4 25.5 16.4 37.6s15.2 23.1 24.4 33c15.7 16.9 39.6 18.4 57.2 8.7v.9c0 9.2 2.7 18.5 7.9 26.3H29.7C13.3 512 0 498.7 0 482.3C0 383.8 79.8 304 178.3 304zM436 218.2c0-7 4.5-13.3 11.3-14.8c10.5-2.4 21.5-3.7 32.7-3.7s22.2 1.3 32.7 3.7c6.8 1.5 11.3 7.8 11.3 14.8v17.7c0 7.8 4.8 14.8 11.6 18.7c6.8 3.9 15.1 4.5 21.8 .6l13.8-7.9c6.1-3.5 13.7-2.7 18.5 2.4c7.6 8.1 14.3 17.2 20.1 27.2s10.3 20.4 13.5 31c2.1 6.7-1.1 13.7-7.2 17.2l-14.4 8.3c-6.5 3.7-10 10.9-10 18.4s3.5 14.7 10 18.4l14.4 8.3c6.1 3.5 9.2 10.5 7.2 17.2c-3.3 10.6-7.8 21-13.5 31s-12.5 19.1-20.1 27.2c-4.8 5.1-12.5 5.9-18.5 2.4l-13.8-7.9c-6.7-3.9-15.1-3.3-21.8 .6c-6.8 3.9-11.6 10.9-11.6 18.7v17.7c0 7-4.5 13.3-11.3 14.8c-10.5 2.4-21.5 3.7-32.7 3.7s-22.2-1.3-32.7-3.7c-6.8-1.5-11.3-7.8-11.3-14.8V467.8c0-7.9-4.9-14.9-11.7-18.9c-6.8-3.9-15.2-4.5-22-.6l-13.5 7.8c-6.1 3.5-13.7 2.7-18.5-2.4c-7.6-8.1-14.3-17.2-20.1-27.2s-10.3-20.4-13.5-31c-2.1-6.7 1.1-13.7 7.2-17.2l14-8.1c6.5-3.8 10.1-11.1 10.1-18.6s-3.5-14.8-10.1-18.6l-14-8.1c-6.1-3.5-9.2-10.5-7.2-17.2c3.3-10.6 7.7-21 13.5-31s12.5-19.1 20.1-27.2c4.8-5.1 12.4-5.9 18.5-2.4l13.6 7.8c6.8 3.9 15.2 3.3 22-.6c6.9-3.9 11.7-11 11.7-18.9V218.2zm92.1 133.5a48.1 48.1 0 1 0 -96.1 0 48.1 48.1 0 1 0 96.1 0z"/></svg>
                        <span>방장 위임</span>
                    </li>
                    <li class="member-option-box member-kick-box">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512"><path d="M96 128a128 128 0 1 1 256 0A128 128 0 1 1 96 128zM0 482.3C0 383.8 79.8 304 178.3 304h91.4C368.2 304 448 383.8 448 482.3c0 16.4-13.3 29.7-29.7 29.7H29.7C13.3 512 0 498.7 0 482.3zM472 200H616c13.3 0 24 10.7 24 24s-10.7 24-24 24H472c-13.3 0-24-10.7-24-24s10.7-24 24-24z"/></svg>
                        <span>내보내기</span>
                    </li>
                    <li class="member-option-box member-notify-box">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path fill="#ff0000" d="M272 384c9.6-31.9 29.5-59.1 49.2-86.2l0 0c5.2-7.1 10.4-14.2 15.4-21.4c19.8-28.5 31.4-63 31.4-100.3C368 78.8 289.2 0 192 0S16 78.8 16 176c0 37.3 11.6 71.9 31.4 100.3c5 7.2 10.2 14.3 15.4 21.4l0 0c19.8 27.1 39.7 54.4 49.2 86.2H272zM192 512c44.2 0 80-35.8 80-80V416H112v16c0 44.2 35.8 80 80 80zM112 176c0 8.8-7.2 16-16 16s-16-7.2-16-16c0-61.9 50.1-112 112-112c8.8 0 16 7.2 16 16s-7.2 16-16 16c-44.2 0-80 35.8-80 80z"/></svg>
                        <span>신고하기</span>
                    </li>
                </ul>
            </button>
            </div>`
}
