package com.emedicoz.app.Utils;

import android.graphics.Color;

import java.util.Calendar;

/**
 * Created by Cbc-03 on 05/23/17.
 */

public interface Const {

    int color[] = {Color.parseColor("#F44336"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#603e94"),
            Color.parseColor("#8C9EFF"),
            Color.parseColor("#00695C")};

    //development/testing path for Amazon S3

    String AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES = "dams-apps-production/Test_Folder/profile_images";
    String AMAZON_S3_BUCKET_NAME_FANWALL_IMAGES = "dams-apps-production/Test_Folder/fanwall_images";
    String AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES = "dams-apps-production/Test_Folder/video_thumbnails";
    String AMAZON_S3_BUCKET_NAME_DOCUMENT = "dams-apps-production/Test_Folder/doc_folder";
    String AMAZON_S3_BUCKET_NAME_FEEDBACK = "dams-apps-production/Test_Folder/feedback_images";
    String AMAZON_S3_FILE_NAME_CREATION = SharedPreference.getInstance().getLoggedInUser().getId() + "sample_" + Calendar.getInstance().getTimeInMillis();

    //  production/live path for Amazon S3

//    String AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES = "dams-apps-production/profile_images";
//    String AMAZON_S3_BUCKET_NAME_FANWALL_IMAGES = "dams-apps-production/fanwall_images";
//    String AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES = "dams-apps-production/video_thumbnails";
//    String AMAZON_S3_BUCKET_NAME_DOCUMENT = "dams-apps-production/doc_folder";
//    String AMAZON_S3_BUCKET_NAME_FEEDBACK = "dams-apps-production/feedback_images";
//    String AMAZON_S3_FILE_NAME_CREATION = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis();

    String AMAZON_S3_ACCESS_KEY = "AKIAJOX5DIUSM6P6AIYA";
    String AMAZON_S3_SECRET_KEY = "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";
    String AMAZON_S3_END_POINT = "https://s3.ap-south-1.amazonaws.com/";

    String AMAZON_S3_BUCKET_NAME_VIDEO = "dams-apps-production";

    String AMAZON_S3_IMAGE_PREFIX = "https://s3.ap-south-1.amazonaws.com/";
    String GOOGLE_PREVIEW_DOC_URL = "https://docs.google.com/gview?embedded=true&url=";


    //OFFLINE DATA CONTRAINTS
    String OFFLINE_FEEDS = "offline_feeds";
    String OFFLINE_SAVEDNOTES = "offline_savednotes";

    //VARIABLES FOR REFRESHING FEEDS IN CERTAIN CONDITIONS
    String POST = "post";
    String BOOKMARK_APPEND = "?bookmark_request=yes";
    String IS_WATCHER = "?is_watcher=";
    String FOLLOWER_ID = "follower_id";
    String FILE_NAME = "file_name";

    // global topic to receive app wide push notifications
    int REQUEST_TAKE_GALLERY_VIDEO = 2221;
    int REQUEST_TAKE_GALLERY_IMAGE = 2222;
    int REQUEST_TAKE_GALLERY_DOC = 23;

    // broadcast receiver intent filters
    String NOTIFICATION_CODE = "notification_code";
    String NOTIFICATION_COUNT = "notification_count";

    String COMMENT_POST = "post_comment";
    String LIKE_POST = "post_like";
    String SHARE_POST = "post_share";

    String FOLLOWING_USER = "following";

    // id to handle the notification in the notification tray
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;
    String IS_NOTIFICATION_BLOCKED = "is_notification_blocked";

    String SHARED_PREF = "ah_firebase";
    String FIREBASE_TOKEN_ID = "firebase_token_id";
    ////////////////////////////////


    String PATH = "path";
    String IMAGE_PATH = "image_path";
    String SIGNUP = "SIGNUP";
    String LOGIN = "LOGIN";

    String IS_USER_LOGGED_IN = "is_user_logged_in";
    String IS_USER_REGISTRATION_DONE = "is_user_reg_done";
    String IS_PHONE_VERIFIED = "is_phone_verified";
    String USER_LOGGED_IN = "user_logged_in";
    String DEVICE_TYPE_ANDROID = "1";
    String SOCIAL_TYPE_TRUE = "1";
    String SOCIAL_TYPE_FALSE = "0";

    String STATUS = "status";
    String TRUE = "true";
    String FALSE = "false";
    String MESSAGE = "message";
    String DATA = "data";
    String ERROR = "error";
    String TITLE = "title";
    String DESCRIPTION = "description";
    String CATEGORY = "category";
    String TITLE_DATA = "title_data";
    String COUNTER = "counter";

    String NAME = "name";
    String ID = "id";
    String EMAIL = "email";
    String PASSWORD = "password";
    String MOBILE = "mobile";
    String GENDER = "gender";
    String HLS = "hls";
    String DAMS_USERNAME = "dams_username";
    String DAMS_PASSWORD = "dams_password";

    //image from facebook
    String PICTURE = "picture";
    String URL = "url";

    String IMGURL = "img_url";// image from google+
    String PICTUREURL = "pictureUrl"; //image from linkedin
    String PROFILE_PICTURE = "profile_picture"; //image from linkedin // dams local variable

    String IS_DAMS_USER = "isdamsuser";
    String DAMS_TOKEN = "dams_tokken";
    String USER_TOKEN = "user_tokken";

    String DEVICE_TYPE = "device_type";// 0 for none , 1 for android , 2 for IOS
    String DEVICE_TOKEN = "device_tokken";

    String IS_SOCIAL = "is_social";// 0 for no , 1 for yes
    String SOCIAL_TYPE = "social_type";// 0 for none , 1 for facebook , 2 for gmail , 3 for linkedin
    String SOCIAL_TOKEN = "social_tokken";
    String SOCIAL_TYPE_FACEBOOK = "1";
    String SOCIAL_TYPE_GMAIL = "2";
    String SOCIAL_TYPE_LINKEDIN = "3";

    String OTP = "otp";
    String FRAG_TYPE = "frag_type";
    String TYPE = "type";
    String YOUTUBE_ID = "youtube_id";

    //CONSTANTS FOR THE FRAGMENTS
    String CHANGEPASSWORD = "changepassword";
    String FORGETPASSWORD = "forgetpassword";
    String MOBILEVERIFICATION = "mobileverification";
    String OTPVERIFICATION = "otpverification";
    String REGISTRATION = "registration";
    String COURSES = "Courses";
    String ALLCOURSES = "All Courses";
    String MYCOURSES = "My Courses";
    String FEEDS = "Feeds";
    String SPECIALITIES = "Specialities";
    String SAVEDNOTES = "Saved Notes";
    String FEEDBACK = "Feedback";
    String RATEUS = "Rate Us";
    String APPSETTING = "App Settings";
    String LOGOUT = "Logout";
    String PROFILE = "PROFILE";
    String COMMENT = "comment";
    String NOTIFICATION = "notification";
    String YOUTUBE = "youtube";

    String SUBTITLECOURSE = "subtitlecourse";
    String SUBTITLE = "subtitle";
    String SEARCHED_QUERY = "searched_query";
    String IS_PROFILE_CHANGED = "is_profile_changed";

    //HEADER INFORMATION
    String DEVICE_ID = "device_id";
    String SESSION_ID = "session_id";
    String SETUP_VERSION = "setup_version";
    String DEVICE_INFO = "device_info";

    //CONSTANTS FOR THE FRAGMENT MANAGER TO ADD TO THE STACK AND RETREIVE
    String REGISTRATIONFRAGMENT = "RegistrationFragment";
    String SUBSTREAMFRAGMENT = "subStreamFragment";
    String SPCIALISATIONFRAGMENT = "specializationFragment";
    String INTERESTEDCOURSESFRAGMENT = "CourseInterestedInFragment";

    String NO_INTERNET = "Please check your internet connection.";

    //FEEDS CONSTANTS
    String POST_ID = "post_id";
    String POST_TYPE = "post_type";

    String POST_TEXT_TYPE_TEXT = "text";
    String POST_TEXT_TYPE_YOUTUBE_TEXT = "youtube_text";

    String POST_TYPE_LIVE_STREAM = "user_post_type_livestream";
    String POST_TYPE_MCQ = "user_post_type_mcq";
    String POST_TYPE_NORMAL = "user_post_type_normal";
    String POST_TYPE_PEOPLEYMK = "user_post_type_people";
    String POST_TYPE_BANNER = "user_post_type_banner";

    String POST_COMMENT = "comment";

    String POST_FRAG = "post_frag";

    //LINKEDIN AUTHNETICATIONS
    String LINKEDIN_CONSUMER_KEY = "77wyaocg0phxre";
    String LINKEDIN_CONSUMER_SECRET = "O84Zy3TX1RnnRuT1";
    String OAUTH_CALLBACK_URL = "http://www.appsquadz.com/accept";

    //POST MCQ CONSTANTS
    String USER_ID = "user_id";// primary id of logged in user
    String COLOR_CODE = "color_code";// primary id of logged in user
    String LAST_POST_ID = "last_post_id";//last post id for pagination
    String TAG_ID = "tag_id";//to add tag related search
    String SEARCH_TEXT = "search_text";//to add the text search
    String LAST_ACTIVITY_ID = "last_activity_id";//last notification id for pagination
    String LAST_COMMENT_ID = "last_comment_id";//last comment id for pagination
    String QUESTION = "question";
    String ANSWER_ONE = "answer_one";
    String ANSWER_TWO = "answer_two";
    String ANSWER_THREE = "answer_three";
    String ANSWER_FOUR = "answer_four";
    String ANSWER_FIVE = "answer_five";
    String RIGHT_ANSWER = "right_answer";

    String LINK = "link";
    String IMAGE = "image";
    String VIDEO = "video";
    String VIDEO_IMAGE = "video_image";
    String PDF = "pdf";
    String DOC = "doc";
    String XLS = "xls";
    String IMAGES = "images";
    String POSITION = "position";
    String FILE = "file";
    String TAGLIST_OFFLINE = "taglist_offline";
    String POST_TAG = "post_tag";
    String DELETE_META = "delete_meta";
    String YOUTUBE_VIDEO = "youtube_video";

    String TEXT = "text";
    String VIDEO_LINK = "video_link";
    String VIDEO_STREAM = "stream";
    String VIDEO_LIVE = "live";

    //REGISTRATION COSTANTS
    String MASTER_ID = "master_id";
    String MASTER_ID_NAME = "master_id_name";
    String MASTER_ID_LEVEL_ONE = "master_id_level_one";
    String MASTER_ID_LEVEL_ONE_NAME = "master_id_level_one_name";
    String MASTER_ID_LEVEL_TWO = "master_id_level_two";
    String MASTER_ID_LEVEL_TWO_NAME = "master_id_level_two_name";
    String OPTIONAL_TEXT = "optional_text";
    String INTERESTED_COURSE = "interested_course";
    String INTERESTED_COURSE_TEXT = "interested_course_text";


    String STREAM = "stream";
    String SUB_STREAM = "sub_stream";
    String OTHERS = "Others";


    String RESULT_AS = "resultAs";
    String MODE = "mode   ";
    int REQUEST_CODE_GALLERY = 01;
    int PICK_CAMERA = 12;
    int PICK_GALLERY = 21;
    int CROP_REQ = 222;
}
