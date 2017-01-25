/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var x;
var lun;
var mar;
var mer;
var gio;
var ven;
var sab;
var dom;
var from;
var to;


function setting(a,b,c,d,e,f,g,h,i){
   lun = a;
   mar = b;
   mer = c;
   gio = d;
   ven = e;
   sab = f;
   dom = g;
   from = h;
   to = i;
   x = document.getElementById("contenitore");
}

function generate(numberId){
    return "<div class='row'><div class='col-md-4'></div><div class='col-md-4'><div class='row'><div class='col-md-4'><select class='form-group selectBar' name='day" + numberId + "'><option value='0'>" + lun + "</option><option value='1'>" + mar + "</option><option value='2'>" + mer + "</option><option value='3'>" + gio + "</option><option value='4'>" + ven + "</option><option value='5'>" + sab + "</option><option value='6'>" + dom + "</option></select></div><div class='col-md-8'><div class='row'><div class='col-md-2'><label class='control-form'>" + from + "</label></div><div class='col-md-4'><input type='number' name='apH" + numberId + "' min='0' max='23' class='form-control'/></div><div class='col-md-1'><label class='control-label'> : </label></div><div class='col-md-4'><input type='number' name='apM" + numberId + "' min='0' max='59' class='form-control'/></div></div><br><div class='row'><div class='col-md-2'><label class='control-form'>" + to + "</label></div><div class='col-md-4'><input type='number' name='chH" + numberId + "' min='0' max='23' class='form-control'/></div><div class='col-md-1'><label class='control-label'> : </label></div><div class='col-md-4'><input type='number' name='chM" + numberId + "' min='0' max='59' class='form-control'/></div></div></div><div class='col-md-4'></div></div><hr>";
}

function aggiorna(){
    x.innerHTML = "";
    var n = parseInt(document.getElementById("numberInputs").value);
    
    for(var i = 0; i < n; i++){
        x.innerHTML += generate(i);
    }
}

exports.setting = setting;
exports.aggiorna = aggiorna;