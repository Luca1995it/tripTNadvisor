$(document).ready(function (){
    document.getElementById('setButton').addEventListener('click', open, true);
});

var open = function open(){
    document.getElementById('newpassform').style.display = 'block';
};

var send = function(source){
    $.get(source + '/NuovaPassword' + '?' + 'mail=' + document.getElementById("email").value);
    alert("La nuova password e' stata inviata sulla tua mail");
};

exports.send = send;
exports.open = open;