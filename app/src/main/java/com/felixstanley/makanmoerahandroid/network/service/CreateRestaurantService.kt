package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.restaurant.*
import retrofit2.http.*
import java.io.File

private const val BASE_URL = "createRestaurant"

interface CreateRestaurantService {

    @GET("$BASE_URL/getRestaurantBelongToUser")
    suspend fun getRestaurantBelongToUser(): Restaurant

    @POST("$BASE_URL/submitGeneralInformation")
    suspend fun submitGeneralInformation(
        @Body generalInformationRestaurant: GeneralInformationRestaurant
    )

    @POST("$BASE_URL/submitOperatingHours")
    suspend fun submitOperatingHours(
        @Body hoursRestaurantSubmit: HoursRestaurantSubmit
    )

    @POST("$BASE_URL/submitHolidayHours")
    suspend fun submitHolidayHours(
        @Body holidayHoursRestaurantSubmit: HolidayHoursRestaurantSubmit
    )

    @POST("$BASE_URL/submitMenus")
    suspend fun submitMenus(
        @Body menusRestaurantSubmit: MenusRestaurantSubmit
    )

    @POST("$BASE_URL/submitFacilities")
    suspend fun submitFacilities(
        @Body facilitiesRestaurantSubmit: FacilitiesRestaurantSubmit
    )

    @POST("$BASE_URL/submitPaymentModes")
    suspend fun submitPaymentModes(
        @Body paymentModeRestaurantSubmit: PaymentModeRestaurantSubmit
    )

    @POST("$BASE_URL/submitDiscountTemplate")
    suspend fun submitDiscountTemplate(
        @Body discountTemplateRestaurantSubmit: DiscountTemplateRestaurantSubmit
    ): String

    @POST("$BASE_URL/deleteDiscountTemplate")
    suspend fun deleteDiscountTemplate(
        @Body discountTemplateId: Int
    )

    @POST("$BASE_URL/submitCapacity")
    suspend fun submitCapacity(
        @Body capacityRestaurantSubmit: CapacityRestaurantSubmit
    )

    @POST("$BASE_URL/deleteCapacity")
    suspend fun deleteCapacity(
        @Body deleteCapacityRestaurantSubmit: DeleteCapacityRestaurantSubmit
    )

    @Multipart
    @POST("$BASE_URL/uploadImage")
    suspend fun uploadImage(
        @Part imageFile: File
    )

    @POST("$BASE_URL/deleteImage")
    suspend fun deleteImage(
        @Body imageId: Int
    )
}