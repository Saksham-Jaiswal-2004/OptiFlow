# OptiFlow

A modern JavaFX desktop application focused on delivering a clean, professional login experience.

OptiFlow is built with JavaFX + FXML and designed with separation of concerns in mind, so you can easily extend it into a full desktop product (authentication, dashboards, settings, and more).

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Run the Application](#run-the-application)
- [Run from IDE](#run-from-ide)
- [Project Structure](#project-structure)
- [Configuration Notes](#configuration-notes)
- [Contributing](#contributing)
- [Roadmap Ideas](#roadmap-ideas)
- [License](#license)

## Overview

This project demonstrates a polished login UI for JavaFX desktop apps. The UI is defined in FXML, which keeps layout concerns separate from Java logic and makes the project easier to maintain.

## Features

- Centered login form with clean spacing and alignment
- Username and password input fields with prompt text
- Styled login button for primary action
- "Forgot Password?" link for recovery flow
- Optional "Sign Up" link for onboarding
- Modular structure that is easy to connect to backend authentication

## Tech Stack

- Java 21+
- JavaFX 21
- Maven 3.8+
- FXML for UI layout

## Prerequisites

Install the following tools before running the project:

- JDK 21 or later
- Maven 3.8 or later
- JavaFX SDK 21

Optional IDE support:

- IntelliJ IDEA
- Eclipse
- NetBeans

Make sure `JAVA_HOME` points to your JDK installation.

## ER Diagram
![OptiFlow ER Diagram](/src/main/resources/images/ER Diagram.png)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/OptiFlow.git
cd OptiFlow
```

### 2. Build the project

```bash
mvn clean install
```

This downloads dependencies and compiles the project.

## Run the Application

Run with Maven:

```bash
mvn javafx:run
```

If JavaFX is not detected automatically, add VM options:

```text
--module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml
```

Replace `/path/to/javafx-sdk-21/lib` with your local JavaFX SDK path.

## Run from IDE

1. Open the project in your IDE.
2. Configure JavaFX SDK in project settings.
3. Run `Main.java`.
4. If needed, add the same VM options shown above.

## Project Structure

```text
OptiFlow/
├─ src/
│  └─ main/
│     ├─ java/com/optiflow/Main.java
│     └─ resources/fxml/login.fxml
├─ pom.xml
└─ README.md
```

Key files:

- `src/main/java/com/optiflow/Main.java`: application entry point
- `src/main/resources/fxml/login.fxml`: login UI layout
- `pom.xml`: dependencies and build configuration

## Configuration Notes

- If startup fails, verify Java and JavaFX versions are compatible.
- On Windows, check that JavaFX path separators and quoting are correct.
- If IDE run config fails, confirm VM options are applied to the run configuration.

## Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make changes and commit with clear messages.
4. Push your branch.
5. Open a Pull Request.

Example:

```bash
git checkout -b feature/add-dashboard
git commit -m "Add dashboard screen scaffold"
git push origin feature/add-dashboard
```

## Roadmap Ideas

- Authentication integration (DB/API)
- Form validation and error states
- Signup and password reset screens
- Remember-me/session handling
- Dashboard after login

## License

Add your license here (for example, MIT).

If you choose MIT, create a `LICENSE` file and update this section accordingly.