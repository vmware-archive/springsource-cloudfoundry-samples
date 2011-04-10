function message(msg){
    msg.userId = !msg.userId ? 'Status' : msg.userId;
    $('<p><b>'+ esc(msg.userId) +': </b>' + esc(msg.chatMessage) +'</p>').
        appendTo('#chat');
    $('#chat').scrollTop(1000000);
    if( msg.chatMessage && window.console && console.log ) console.log(msg.userId, msg.chatMessage);
}

function send(){
    var val = $('#text').attr('value');
    socket.send(val);
    message({ chatMessage: val, userId: 'you' });
    $('#text').attr('value', '');
}

function esc(msg){
    return msg.replace(/</g, '&lt;').replace(/>/g, '&gt;');
};

var socket = new io.Socket(location.hostname, {rememberTransport:false, transports: ['xhr-polling']});
$(document).ready(function() {
    setTimeout(function () {
        socket.connect();
        socket.on('message', function(msg){
            message(msg);
        });

        socket.on('connect', function(){ 
            $('#form').css('display','block');
            $('#chat').html('');
            message({ chatMessage: 'Connected'}); 
        });

        socket.on('disconnect', function(){ message({ chatMessage: 'Disconnected'}); });
        socket.on('reconnect', function(){ message({ chatMessage: 'Reconnected to server'}); });
        socket.on('reconnecting', function( nextRetry ){ message({ chatMessage: 'Attempting to re-connect to the server, next attempt in ' + nextRetry + 'ms'}); });
        socket.on('reconnect_failed', function(){ message({ chatMessage: 'Reconnected to server FAILED.'}); });
    }, 1000);
});