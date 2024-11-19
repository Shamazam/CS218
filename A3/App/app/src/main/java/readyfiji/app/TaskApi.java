package readyfiji.app;

import java.util.List;

import AboutUs.AboutUsSection;
import AboutUs.ContactUs;
import AdminAddShelter.AdminShelter;
import AdminEmergencyContacts.AdminContactResponse;
import AdminEmergencyContacts.AdminDepartmentResponse;
import AdminQuickLinks.CategoryData;
import AdminQuickLinks.LinkData;
import AdminNotification.NotificationData;
import AdminSendAlert.Alert;
import EditProfile.District;
import EditProfile.Island;
import EditProfile.Suburb;
import EditProfile.TownResponse;
import EditProfile.UserProfile;
import EditProfile.UserProfileWrapper;
import EditProfile.UserResponse;
import EditProfile.Village;
import EmergencyContacts.ContactResponse;
import EmergencyContacts.DepartmentResponse;
import HomeScreen.NewsItem;
import HomeScreen.UnreadNotificationResponse;
import NotificationInbox.NotificationResponse;
import QuickLinks.QuickLinksResponse;
import SuperAdmin.Admin;
import SuperAdmin.AdminResponse;
import SuperAdmin.CountsResponse;
import Weather.FloodType;
import Weather.PolygonData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import Shelter.Shelter;

public interface TaskApi {

    // Fetch disasters
    @GET("fetchDisasters.php")
    Call<List<Disaster>> getDisasters();

    /// Fetch tasks for a specific disaster and user
    @GET("fetchTasks.php")
    Call<List<TaskItem>> getTasksForDisaster(
            @Query("disaster_id") int disasterId,
            @Query("user_id") int userId
    );

    @GET("fetchDisasterTasks.php")
    Call<List<TaskItem>> fetchDisasterTasks(@Query("disaster_id") int disasterId);


    @FormUrlEncoded
    @POST("addTask.php")
    Call<TaskItem> addTask(
            @Field("disaster_id") int disasterId,
            @Field("task_name") String taskName,
            @Field("task_type") String taskType,   // Track user/admin tasks
            @Field("user_id") int userId           // Include user_id for user-specific tasks
    );


    // Delete a task
    @FormUrlEncoded
    @POST("deleteTask.php")
    Call<Void> deleteTask(
            @Field("task_id") int taskId,
            @Field("task_type") String taskType,  // Track if it's user or admin task
            @Field("user_id") int userId          // Pass the user ID to validate ownership or track admin task status
    );


    // Update a task
    @FormUrlEncoded
    @POST("updateTask.php")
    Call<Void> updateTask(
            @Field("task_id") int taskId,
            @Field("task_name") String taskName,
            @Field("task_type") String taskType,
            @Field("completed") int completed,  // This field tracks completion status
            @Field("user_id") int userId        // Track who is completing the task
    );

    // Fetch the completion status of a specific task for a user
    @GET("getTaskCompletionStatus.php")
    Call<Integer> getTaskCompletionStatus(
            @Query("task_id") int taskId,
            @Query("user_id") int userId
    );

    // Update the completion status of a specific task for a user
    @FormUrlEncoded
    @POST("updateTaskCompletion.php")
    Call<Void> updateTaskCompletion(
            @Field("task_id") int taskId,
            @Field("user_id") int userId,
            @Field("completed") int completed
    );


    // Register a user
    @FormUrlEncoded
    @POST("register_user.php")
    Call<ResponseBody> registerUser(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("user_email") String email,
            @Field("password") String password,
            @Field("device_token") String deviceToken
    );

    // Login a user
    @FormUrlEncoded
    @POST("login_user.php")
    Call<ResponseBody> loginUser(
            @Field("user_input") String email,
            @Field("password") String password
    );

    // Fetch list of shelters
    @GET("fetchShelters.php")
    Call<List<Shelter>> getShelters();

    // Fetch specific shelter details
    @GET("fetchShelterDetails.php")
    Call<Shelter> getShelterDetails(@Query("shelter_id") int shelterId);

    // Fetch news items (Corrected)
    @GET("fetch_news.php")  // No need for @FormUrlEncoded here
    Call<List<NewsItem>> getNewsItems();

    @GET("getDepartments.php")
    Call<DepartmentResponse> getDepartments();


    // Get emergency contacts based on the department
    @GET("getEmergencyContacts.php")
    Call<ContactResponse> getContactsForDepartment(@Query("department_id") int departmentId);

    @GET("getAllContacts.php")  // Assuming you have a PHP endpoint to get all contacts
    Call<ContactResponse> getAllContacts();

    @GET("getNotifications.php")
    Call<NotificationResponse> getNotificationsForUser(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("markNotifications.php")
    Call<ResponseBody> markNotificationAsRead(
            @Field("user_id") int userId,
            @Field("notification_id") int notificationId
    );

    @FormUrlEncoded
    @POST("changepassword.php") // Assuming the PHP file is named changePassword.php
    Call<ResponseBody> changePassword(
            @Field("user_id") int userId,
            @Field("email") String email,
            @Field("current_password") String currentPassword,
            @Field("new_password") String newPassword
    );

    @FormUrlEncoded
    @POST("requestPasswordReset.php")
    Call<Void> requestPasswordReset(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("verifyCode.php")
    Call<Void> verifyCode(
            @Field("email") String email,
            @Field("code") int code
    );

    @FormUrlEncoded
    @POST("resetPassword.php")
    Call<Void> resetPassword(
            @Field("email") String email,
            @Field("password") String newPassword
    );

    // API method to add a disaster
    @FormUrlEncoded
    @POST("addDisaster.php")
    Call<Void> addDisaster(
            @Field("disaster_name") String disasterName
    );

    // Add an admin task
    @FormUrlEncoded
    @POST("addAdminTask.php")
    Call<TaskItem> addAdminTask(
            @Field("disaster_id") int disasterId,
            @Field("task_name") String taskName
    );

    @FormUrlEncoded
    @POST("addDepartment.php")
    Call<Void> addDepartment(@Field("department_name") String departmentName);

    @FormUrlEncoded
    @POST("addEmergencyContact.php")
    Call<Void> addEmergencyContact(
            @Field("department_id") int departmentId,
            @Field("landline1") String landlineOne,
            @Field("landline2") String landlineTwo,
            @Field("mobile1") String mobileOne,
            @Field("mobile2") String mobileTwo,
            @Field("mobile3") String mobileThree,
            @Field("mobile4") String mobileFour,
            @Field("street_address") String streetAddress,
            @Field("town") String town,
            @Field("city") String city,
            @Field("region") String region,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude,
            @Field("building_name") String buildingName
    );

    @FormUrlEncoded
    @POST("insertNotifications.php") // Your API endpoint
    Call<Void> sendNotification(
            @Field("title") String title,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("addShelter.php")
    Call<ResponseBody> addShelter(
            @Field("shelter_name") String shelterName,
            @Field("person_in_charge") String personInCharge,
            @Field("primary_contact") String primaryContact,
            @Field("secondary_contact") String secondaryContact,
            @Field("street_address") String streetAddress,
            @Field("town") String town,
            @Field("city") String city,
            @Field("region") String region,
            @Field("latitude") String latitude,       // Send latitude as String
            @Field("longitude") String longitude,     // Send longitude as String
            @Field("capacity") int capacity
    );


    @GET("getFloodTypes.php")
    Call<List<FloodType>> getFloodTypes();


    @FormUrlEncoded
    @POST("savePolygon.php")
    Call<ResponseBody> savePolygon(
            @Field("polygon_name") String polygonName,
            @Field("coordinates") String coordinatesJson,
            @Field("flood_type_id") int floodTypeId,
            @Field("color") String polygonColor  // Add the color field
    );

    @GET("getPolygons.php")
    Call<List<PolygonData>> getSavedPolygons();

    @FormUrlEncoded
    @POST("deletePolygon.php")
    Call<ResponseBody> deletePolygon(@Field("id") int polygonId);

    @FormUrlEncoded
    @POST("addFloodType.php")
    Call<ResponseBody> addFloodType(@Field("flood_type") String floodTypeName);

    @GET("count_unreadNotifications.php")
    Call<UnreadNotificationResponse> getUnreadNotifications(@Query("user_id") int userId);


    @Multipart
    @POST("upload.php") // Ensure the path is correct
    Call<ResponseBody> sendAlert(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @GET("getAlerts.php")
    Call<List<Alert>> getAlerts();


    // Delete an alert by ID
    @FormUrlEncoded
    @POST("deleteAlerts.php")
    Call<ResponseBody> deleteAlert(@Field("alertId") String id);

    // Fetch all towns
    @GET("getTowns.php")
    Call<TownResponse> getTowns();


    // Fetch suburbs for a specific town
    @GET("getSuburbsForTown.php")
    Call<List<Suburb>> getSuburbsForTown(@Query("town_id") int townId);

    // Fetch all districts
    @GET("getDistricts.php")
    Call<List<District>> getDistricts();

    // Fetch villages for a specific district
    @GET("getVillagesForDistrict.php")
    Call<List<Village>> getVillagesForDistrict(@Query("district_id") int districtId);


    // Fetch all islands
    @GET("getIslands.php")
    Call<List<Island>> getIslands();


    // Fetch villages for a specific island
    @GET("getVillagesForIsland.php")
    Call<List<Village>> getVillagesForIsland(@Query("island_id") int islandId);

    @GET("getUserProfile.php")
    Call<UserProfileWrapper> getUserProfile(@Query("user_id") String userId);


    @Multipart
    @POST("saveUserProfile.php")
    Call<UserResponse> updateUserProfileWithImage(
            @Part("user_id") RequestBody userId,
            @Part("first_name") RequestBody firstName,
            @Part("last_name") RequestBody lastName,
            @Part("username") RequestBody username,
            @Part("phone_number") RequestBody phoneNumber,
            @Part("street_address") RequestBody streetAddress,
            @Part("location_type") RequestBody locationType,
            @Part("location_value_id") RequestBody locationValueId,
            @Part MultipartBody.Part profile_image  // Profile image file part
    );

    @GET("fetch_links.php")
    Call<QuickLinksResponse> getQuickLinks();

    @GET("about_us.php")  // API endpoint for fetching About Us data
    Call<List<AboutUsSection>> getAboutUsSections();

    @GET("contact_us.php")  // API endpoint for fetching Contact Us data
    Call<ContactUs> getContactUs();

    // Use @GET and @Query for dynamic parameters
    @GET("getProfileImage.php")
    Call<UserProfile> getUserProfile(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("delete_disaster.php")
    Call<Void> deleteDisaster(@Field("disaster_id") int disasterId);

    @FormUrlEncoded
    @POST("deleteTaskAdmin.php")
    Call<Void> deleteTask(@Field("task_id") int taskId);


    @GET("fetchAdminTasks.php")
    Call<List<TaskItem>> getTasksForDisaster(@Query("disaster_id") int disasterId, @Query("task_type") String taskType);

    @FormUrlEncoded
    @POST("deleteuser.php")  // Assuming your PHP file is named deleteUser.php
    Call<Void> deleteUser(
            @Field("user_id") int userId  // Pass the user_id as a POST field
    );

    @FormUrlEncoded
    @PUT("editDisaster.php")
    Call<Void> editDisaster(
            @Field("disaster_id") int disasterId,
            @Field("new_disaster_name") String newDisasterName
    );

    // Edit Task Name
    @FormUrlEncoded
    @PUT("editTask.php")
    Call<Void> editTask(
            @Field("task_id") int taskId,
            @Field("new_task_name") String newTaskName
    );

    @GET("fetchShelters.php")
    Call<List<AdminShelter>> getadminShelters();

    @FormUrlEncoded
    @POST("updateShelter.php")
    Call<Void> updateShelter(
            @Field("id") int shelterId,
            @Field("shelter_name") String shelterName,
            @Field("person_in_charge") String personInCharge,
            @Field("primary_contact") String primaryContact,
            @Field("secondary_contact") String secondaryContact,
            @Field("street_address") String streetAddress,
            @Field("town") String town,
            @Field("city") String city,
            @Field("region") String region,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("capacity") int capacity
    );

    // Delete a shelter
    @FormUrlEncoded
    @POST("deleteShelter.php")
    Call<Void> deleteShelter(
            @Field("id") int shelterId
    );

    @GET("getDepartments.php")
    Call<AdminDepartmentResponse> getAdminDepartments();

    // Get emergency contacts based on the department
    @GET("getEmergencyContacts.php")
    Call<AdminContactResponse> getAdminContactsForDepartment(@Query("department_id") int departmentId);

    @GET("getAllContacts.php")  // Assuming you have a PHP endpoint to get all contacts
    Call<AdminContactResponse> getAdminAllContacts();

    @FormUrlEncoded
    @POST("updateDepartment.php")
    Call<Void> updateDepartment(
            @Field("department_id") int departmentId,
            @Field("new_name") String newDepartmentName
    );

    @FormUrlEncoded
    @POST("deleteDepartment.php")
    Call<Void> deleteDepartment(
            @Field("department_id") int departmentId
    );

    @FormUrlEncoded
    @POST("updateEmergencyContact.php")
    Call<Void> updateEmergencyContact(
            @Field("id") int contactId,
            @Field("department_id") int departmentId,
            @Field("building_name") String buildingName,
            @Field("landline1") String landline1,
            @Field("landline2") String landline2,
            @Field("mobile1") String mobile1,
            @Field("mobile2") String mobile2,
            @Field("mobile3") String mobile3,
            @Field("mobile4") String mobile4,
            @Field("street_address") String streetAddress,
            @Field("town") String town,
            @Field("city") String city,
            @Field("region") String region,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude
    );

    @FormUrlEncoded
    @POST("deleteEmergencyContact.php")
    Call<Void> deleteEmergencyContact(
            @Field("id") int contactId
    );

    @FormUrlEncoded
    @POST("deleteFloodType.php")
    Call<Void> deleteFloodType(
            @Field("id") int id  // Flood type ID
    );

    @FormUrlEncoded
    @POST("deleteNotification.php")
    Call<ResponseBody> deleteNotification(@Field("id") int notificationId);

    @GET("getNotificationTitles.php")
    Call<List<NotificationData>> getNotificationTitles();

    // Get all link categories
    @GET("getCategories.php")
    Call<List<CategoryData>> getCategories();

    // Add a new link category
    @FormUrlEncoded
    @POST("addCategory.php")
    Call<ResponseBody> addCategory(@Field("category_name") String categoryName);

    // Edit a link category
    @FormUrlEncoded
    @POST("editCategory.php")
    Call<ResponseBody> editCategory(@Field("category_id") int categoryId, @Field("category_name") String updatedName);

    // Delete a link category
    @FormUrlEncoded
    @POST("deleteCategory.php")
    Call<ResponseBody> deleteCategory(@Field("category_id") int categoryId);

    // Get links for a specific category
    @GET("getLinks.php")
    Call<List<LinkData>> getLinksForCategory(@Query("category_id") int categoryId);

    // Manage links (add and delete)
    @FormUrlEncoded
    @POST("manageLinks.php")
    Call<ResponseBody> manageLink(
            @Field("action") String action,
            @Field("category_id") Integer categoryId,  // For adding links
            @Field("link_id") Integer linkId,          // For deleting links
            @Field("link_title") String linkTitle,
            @Field("link_url") String linkUrl);

    @FormUrlEncoded
    @POST("updateShelterCapacity.php")
    Call<ResponseBody> updateShelterCapacity(
            @Field("shelter_id") int shelterId,
            @Field("new_capacity") String newCapacity
    );

    @FormUrlEncoded
    @POST("addAdmins.php")  // Replace with the actual path to your PHP file
    Call<AdminResponse> addAdmin(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("getAdmins.php")
    Call<List<Admin>> getAdmins();

    // Edit admin details
    @FormUrlEncoded
    @POST("editAdminDetails.php")
    Call<AdminResponse> editAdminDetails(
            @Field("admin_id") int adminId,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("username") String username
    );

    // Change admin password
    @FormUrlEncoded
    @POST("changeAdminPassword.php")
    Call<AdminResponse> changeAdminPassword(
            @Field("admin_id") int adminId,
            @Field("new_password") String newPassword
    );

    @GET("getCounts.php") // Assuming the PHP file is named getCounts.php
    Call<CountsResponse> getCounts();

}
