package com.example.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Nimi on pakkollinen")
    @Size(min = 2, message = "On liian lyhyt nimi")
    private String firstName;

    @NotBlank(message = "Sukunimion pakkollinen")
    private String lastName;

    @Email(message = "posti virhe")
    @NotBlank
    private String email;

    private String position;

    // get+set
}