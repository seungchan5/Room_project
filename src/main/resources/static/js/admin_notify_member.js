$(function(){

    $('.cancelBtn').on('click', function(){
        close();
    });

    memberFreeze();
});

function memberFreeze(){

    $('.freezeBtn').on('click', function(){

        // var memberId = parseInt($('#memberId').val());
        // var memberAccount = $('#memberAccount').val();
        // var memberName = $('#memberName').val();
        // var memberNickname = $('#memberNickname').val();
        // var phone = $('#memberPhone').val();
        // var freezePeriod = $('#freezePeriod').val();
        // var freezeReason = $('#freezeReason').val();

        // $.ajax({
        //     url : '/admin/notify/member/freeze',
        //     type : 'POST',
        //     contentType : 'application/json',
        //     data : JSON.stringify({
        //         memberId : memberId, 
        //         memberAccount:memberAccount,
        //         memberName : memberName,
        //         memberNickname : memberNickname,
        //         phone : phone,
        //         freezePeriod : freezePeriod, 
        //         freezeReason : freezeReason}),
        //     success : function(){
        //         close();
        //         window.opener.location.reload();
        //     },
        //      error: function(xhr, status, error) {
        //         alert('이미 영구 정지된 회원입니다');
        //     }
        // });
        alert('현재 비활성화된 기능입니다');
    });
};