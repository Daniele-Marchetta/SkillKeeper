package com.registroformazione.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE persone SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Entity
@Table(name="persone")
public class Persona {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nome;
	private String cognome;
    private boolean inForza;
    @JsonIgnore
    private boolean deleted = Boolean.FALSE;
	@ManyToOne()
    @JoinColumn(name = "cc_id")
	@JsonManagedReference
    private Cc cc;
	@OneToMany(mappedBy = "persona", fetch = FetchType.EAGER)
	@ToStringExclude
	@JsonBackReference
	private List<Registro> registro;
	@ManyToOne()
    @JoinColumn(name = "area_id")
	@JsonManagedReference
    private Area area;
}
