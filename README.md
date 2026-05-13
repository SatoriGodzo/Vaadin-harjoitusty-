# Company Manager app README

- [ ] 

## Project Structure

This project has the following structure:

```
src
├── main/java/com/example
│   ├── base.ui
│   │   ├── MainLayout.java        # Pääasettelu ja navigointi
│   │   └── ViewTitle.java         # Näkymien otsikkokomponentti
│   ├── data                       # Tietokantakerros ja liiketoimintalogiikka
│   │   ├── User.java, Employee.java, Project.java...
│   │   ├── UserRepository.java, EmployeeRepository.java...
│   │   └── EmployeeService.java, UserDetailsServiceImpl.java...
│   ├── views                      # Käyttöliittymänäkymät ja lomakkeet
│   │   ├── HomeView.java          # Aloitussivu tilastoilla
│   │   ├── AdvancedSearchView.java # Kriteeripohjainen haku
│   │   ├── EmployeeView.java      # Työntekijöiden hallinta
│   │   └── LoginView.java         # Kirjautumissivu
│   ├── Application.java           # Sovelluksen käynnistysluokka (@Push päällä)
│   ├── SecurityConfig.java        # Spring Securityn määritykset
│   └── CustomI18NProvider.java    # Monikielisyyden tuki (FI/EN)
├── main/resources
│   ├── messages_en.properties     # Englanninkieliset käännökset
│   ├── messages_fi.properties     # Suomenkieliset käännökset
│   ├── application.properties     # Sovelluksen asetukset (H2, JPA)
│   └── META-INF/resources/icons   # Sovelluksen kuvakkeet (SVG)
└── frontend
    └── styles.css                 # Globaalit, interaktiiviset ja näkymäkohtaiset tyylit      
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that starts up 
the Spring Boot application.

Project Structure

This project follows a feature-oriented structure, making it easy to scale and maintain. Based on the actual implementation, the code is organized as follows:

base.ui: Contains core layout components like MainLayout.java and shared UI elements used across the entire application.

data: The heart of the application’s business logic. It includes JPA Entities (Employee, Project, User), Spring Data Repositories, and Services that handle data persistence and auditing.

views: Contains all functional UI views and forms, such as AdvancedSearchView, EmployeeView, and HomeView. Each view is a self-contained unit of functionality.

Deployment & Development
Starting in Development Mode
To start the application in development mode with live reload, run:

Bash
./mvnw
Building for Production
To package the application for production (optimizing CSS/JS and enabling production mode):

Bash
./mvnw package -Pproduction
Running with Docker (Recommended)
The project is fully containerized. To build and start the entire stack (including the application and its environment) with a single command:

Bash
docker-compose up --build
The application will be available at http://localhost:8080.

## 🔐 Default Credentials

The application initializes with three predefined users for testing different access levels (Roles):

| Username | Password | Role | Access Level |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin123` | `ADMIN` | Full access (Management & Settings) | can manage the rights of other users (delete, edit, create)
| **User** | `user123` | `USER` | Standard view/edit access |
| **sup** | `sup` | `SUPER` | Elevated permissions |

> **Note:** The data is initialized automatically on the first run via `CommandLineRunner` in `Application.java`.voi hallita muiden käyttäjien oikeuksia (poistaa, muokata, luoda)
The application allows you to manage employees, departments, projects, and employee cards, as well as perform complex searches by entering several variables at once.