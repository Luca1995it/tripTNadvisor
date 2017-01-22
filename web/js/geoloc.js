function getLocationConstant() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(onGeoSuccess, onGeoError);
        document.getElementById("temp").style.display = 'block';
    } else {
        alert("Your browser or device doesn't support Geolocation");
    }
}

// If we have a successful location update
function onGeoSuccess(event) {
    document.getElementById("Latitude").value = event.coords.latitude;
    document.getElementById("Longitude").value = event.coords.longitude;
    document.getElementById("temp").style.display = 'none';
    document.getElementById("received").style.display = 'block';
    document.getElementById("getLoc").style.display = 'none';
}

// If something has gone wrong with the geolocation request
function onGeoError(event) {
    alert("Error code " + event.code + ". " + event.message);
}