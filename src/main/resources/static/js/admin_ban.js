$(function(){

    var word = $('#word');
    var searchBtn = $('#searchBtn');

    word.on('keydown', function(e){
        if(e.key == "Enter"){
            sendRequest();
        }
    });

    searchBtn.on('click', function(){
        sendRequest();
    });

    liftTheBan();
    banOptionOpen();
    banOptionClose();

});

function banOptionOpen(){
    $('.option-more').on('click', function(e) {
        if ($(e.target).hasClass('option-more')) {
            initMoreList();
            let menu = $(e.target).children('[name="option-menu"]');
            menu.removeClass('disabled');
        }
    });
};

function initMoreList() {
    $('.option-menu').addClass('disabled');
};

function banOptionClose() {
    let container = $(':not(.option-more):not(.option-more *)').first();

    container.on('click', function(event) {
        let target = $(event.target);
        if (!target.hasClass('option-more') && !target.hasClass('option-change')) {
            $('.option-menu').each(function() {
                let optionMenu = $(this);
                if (!optionMenu.hasClass('disabled')) {
                    optionMenu.addClass('disabled');
                }
            });
        }
    });
}

function liftTheBan() {
    $('.option-menu').each(function() {
        let change  = $(this).children('[name="option-change"]');
        change.on('click', function() {
            // $(this).closest('.option-menu').addClass('disabled');
            // var memberId = $(this).attr('id');
            
            // $.ajax({
            //     url: '/admin/ban/lift',
            //     type: 'POST',
            //     contentType: 'application/json',
            //     data: JSON.stringify({ memberId: memberId }),
            //     success: function() {
            //         window.location.reload();
            //     }
            // });
            alert('현재 비활성화중인 기능입니다');
        });
    });
};

function sendRequest(page) {

    var word = $('#word').val();

    $.ajax({
        type: 'GET',
        url: '/admin/bans/get',
        data: {
            word : word,
            page : page
        },
        success: function(response) {
            banList(response);
            paging(response.totalPages, response.number);
            
            banOptionOpen();
            banOptionClose();
            liftTheBan();

            if(page==null){
                var newUrl = '/admin/bans?page=1';
            } else {
                var newUrl = '/admin/bans?page=' + page;
            }

            if(word!=""){
                newUrl += '&word=' + word;
            }
            history.pushState({ path: newUrl }, '', newUrl);
        }
    });
}

function banList(response) {
    var bans = response.content;

    var $list = $('.table-list');
    $list.empty();
    bans.forEach(function(ban) {
        var $entity =  $(`<div class="entity">
        <div class="banId">${ban.banId}</div>
        <div class="memberAccount">${ban.memberAccount}</div>
        <div class="memberName">${ban.memberName}</div>
        <div class="memberNickname">${ban.memberNickname}</div>
        <div class="phone">${ban.phone}</div>
        <div class="suspendedDate">${ban.suspendedDate}</div>
            <div class="option">
                <button type="button" class="option-more">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><path d="M8 256a56 56 0 1 1 112 0A56 56 0 1 1 8 256zm160 0a56 56 0 1 1 112 0 56 56 0 1 1 -112 0zm216-56a56 56 0 1 1 0 112 56 56 0 1 1 0-112z"/></svg>
                        <ul class="option-menu disabled" name="option-menu">
                            <li class="option-change" name="option-change" id="${ban.memberId}">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z"/></svg>
                                    <span>정지 해제</span>
                            </li>
                        </ul>
                </button>
            </div>
        </div>`);
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
