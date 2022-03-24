package com.felixstanley.makanmoerahandroid.constants

object Constants {

    const val GOOGLE_RECAPTCHA_SITE_KEY = "6Le9t6McAAAAAF_5osfzVBiAVlpDxinjBuNttDKp"

    // Component Height
    const val RESTAURANT_CARD_CAROUSEL_HEIGHT_DP = 440

    // Default Booking Minute Interval
    const val DEFAULT_BOOKING_MINUTE_INTERVAL = 30

    // Backend API Null Representation
    const val NULL_REPRESENTATION = -1

    // Explore Fragment Number Of People Dropdown Items
    val EXPLORE_FRAGMENT_NUM_OF_PEOPLE_DROPDOWN_ITEMS = listOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20"
    )

    // Restaurant Details Fragment Discount Selection Initial Dropdown Items
    const val RESTAURANT_DETAILS_FRAGMENT_DISCOUNT_SELECTION_INITIAL_DROPDOWN_ITEM_NOT_AVAILABLE =
        "N/A"
    val RESTAURANT_DETAILS_FRAGMENT_DISCOUNT_SELECTION_INITIAL_DROPDOWN_ITEMS =
        listOf(RESTAURANT_DETAILS_FRAGMENT_DISCOUNT_SELECTION_INITIAL_DROPDOWN_ITEM_NOT_AVAILABLE)

    // Restaurant Details Fragment Location Map Zoom Level
    const val RESTAURANT_DETAILS_FRAGMENT_LOCATION_MAP_ZOOM_LEVEL = 16.0F

    // Shared Preferences Key
    const val SHARED_PREFERENCES_LOGIN_EMAIL_KEY = "loginEmail"
    const val SHARED_PREFERENCES_LOGIN_PASSWORD_KEY = "loginPassword"

    // Room Database
    const val COOKIE_DATABASE_NAME = "cookie_database"

    // OAuth2 URL
    private const val OAUTH2_BASE_URL = "https://makanmoerah.com/oauth2/authorization/"
    const val FACEBOOK_OAUTH2_AUTHORIZATION_URL = OAUTH2_BASE_URL + "facebook"
    const val GOOGLE_OAUTH2_AUTHORIZATION_URL = OAUTH2_BASE_URL + "google"

    // OAuth2 Redirection Cookie
    const val OAUTH2_REDIRECTION_URL_COOKIE_VALUE_QUERY_PARAMETER_NAME = "cookies"

    // Delimiter to separate Cookie Value at Query Parameters
    const val OAUTH2_REDIRECTION_URL_COOKIE_VALUE_DELIMITER = "~"
}