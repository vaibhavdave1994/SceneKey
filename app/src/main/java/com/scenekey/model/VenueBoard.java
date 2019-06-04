package com.scenekey.model;

import java.util.List;

public class VenueBoard {


    /**
     * status : success
     * eventTag : [{"cat_id":"21","category_name":"Venue Type","color_code":"#7725d1","category_image":"https://s3-us-west-1.amazonaws.com/scenekey-category-image/dev/1549346740category.jpg","tagList":[{"biz_tag_id":"1","tag_name":"Pub","color_code":"#b3cab4","tag_text":"","tag_image":"https://s3-us-west-1.amazonaws.com/scenekey-tag-image/dev/1548251800tag.jpg"}]}]
     */

    private String status;
    private List<EventTagBean> eventTag;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EventTagBean> getEventTag() {
        return eventTag;
    }

    public void setEventTag(List<EventTagBean> eventTag) {
        this.eventTag = eventTag;
    }

    public static class EventTagBean {
        /**
         * cat_id : 21
         * category_name : Venue Type
         * color_code : #7725d1
         * category_image : https://s3-us-west-1.amazonaws.com/scenekey-category-image/dev/1549346740category.jpg
         * tagList : [{"biz_tag_id":"1","tag_name":"Pub","color_code":"#b3cab4","tag_text":"","tag_image":"https://s3-us-west-1.amazonaws.com/scenekey-tag-image/dev/1548251800tag.jpg"}]
         */

        private String cat_id;
        private String category_name;
        private String color_code;
        private String category_image;
        private List<TagListBean> tagList;

        public String getCat_id() {
            return cat_id;
        }

        public void setCat_id(String cat_id) {
            this.cat_id = cat_id;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public String getColor_code() {
            return color_code;
        }

        public void setColor_code(String color_code) {
            this.color_code = color_code;
        }

        public String getCategory_image() {
            return category_image;
        }

        public void setCategory_image(String category_image) {
            this.category_image = category_image;
        }

        public List<TagListBean> getTagList() {
            return tagList;
        }

        public void setTagList(List<TagListBean> tagList) {
            this.tagList = tagList;
        }

        public static class TagListBean {
            /**
             * biz_tag_id : 1
             * tag_name : Pub
             * color_code : #b3cab4
             * tag_text :
             * tag_image : https://s3-us-west-1.amazonaws.com/scenekey-tag-image/dev/1548251800tag.jpg
             */

            private String biz_tag_id;
            private String tag_name;
            private String color_code;
            private String tag_text;
            private String tag_image;
            private String is_tag_follow;

            public String getIs_tag_follow() {
                return is_tag_follow;
            }

            public void setIs_tag_follow(String is_tag_follow) {
                this.is_tag_follow = is_tag_follow;
            }

            public String getBiz_tag_id() {
                return biz_tag_id;
            }

            public void setBiz_tag_id(String biz_tag_id) {
                this.biz_tag_id = biz_tag_id;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }

            public String getColor_code() {
                return color_code;
            }

            public void setColor_code(String color_code) {
                this.color_code = color_code;
            }

            public String getTag_text() {
                return tag_text;
            }

            public void setTag_text(String tag_text) {
                this.tag_text = tag_text;
            }

            public String getTag_image() {
                return tag_image;
            }

            public void setTag_image(String tag_image) {
                this.tag_image = tag_image;
            }
        }
    }
}
