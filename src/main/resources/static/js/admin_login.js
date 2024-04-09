$(function (){

    let form = $('#login-form');

    form.on('submit', function(e){
        e.preventDefault();

        var formData = $(this).serialize();

        $.ajax({
            type : 'POST',
            url : '/admin/login.do',
            data : formData,
            success : function(response){
                $(location).attr('href', response);
            }
        })
    });
});