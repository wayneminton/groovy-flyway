# Groovy / Flyway Boilerplate

A simple starter project that uses [Flyway](https://flywaydb.org/) for database
migration / evolution.  The simple purpose here is to assemble the basics for
an integration test framework that uses the project's Flyway Migrations to
establish the test database.  For production either add calls to Flyway on
startup or use the command line tool during deployments.
