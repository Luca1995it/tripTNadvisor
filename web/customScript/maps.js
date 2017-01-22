function initMap(source) {
    $.get(source, function (data) {
        var coord = JSON.parse(data);
        var myLatLng = new google.maps.LatLng(coord.myLat, coord.myLng);

        var map = new google.maps.Map(document.getElementById('gmaps-canvas'), {
            zoom: 14,
            center: myLatLng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        });

        for (i in coord.vicini) {
            var infowindow = new google.maps.InfoWindow();

            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(coord.vicini[i].lat, coord.vicini[i].lng),
                map: map
            });
            var content = coord.vicini[i].name;
            
            google.maps.event.addListener(marker, 'click', (function (marker, content, infowindow) {
                return function () {
                    infowindow.setContent(content);
                    infowindow.open(map, marker);
                };
            })(marker, content, infowindow));
        }
    });

}
exports.initMap = initMap;
