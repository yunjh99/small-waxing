// Naver Map API
var dmap = null;
var fmap = null;
var shopPosition = new naver.maps.LatLng(37.50075, 127.03650);
var shopInfoContent =
    '<div style="padding:10px 14px; font-size:14px; font-weight:600; white-space:nowrap;">' +
    '스몰왁싱 강남점' +
    '</div>';

function initDMap() {
    var mapElement1 = document.getElementById('dmap');
    if (mapElement1) {  // dmap이 존재하면 초기화
        dmap = new naver.maps.Map(mapElement1, {
            center: shopPosition,
            zoom: 17
        });

        var marker1 = new naver.maps.Marker({
            position: shopPosition,
            map: dmap,
            title: '스몰왁싱 강남점'
        });
        var infoWindow1 = new naver.maps.InfoWindow({
            content: shopInfoContent,
            anchorSkew: true
        });
        infoWindow1.open(dmap, marker1);
        dmap.setCenter(marker1.getPosition());
        dmap.setZoom(17);
    }
}

function initFMap() {
    var mapElement2 = document.getElementById('fmap');
    if (mapElement2) {  // fmap이 존재하면 초기화
        fmap = new naver.maps.Map(mapElement2, {
            center: shopPosition,
            zoom: 17
        });

        var marker2 = new naver.maps.Marker({
            position: shopPosition,
            map: fmap,
            title: '스몰왁싱 강남점'
        });
        var infoWindow2 = new naver.maps.InfoWindow({
            content: shopInfoContent,
            anchorSkew: true
        });
        infoWindow2.open(fmap, marker2);
        fmap.setCenter(marker2.getPosition());
        fmap.setZoom(17);
    }
}

// 페이지 로드 시 각 지도 초기화 호출
window.onload = function() {
    initDMap();
    initFMap();
};
