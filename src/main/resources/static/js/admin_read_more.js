$(function(){
  
    $('.cancelBtn').on('click', function(){
        close();
    });
  
    processComplete();

});


function processComplete() {
    $('.completeBtn').on('click', function() {
        // var notifyId = parseInt($('.notify-number').val());

        // $.ajax({
        //     url: '/admin/notify/status/change',
        //     type: 'POST',
        //     contentType: 'application/json',
        //     data: JSON.stringify({ notifyId: notifyId }),
        //     success: function() {
        //         close();
        //         window.opener.location.reload();
        //     }
        // });
        alert('현재 비활성화된 기능입니다');
    });
};

