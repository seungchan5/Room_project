window.addEventListener('load', () => {

    const center_wrap = document.querySelector('.center-wrap');
    center_wrap.classList.remove('display');

    const enter = document.querySelector('#enter-room');
    enter.addEventListener('click', () => enterRoom());


    const password = document.querySelector('input[name="roomPassword"]');
    password.addEventListener('focus', () => msg.innerHTML = '');

    const msg = document.querySelector('.errorMsg');

})
function enterRoom() {
    const password = document.querySelector('input[name="roomPassword"]');
    const msg = document.querySelector('.errorMsg');

    if (password == null || password.value.length == 0) {
        msg.innerHTML = '비밀번호를 입력해주세요';
        return;
    }

    fetchPost(window.location.href, password.value, enterRoomResult);
}
function enterRoomResult(json) {
    const password = document.querySelector('input[name="roomPassword"]');
    const msg = document.querySelector('.errorMsg');
    password.value = '';
    msg.textContent = '';

    if (json.result == 'ok') {
        window.location.href = json.message
    } else if (json.result == 'invalidPassword') {
        msg.textContent = json.message;
    } else {
        alert(json.message);
        window.location.href='/';
    }
}


function fetchPost(url, data, callback) {
    fetch(url , { method : 'post'
					, headers : {'Content-Type' : 'application/json'}
					, body : data
				})
    .then(res => res.json())
    .then(map => callback(map));;
}