# Currency Converter

This is a (not so) small coding assignment to evaluate one's development skills. This repo contains two separate applications - a Vue.js frontend and a Java Spring Boot backend for a currency conversion application utilizing [Swop](http://swop.cx/) for exchange rates.

## API endpoints
- The API exposes two public REST API endpoints, no authentication required
- `GET /currencies` to get a list of supported currencies
	- Returns `List<String>` as a JSON
- `GET /convert` to convert a source currency to a target currency
	- Takes query params:
		- `String source`
		- `String target`
		- `Double value`
		- For example: `/convert?source=EUR&target=GBP&value=20`
	- Returns a JSON object:
		- `String sourceCurrency` requested source currency
		- `String targetCurrency` requested target currency
		- `double exchangeRate` exchange rate used, rounded to 4 digits
		- `double originalValue` original value received, rounded to 2 digits
		- `double convertedValue` converted value, rounded to 2 digits
		- `String operationDate` in ISO-8601 format, the time and date when the conversion has been performed.

## Running the application

### Vue Frontend
> Requires Node 20 or above and Yarn installed
- Navigate to `/services/vue-frontend` folder.
- Run `yarn` to download dependencies.
- Run `yarn dev` to start the dev server.
- The application will be available at `http://localhost:8080/`.

### Java API
> Requires Java 17 or above and Gradle installed
- Navigate to `/services/monolith` folder.
- Create `secret.properties` file inside the `src/main/resources/` folder.
- Put your swop.cx API key at `secrets.swop.apikey` key (see `secret.properties.example`).
- Run `gradle bootRun`.
- The API will be available at `http://localhost:3000/`.
