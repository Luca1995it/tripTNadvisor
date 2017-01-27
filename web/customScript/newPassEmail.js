$(document).ready(function (){
    document.getElementById('setButton').addEventListener('click',function (){
        document.getElementById('newpassform').style.display = 'block';
    }, true);
    
    document.getElementById('openButton').addEventListener('click',function() {
        document.getElementById('newemailform').style.display = 'block';
    }, true);
});

var send = function(source){
    $.get(source + '/NuovaPassword' + '?' + 'mail=' + document.getElementById("email1").value);
    alert("La nuova password e' stata inviata sulla tua mail");
};

var launch = function(source){
    $.get(source + '/NuovaMailAttivazione' + '?' + 'mail=' + document.getElementById("email2").value);
    alert("Ti è stata inviata una nuova mail per attivare il tuo account");
};

exports.send = send;
exports.launch = launch;