$(function(){
    $('#subscribe-form').on('submit', function(e){
        e.preventDefault();
        $.ajax({
            url: '/video/synopsis', //this is the submit URL
            type: 'POST', //or POST
            data: $('#subscribe-form').serialize(),
            success: function(data){
                alert('successfully submitted')
            }
        });
    });
});