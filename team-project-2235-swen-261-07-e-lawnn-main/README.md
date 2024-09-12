# U-Fund:  Bucks for Baddies

An online U-Fund system built in SpringBoot and Angular 17
  
## Team L.A.W.N.N.

- Leif Gunhus
- Nathan McAndrew
- Nathan Klein
- Ada Grande Arriola
- Wendy Carrillo-Monarca


## Prerequisites

- Java 11=>17 (Make sure to have correct JAVA_HOME setup in your environment)
- Maven
- Angular CLI


## Startup
1. Clone the repository and go to the root directory.

**Running the Backend**

2. Navigate to `ufund-api/`  directory
3. Execute `mvn compile exec:java`

**Running the Frontend**

3. Naigate to `ufund-ui/` directory
4. Execute `ng serve -o`


## Testing

**Backend JUnit Tests:**
The Maven build script provides hooks for run unit tests and generate code coverage
reports in HTML.

To run tests on all tiers together do this:

1. Execute `mvn clean test jacoco:report`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/index.html`

To run tests on a single tier do this:

1. Execute `mvn clean test-compile surefire:test@tier jacoco:report@tier` where `tier` is one of `controller`, `model`, `persistence`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/{controller, model, persistence}/index.html`

To run tests on all the tiers in isolation do this:

1. Execute `mvn exec:exec@tests-and-coverage`
2. To view the Controller tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
3. To view the Model tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
4. To view the Persistence tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`

*(Consider using `mvn clean verify` to attest you have reached the target threshold for coverage)

**Frontend tests:**
See acceptance test plan in /etc.
  
## How to generate the Design documentation PDF

1. ~~Access the `PROJECT_DOCS_HOME/` directory~~
2. ~~Execute `mvn exec:exec@docs`~~
3. ~~The generated PDF will be in `PROJECT_DOCS_HOME/` directory~~

Design Docs are available as .pdf in /docs

## License

MIT License

See LICENSE for details.
