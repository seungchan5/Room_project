$(function(){
   
    let logoutBtn = $('#btn-logout');

    logoutBtn.on('click', function(){        
        $.ajax({
            url : '/admin/logout',
            type : 'POST',
            success : function(response){
                window.location.href = response;
            }
        });
    });
});