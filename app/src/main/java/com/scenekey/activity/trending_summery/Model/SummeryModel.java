package com.scenekey.activity.trending_summery.Model;

import java.io.Serializable;
import java.util.List;

public class SummeryModel implements Serializable {

    /**
     * status : success
     * event : {"event_name":"Monday@dovies","venue_name":"dovies","venue_lat":"22.7051382","venue_long":"75.9090618","venue_address":"MINDIII Systems Pvt. Ltd., Main Road, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh, India","description":"","venue_type":"Pub","event_date":"2019-12-16","open_today":"5P-2A","venue_hour":[{"id":"5696","venue_id":"145210","sunday":"5P-2A","monday":"5P-2A","tuesday":"5P-2A","wednesday":"5P-2A","thursday":"5P-2A","friday":"5P-2A","saturday":"5P-2A"}],"image":"https://s3-us-west-1.amazonaws.com/scenekey-venues/prod/5b8e9688c5743venue.jpg","menu":"","feedPost":[{"id":"2169","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d9ef1a48df20.jpg"},{"id":"2166","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d9b3c52a29e8.jpg"},{"id":"2157","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d96f1871b48f.jpg"},{"id":"1745","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5bd9aacab86be.jpg"},{"id":"1734","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5bd94b232e3ec.jpg"},{"id":"1617","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5ba4999c082e5.jpg"},{"id":"1611","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5ba496809c0e9.jpg"},{"id":"1610","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5ba49668ea9fe.jpg"}],"keyInUser":[]}
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

    public static class EventBean implements Serializable {
        /**
         * event_name : Monday@dovies
         * venue_name : dovies
         * venue_lat : 22.7051382
         * venue_long : 75.9090618
         * venue_address : MINDIII Systems Pvt. Ltd., Main Road, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh, India
         * description :
         * venue_type : Pub
         * event_date : 2019-12-16
         * open_today : 5P-2A
         * venue_hour : [{"id":"5696","venue_id":"145210","sunday":"5P-2A","monday":"5P-2A","tuesday":"5P-2A","wednesday":"5P-2A","thursday":"5P-2A","friday":"5P-2A","saturday":"5P-2A"}]
         * image : https://s3-us-west-1.amazonaws.com/scenekey-venues/prod/5b8e9688c5743venue.jpg
         * menu :
         * feedPost : [{"id":"2169","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d9ef1a48df20.jpg"},{"id":"2166","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d9b3c52a29e8.jpg"},{"id":"2157","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d96f1871b48f.jpg"},{"id":"1745","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5bd9aacab86be.jpg"},{"id":"1734","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5bd94b232e3ec.jpg"},{"id":"1617","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5ba4999c082e5.jpg"},{"id":"1611","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5ba496809c0e9.jpg"},{"id":"1610","feed_image":"https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5ba49668ea9fe.jpg"}]
         * keyInUser : []
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
        private List<?> keyInUser;

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

        public List<?> getKeyInUser() {
            return keyInUser;
        }

        public void setKeyInUser(List<?> keyInUser) {
            this.keyInUser = keyInUser;
        }

        public static class VenueHourBean implements Serializable {
            /**
             * id : 5696
             * venue_id : 145210
             * sunday : 5P-2A
             * monday : 5P-2A
             * tuesday : 5P-2A
             * wednesday : 5P-2A
             * thursday : 5P-2A
             * friday : 5P-2A
             * saturday : 5P-2A
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

        public static class FeedPostBean implements Serializable{
            /**
             * id : 2169
             * feed_image : https://s3-us-west-1.amazonaws.com/scenekey-events/prod/5d9ef1a48df20.jpg
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
    }
}
