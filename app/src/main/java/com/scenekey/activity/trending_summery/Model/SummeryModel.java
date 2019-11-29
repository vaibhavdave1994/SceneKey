package com.scenekey.activity.trending_summery.Model;

import java.io.Serializable;
import java.util.List;

public class SummeryModel implements Serializable {


    /**
     * status : success
     * event : {"event_name":"Tuesday@World cup","venue_name":"World cup","venue_lat":"22.7093523","venue_long":"75.9014392","venue_address":"Pipliyahana, Indore, Madhya Pradesh, India","description":"","venue_type":"cafe","event_date":"2019-11-12","open_today":"10A-10P","venue_hour":[{"id":"803","venue_id":"159875","sunday":"Closed-Closed","monday":"10A-10P","tuesday":"2:30P-10P","wednesday":"10A-10P","thursday":"10A-10P","friday":"10A-12A","saturday":"10:30A-10P"}],"image":"https://s3-us-west-1.amazonaws.com/scenekey-venues/dev/5d2c30745d681venue.jpg","menu":"","feedPost":[{"id":"8477","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/dev/5dcabd01371e7.jpg"},{"id":"8476","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/dev/5dcabcf029aee.jpg"}],"keyInUser":[{"userid":"2161","userImage":"dev-2161/5dc9206d1536buserImage.jpg","userName":"sandesh","bio":"Thtbg","userFacebookId":"2161","user_status":"1","keyIn":"1"},{"userid":"1430","userImage":"dev-1654378237975621/1654378237975621.jpg","userName":"Santosh","bio":"Hello","userFacebookId":"1654378237975621","user_status":"1","keyIn":"1"}]}
     */

    private String status;
    private EventBean event;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EventBean getEvent() {
        return event;
    }

    public void setEvent(EventBean event) {
        this.event = event;
    }

    public static class EventBean {
        /**
         * event_name : Tuesday@World cup
         * venue_name : World cup
         * venue_lat : 22.7093523
         * venue_long : 75.9014392
         * venue_address : Pipliyahana, Indore, Madhya Pradesh, India
         * description :
         * venue_type : cafe
         * event_date : 2019-11-12
         * open_today : 10A-10P
         * venue_hour : [{"id":"803","venue_id":"159875","sunday":"Closed-Closed","monday":"10A-10P","tuesday":"2:30P-10P","wednesday":"10A-10P","thursday":"10A-10P","friday":"10A-12A","saturday":"10:30A-10P"}]
         * image : https://s3-us-west-1.amazonaws.com/scenekey-venues/dev/5d2c30745d681venue.jpg
         * menu :
         * feedPost : [{"id":"8477","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/dev/5dcabd01371e7.jpg"},{"id":"8476","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/dev/5dcabcf029aee.jpg"}]
         * keyInUser : [{"userid":"2161","userImage":"dev-2161/5dc9206d1536buserImage.jpg","userName":"sandesh","bio":"Thtbg","userFacebookId":"2161","user_status":"1","keyIn":"1"},{"userid":"1430","userImage":"dev-1654378237975621/1654378237975621.jpg","userName":"Santosh","bio":"Hello","userFacebookId":"1654378237975621","user_status":"1","keyIn":"1"}]
         */

        private String event_name;
        private String venue_name;
        private String venue_lat;
        private String venue_long;
        private String venue_address;
        private String description;
        private String venue_type;
        private String event_date;
        private String open_today;
        private String image;
        private String menu;
        private List<VenueHourBean> venue_hour;
        private List<FeedPostBean> feedPost;
        private List<KeyInUserBean> keyInUser;

        public String getEvent_name() {
            return event_name;
        }

        public void setEvent_name(String event_name) {
            this.event_name = event_name;
        }

        public String getVenue_name() {
            return venue_name;
        }

        public void setVenue_name(String venue_name) {
            this.venue_name = venue_name;
        }

        public String getVenue_lat() {
            return venue_lat;
        }

        public void setVenue_lat(String venue_lat) {
            this.venue_lat = venue_lat;
        }

        public String getVenue_long() {
            return venue_long;
        }

        public void setVenue_long(String venue_long) {
            this.venue_long = venue_long;
        }

        public String getVenue_address() {
            return venue_address;
        }

        public void setVenue_address(String venue_address) {
            this.venue_address = venue_address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVenue_type() {
            return venue_type;
        }

        public void setVenue_type(String venue_type) {
            this.venue_type = venue_type;
        }

        public String getEvent_date() {
            return event_date;
        }

        public void setEvent_date(String event_date) {
            this.event_date = event_date;
        }

        public String getOpen_today() {
            return open_today;
        }

        public void setOpen_today(String open_today) {
            this.open_today = open_today;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getMenu() {
            return menu;
        }

        public void setMenu(String menu) {
            this.menu = menu;
        }

        public List<VenueHourBean> getVenue_hour() {
            return venue_hour;
        }

        public void setVenue_hour(List<VenueHourBean> venue_hour) {
            this.venue_hour = venue_hour;
        }

        public List<FeedPostBean> getFeedPost() {
            return feedPost;
        }

        public void setFeedPost(List<FeedPostBean> feedPost) {
            this.feedPost = feedPost;
        }

        public List<KeyInUserBean> getKeyInUser() {
            return keyInUser;
        }

        public void setKeyInUser(List<KeyInUserBean> keyInUser) {
            this.keyInUser = keyInUser;
        }

        public static class VenueHourBean implements Serializable {
            /**
             * id : 803
             * venue_id : 159875
             * sunday : Closed-Closed
             * monday : 10A-10P
             * tuesday : 2:30P-10P
             * wednesday : 10A-10P
             * thursday : 10A-10P
             * friday : 10A-12A
             * saturday : 10:30A-10P
             */

            private String id;
            private String venue_id;
            private String sunday;
            private String monday;
            private String tuesday;
            private String wednesday;
            private String thursday;
            private String friday;
            private String saturday;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getVenue_id() {
                return venue_id;
            }

            public void setVenue_id(String venue_id) {
                this.venue_id = venue_id;
            }

            public String getSunday() {
                return sunday;
            }

            public void setSunday(String sunday) {
                this.sunday = sunday;
            }

            public String getMonday() {
                return monday;
            }

            public void setMonday(String monday) {
                this.monday = monday;
            }

            public String getTuesday() {
                return tuesday;
            }

            public void setTuesday(String tuesday) {
                this.tuesday = tuesday;
            }

            public String getWednesday() {
                return wednesday;
            }

            public void setWednesday(String wednesday) {
                this.wednesday = wednesday;
            }

            public String getThursday() {
                return thursday;
            }

            public void setThursday(String thursday) {
                this.thursday = thursday;
            }

            public String getFriday() {
                return friday;
            }

            public void setFriday(String friday) {
                this.friday = friday;
            }

            public String getSaturday() {
                return saturday;
            }

            public void setSaturday(String saturday) {
                this.saturday = saturday;
            }
        }

        public static class FeedPostBean implements  Serializable{
            /**
             * id : 8477
             * feed_image : https://s3-us-west-1.amazonaws.com/scenekey-events/dev/5dcabd01371e7.jpg
             */

            private String id;
            private String feed_image;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getFeed_image() {
                return feed_image;
            }

            public void setFeed_image(String feed_image) {
                this.feed_image = feed_image;
            }
        }

        public static class KeyInUserBean implements Serializable{
            /**
             * userid : 2161
             * userImage : dev-2161/5dc9206d1536buserImage.jpg
             * userName : sandesh
             * bio : Thtbg
             * userFacebookId : 2161
             * user_status : 1
             * keyIn : 1
             */

            private String userid;
            private String userImage;
            private String userName;
            private String bio;
            private String userFacebookId;
            private String user_status;
            private String keyIn;

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getUserImage() {
                return userImage;
            }

            public void setUserImage(String userImage) {
                this.userImage = userImage;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getBio() {
                return bio;
            }

            public void setBio(String bio) {
                this.bio = bio;
            }

            public String getUserFacebookId() {
                return userFacebookId;
            }

            public void setUserFacebookId(String userFacebookId) {
                this.userFacebookId = userFacebookId;
            }

            public String getUser_status() {
                return user_status;
            }

            public void setUser_status(String user_status) {
                this.user_status = user_status;
            }

            public String getKeyIn() {
                return keyIn;
            }

            public void setKeyIn(String keyIn) {
                this.keyIn = keyIn;
            }
        }
    }
}
