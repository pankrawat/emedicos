package com.emedicoz.app.Utils.Network;

/**
 * Created by Cbc-03 on 08/10/16.
 */
public interface API {

    String MAIN_SERVER_URL = "http://emedicoz.com/production_dams/index.php/"; // Dev server

//    String MAIN_SERVER_URL = "http://emedicoz.com/index.php/"; // Live server

    String API_OTP = MAIN_SERVER_URL + "data_model/user/mobile_auth/send_otp";

    String API_CHANGE_PASSWORD_OTP = MAIN_SERVER_URL + "data_model/user/Mobile_auth/send_otp_for_change_password";

    String API_UPDATE_PASSWORD_WITH_OTP = MAIN_SERVER_URL + "data_model/user/registration/update_password_via_otp";

    String API_REGISTER_USER = MAIN_SERVER_URL + "data_model/user/registration";

    String API_USER_DAMS_VERIFICATION = MAIN_SERVER_URL + "data_model/user/dams_user_verification/user_verification";

    String API_USER_LOGIN_AUTHENTICATION = MAIN_SERVER_URL + "data_model/user/registration/login_authentication";

    String API_USER_DAMS_LOGIN_AUTHENTICATION = MAIN_SERVER_URL + "data_model/user/registration/dams_auth";

    String API_POST_MCQ = MAIN_SERVER_URL + "data_model/user/post_mcq/add_mcq";

    String API_POST_NORMAL_VIDEO = MAIN_SERVER_URL + "data_model/user/post_text/add_post";

    String API_PEOPLE_YOU_MAY_KNOW = MAIN_SERVER_URL + "data_model/fanwall/people_you_may_know/get_list";

    String API_GET_FEEDS_FOR_USER = MAIN_SERVER_URL + "data_model/fanwall/fan_wall/get_fan_wall_for_user";

    String API_LIKE_POST = MAIN_SERVER_URL + "data_model/user/post_like/like_post";

    String API_DISLIKE_POST = MAIN_SERVER_URL + "data_model/user/post_like/dislike_post";

    String API_REPORT_POST = MAIN_SERVER_URL + "data_model/user/Post_report_abuse/report";

    String API_DELETE_POST = MAIN_SERVER_URL + "data_model/user/Post_delete/delete_post";

    String API_SHARE_POST = MAIN_SERVER_URL + "data_model/user/post_share/share_post";

    String API_ADD_BOOKMARK = MAIN_SERVER_URL + "data_model/user/post_bookmarks/add_to_bookmarks";

    String API_REMOVE_BOOKMARK = MAIN_SERVER_URL + "data_model/user/post_bookmarks/remove_from_bookmarks";

    String API_ADD_COMMENT = MAIN_SERVER_URL + "data_model/user/post_comment/add_comment";

    String API_EDIT_COMMENT = MAIN_SERVER_URL + "data_model/user/post_comment/update_comment";

    String API_DELETE_COMMENT = MAIN_SERVER_URL + "data_model/user/post_comment/delete_comment";

    String API_GET_COMMENT_LIST = MAIN_SERVER_URL + "data_model/user/post_comment/get_post_comment";

    String API_GET_COURSE_LIST_ZERO_LEVEL = MAIN_SERVER_URL + "data_model/user/User_category_handling/get_category_basic";

    String API_GET_COURSE_LIST_FIRST_LEVEL = MAIN_SERVER_URL + "data_model/user/User_category_handling/get_category_basic_level_one";

    String API_GET_COURSE_LIST_SECOND_LEVEL = MAIN_SERVER_URL + "data_model/user/User_category_handling/get_category_basic_level_two";

    String API_GET_COURSE_INTERESTED_IN = MAIN_SERVER_URL + "data_model/user/User_category_handling/get_intersted_courses";

    String API_STREAM_REGISTRATION = MAIN_SERVER_URL + "data_model/user/registration/stream_registration";

    String API_GET_USER = MAIN_SERVER_URL + "data_model/user/Registration/get_active_user/";

    String API_FOLLOW = MAIN_SERVER_URL + "data_model/user/user_follow/follow_user";

    String API_UNFOLLOW = MAIN_SERVER_URL + "data_model/user/user_follow/unfollow_user";

    String API_SUBMIT_QUERY = MAIN_SERVER_URL + "data_model/user/user_queries/submit_query";

    String API_FOLLOWING_LIST = MAIN_SERVER_URL + "data_model/user/user_follow/following_list";

    String API_FOLLOWERS_LIST = MAIN_SERVER_URL + "data_model/user/user_follow/followers_list";

    String API_FEEDS_BANNER = MAIN_SERVER_URL + "data_model/fanwall/Fan_wall_banner/get_fan_wall_banner";

    String API_SINGLE_POST_FOR_USER = MAIN_SERVER_URL + "data_model/fanwall/fan_wall/get_single_post_data_for_user";

    String API_GET_TAG_LISTS = MAIN_SERVER_URL + "data_model/user/post_meta_tags/get_list_tags/";

    String API_GET_USER_NOTIFICATIONS = MAIN_SERVER_URL + "data_model/notification_genrator/activity_logger/get_user_activity";

    String API_CHANGE_NOTIFICATION_STATE = MAIN_SERVER_URL + "data_model/notification_genrator/activity_logger/make_activity_viewed";

    String API_GET_LIVE_STREAM = MAIN_SERVER_URL + "data_model/fanwall/live_stream/top_video_stream";

    String API_GET_APP_VERSION = MAIN_SERVER_URL + "data_model/version/version/get_version";

    String API_GET_NOTIFICATION_COUNT = MAIN_SERVER_URL + "data_model/notification_genrator/activity_logger/all_notification_counter";

    String API_UPDATE_DEVICE_TOKEN = MAIN_SERVER_URL + "data_model/user/registration/update_device_info";

    String API_UPDATE_DAMS_TOKEN = MAIN_SERVER_URL + "data_model/user/registration/update_dams_id_user";

    String API_REQUEST_VIDEO_LINK = MAIN_SERVER_URL + "data_model/fanwall/fan_wall/on_request_create_video_link";

    String API_EDIT_MCQ_POST = MAIN_SERVER_URL + "data_model/user/post_mcq/edit_mcq";

    String API_EDIT_NORMAL_POST = MAIN_SERVER_URL + "data_model/user/post_text/edit_post";

    String API_ALL_NOTIFICATION_READ = MAIN_SERVER_URL + "data_model/notification_genrator/Activity_logger/set_all_read";
}