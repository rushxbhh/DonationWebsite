# Donation Payment Integration with Stripe

A Spring Boot application that integrates Stripe payment gateway to process donations through secure checkout sessions. This implementation provides a simple yet powerful way to accept donations with customizable amounts, currencies, and donor information.

## Features

- **Stripe Checkout Integration**: Secure payment processing using Stripe's hosted checkout page
- **Flexible Donations**: Support for any currency and amount
- **RESTful API**: Clean HTTP endpoint for creating payment sessions
- **Secure Configuration**: API keys managed through application properties
- **Success/Cancel Handling**: Customizable redirect URLs for payment outcomes
- **Error Handling**: Robust error management with detailed responses

## Tech Stack

- **Backend**: Spring Boot (Java)
- **Payment Gateway**: Stripe API
- **Build Tool**: Maven/Gradle
- **Additional**: Lombok for cleaner code

## Prerequisites

Before running this application, ensure you have the following:

- Java 11 or higher
- Maven 3.6+ or Gradle 6.0+
- Stripe account (sign up at [stripe.com](https://stripe.com))
- Git

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/donation-service.git
cd donation-service
```

2. Add dependencies to your `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.stripe</groupId>
        <artifactId>stripe-java</artifactId>
        <version>24.0.0</version>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

3. Configure your `application.properties`:
```properties
# Server Configuration
server.port=8080

# Stripe Configuration
stripe.secret.key=sk_test_your_secret_key_here

# Application Configuration
spring.application.name=donation-service
```

4. Build the project:
```bash
./mvnw clean install
```

5. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Create Checkout Session

#### Endpoint
```http
POST /donation/checkout
Content-Type: application/json
```

#### Request Body
```json
{
  "donorName": "John Doe",
  "amount": 5000,
  "currency": "usd"
}
```

**Parameters:**
- `donorName` (String) - Name of the person making the donation
- `amount` (Long) - Donation amount in cents (e.g., 5000 = $50.00)
- `currency` (String) - Three-letter ISO currency code (e.g., "usd", "eur", "gbp")

#### Success Response
```json
{
  "status": "success",
  "message": "Session created successfully",
  "sessionId": "cs_test_a1b2c3d4e5f6g7h8i9j0",
  "sessionUrl": "https://checkout.stripe.com/pay/cs_test_..."
}
```

#### Error Response
```json
{
  "status": "error",
  "message": "Invalid API Key provided"
}
```

## Project Structure

```
src/main/java/com/donation/
├── controllers/
│   └── DonationController.java      # REST endpoint handler
├── services/
│   └── StripeService.java           # Stripe integration logic
├── dto/
│   ├── DonationRequest.java         # Request payload model
│   └── StripeResponse.java          # Response payload model
└── DonationApplication.java         # Main application class
```

## Data Transfer Objects (DTOs)

### DonationRequest.java
```java
package com.donation.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DonationRequest {
    private String donorName;
    private Long amount;        // Amount in smallest currency unit (cents)
    private String currency;    // ISO 4217 currency code
}
```

### StripeResponse.java
```java
package com.donation.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class StripeResponse {
    private String status;      // "success" or "error"
    private String message;     // Descriptive message
    private String sessionId;   // Stripe checkout session ID
    private String sessionUrl;  // URL to redirect user to Stripe checkout
}
```

## How It Works

### Payment Flow
1. **Create Request**: Client sends donation details to `/donation/checkout`
2. **Session Creation**: Server creates a Stripe checkout session with the provided details
3. **Redirect**: Client receives `sessionUrl` and redirects user to Stripe's hosted checkout page
4. **Payment**: User completes payment on Stripe's secure platform
5. **Callback**: Stripe redirects user to success or cancel URL based on payment outcome

### Architecture
```
┌─────────────┐      POST /donation/checkout      ┌─────────────────┐
│   Client    │──────────────────────────────────►│ Spring Boot App │
│ (Frontend)  │                                    │  (Your Server)  │
└─────────────┘                                    └────────┬────────┘
       │                                                    │
       │                                                    │ Create Session
       │                                                    ▼
       │                                            ┌──────────────┐
       │                                            │  Stripe API  │
       │                                            └──────┬───────┘
       │                                                   │
       │◄──────────────────────────────────────────────────┘
       │         Return sessionUrl                         
       │                                                    
       ▼                                                    
┌─────────────┐                                           
│   Stripe    │                                           
│  Checkout   │────► Payment Success ────► success.html  
│    Page     │────► Payment Cancel  ────► cancel.html   
└─────────────┘                                           
```

## Usage Examples

### Using cURL
```bash
curl -X POST http://localhost:8080/donation/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "donorName": "Jane Smith",
    "amount": 2500,
    "currency": "usd"
  }'
```

### Using JavaScript (Fetch API)
```javascript
async function createDonationCheckout() {
  try {
    const response = await fetch('http://localhost:8080/donation/checkout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        donorName: 'Jane Smith',
        amount: 2500,  // $25.00
        currency: 'usd'
      })
    });

    const data = await response.json();
    
    if (data.status === 'success') {
      // Redirect to Stripe checkout
      window.location.href = data.sessionUrl;
    } else {
      console.error('Error:', data.message);
    }
  } catch (error) {
    console.error('Request failed:', error);
  }
}
```

### Using Python (Requests)
```python
import requests

url = 'http://localhost:8080/donation/checkout'
payload = {
    'donorName': 'Jane Smith',
    'amount': 2500,
    'currency': 'usd'
}

response = requests.post(url, json=payload)
data = response.json()

if data['status'] == 'success':
    print(f"Checkout URL: {data['sessionUrl']}")
```

## Configuration

### Stripe API Keys

Get your API keys from the [Stripe Dashboard](https://dashboard.stripe.com/apikeys):

**Test Mode:**
```properties
stripe.secret.key=sk_test_51AbCdEf...
```

**Production Mode:**
```properties
stripe.secret.key=sk_live_51AbCdEf...
```

### Custom Redirect URLs

Update URLs in `StripeService.java`:
```java
.setSuccessUrl("https://yourdomain.com/success.html")
.setCancelUrl("https://yourdomain.com/cancel.html")
```

### Supported Currencies

Stripe supports 135+ currencies. Common examples:
- `usd` - US Dollar
- `eur` - Euro
- `gbp` - British Pound
- `cad` - Canadian Dollar
- `aud` - Australian Dollar
- `jpy` - Japanese Yen (zero-decimal currency)

**Note:** Zero-decimal currencies like JPY don't use cents. Amount 1000 = ¥1,000 (not ¥10.00).

## Success and Cancel Pages

Create HTML pages for redirect handling:

### success.html
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donation Successful</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            padding: 50px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .container {
            background: white;
            color: #333;
            padding: 40px;
            border-radius: 10px;
            max-width: 500px;
            margin: 0 auto;
        }
        h1 { color: #28a745; }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ Thank You!</h1>
        <p>Your donation has been processed successfully.</p>
        <p>We greatly appreciate your generosity.</p>
        <a href="/">Return to Home</a>
    </div>
</body>
</html>
```

### cancel.html
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donation Cancelled</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            padding: 50px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }
        .container {
            background: white;
            color: #333;
            padding: 40px;
            border-radius: 10px;
            max-width: 500px;
            margin: 0 auto;
        }
        h1 { color: #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <h1>❌ Payment Cancelled</h1>
        <p>Your donation was cancelled.</p>
        <p>You have not been charged.</p>
        <a href="/">Try Again</a>
    </div>
</body>
</html>
```

## Testing

### Test Cards

Use Stripe's test cards in test mode:

| Card Number | Description |
|-------------|-------------|
| 4242 4242 4242 4242 | Visa - Success |
| 4000 0000 0000 9995 | Visa - Declined |
| 5555 5555 5555 4444 | Mastercard - Success |
| 3782 822463 10005 | American Express - Success |

**CVC:** Any 3 digits (or 4 for Amex)  
**Expiry:** Any future date  
**ZIP:** Any 5 digits

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## Security Considerations

### API Key Security
- ✅ **DO:** Store keys in `application.properties` or environment variables
- ❌ **DON'T:** Hardcode keys in source code or commit to version control
- ✅ **DO:** Use different keys for test and production
- ❌ **DON'T:** Expose secret keys in client-side code

### Production Checklist
- [ ] Replace test keys with live keys
- [ ] Enable HTTPS (SSL/TLS certificates)
- [ ] Implement authentication/authorization
- [ ] Add rate limiting to prevent abuse
- [ ] Configure CORS properly
- [ ] Set up webhook endpoints for payment confirmations
- [ ] Implement input validation and sanitization
- [ ] Enable logging and monitoring
- [ ] Use environment variables for sensitive config
- [ ] Set up error tracking (Sentry, etc.)

### CORS Configuration

Add to your application for frontend integration:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/donation/**")
                .allowedOrigins("https://yourdomain.com")
                .allowedMethods("POST", "GET")
                .allowedHeaders("*");
    }
}
```

## Webhooks (Optional)

To receive real-time payment events, set up webhooks:

```java
@PostMapping("/webhook")
public ResponseEntity<String> handleWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader) {
    
    try {
        Event event = Webhook.constructEvent(
            payload, sigHeader, webhookSecret
        );
        
        // Handle different event types
        switch (event.getType()) {
            case "checkout.session.completed":
                // Payment succeeded
                break;
            case "checkout.session.expired":
                // Payment failed/expired
                break;
        }
        
        return ResponseEntity.ok("Webhook handled");
    } catch (SignatureVerificationException e) {
        return ResponseEntity.status(400).body("Invalid signature");
    }
}
```

## Troubleshooting

### Common Issues

**Issue:** "Invalid API Key provided"
- **Solution:** Verify your Stripe secret key in `application.properties`
- Ensure you're using the correct key for your environment (test vs. live)

**Issue:** CORS errors when calling from frontend
- **Solution:** Add CORS configuration (see Security section above)

**Issue:** "Amount must be at least $0.50"
- **Solution:** Stripe requires minimum amounts. For USD, minimum is 50 cents (amount: 50)

**Issue:** Currency not supported
- **Solution:** Check [Stripe's supported currencies](https://stripe.com/docs/currencies)

**Issue:** Redirect URLs not working
- **Solution:** Ensure URLs are publicly accessible. For local testing, use ngrok or similar tools

## Environment Variables

For production, use environment variables instead of `application.properties`:

```bash
export STRIPE_SECRET_KEY=sk_live_...
export SERVER_PORT=8080
```

Then in your application:
```properties
stripe.secret.key=${STRIPE_SECRET_KEY}
server.port=${SERVER_PORT:8080}
```

## Deployment

### Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/donation-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Build and Run
```bash
docker build -t donation-service .
docker run -p 8080:8080 \
  -e STRIPE_SECRET_KEY=sk_live_... \
  donation-service
```

## Use Cases

- **Non-Profit Organizations**: Accept donations for charitable causes
- **Content Creators**: Enable fans to support creators
- **Open Source Projects**: Collect funding for project development
- **Fundraising Campaigns**: Manage crowdfunding initiatives
- **Event Registration**: Accept payments for event participation

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Future Enhancements

- [ ] Recurring donation support
- [ ] Multiple payment methods (Apple Pay, Google Pay)
- [ ] Donation history tracking
- [ ] Email receipts to donors
- [ ] Admin dashboard for donation analytics
- [ ] Custom donation amounts with presets
- [ ] Multi-language support
- [ ] Tax receipt generation

## Resources

- [Stripe API Documentation](https://stripe.com/docs/api)
- [Stripe Testing Guide](https://stripe.com/docs/testing)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Stripe Checkout Integration](https://stripe.com/docs/payments/checkout)

## License

This project is open source and available under the [MIT License](LICENSE).

## Contact

For questions or support, please open an issue on GitHub.

---

**⚠️ Important:** This is a demonstration project. For production use, implement additional security measures, proper error handling, logging, and thorough testing. Always follow PCI compliance guidelines when handling payment data.
