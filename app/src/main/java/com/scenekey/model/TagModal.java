package com.scenekey.model;

import java.io.Serializable;

public class TagModal  implements Serializable {

  /*  "status": "success",
            "tagList": [
    {
        "biz_tag_id": "1",
            "tag_name": "Pub",
            "color_code": "#b3cab4",
            "tag_image": "https://s3-us-west-1.amazonaws.com/scenekey-tag-image/dev/1548251800tag.jpg"
    },*/


  public  String biz_tag_id;
  public  String tag_name;
  public  String color_code;
  public  String tag_text;
  public  String tag_image;
  public  String distance;
  public  String category_name;
  public  String status;
  public  String is_tag_follow;
  public  String isVenue;
  public  boolean makeOwnItem = false;
  public  String cat_id;



  /*{
    "biz_tag_id":"53",
          "tag_name":"Bear",
          "color_code":"#0000ff",
          "tag_text":"Hot new bear for $5",
          "tag_image":"https:\/\/s3-us-west-1.amazonaws.com\/scenekey-tag-image\/dev\/1550935160tag.jpg",
          "distance":"0.15895444193097066"
  },*/

}
