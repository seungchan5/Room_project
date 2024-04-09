window.addEventListener('load', function(){

    let checkbox = $('#containComplete');
    var word = $('#word');
    var searchBtn = $('#searchBtn');

    checkbox.on('change', function() {
        sendRequest();
    });

    word.on('keydown', function(e){
        if(e.key == "Enter"){
            sendRequest();
        }
    });

    searchBtn.on('click', function(){
        sendRequest();
    });

    moreBtnClick();
    infoClick();

});

function sendRequest(page) {

    var containComplete = $('#containComplete').is(':checked');
    var word = $('#word').val();

    $.ajax({
        type: 'GET',
        url: '/admin/notify/get',
        data: {
            word : word,
            containComplete : containComplete,
            page : page
        },
        success: function(response) {
            notifyList(response);
            paging(response.totalPages, response.number);
            
            moreBtnClick();
            infoClick();

            if(page==null){
                var newUrl = '/admin/notify?page=1';
            } else {
                var newUrl = '/admin/notify?page=' + page;
            }
            
            if(containComplete==true){
                newUrl += '&containComplete=' + containComplete;
            }

            if(word!=""){
                newUrl += '&word=' + word;
            }
            history.pushState({ path: newUrl }, '', newUrl);
        }
    });
};

function notifyList(response) {
    var notifys = response.content;

    var $list = $('.table-list');
    $list.empty();
    notifys.forEach(function(notify) {
        var $entity = `<div class="entity">
            <div class="donotifymember">${notify.reporterMemberAccount}</div>
            <div class="benotifiedmember"><a class="memberInfoPop" href="" id="${notify.notifyId}">${notify.criminalMemberAccount}</a></div>
            <div class="notifyreason">${notify.notifyReason.notifyType}</div>
            <div class="notifydate">${notify.notifyDate}</div>
            <div class="roomid">${notify.roomId}</div>
            <div class="reed-more">
                <button type="button" class="reed-moreBtn" id="${notify.notifyId}">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M416 208c0 45.9-14.9 88.3-40 122.7L502.6 457.4c12.5 12.5 12.5 32.8 0 45.3s-32.8 12.5-45.3 0L330.7 376c-34.4 25.2-76.8 40-122.7 40C93.1 416 0 322.9 0 208S93.1 0 208 0S416 93.1 416 208zM208 352a144 144 0 1 0 0-288 144 144 0 1 0 0 288z"/></svg>
                </button>
            </div>
            <div class="status">${notify.notifyStatus}</div>
        </div>`
        $list.append($entity);
    });
    
};

function paging(totalPages, currentPage) {
    var $pageBox = $('.page-box');
    $pageBox.empty();

    if(totalPages >0 ){

        var pageBtnStart = $(`<div class="pagination">`);

        var pageBtnMid = $(`<div id="page">`);

        if (currentPage > 1) {
            var pageBtnLastLeft = $(`<div class="buttons">
                                        <button type="button" id="last-left" class="page-num" value="1">
                                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M41.4 233.4c-12.5 12.5-12.5 32.8 0 45.3l160 160c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L109.3 256 246.6 118.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0l-160 160zm352-160l-160 160c-12.5 12.5-12.5 32.8 0 45.3l160 160c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L301.3 256 438.6 118.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0z"/></svg>
                                        </button>
                                    </div>`);
            pageBtnStart.append(pageBtnLastLeft);
        }
        if(currentPage > 0){
            var pageBtnLeft = $(`<div class="buttons">
                                    <button type="button" id="left" class="page-num" value="${currentPage}">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M41.4 233.4c-12.5 12.5-12.5 32.8 0 45.3l160 160c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L109.3 256 246.6 118.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0l-160 160z"/></svg>
                                    </button>
                                </div>`);
            pageBtnStart.append(pageBtnLeft);
        }

        for (var i = 1; i <= totalPages; i++) {
            var button = $('<button/>', {
                type: 'button',
                class: 'page-num',
                val: i,
                text: i
            });
            pageBtnMid.append(button);
        }

        if (currentPage+1 < totalPages) {
            var pageRightBtn = $(`<div class="buttons">
                                    <button type="button" id="right" class="page-num" value="${currentPage+2}">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M278.6 233.4c12.5 12.5 12.5 32.8 0 45.3l-160 160c-12.5 12.5-32.8 12.5-45.3 0s-12.5-32.8 0-45.3L210.7 256 73.4 118.6c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0l160 160z"/></svg>
                                    </button>
                                </div>`);
            pageBtnMid.append(pageRightBtn);
        }
        
        if(currentPage+2 < totalPages){
            var pageLastRight = $(`<div class="buttons">
                                    <button type="button" id="last-right" class="page-num" value="${totalPages}">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M470.6 278.6c12.5-12.5 12.5-32.8 0-45.3l-160-160c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L402.7 256 265.4 393.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0l160-160zm-352 160l160-160c12.5-12.5 12.5-32.8 0-45.3l-160-160c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L210.7 256 73.4 393.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0z"/></svg>
                                    </button>
                                </div>`);
            pageBtnMid.append(pageLastRight);
        }

        
        $pageBox.append(pageBtnStart.append(pageBtnMid));

        $('.page-num').on('click', function(){
            var page =  parseInt($(this).val());
            sendRequest(page);
        });
    }

    if(totalPages == 0){
        var errorMsg = $(`<div class="errorMsg"}">결과가 존재하지 않습니다.</div>`);
        $pageBox.append(errorMsg);
    }
};

function moreBtnClick(){
    $('.reed-moreBtn').each(function() {
        $(this).on('click', function() {
            var notifyId = $(this).attr('id');
            notifyReedMoreOpenPopUp(notifyId);
        });
    });
};

function infoClick(){

    $('.memberInfoPop').each(function() {
        $(this).on('click', function(e) {
            e.preventDefault();
            var notifyId = $(this).attr('id');
            var account = $(this).html();
            memberInfoOpenPopUp(account, notifyId);
        });
    });
};

function notifyReedMoreOpenPopUp(notifyId){
    window.open('/admin/notify/read_more?notifyId=' + notifyId, '신고 자세히 보기', 'width=500, height=750');
};

function memberInfoOpenPopUp(account, notifyId){
    window.open('/admin/notify/member_info?account=' + account + '&notifyId=' + notifyId, '멤버 정보', 'width=600, height=750');
};
