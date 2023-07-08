package com.registroformazione.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="vendors")
public class Vendor {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
private String nome;
@OneToMany(mappedBy = "vendor", fetch = FetchType.EAGER)
@ToStringExclude
@JsonBackReference
private List<Attivita> attivitaList;
}
