
## Features

- **Order Management**: Create, customize, and manage pancake orders.
- **Pancake Customization**: Choose from base ingredients or add custom ingredients to pancakes.
- **Order Lifecycle**: Manage the lifecycle of an order (Create → Prepare → Deliver).
- **Thread-Safe Operations**: Concurrent access to orders is handled safely to ensure consistency.
- **Validation**: Input data is validated to ensure only valid orders are processed.
- **Logging**: All actions on orders (e.g., adding pancakes, placing orders) are logged for traceability.

---

## Table of Contents

- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)
- [Folder Structure](#folder-structure)
- [Example Workflow](#example-workflow)
- [Tests](#tests)

---

## Technologies Used

- **Java 21**
- **JUnit 5** for unit testing
- **Maven** for dependency management and build
- **ConcurrentHashMap** and **ConcurrentLinkedQueue** for thread-safe operations

---

## Setup Instructions

### Prerequisites
1. Install **Java 21** or higher.
2. Install **Maven**.
3. Clone this repository:
   ```bash
   git clone <repository-url>
   cd OrderService
   ```

### Build the Project
Run the following Maven command to build the project:
```bash
mvn clean install
```

### Run the Application
To run the application:
```bash
mvn exec:java -Dexec.mainClass="org.pancakelab.Main"
```

---

## Usage

### Creating an Order
1. Disciples can create an order by specifying the building and room number.
2. Pancakes can be added with a combination of base and custom ingredients.
3. Orders can then be placed, prepared, and delivered.

---

## Folder Structure

```
src/
├── main/
│   ├── java/
│   │   ├── org.pancakelab/
│   │   │   ├── Main.java          // Entry point of the application
│   │   │   ├── model/             // Data models
│   │   │   │   ├── Order.java
│   │   │   │   ├── Pancake.java
│   │   │   │   ├── Ingredient.java
│   │   │   │   ├── IngredientMenu.java
│   │   │   ├── service/           // Business logic and services
│   │   │   │   ├── PancakeService.java
│   │   │   │   ├── OrderLog.java
├── test/
│   ├── java/
│   │   ├── org.pancakelab/
│   │   │   ├── service/
│   │   │   │   ├── PancakeServiceTest.java
│   │   │   ├── model/
│   │   │   │   ├── OrderTest.java
│   │   │   │   ├── PancakeTest.java
```

---

## Example Workflow

### Sample Code
```java
public class Main {
    public static void main(String[] args) {
        PancakeService service = new PancakeService();

        // Step 1: Create an order
        UUID orderId = service.createOrder(5, 101).getId();

        // Step 2: Add pancakes to the order
        Pancake pancake = Pancake.Builder
                .standard()
                .addCustomIngredient(Ingredient.DARK_CHOCOLATE)
                .build();
        service.addPancakeToOrder(orderId, pancake);

        // Step 3: Place the order
        service.placeOrder(orderId);

        // Step 4: Prepare the order
        service.prepareOrder();

        // Step 5: Deliver the order
        service.deliverOrder();
    }
}
```

### Output
```plaintext
Final Order Details:
Order ID: 123e4567-e89b-12d3-a456-426614174000
Building: 5
Room: 101
Status: DELIVERED
Pancakes:
  - Pancake{baseIngredients=[FLOUR, MILK, EGG], customIngredients=[DARK_CHOCOLATE]}
```

---

## Tests

### Running Tests
To run the test suite:
```bash
mvn test
```

### Key Test Cases
1. **Order Creation**:
    - Validates that orders are created successfully with valid inputs.
    - Ensures invalid inputs are rejected.

2. **Pancake Customization**:
    - Tests the addition of base and custom ingredients.
    - Ensures ingredient validation works correctly.

3. **Order Lifecycle**:
    - Tests the state transitions of orders (INIT → CREATED → PREPARED → DELIVERED).
    - Validates that only valid state transitions are allowed.

4. **Concurrency**:
    - Ensures that concurrent modifications to the same order are handled safely.