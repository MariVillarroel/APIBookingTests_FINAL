@run
Feature: Booking API

  Scenario: Update booking information
    Given I create a test booking
    And I store the booking ID
    When I update the booking with new information:
      | Mariana    |
      | Villarroel |
      | 300        |
      | Lunch      |
    Then I verify status code is 200
    And I verify booking is updated successfully

  Scenario: Delete a booking
    Given I create a test booking
    And I store the booking ID
    When I delete the booking
    Then I verify status code is 201
    And I verify booking is deleted successfully

  Scenario: Booking with multiple additional needs
    Given I create a booking with multiple additional needs:
       | Mariana       |
       | Villarroel    |
       | 250           |
       | Breakfast, Lunch, Dinner, WiFi |
    Then I verify status code is 200
    And I verify all additional needs are stored correctly

  Scenario: Booking with maximum allowed stay
    Given I create a booking with maximum stay:
      | Victor        |
      | Villarroel    |
      | 500           |
      | 2024-01-01    |
      | 2024-01-31    |
      | Extended Stay |
    Then I verify status code is 200
    And I verify the stay duration is 30 days

  Scenario: Create booking with different room types
    Given I create bookings for different room types:
      | RoomTypeUser |
      | Tester       |
      | single       |
      | 100          |
    And I create bookings for different room types:
      | RoomTypeUser |
      | Tester       |
      | double       |
      | 200          |
    And I create bookings for different room types:
      | RoomTypeUser |
      | Tester       |
      | suite        |
      | 500          |
    Then I verify all bookings are created successfully