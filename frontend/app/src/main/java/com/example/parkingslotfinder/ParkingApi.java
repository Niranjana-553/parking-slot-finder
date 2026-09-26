package com.example.parkingslotfinder;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ParkingApi {
    @GET("api/parking-lots")
    Call<List<ParkingLot>> getParkingLots();

    @GET("api/location/district")
    Call<DistrictResponse> getDistrict(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );

    @GET("api/parking-lots/{id}")
    Call<ParkingLot> getParkingLot(@Path("id") String id);

    @GET("api/parking-slots/available/lot/{parkingLotId}")
    Call<List<ParkingSlot>> getAvailableSlots(@Path("parkingLotId") String parkingLotId);

    @POST("api/bookings")
    Call<Booking> createBooking(
            @Query("userId") String userId,
            @Query("parkingSlotId") int parkingSlotId,
            @Query("parkingLotId") int parkingLotId,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime
    );

    @GET("api/bookings/user/{userId}")
    Call<List<Booking>> getUserBookings(@Path("userId") String userId);

    @PUT("api/bookings/{bookingId}/cancel")
    Call<Booking> cancelBooking(@Path("bookingId") String bookingId);

    @GET("api/notifications/user/{userId}")
    Call<List<NotificationItem>> getNotifications(@Path("userId") String userId);

    @PUT("api/notifications/{notificationId}/read")
    Call<NotificationItem> markNotificationRead(@Path("notificationId") String notificationId);
}
