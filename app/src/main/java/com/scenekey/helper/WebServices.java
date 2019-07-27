package com.scenekey.helper;

/**
 * Created by mindiii on 1/2/18.
 */

public class WebServices {

//    /*New Code*/
    private static final String BASE_URL = "https://scenekey.com/event/";   // Live
//    private static final String BASE_URL = "https://dev.scenekey.com/event/";   // Dev
    private static final String WEBVIEWBASE_URL = "https://dev.scenekey.com/";   // Dev

    //--------deepak's code------
    public final static String IMAGEUPLOAD_BUCKET = BASE_URL + "awsImage";
    public final static String GET_BUCKET_DATA = BASE_URL + "getBucketData";
    public final static String GET_MY_FOLLOW_TAGS = BASE_URL + "getMyFollowTag";
    public final static String DELETE_BUCKET_IMAGE = BASE_URL + "deleteImage";
    //-------------------end-----------------------

    public final static String NORMAL_LOGIN = BASE_URL + "webservices/login";
    public final static String CHECK_EMAIL_REG = BASE_URL + "webservices/checkEmail";
    public final static String REGISTRATION = BASE_URL + "webservices/registration";
    public final static String FORGOTPASSWORD = BASE_URL + "webservices/forgotPassword";

    public final static String CHECK_FB_LOGIN = BASE_URL + "webservices/checkSocialLogin";
    public final static String REWARD_OFFER = BASE_URL + "webservices/getRewardList";
    public final static String REWARD_ADDTOWALLET = BASE_URL + "webservices/addToWallet";
    public final static String REWARD_WALLETS = BASE_URL + "webservices/getWalletList";
    public final static String REWARD_ADDTOREDEEM = BASE_URL + "webservices/addRedeem";
    public final static String CHANGEPASSWORD = BASE_URL + "webservices/resetPassword";
    public final static String UPDATEPROFILE = BASE_URL + "webservices/updateProfile";


    public final static String CHECK_EVENT_STATUS = BASE_URL + "webservices/checkeventstatus";

    public final static String DELETE_FEED = BASE_URL + "deleteFeed";
    public final static String REACTION_USER = BASE_URL + "reactionUser";
    public final static String ADDREACTION_USER = BASE_URL + "addReaction";
    public final static String DELETEREACTION_USER = BASE_URL + "deleteReaction";

    /*Old Code*/
    public final static String TERMS_ = "https://scenekey.com/Terms&conditions.pdf";
    public final static String PRIVACY_ = "https://scenekey.com/Privacypolicy.pdf";
    public final static String BASE_IMAGE_URL = "https://dev.scenekey.com/"; //old

    public final static String EVENT_BY_TAG = "https://0kh929t4ng.execute-api.us-west-1.amazonaws.com/dev/events/search";
    public final static String EVENT_TAG = "https://0kh929t4ng.execute-api.us-west-1.amazonaws.com/dev/tags";
    public final static String DEFAULT_IMAGE = "https://0kh929t4ng.execute-api.us-west-1.amazonaws.com/dev/users";
    public final static String FEED_IMAGE = "https://s3-us-west-1.amazonaws.com/scenekey-events/dev/";
    public final static String VENUE_IMAGE = "https://s3-us-west-1.amazonaws.com/scenekey-venues/dev/";   //prod
    public final static String EVENT_IMAGE = "https://s3-us-west-1.amazonaws.com/scenekey-events/dev/";  //dev is environment type=development
    public final static String USER_IMAGE = "https://s3-us-west-1.amazonaws.com/scenekey-profile-images/";

    public static final String CHK_LOGIN = BASE_URL + "webservices/chkLogin";
    public final static String LOGIN = BASE_URL + "webservices/facebookLogin";
    public final static String BIO = BASE_URL + "webservices/updateBio";
    public final static String EVENT_BY_LOCAL = BASE_URL + "eventByLocation";
    //public final static String TRENDING = BASE_URL + "trending";
    public final static String TRENDING = BASE_URL + "trendingNew";
    public final static String TRENDING_TAG = BASE_URL + "trending_tag";
    public final static String TAG_FOLLOW_UNFOLLOW = BASE_URL + "tagFollowUnfollw";
    public final static String LISTUSEREVENT = BASE_URL + "webservices/listofuserattendedevent";
    public final static String LISTEVENTFEED = BASE_URL + "webservices/listofeventfeeds";
    public final static String VENUE_SEARCH = BASE_URL + "venueSearch";
    public final static String CATEGORY_TAG = BASE_URL + "allCategory";

    public final static String VENUEBOARD_EVENT_TAG = BASE_URL + "eventTags";
    public final static String VENUEBOARD = BASE_URL + "venueBoard";

    public final static String TNC_WEBURL = WEBVIEWBASE_URL + "Terms&conditions-min.pdf";
    public final static String PRIVACY_POLICY_WEBURL = WEBVIEWBASE_URL + "Privacypolicy.pdf";

    /**
     * Event Like , Comment and post picture
     ***/

    public final static String LIKE_EVENT = BASE_URL + "webservices/addEventLike";
    public final static String SEARCH_TAG = BASE_URL + "categoryList";
    public final static String TAG_SEARCH = BASE_URL + "tag_search";
    public final static String TAG_SEARCH_CATEGORY = BASE_URL + "tagSearchCategory";
    public final static String TAGLIST = BASE_URL + "tagList";

    public final static String EVENT_DETAIL = BASE_URL + "webservices/get_event_detail/?event_id=";
    public final static String EVENT_LIKE = BASE_URL + "webservices/addEventLike";
    public final static String EVENT_COMMENT = BASE_URL + "webservices/addeventcomment";
    public final static String EVENT_POST_PIC = BASE_URL + "webservices/addeventpicture";
    public final static String ADD_EVENT = BASE_URL + "webservices/addevent";  //TODO increment key points with checking response
    public final static String ADD_NUDGE = BASE_URL + "webservices/addnudges";
    public final static String GET_NUDGE = BASE_URL + "webservices/getnudges";
    public final static String SET_STATUS = BASE_URL + "webservices/SetUserStatus";
    public final static String LISTATTENDEDEVENT = BASE_URL + "webservices/listofuserattendedevent";

    /*[{"key":"user_id","value":"174"},{"key":"venue_id","value":"130178"},{"key":"date","value":"2017-06-05"},{"key":"time","value":"9:00:00"},{"key":"event_name","value":"Monday ..... Test "},{"key":"interval","value":"15"},{"key":"description","value":"Android Testing Event For IOS also"}]*/
    //Update
    public final static String Update_INFO = BASE_URL + "webservices/updateuserInfo";
    public final static String MAKE_SCENE = BASE_URL + "makescen";
    public final static String MAKE_SCENE_UPDATE = BASE_URL + "makescenUpdate";
    public final static String DELETE_EVENT = BASE_URL + "deleteEvent";
    public final static String ALL_CATEGORY = BASE_URL + "allCategory";
    public final static String ADD_VENUE = BASE_URL + "addVenue";
    public final static String VENUE_DETAILS = BASE_URL + "venueDetail";

}
