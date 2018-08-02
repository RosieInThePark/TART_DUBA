package sjsu.tart.duba;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by RosieHyunahPark on 2018-07-31.
 */

public  class RouteList {
    public static Route HeadRoute;
    public static Route TailRoute;
    public static int size = 0;
    private static final int MAXSIZE = 10;
    private static Marker[] selectedMarkers = new Marker[MAXSIZE];

    public static void addList(String location, String address, Context context) {
        Route newRoute = new Route(location, address);

        if(size==0) { //List is empty
            Log.e("d","d");
            //This code can be used to input new Route into head.
            newRoute.setNext(HeadRoute);
            HeadRoute = newRoute;
            size++;
            if (HeadRoute.getNext() == null) {
                TailRoute = HeadRoute;
            }
        }
        else {
            TailRoute.setNext(newRoute);
            TailRoute = newRoute;
            size++;
        }

        reviseSelectedMarkerToMap(context);
    }
    public static void addList(String location, String address) {
        Route newRoute = new Route(location, address);

        if(size==0) { //List is empty
            Log.e("d","d");
            //This code can be used to input new Route into head.
            newRoute.setNext(HeadRoute);
            HeadRoute = newRoute;
            size++;
            if (HeadRoute.getNext() == null) {
                TailRoute = HeadRoute;
            }
        }
        else {
            TailRoute.setNext(newRoute);
            TailRoute = newRoute;
            size++;
        }
    }
    public static void deleteItem(int id) {
        Route cur = HeadRoute;
        Route prev = HeadRoute;
        for(int i=0;i<id;i++) {
            prev = cur;
            cur = cur.getNext();
        }
        if(cur==HeadRoute) {
            HeadRoute = cur.getNext();
        }
        else if(cur==TailRoute) {
            prev.setNext(null);
            TailRoute = prev;
        }
        else {
            prev.setNext(cur.getNext());
        }

    }
    /* List 항목 보기 */
    public static void printList() {
        Route temp = HeadRoute;

        while(true) {
            if(temp == null) break;
            Log.d("DELETE", "print : "+temp.getLocation());
            temp = temp.getNext();
        }

    }

    /* List 초기화 */
    public static void setSizeZero(){
        size = 0;
        HeadRoute = null;
        TailRoute = null;
    }

    /* Route List에 해당하는 마커를 지도에 띄우기 */
    public static void reviseSelectedMarkerToMap(Context context){
        GoogleMap googleMap = FragmentMap.getGoogleMap();
        removeSelectedMarker();

        Route temp = HeadRoute;
        int i = 0;
        while(true) {
            if(temp == null) break;
            String address = temp.getAddress();
            LatLng currentLocation = findGeoPoint(address, context);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(temp.getLocation());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            Marker currentMarker = googleMap.addMarker(markerOptions);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12.0f));
            selectedMarkers[i++] = currentMarker;
            temp = temp.getNext();
        }
    }

    /* selected marker 없애기 */
    private static void removeSelectedMarker(){
        for(int i = 0; i < MAXSIZE; i++){
            if(selectedMarkers[i] != null){
                selectedMarkers[i].remove();
            }
        }
    }

    /* 주소를 LatLng로 변환하는 함수 */
    private static LatLng findGeoPoint(String address, Context context) {
        Geocoder geocoder = new Geocoder(context);
        Address addr;
        LatLng location = null;
        try {
            List<Address> listAddress = geocoder.getFromLocationName(address, 1);
            if (listAddress.size() > 0) { // 주소값이 존재 하면
                addr = listAddress.get(0); // Address형태로
                double lat = (addr.getLatitude());
                double lng = (addr.getLongitude());
                location = new LatLng(lat, lng);
                Log.d("LanLng", "주소로부터 취득한 위도 : " + lat + ", 경도 : " + lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }
}