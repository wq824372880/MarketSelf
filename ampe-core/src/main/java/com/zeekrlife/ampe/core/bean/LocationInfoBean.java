package com.zeekrlife.ampe.core.bean;


public class LocationInfoBean {


        private LocationInfo locationInfo;
        private int callbackId;
        private int errorCode;
        public void setLocationInfo(LocationInfo locationInfo) {
            this.locationInfo = locationInfo;
        }
        public LocationInfo getLocationInfo() {
            return locationInfo;
        }

        public void setCallbackId(int callbackId) {
            this.callbackId = callbackId;
        }
        public int getCallbackId() {
            return callbackId;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }
        public int getErrorCode() {
            return errorCode;
        }


    public static class LatLng {

        private String coordType;
        private double latitude;
        private double longitude;
        public void setCoordType(String coordType) {
            this.coordType = coordType;
        }
        public String getCoordType() {
            return coordType;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        public double getLatitude() {
            return latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        public double getLongitude() {
            return longitude;
        }

    }


    public static class LocationInfo {

        private int accuracy;
        private String adcode;
        private int altitude;
        private int bearing;
        private long fixTime;
        private int geoDecodeTime;
        private LatLng latLng;
        private int speed;
        public void setAccuracy(int accuracy) {
            this.accuracy = accuracy;
        }
        public int getAccuracy() {
            return accuracy;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }
        public String getAdcode() {
            return adcode;
        }

        public void setAltitude(int altitude) {
            this.altitude = altitude;
        }
        public int getAltitude() {
            return altitude;
        }

        public void setBearing(int bearing) {
            this.bearing = bearing;
        }
        public int getBearing() {
            return bearing;
        }

        public void setFixTime(long fixTime) {
            this.fixTime = fixTime;
        }
        public long getFixTime() {
            return fixTime;
        }

        public void setGeoDecodeTime(int geoDecodeTime) {
            this.geoDecodeTime = geoDecodeTime;
        }
        public int getGeoDecodeTime() {
            return geoDecodeTime;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }
        public LatLng getLatLng() {
            return latLng;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }
        public int getSpeed() {
            return speed;
        }

    }

}
