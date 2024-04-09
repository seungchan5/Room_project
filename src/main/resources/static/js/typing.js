const keywords = ["코딩", "공부", "면접준비", "CS공부"];
let index = 0;
window.addEventListener('load', () => {
    const target = document.querySelector(".typing");

    setInterval(blink, 500) // 커서 깜빡이는 효과

    let arr = nextKeyword();
    // dynamic(target, arr);
    typing(target, arr);


});
function nextKeyword() {
    let arr = keywords[index].split('');
    index = (index+1)%keywords.length;
    return arr;
}
function blink(){
    const target = document.querySelector(".typing"); //html에서 dynamic 선택
    target.classList.toggle("active"); // dynamic에 active 클래스 추가<->삭제 반복
}
function typing(target, arr) {

    insertTyping(target, arr);
    removeTyping(target);
    setTimeout(() => {
        arr = nextKeyword();
        typing(target, arr);
    }, 2650);
}

function insertTyping(target, arr) {
    if (arr.length > 0) {
        target.textContent += arr.shift();

        setTimeout(() => {
            insertTyping(target, arr);
        }, 200);
    } else {
        return;
    }
}
function removeTyping(target) {
    setTimeout(() => {
        let text = target.textContent;
        let timeout;
        if (text.length != 0) {
            timeout = setTimeout(() => {
                text = text.slice(0, -1);
                target.innerText = text;
                reset(target);
            }, 100)
        } else {
            clearTimeout(timeout);
            return;
        }
    }, 1800);
}
//문자열을 한개씩 나타내주는 함수 만들기
function dynamic(target, arr){
    if(arr.length > 0){ //배열의 길이가 0보다 크면(배열에 요소가 하나라도 있다면)
        target.textContent += arr.shift();//dynamic에 textContent에 배열의 요소 추가
        setTimeout(() => {
            dynamic(target, arr)
        }, 200)
    } else { //배열의 길이가 0이하이면(배열에 요소가 없으면)
        setTimeout(()=> {
            reset(target);
            const resplit = nextKeyword();
            dynamic(target, resplit); // dynamic함수에 resplit인자를 넣어서 실행
        }, 1000); // reset함수 실행
    }
}

function reset(target) {
    let text = target.textContent;
    let timeout;
    if (text.length != 0) {
        timeout = setTimeout(() => {
            text = text.slice(0, -1);
            target.innerText = text;
            reset(target);
        }, 100)
    } else {
        clearTimeout(timeout);
        return;
    }
}



function typeAndDelete() {

    const typingContainer = document.querySelector('.typing');
    const currentKeyword = keywords[index];

    if (index % 2 === 0) {
      typingContainer.textContent = currentKeyword;
      setTimeout(() => {
        typingContainer.textContent = '';
        index = (index + 1) % keywords.length;
        setTimeout(typeAndDelete, 500); // 0.5초 후에 다음 키워드 타이핑
      }, 1000); // 1초 후에 키워드 삭제
    } else {
      typingContainer.textContent = currentKeyword;
      setTimeout(() => {
        index = (index + 1) % keywords.length;
        typeAndDelete(); // 다음 키워드 타이핑
      }, 1000); // 1초 후에 키워드 유지
    }
  }