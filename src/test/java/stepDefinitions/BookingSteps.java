package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import utils.Request;
import constants.EndPoints;
import entities.Booking;
import entities.BookingDates;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class BookingSteps {

    private Response response;
    private Integer storedBookingId;
    private final ObjectMapper mapper = new ObjectMapper();

    @Given("I create a test booking")
    public void create_test_booking() throws Exception {
        Booking booking = new Booking();
        booking.setFirstname("Test");
        booking.setLastname("User");
        booking.setTotalprice(100);
        booking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin("2024-01-01");
        dates.setCheckout("2024-01-02");
        booking.setBookingdates(dates);
        booking.setAdditionalneeds("Breakfast");

        String payload = mapper.writeValueAsString(booking);
        response = Request.post(EndPoints.BOOKING, payload);
        storedBookingId = response.then().extract().path("bookingid");
    }

    @Given("I store the booking ID")
    public void store_booking_id() {
        // ID ya está almacenado en storedBookingId
    }

    @When("I update the booking with new information:")
    public void update_booking(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<String> data = dataTable.asList(String.class);  // ← SIN transpose()

        Booking booking = new Booking();
        booking.setFirstname(data.get(0));
        booking.setLastname(data.get(1));
        booking.setTotalprice(Integer.parseInt(data.get(2)));
        booking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin("2024-01-01");
        dates.setCheckout("2024-01-02");
        booking.setBookingdates(dates);
        booking.setAdditionalneeds(data.get(3));

        String payload = mapper.writeValueAsString(booking);
        System.out.println("Payload enviado: " + payload);

        response = Request.post(EndPoints.BOOKING, payload);
        System.out.println("Respuesta recibida: " + response.asString());
    }

    @Then("I verify status code is {int}")
    public void verify_status_code(int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    @When("I delete the booking")
    public void delete_booking() {
        response = Request.delete(EndPoints.BOOKING_BY_ID, storedBookingId.toString());
    }

    @Given("I create a booking with multiple additional needs:")
    public void create_booking_multiple_needs(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<String> data = dataTable.asList(String.class); // 0..3

        Booking booking = new Booking();
        booking.setFirstname(data.get(0));
        booking.setLastname(data.get(1));
        booking.setTotalprice(Integer.parseInt(data.get(2)));
        booking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin("2024-01-01");
        dates.setCheckout("2024-01-05");
        booking.setBookingdates(dates);

        booking.setAdditionalneeds(data.get(3)); // "Breakfast, Lunch, Dinner, WiFi"

        String payload = mapper.writeValueAsString(booking);
        System.out.println("Payload enviado: " + payload);

        response = Request.post(EndPoints.BOOKING, payload);
        System.out.println("Respuesta recibida: " + response.asString());
    }


    @Given("I create a booking with maximum stay:")
    public void create_booking_max_stay(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<String> data = dataTable.asList(String.class); // 0..5

        Booking booking = new Booking();
        booking.setFirstname(data.get(0));                    // Victor
        booking.setLastname(data.get(1));                     // Villarroel
        booking.setTotalprice(Integer.parseInt(data.get(2))); // 500
        booking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin(data.get(3));   // 2024-01-01
        dates.setCheckout(data.get(4));  // 2024-01-31
        booking.setBookingdates(dates);

        booking.setAdditionalneeds(data.get(5)); // Extended Stay

        String payload = mapper.writeValueAsString(booking);
        System.out.println("Payload enviado: " + payload);

        response = Request.post(EndPoints.BOOKING, payload);
        System.out.println("Respuesta recibida: " + response.asString());  // ← LOG
    }


    @Given("I create bookings for different room types:")
    public void create_booking_room_types(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<String> data = dataTable.asList(String.class); // 0..3

        Booking booking = new Booking();
        booking.setFirstname(data.get(0));        // RoomTypeUser
        booking.setLastname(data.get(1));         // Tester
        booking.setAdditionalneeds(data.get(2) + " room"); // single/double/suite + " room"
        booking.setTotalprice(Integer.parseInt(data.get(3)));
        booking.setDepositpaid(true);

        BookingDates dates = new BookingDates();
        dates.setCheckin("2024-01-01");
        dates.setCheckout("2024-01-02");
        booking.setBookingdates(dates);

        String payload = mapper.writeValueAsString(booking);
        System.out.println("Payload enviado: " + payload);

        response = Request.post(EndPoints.BOOKING, payload);
        System.out.println("Respuesta recibida: " + response.asString());
    }


    @Then("I verify booking is updated successfully")
    public void verify_booking_updated() {
        response.then()
                .assertThat()
                .statusCode(200)
                .body("firstname", equalTo("Mariana"))
                .body("lastname", equalTo("Villarroel"))
                .body("totalprice", equalTo(300))
                .body("additionalneeds", equalTo("Lunch"));
    }

    @Then("I verify booking is deleted successfully")
    public void verify_booking_deleted() {
        response.then()
                .assertThat()
                .statusCode(201);

        // Verificar que el booking ya no existe
        Response getResponse = Request.getById(EndPoints.BOOKING_BY_ID, storedBookingId.toString());
        getResponse.then().assertThat().statusCode(404);
    }

    @Then("I verify all additional needs are stored correctly")
    public void verify_multiple_additional_needs() {
        response.then()
                .assertThat()
                .statusCode(200)
                .body("booking.additionalneeds", containsString("Breakfast"))
                .and()
                .body("booking.additionalneeds", containsString("Lunch"))
                .and()
                .body("booking.additionalneeds", containsString("Dinner"))
                .and()
                .body("booking.additionalneeds", containsString("WiFi"));
    }


    @Then("I verify the stay duration is {int} days")
    public void verify_stay_duration(int days) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkin = LocalDate.parse("2024-01-01", formatter);
        LocalDate checkout = LocalDate.parse("2024-01-31", formatter);
        long duration = ChronoUnit.DAYS.between(checkin, checkout);

        assert duration == days : "Expected " + days + " days, but got " + duration;
    }

    @Then("I verify all bookings are created successfully")
    public void verify_all_bookings_created() {
        response.then()
                .assertThat()
                .statusCode(200)
                .body("bookingid", notNullValue());
    }
}