# Development reflection

**Authors:** Asaf Ziv and Iness Goldes.

This document preserves the substance of the submitted project reflection on AI use. Personal identity numbers have been omitted.

## Where AI was used

We used AI during database design and implementation, including drafting `CREATE TABLE` and `INSERT` statements and understanding how to integrate JDBC with the Java backend.

It helped with repetitive SQL drafting and with the transition from the original object-oriented model to relational storage. Generated code required review and adjustments to the project's schema and Java classes.

## How the work was checked

In the original project, we reviewed the generated code, ran SQL queries in PostgreSQL and compared their results with the Java application. We tested database interaction methods and refactored generated code to fit the existing logic. This describes the original project work, rather than a new integration test of this repository snapshot.

| Task | Tool | Request | Review and changes |
| --- | --- | --- | --- |
| SQL schema | Gemini | Convert the Java OOP project into a PostgreSQL schema in third normal form. | Adjusted data types, table and column names, and normalization decisions. |
| Sample records | Gemini | Populate the tables with dummy records that satisfy the schema rules. | Reviewed and used the sample data. |
| JDBC integration | Gemini | Explain the Java changes needed to integrate PostgreSQL using JDBC in IntelliJ. | Iterated on generated code and corrected integration problems. |
| Database trigger | Gemini | Suggest useful triggers for the schema. | Selected and implemented the chairman-degree validation trigger. |
