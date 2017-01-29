var h1, h2;
h1 = false;
h2 = false;

$(document).ready(function (){
    document.getElementById('setButton').addEventListener('click',function (){
        if(h1){
            document.getElementById('newpassform').style.display = 'block';
        }
        else {
            document.getElementById('newpassform').style.display = 'none';
        }
        h1 = !h1;
    }, true);
    
    document.getElementById('openButton').addEventListener('click',function() {
        if(h2){
            document.getElementById('newemailform').style.display = 'block';
        } else{
            document.getElementById('newemailform').style.display = 'none';
        }
        h2 = !h2;
    }, true);
});

var send = function(source){
    $.get(source + '/NuovaPassword' + '?' + 'mail=' + document.getElementById("email1").value);
    alert("La nuova password e' stata inviata sulla tua mail");
};

var launch = function(source){
    $.get(source + '/NuovaMailAttivazione' + '?' + 'mail=' + document.getElementById("email2").value);
    alert("Ti e' stata inviata una nuova mail per attivare il tuo account");
};

exports.send = send;
exports.launch = launch;